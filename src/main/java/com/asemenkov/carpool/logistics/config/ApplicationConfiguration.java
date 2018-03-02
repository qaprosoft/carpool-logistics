package com.asemenkov.carpool.logistics.config;

import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.context.annotation.Scope;

import com.asemenkov.carpool.logistics.models.db.Task;
import com.asemenkov.carpool.logistics.models.dto.HubLocationDto;
import com.asemenkov.carpool.logistics.models.dto.LogisticsDto;
import com.asemenkov.carpool.logistics.models.dto.RouteDto;
import com.asemenkov.carpool.logistics.models.dto.UserLocationDto;
import com.asemenkov.carpool.logistics.services.algorithms.Car;
import com.asemenkov.carpool.logistics.services.algorithms.KernighanLinAlgorithm;
import com.asemenkov.carpool.logistics.services.algorithms.LittlesAlgorithm;
import com.asemenkov.carpool.logistics.services.algorithms.Mixable;
import com.asemenkov.carpool.logistics.services.enums.State;
import com.asemenkov.carpool.logistics.services.enums.Status;
import com.asemenkov.carpool.logistics.services.googlemaps.GoogleMapsPoint;
import com.asemenkov.carpool.logistics.services.logistics.LogisticsProcess;
import com.asemenkov.carpool.logistics.services.logistics.LogisticsService;
import com.asemenkov.carpool.logistics.utils.Factories.DuoFactory;
import com.asemenkov.carpool.logistics.utils.Factories.MonoFactory;
import com.asemenkov.carpool.logistics.utils.Factories.TriFactory;

/**
 * @author asemenkov
 * @since Feb 5, 2018
 */
@Configuration
@ComponentScan("com.asemenkov.carpool.logistics.services.enums")
@PropertySource("classpath:aplication.properties")
public class ApplicationConfiguration {

	@Value("${max.route_duration}")
	private int maxRouteDuration;

	@Bean
	public LogisticsService carPoolService() {
		return new LogisticsService();
	}

	@Bean
	public DuoFactory<Integer, int[][], Car> carFactory() {
		return this::getCar;
	}

	@Bean
	public DuoFactory<String, long[], LogisticsProcess> logisticsProcessFactory() {
		return this::getLogisticsProcess;
	}

	@Bean
	public MonoFactory<Car[], KernighanLinAlgorithm<Car>> kernighanLinForCarsFactory() {
		return this::getKernighanLinAlgorithm;
	}

	@Bean
	public DuoFactory<Task, GoogleMapsPoint, HubLocationDto> hubLocationDtoFactory() {
		return this::getHubLocationDto;
	}

	@Bean
	public DuoFactory<Task, GoogleMapsPoint, UserLocationDto> userLocationDtoFactory() {
		return this::getUserLocationDto;
	}

	@Bean
	public TriFactory<Car, List<HubLocationDto>, List<UserLocationDto>, RouteDto> routeDtoFactory() {
		return this::getRouteDto;
	}

	@Bean
	@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
	public LittlesAlgorithm littlesAlgorithm() {
		return new LittlesAlgorithm();
	}

	@Bean
	@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
	public LogisticsDto processDto() {
		LogisticsDto processDto = new LogisticsDto();
		processDto.setStartTime(new Date());
		processDto.setState(State.RUNNING);
		processDto.setStatus(Status.RUNNING_INITIALIZATION);
		return processDto;
	}

	@Bean
	@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
	protected Car getCar(int capacity, int[][] globalMatrix) {
		Car car = new Car(capacity, globalMatrix);
		car.setMaxLength(maxRouteDuration);
		return car;
	}

	@Bean
	@SuppressWarnings("rawtypes")
	@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
	protected <T> KernighanLinAlgorithm<T> getKernighanLinAlgorithm(Mixable[] mixables) {
		return new KernighanLinAlgorithm<T>(mixables);
	}

	@Bean
	@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
	protected LogisticsProcess getLogisticsProcess(String id, long[] ids) {
		return new LogisticsProcess(id, maxRouteDuration, ids);
	}

	@Bean
	@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
	protected HubLocationDto getHubLocationDto(Task task, GoogleMapsPoint googleMapsPoint) {
		HubLocationDto hubLocationDto = new HubLocationDto();
		hubLocationDto.setAddress(task.getPickupAddress());
		hubLocationDto.setIndex(googleMapsPoint.getIndex());
		hubLocationDto.setLat(googleMapsPoint.getLatitude());
		hubLocationDto.setLon(googleMapsPoint.getLongtitude());
		hubLocationDto.setName(task.getHub().getName());
		hubLocationDto.setColor(task.getHub().getColor());
		hubLocationDto.setId(task.getHub().getId());
		hubLocationDto.setTime(task.getPickupTime());
		return hubLocationDto;
	}

	@Bean
	@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
	protected UserLocationDto getUserLocationDto(Task task, GoogleMapsPoint googleMapsPoint) {
		UserLocationDto userLocationDto = new UserLocationDto();
		userLocationDto.setTask(task.getId());
		userLocationDto.setAddress(task.getDropoffAddress());
		userLocationDto.setLat(googleMapsPoint.getLatitude());
		userLocationDto.setLon(googleMapsPoint.getLongtitude());
		userLocationDto.setIndex(googleMapsPoint.getIndex());
		userLocationDto.setName(task.getUser().getFirstName() + " " + task.getUser().getLastName());
		userLocationDto.setPhone(task.getUser().getPhone());
		userLocationDto.setId(task.getUser().getId());
		return userLocationDto;
	}

	@Bean
	@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
	protected RouteDto getRouteDto(Car car, List<HubLocationDto> hubs, List<UserLocationDto> users) {
		RouteDto routeDto = new RouteDto();
		routeDto.setSeats(car.getSeatsOccupied());
		routeDto.setLength(car.getMixResult());
		routeDto.setHubs(hubs);
		routeDto.setPassengers(users);
		return routeDto;
	}

}
