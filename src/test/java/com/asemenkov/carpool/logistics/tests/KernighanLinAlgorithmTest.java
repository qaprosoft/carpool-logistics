package com.asemenkov.carpool.logistics.tests;

import static com.asemenkov.carpool.logistics.RealWorldData.REAL_DURATIONS;

import java.util.Arrays;

import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import com.asemenkov.carpool.logistics.services.algorithms.Car;
import com.asemenkov.carpool.logistics.services.algorithms.KernighanLinAlgorithm;
import com.asemenkov.carpool.logistics.utils.CustomLogger;

/**
 * @author asemenkov
 * @since Feb 12, 2018
 */
@Test
public class KernighanLinAlgorithmTest extends AbstractTest {

	private final static int EXPECTED_LENGTH = 3480;
	private final static int EXPECTED_CARS = 4;

	private final static int[][] POINTS = { { 6, 0 }, { 6, 1 }, { 5, 2 }, { 6, 3 }, { 5, 4 }, { 5, 7 } };
	private final static int[][] FAKE_MATRIX = new int[REAL_DURATIONS.length][REAL_DURATIONS.length];

	@BeforeClass
	public void divideDistancesByTwo() {
		for (int i = 0; i < FAKE_MATRIX.length; i++)
			for (int j = 0; j < FAKE_MATRIX.length; j++)
				FAKE_MATRIX[i][j] = REAL_DURATIONS[i][j] / 2;
	}

	@Test
	public void testManualMixing() {
		Car[] cars = Arrays.stream(POINTS) //
				.map(p -> carFactory.get(4, FAKE_MATRIX).putPassenger(p[0], p[1])) //
				.toArray(Car[]::new);

		long time1 = System.currentTimeMillis();
		for (int i = 0; i < 10; i++) {
			cars[0].mixTwoItems(cars[1]);

			cars[0].mixTwoItems(cars[2]);
			cars[1].mixTwoItems(cars[2]);

			cars[0].mixTwoItems(cars[3]);
			cars[1].mixTwoItems(cars[3]);
			cars[2].mixTwoItems(cars[3]);

			cars[0].mixTwoItems(cars[4]);
			cars[1].mixTwoItems(cars[4]);
			cars[2].mixTwoItems(cars[4]);
			cars[3].mixTwoItems(cars[4]);

			cars[0].mixTwoItems(cars[5]);
			cars[1].mixTwoItems(cars[5]);
			cars[2].mixTwoItems(cars[5]);
			cars[3].mixTwoItems(cars[5]);
			cars[4].mixTwoItems(cars[5]);
		}

		long time2 = System.currentTimeMillis();
		verifyCars(cars, time1, time2);
	}

	@Test
	public void testKernighanLinMixing() {
		Car[] cars = Arrays.stream(POINTS) //
				.map(p -> carFactory.get(4, FAKE_MATRIX).putPassenger(p[0], p[1])) //
				.toArray(Car[]::new);

		KernighanLinAlgorithm<Car> kernighanLinAlgorithm = kernighanLinForCarsFactory.get(cars);

		long time1 = System.currentTimeMillis();
		kernighanLinAlgorithm.mix();
		long time2 = System.currentTimeMillis();
		verifyCars(cars, time1, time2);
	}

	private void verifyCars(Car[] cars, long time1, long time2) {
		long totalLength = Arrays.stream(cars).mapToInt(Car::getMixResult).sum();
		long totalCars = Arrays.stream(cars).filter(car -> car.getSeatsOccupied() != 0).count();

		Arrays.stream(cars).forEach(CustomLogger::log);
		CustomLogger.log("Total length: " + totalLength + "s");
		CustomLogger.log("Average route length: " + totalLength / totalCars + "s");
		CustomLogger.log("Time taken: " + (time2 - time1) + "ms");

		Assert.assertTrue(time2 - time1 < 10000, "Manual mixing was lasting for too long.");
		Assert.assertEquals(totalLength, EXPECTED_LENGTH, "Wrong total length.");
		Assert.assertEquals(totalCars, EXPECTED_CARS, "Wrong total cars.");
	}

}
