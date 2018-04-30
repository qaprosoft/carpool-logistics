package com.asemenkov.carpool.logistics.tests;

import java.util.Arrays;

import org.testng.annotations.Test;
import org.testng.asserts.SoftAssert;

import com.asemenkov.carpool.logistics.RealWorldData;
import com.asemenkov.carpool.logistics.services.googlemaps.GoogleMapsPoint;
import com.asemenkov.carpool.logistics.services.googlemaps.PointsNeighbourship;
import com.asemenkov.carpool.logistics.utils.CustomLogger;

/**
 * @author asemenkov
 * @since Feb 7, 2018
 */
@Test
public class GoogleMapsTest extends AbstractTest {

	private static final boolean ENABLE_HUGE_GOOGLE_MAPS_TESTS = false;

	@Test
	public void testGoogleMapsMatrix3Passangers2Hubs() {
		GoogleMapsPoint[] drops = Arrays.copyOfRange(RealWorldData.REAL_POINTS, 0, 3);
		GoogleMapsPoint[] picks = Arrays.copyOfRange(RealWorldData.REAL_POINTS, 3, 5);

		PointsNeighbourship pointsNeighbourship = pointsNeighbourshipFactory.get(picks, drops);
		long time = System.currentTimeMillis();
		pointsNeighbourship.requestGoogleMapsForDistanceMatrices();
		long duration = System.currentTimeMillis() - time;
		CustomLogger.log("Time spent: " + duration + "ms");
		CustomLogger.log(pointsNeighbourship);

		int[][] distances = pointsNeighbourship.getDistanceMatrix();
		int[][] expectedDistances = new int[5][5];
		for (int i = 0; i < 5; i++)
			System.arraycopy(RealWorldData.REAL_DISTANCES[i], 0, expectedDistances[i], 0, 5);
		verifyDistanceMatrix(expectedDistances, distances);

		int[][] durations = pointsNeighbourship.getDurationMatrix();
		int[][] expectedDurations = new int[5][5];
		for (int i = 0; i < 5; i++)
			System.arraycopy(RealWorldData.REAL_DURATIONS[i], 0, expectedDurations[i], 0, 5);
		verifyDurationMatrix(expectedDurations, durations);
	}

	@Test(enabled = ENABLE_HUGE_GOOGLE_MAPS_TESTS, dependsOnMethods = "testGoogleMapsMatrix3Passangers2Hubs")
	public void testGoogleMapsMatrix7Passangers3Hubs() {
		GoogleMapsPoint[] drops = Arrays.copyOfRange(RealWorldData.REAL_POINTS, 0, 7);
		GoogleMapsPoint[] picks = Arrays.copyOfRange(RealWorldData.REAL_POINTS, 7, 10);

		PointsNeighbourship pointsNeighbourship = pointsNeighbourshipFactory.get(picks, drops);
		long time = System.currentTimeMillis();
		pointsNeighbourship.requestGoogleMapsForDistanceMatrices();
		long duration = System.currentTimeMillis() - time;
		CustomLogger.log("Time spent: " + duration + "ms");
		CustomLogger.log(pointsNeighbourship);

		int[][] distances = pointsNeighbourship.getDistanceMatrix();
		int[][] expectedDistances = new int[10][10];
		for (int i = 0; i < 10; i++)
			System.arraycopy(RealWorldData.REAL_DISTANCES[i], 0, expectedDistances[i], 0, 10);
		verifyDistanceMatrix(expectedDistances, distances);

		int[][] durations = pointsNeighbourship.getDurationMatrix();
		int[][] expectedDurations = new int[10][10];
		for (int i = 0; i < 10; i++)
			System.arraycopy(RealWorldData.REAL_DURATIONS[i], 0, expectedDurations[i], 0, 10);
		verifyDurationMatrix(expectedDurations, durations);
	}

	@Test(enabled = ENABLE_HUGE_GOOGLE_MAPS_TESTS, dependsOnMethods = "testGoogleMapsMatrix7Passangers3Hubs")
	public void testGoogleMapsMatrix12Passangers4Hubs() {
		GoogleMapsPoint[] drops = Arrays.copyOfRange(RealWorldData.REAL_POINTS, 0, 12);
		GoogleMapsPoint[] picks = Arrays.copyOfRange(RealWorldData.REAL_POINTS, 12, 16);

		PointsNeighbourship pointsNeighbourship = pointsNeighbourshipFactory.get(picks, drops);
		long time = System.currentTimeMillis();
		pointsNeighbourship.requestGoogleMapsForDistanceMatrices();
		long duration = System.currentTimeMillis() - time;
		CustomLogger.log("Time spent: " + duration + "ms");
		CustomLogger.log(pointsNeighbourship);

		int[][] distances = pointsNeighbourship.getDistanceMatrix();
		int[][] expectedDistances = new int[16][16];
		for (int i = 0; i < 16; i++)
			System.arraycopy(RealWorldData.REAL_DISTANCES[i], 0, expectedDistances[i], 0, 16);
		verifyDistanceMatrix(expectedDistances, distances);

		int[][] durations = pointsNeighbourship.getDurationMatrix();
		int[][] expectedDurations = new int[16][16];
		for (int i = 0; i < 16; i++)
			System.arraycopy(RealWorldData.REAL_DURATIONS[i], 0, expectedDurations[i], 0, 16);
		verifyDurationMatrix(expectedDurations, durations);
	}

	private void verifyDistanceMatrix(int[][] expectedMatrix, int[][] actualMatrix) {
		SoftAssert softAssert = new SoftAssert();
		softAssert.assertEquals(actualMatrix.length, actualMatrix[0].length, "Matrix is not square.");
		softAssert.assertEquals(actualMatrix.length, expectedMatrix.length, "Wrong number of Rows.");
		softAssert.assertEquals(actualMatrix[0].length, expectedMatrix[0].length, "Wrong number of Cols.");

		for (int i = 0; i < expectedMatrix.length; i++)
			for (int j = 0; j < actualMatrix.length; j++)
				softAssert.assertTrue(Math.abs(actualMatrix[i][j] - expectedMatrix[i][j] - 1) < 1000,
						"Distance at [" + i + " " + j + "] is wrong, should be ~ " + expectedMatrix[i][j]);

		softAssert.assertAll();
	}

	private void verifyDurationMatrix(int[][] expectedMatrix, int[][] actualMatrix) {
		SoftAssert softAssert = new SoftAssert();
		softAssert.assertEquals(actualMatrix.length, actualMatrix[0].length, "Matrix is not square.");
		softAssert.assertEquals(actualMatrix.length, expectedMatrix.length, "Wrong number of Rows.");
		softAssert.assertEquals(actualMatrix[0].length, expectedMatrix[0].length, "Wrong number of Cols.");

		for (int i = 0; i < expectedMatrix.length; i++)
			for (int j = 0; j < actualMatrix.length; j++)
				softAssert.assertTrue(Math.abs(actualMatrix[i][j] - expectedMatrix[i][j] - 1) < 120,
						"Duration at [" + i + " " + j + "] is wrong, should be ~ " + expectedMatrix[i][j]);

		softAssert.assertAll();
	}
}
