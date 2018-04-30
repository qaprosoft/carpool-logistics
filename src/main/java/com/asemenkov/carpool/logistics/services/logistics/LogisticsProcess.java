package com.asemenkov.carpool.logistics.services.logistics;

import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;
import java.util.function.IntConsumer;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import com.asemenkov.carpool.logistics.models.db.Task;
import com.asemenkov.carpool.logistics.models.dto.HubLocationDto;
import com.asemenkov.carpool.logistics.models.dto.LocationDto;
import com.asemenkov.carpool.logistics.models.dto.LogisticsDto;
import com.asemenkov.carpool.logistics.models.dto.RouteDto;
import com.asemenkov.carpool.logistics.models.dto.UserLocationDto;
import com.asemenkov.carpool.logistics.repositories.TaskRepository;
import com.asemenkov.carpool.logistics.services.algorithms.Car;
import com.asemenkov.carpool.logistics.services.algorithms.KernighanLinAlgorithm;
import com.asemenkov.carpool.logistics.services.enums.State;
import com.asemenkov.carpool.logistics.services.enums.Status;
import com.asemenkov.carpool.logistics.services.googlemaps.GoogleMapsPoint;
import com.asemenkov.carpool.logistics.services.googlemaps.PointsNeighbourship;
import com.asemenkov.carpool.logistics.utils.Factories.DuoFactory;
import com.asemenkov.carpool.logistics.utils.Factories.MonoFactory;
import com.asemenkov.carpool.logistics.utils.Factories.TriFactory;
import com.asemenkov.carpool.logistics.utils.WaitUtil;

/**
 * @author asemenkov
 * @since Feb 18, 2018
 */
public class LogisticsProcess {

	private static final int CAPACITY = 4;
	private final String id;
	private int maxLength;
	private int[][] globalMatrix;
	private long[] ids;

	private List<Task> tasks;
	private Car[] cars;

	private GoogleMapsPoint[] picks;
	private GoogleMapsPoint[] drops;

	private Thread logisticsThread;

	private @Autowired LogisticsDto logisticsDto;
	private @Autowired DuoFactory<GoogleMapsPoint[], GoogleMapsPoint[], PointsNeighbourship> pointsNeighbourshipFactory;
	private @Autowired MonoFactory<Car[], KernighanLinAlgorithm<Car>> kernighanLinForCarsFactory;
	private @Autowired DuoFactory<Integer, int[][], Car> carFactory;
	private @Autowired DuoFactory<Task, GoogleMapsPoint, HubLocationDto> hubLocationDtoFactory;
	private @Autowired DuoFactory<Task, GoogleMapsPoint, UserLocationDto> userLocationDtoFactory;
	private @Autowired TriFactory<Car, List<HubLocationDto>, List<UserLocationDto>, RouteDto> routeDtoFactory;
	private @Autowired TaskRepository taskRepository;

	public LogisticsProcess(String id, int maxLength, long... ids) {
		this.id = id;
		this.ids = ids;
		this.maxLength = maxLength;
	}

	/**
	 * Starts Logistics Process in parallel thread and return control
	 */
	public synchronized void startLogisticsProcess() {
		logisticsDto.setId(id);
		logisticsThread = new Thread(this::fetchDataBase);
		logisticsThread.start();
	}

	/**
	 * @return LogisticsDto as it is at current time
	 */
	public synchronized LogisticsDto getLogisticsDto() {
		return logisticsDto;
	}

	/**
	 * If Logistics Process is running, stops the thread and update LogisticsDto
	 */
	@SuppressWarnings("deprecation")
	public synchronized void abortLogisticsProcess() {
		if (logisticsThread.isAlive()) {
			logisticsThread.stop();
			fillErrorLogisticsDto(Status.ERROR_ABORTED_BY_USER);
		}
	}

	/**
	 * The first stage of Logistics Process:</br>
	 * 1. Retrieves TASK entities from database by taskIds</br>
	 * 2. Creates pick up and drop off Location Points from each task</br>
	 * 3. Sets index into each Point, that will be used as Global Matrix index
	 */
	@Transactional(readOnly = true)
	private void fetchDataBase() {
		logisticsDto.setStatus(Status.RUNNING_FETCH_DATABASE);

		try {
			tasks = taskRepository.findByIdIn(ids);
		} catch (Exception e) {
			fillErrorLogisticsDto(Status.ERROR_FETCH_DATABASE_SQL_EXCEPTION, e.getClass().toString());
			return;
		}

		drops = tasks.stream().map(Task::getDropoffLocation).map(GoogleMapsPoint::new).distinct()
				.toArray(GoogleMapsPoint[]::new);
		picks = tasks.stream().map(Task::getPickupLocation).map(GoogleMapsPoint::new).distinct()
				.toArray(GoogleMapsPoint[]::new);

		AtomicInteger index = new AtomicInteger(0);
		IntStream.range(0, drops.length).forEach(i -> drops[i].setIndex(index.getAndIncrement()));
		IntStream.range(0, picks.length).forEach(i -> picks[i].setIndex(index.getAndIncrement()));

		sendRequestsToGoogleMaps();
	}

	/**
	 * The second stage of Logistics Process:</br>
	 * 1. Sends requests to Google Maps service in parallel thread</br>
	 * 2. Builds Global Matrix based on responses from Google Maps</br>
	 * 3. Allocate Cars array
	 */
	private void sendRequestsToGoogleMaps() {
		logisticsDto.setStatus(Status.RUNNING_REQUESTS_TO_GOOGLE_MAPS);

		PointsNeighbourship pointsNeighbourship = pointsNeighbourshipFactory.get(picks, drops);
		Thread googleMapsThread = new Thread(pointsNeighbourship::requestGoogleMapsForDistanceMatrices);
		googleMapsThread.run();

		globalMatrix = pointsNeighbourship.getDurationMatrix();
		fillCars();

		WaitUtil.waitUntilAny((thread) -> !thread.isAlive(), 60000, 100, googleMapsThread);

		if (googleMapsThread.isAlive()) {
			googleMapsThread.interrupt();
			fillErrorLogisticsDto(Status.ERROR_REQUESTS_TO_GOOGLE_MAPS_NO_RESPONSE, "1 min elapsed");
			return;

		} else if (pointsNeighbourship.getStatus() != Status.RUNNING_REQUESTS_TO_GOOGLE_MAPS) {
			fillErrorLogisticsDto(pointsNeighbourship.getStatus(), pointsNeighbourship.getMessage());
			return;
		}

		executeKernighanLinAlgorithm();
	}

	/**
	 * The third stage of Logistics Process:</br>
	 * 1. Checks whether any passenger lives too far from his hub</br>
	 * 2. Executes Kernighan-Lin algorithm</br>
	 * 3. If it's not possible to allocate passengers in this amount of cars not
	 * braking condition [each car rides <= 60 min], adds one more empty car.
	 */
	private void executeKernighanLinAlgorithm() {
		logisticsDto.setStatus(Status.RUNNING_KERNIGHAN_LIN_ALGORITHM);

		if (Arrays.stream(cars).anyMatch(Car::isAnyRouteGreaterThanMaxLength)) {
			fillErrorLogisticsDto(Status.ERROR_KERNIGHAN_LIN_ALGORITHM_BIG_TASK);
			return;
		}

		try {
			boolean isRouteTooLong = true;
			while (isRouteTooLong) {
				kernighanLinForCarsFactory.get(cars).mix();
				if (isRouteTooLong = Arrays.stream(cars).anyMatch(car -> car.getMixResult() > maxLength))
					cars = Stream.concat(Stream.of(cars), Stream.of(carFactory.get(CAPACITY, globalMatrix)))
							.toArray(Car[]::new);
			}

		} catch (IllegalStateException e) {
			fillErrorLogisticsDto(Status.ERROR_KERNIGHAN_LIN_ALGORITHM_BECOMING_BIGGER, e.getMessage());
			return;

		} catch (Exception e) {
			fillErrorLogisticsDto(Status.ERROR_KERNIGHAN_LIN_ALGORITHM_EXCEPTION, e.getClass().toString());
			return;
		}

		fillSuccessLogisticsDto();
	}

	/**
	 * @param status
	 *            -- error status, full list can be found in messages.properties
	 * @param messages
	 *            -- any messages which will be appended to original message
	 */
	private synchronized void fillErrorLogisticsDto(Status status, String... messages) {
		logisticsDto.setState(State.ERROR);
		logisticsDto.setStatus(status);
		if (messages.length > 0)
			logisticsDto.appendMessage(messages);
		logisticsDto.setEndTime(new Date());
		cleanUp();
	}

	/**
	 * Should be called in the end of Logistics Process</br>
	 * Fill all the logistics information into LogisticsDto
	 */
	private synchronized void fillSuccessLogisticsDto() {
		logisticsDto.setState(State.SUCCESS);
		logisticsDto.setStatus(Status.SUCCESS);
		logisticsDto.setOptimization("DURATION");
		logisticsDto.setLength(Arrays.stream(cars).mapToInt(Car::getMixResult).sum());
		logisticsDto.setCars(cars.length);
		logisticsDto.setHubs(picks.length);
		logisticsDto.setPassengers(drops.length);
		logisticsDto.setRoutes(Arrays.stream(cars).map(this::mapCarToRouteDto).collect(Collectors.toList()));
		logisticsDto.setEndTime(new Date());
		cleanUp();
	}

	/**
	 * Guarantees that the same pick up location of different tasks will have the
	 * same index and the same position in Global Matrix
	 * 
	 * @param task
	 *            -- a task which corresponds to one passenger
	 * @return GoogleMapsPoint array of size [2]
	 */
	private GoogleMapsPoint[] getPickAndDropByTask(Task task) {
		GoogleMapsPoint[] toReturn = new GoogleMapsPoint[2];

		toReturn[0] = Arrays.stream(picks) //
				.filter(point -> point.getDbRepresentation().equals(task.getPickupLocation())) //
				.findFirst().get();

		toReturn[1] = Arrays.stream(drops) //
				.filter(point -> point.getDbRepresentation().equals(task.getDropoffLocation())) //
				.findFirst().get();

		return toReturn;
	}

	/**
	 * Calculates the minimal amount of cars needed to allocate all of the available
	 * passengers</br>
	 * Based on calculated amount, populates this.cars array with new cars
	 */
	private void fillCars() {
		cars = new Car[tasks.size() / CAPACITY + (tasks.size() % CAPACITY > 0 ? 1 : 0)];
		IntStream.range(0, cars.length).forEach(i -> cars[i] = carFactory.get(CAPACITY, globalMatrix));

		AtomicInteger counter = new AtomicInteger(0);
		tasks.stream().map(this::getPickAndDropByTask) //
				.forEach(p -> cars[counter.getAndIncrement() / CAPACITY] //
						.putPassenger(p[0].getIndex(), p[1].getIndex()));
	}

	/**
	 * 1. Gets picks and drops point objects based on their indexes</br>
	 * 
	 * 2. Gets list of tasks which corresponds each pick and drop pair</br>
	 * 
	 * 3. Removes duplicates of hubs locations, so only distinct ones left</br>
	 * 
	 * 4. Finds the correct order of pick up and drop off locations based on Car's
	 * route[] array</br>
	 * 
	 * 5. Builds Hub and User LocationDTOs based pick up and drop off locations and
	 * sort them according to order found in step 4</br>
	 * 
	 * 6. If 2 or more passengers should be picked up from the same hub but in
	 * different time, the later one will be chosen to be route start time</br>
	 * 
	 * 7. Estimates hub/passenger's arrival time as time of previous wayPoint plus
	 * time needed to get to the current wayPoint
	 */
	private RouteDto mapCarToRouteDto(Car car) {
		List<GoogleMapsPoint> picks = getPointsByIndexes(car.getPicks(), this.picks);
		List<GoogleMapsPoint> drops = getPointsByIndexes(car.getDrops(), this.drops);
		List<Task> tasks = getTasksByPicksDrops(picks, drops);
		List<GoogleMapsPoint> distinctPicks = picks.stream().distinct().collect(Collectors.toList());

		int[] picksOrder = Arrays.stream(car.getSuggestedRoute()) //
				.mapToInt(route -> route[0]).limit(distinctPicks.size()).toArray();
		int[] dropsOrder = Arrays.stream(car.getSuggestedRoute()) //
				.mapToInt(route -> route[1]).skip(distinctPicks.size() - 1).toArray();

		List<HubLocationDto> hubs = IntStream.range(0, distinctPicks.size())
				.mapToObj(i -> hubLocationDtoFactory.get(tasks.get(i), distinctPicks.get(i)))
				.sorted((a, b) -> arrayIndex(picksOrder, a.getIndex()) - arrayIndex(picksOrder, b.getIndex()))
				.collect(Collectors.toList());

		List<UserLocationDto> users = IntStream.range(0, drops.size())
				.mapToObj(i -> userLocationDtoFactory.get(tasks.get(i), drops.get(i)))
				.sorted((a, b) -> arrayIndex(dropsOrder, a.getIndex()) - arrayIndex(dropsOrder, b.getIndex()))
				.collect(Collectors.toList());

		if (tasks.stream().mapToLong(task -> task.getPickupTime().getTime()).distinct().count() > 1)
			hubs.get(0).setTime(new Date(tasks.stream()//
					.filter(task -> task.getHub().getId().equals(hubs.get(0).getId()))
					.mapToLong(task -> task.getPickupTime().getTime()).max().orElse(-1)));

		AtomicLong start = new AtomicLong(hubs.get(0).getTime().getTime());
		IntStream.range(1, hubs.size()).forEach(getLambdaForTimeUpdate(start, hubs));

		int delta = 1000 * globalMatrix[hubs.get(hubs.size() - 1).getIndex()][users.get(0).getIndex()];
		users.get(0).setTime(new Date(start.addAndGet(delta)));
		IntStream.range(1, users.size()).forEach(getLambdaForTimeUpdate(start, users));

		return routeDtoFactory.get(car, hubs, users);
	}

	/**
	 * @param indexes
	 *            -- pick up or drop off locations indexes from Car object
	 * @param points
	 *            -- array of all pick up or drop off locations used by all Cars
	 * @return list of GoogleMapsPoints based on indexes from Car object
	 */
	private List<GoogleMapsPoint> getPointsByIndexes(int[] indexes, GoogleMapsPoint[] points) {
		return Arrays.stream(indexes).filter(i -> i >= 0)
				.mapToObj(i -> Arrays.stream(points).filter(p -> p.getIndex() == i).findFirst().get())
				.collect(Collectors.toList());
	}

	/**
	 * For each pick[i]-drop[i] pair, finds corresponding task</br>
	 * picks.size() must be equals to drops.size()
	 * 
	 * @return list of all tasks found
	 */
	private List<Task> getTasksByPicksDrops(List<GoogleMapsPoint> picks, List<GoogleMapsPoint> drops) {
		return IntStream.range(0, drops.size())
				.mapToObj(i -> tasks.stream()
						.filter(t -> t.getDropoffLocation().equals(drops.get(i).getDbRepresentation()))
						.filter(t -> t.getPickupLocation().equals(picks.get(i).getDbRepresentation())) //
						.findFirst().get())
				.collect(Collectors.toList());
	}

	/**
	 * Simple iterative search, no sorting needed
	 * 
	 * @param array
	 *            -- integer array of any length
	 * @param element
	 *            -- integer element which is being looked for in array
	 * @return index of the first element appearance, or -1 otherwise
	 */
	private int arrayIndex(int[] array, int element) {
		return IntStream.range(0, array.length).filter(i -> array[i] == element).findFirst().orElse(-1);
	}

	/**
	 * For each integer i, retrieves duration between locs[i-1] and locs[i], using
	 * globalMatrix. This value is mapped from seconds to milliseconds. Then it's
	 * added to start (AtomicLong accumulator). And finally this long value is
	 * mapped to Date.
	 * 
	 * @param start
	 *            -- time value of previous wayPoint
	 * @param locs
	 *            -- hubs' or passengers' LocationDtos with location indexes
	 * @return lambda expression
	 */
	private IntConsumer getLambdaForTimeUpdate(AtomicLong start, List<? extends LocationDto> locs) {
		return (int i) -> locs.get(i).setTime(new Date( //
				start.addAndGet(1000 * globalMatrix[locs.get(i - 1).getIndex()][locs.get(i).getIndex()])));
	}

	/**
	 * Unlinks needless objects and calls Garbage Collector
	 */
	private void cleanUp() {
		globalMatrix = null;
		ids = null;
		tasks = null;
		cars = null;
		picks = null;
		drops = null;
		System.gc();
	}
}
