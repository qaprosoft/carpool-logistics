package com.asemenkov.carpool.logistics.tests;

import static com.asemenkov.carpool.logistics.RealWorldData.REAL_DURATIONS;

import java.util.Arrays;
import java.util.Random;
import java.util.stream.IntStream;

import org.testng.Assert;
import org.testng.annotations.Test;

import com.asemenkov.carpool.logistics.services.algorithms.LittlesAlgorithm;
import com.asemenkov.carpool.logistics.utils.CustomLogger;

/**
 * @author asemenkov
 * @since Feb 1, 2018
 */
@Test
public class LittlesAlgorithmTest extends AbstractTest {

	private Random random = new Random();

	@Test(invocationTimeOut = 10000)
	public void testPredefinedGraph2x2() {
		int[][] graph = { //
				{ 0, 12 }, //
				{ 25, 0 } };

		CustomLogger.log(graph);
		long start = System.nanoTime();
		littlesAlgorithm.findHamiltonianCycle(graph);
		long duration = System.nanoTime() - start;
		CustomLogger.log("Time spent: " + duration + "ns");

		int[][] pathCycle = littlesAlgorithm.getPathCycle();
		int pathLength = littlesAlgorithm.getPathLength();
		printBottomLineAndCycle(pathCycle, pathLength);

		Assert.assertTrue(duration < 500000, "Time taken for graph 2x2 is too long: " + duration);
		Assert.assertEquals(pathLength, 37, "Incorrect length of Cycle for graph 2x2.");
		verifyCycle(graph, pathCycle, pathLength);
	}

	@Test(invocationTimeOut = 10000)
	public void testPredefinedGraph3x3() {
		int[][] graph = { //
				{ 0, 98, 172 }, //
				{ 250, 0, 116 }, //
				{ 234, 300, 0 } };

		CustomLogger.log(graph);
		long start = System.nanoTime();
		littlesAlgorithm.findHamiltonianCycle(graph);
		long duration = System.nanoTime() - start;
		CustomLogger.log("Time spent: " + duration + "ns");

		int[][] pathCycle = littlesAlgorithm.getPathCycle();
		int pathLength = littlesAlgorithm.getPathLength();
		printBottomLineAndCycle(pathCycle, pathLength);

		Assert.assertTrue(duration < 500000, "Time taken for graph 3x3 is too long: " + duration);
		Assert.assertEquals(pathLength, 448, "Incorrect length of Cycle for graph 3x3.");
		verifyCycle(graph, pathCycle, pathLength);
	}

	@Test(invocationTimeOut = 10000)
	public void testPredefinedGraph4x4() {
		int[][] graph = { //
				{ 0, 14, 7, 100 }, //
				{ 18, 0, 6, 11 }, //
				{ 17, 11, 0, 12 }, //
				{ 5, 5, 5, 0 } };

		CustomLogger.log(graph);
		long start = System.nanoTime();
		littlesAlgorithm.findHamiltonianCycle(graph);
		long duration = System.nanoTime() - start;
		CustomLogger.log("Time spent: " + duration + "ns");

		int[][] pathCycle = littlesAlgorithm.getPathCycle();
		int pathLength = littlesAlgorithm.getPathLength();
		printBottomLineAndCycle(pathCycle, pathLength);

		Assert.assertTrue(duration < 3000000, "Time taken for graph 4x4 is too long: " + duration);
		Assert.assertEquals(pathLength, 34, "Incorrect length of Cycle for graph 4x4.");
		verifyCycle(graph, pathCycle, pathLength);
	}

	@Test(invocationTimeOut = 10000)
	public void testPredefinedGraph5x5() {
		int[][] graph = { //
				{ 0, 35, 45, 20, 11 }, //
				{ 9, 0, 17, 6, 8 }, //
				{ 21, 31, 0, 2, 11 }, //
				{ 30, 15, 40, 0, 10 }, //
				{ 10, 9, 8, 7, 0 } };

		CustomLogger.log(graph);
		long start = System.nanoTime();
		littlesAlgorithm.findHamiltonianCycle(graph);
		long duration = System.nanoTime() - start;
		CustomLogger.log("Time spent: " + duration + "ns");

		int[][] pathCycle = littlesAlgorithm.getPathCycle();
		int pathLength = littlesAlgorithm.getPathLength();
		printBottomLineAndCycle(pathCycle, pathLength);

		Assert.assertTrue(duration < 1000000, "Time taken for graph 5x5 is too long: " + duration);
		Assert.assertEquals(pathLength, 45, "Incorrect length of Cycle for graph 5x5.");
		verifyCycle(graph, pathCycle, pathLength);
	}

	@Test(invocationTimeOut = 10000)
	public void testPredefinedGraph6x6() {
		int[][] graph = { //
				{ 0, 31, 15, 19, 8, 55 }, //
				{ 19, 0, 22, 31, 7, 35 }, //
				{ 25, 43, 0, 53, 57, 16 }, //
				{ 5, 50, 49, 0, 39, 9 }, //
				{ 24, 24, 33, 5, 0, 14 }, //
				{ 34, 26, 6, 3, 36, 0 } };

		CustomLogger.log(graph);
		long start = System.nanoTime();
		littlesAlgorithm.findHamiltonianCycle(graph);
		long duration = System.nanoTime() - start;
		CustomLogger.log("Time spent: " + duration + "ns");

		int[][] pathCycle = littlesAlgorithm.getPathCycle();
		int pathLength = littlesAlgorithm.getPathLength();
		printBottomLineAndCycle(pathCycle, pathLength);

		Assert.assertTrue(duration < 10000000, "Time taken for graph 6x6 is too long: " + duration);
		Assert.assertEquals(pathLength, 74, "Incorrect length of Cycle for graph 6x6.");
		verifyCycle(graph, pathCycle, pathLength);
	}

	@Test(invocationTimeOut = 10000)
	public void testPredefinedGraph7x7() {
		int[][] graph = { //
				{ 0, 12, 22, 28, 32, 40, 46 }, //
				{ 12, 0, 10, 40, 20, 28, 34 }, //
				{ 22, 10, 0, 50, 10, 18, 24 }, //
				{ 28, 27, 17, 0, 27, 35, 41 }, //
				{ 32, 20, 10, 60, 0, 8, 14 }, //
				{ 46, 34, 24, 74, 14, 0, 6 }, //
				{ 52, 40, 30, 80, 20, 6, 0 } };

		CustomLogger.log(graph);
		long start = System.nanoTime();
		littlesAlgorithm.findHamiltonianCycle(graph);
		long duration = System.nanoTime() - start;
		CustomLogger.log("Time spent: " + duration + "ns");

		int[][] pathCycle = littlesAlgorithm.getPathCycle();
		int pathLength = littlesAlgorithm.getPathLength();
		printBottomLineAndCycle(pathCycle, pathLength);

		Assert.assertTrue(duration < 300000000, "Time taken for graph 7x7 is too long: " + duration);
		Assert.assertEquals(pathLength, 121, "Incorrect length of Cycle for graph 7x7.");
		verifyCycle(graph, pathCycle, pathLength);
	}

	@Test(invocationTimeOut = 10000)
	public void testPredefinedGraph9x9() {
		int[][] graph = { //
				{ 0, 18, 40, 27, 15, 4, 13, 38, 15 }, //
				{ 18, 0, 33, 9, 19, 26, 18, 8, 35 }, //
				{ 38, 33, 0, 17, 22, 14, 26, 22, 11 }, //
				{ 25, 10, 15, 0, 33, 22, 6, 20, 5 }, //
				{ 15, 21, 21, 31, 0, 10, 26, 33, 27 }, //
				{ 6, 27, 16, 24, 10, 0, 22, 25, 32 }, //
				{ 12, 19, 26, 5, 25, 21, 0, 28, 20 }, //
				{ 36, 7, 24, 21, 31, 27, 26, 0, 13 }, //
				{ 15, 33, 10, 5, 27, 32, 19, 12, 0 } };

		CustomLogger.log(graph);
		long start = System.nanoTime();
		littlesAlgorithm.findHamiltonianCycle(graph);
		long duration = System.nanoTime() - start;
		CustomLogger.log("Time spent: " + duration + "ns");

		int[][] pathCycle = littlesAlgorithm.getPathCycle();
		int pathLength = littlesAlgorithm.getPathLength();
		printBottomLineAndCycle(pathCycle, pathLength);

		Assert.assertTrue(duration < 50000000, "Time taken for graph 9x9 is too long: " + duration);
		Assert.assertEquals(pathLength, 92, "Incorrect length of Cycle for graph 9x9.");
		verifyCycle(graph, pathCycle, pathLength);
	}

	@Test(invocationTimeOut = 10000)
	public void testRandomGraph10x10() {
		int[][] graph = new int[10][10];
		for (int j = 0; j < 10; j++)
			for (int k = 0; k < 10; k++)
				graph[j][k] = random.nextInt(999) + 1;

		CustomLogger.log(graph);
		long start = System.nanoTime();
		littlesAlgorithm.findHamiltonianCycle(graph);
		long duration = System.nanoTime() - start;
		CustomLogger.log("Time spent: " + duration + "ns");

		int[][] pathCycle = littlesAlgorithm.getPathCycle();
		int pathLength = littlesAlgorithm.getPathLength();
		printBottomLineAndCycle(pathCycle, pathLength);

		Assert.assertTrue(duration < 15000000, "Time taken for graph 10x10 is too long: " + duration);
		verifyCycle(graph, pathCycle, pathLength);
	}

	@Test(invocationTimeOut = 10000)
	public void testRealWorldScenario1() {
		int[] picks = { 11, 12, 13, 14 };
		int[] drops = { 0, 5, 6, 9 };
		int[][] graph = new int[8][8];

		for (int i = 0; i < 4; i++)
			for (int j = 0; j < 4; j++)
				graph[i][j] = REAL_DURATIONS[picks[i]][picks[j]];

		for (int i = 4; i < 8; i++)
			for (int j = 4; j < 8; j++)
				graph[i][j] = REAL_DURATIONS[drops[i - 4]][drops[j - 4]];

		for (int i = 0; i < 4; i++)
			for (int j = 4; j < 8; j++)
				graph[i][j] = REAL_DURATIONS[picks[i]][drops[j - 4]];

		for (int i = 4; i < 8; i++)
			for (int j = 0; j < 4; j++)
				graph[i][j] = LittlesAlgorithm.INF3;

		CustomLogger.log(graph);
		long start = System.nanoTime();
		littlesAlgorithm.findHamiltonianCycle(graph);
		long duration = System.nanoTime() - start;
		CustomLogger.log("Time spent: " + duration + "ns");

		int[][] pathCycle = littlesAlgorithm.getPathCycle();
		int pathLength = littlesAlgorithm.getPathLength();
		printBottomLineAndCycle(pathCycle, pathLength);
		verifyCycle(graph, pathCycle, pathLength);
	}

	@Test(invocationTimeOut = 10000)
	public void testRealWorldScenario2() {
		int BIG = LittlesAlgorithm.INF3;
		int[][] graph = { //
				{ 0, 0, 747, 834, 392, 667 }, //
				{ 0, 0, 747, 834, 392, 667 }, //
				{ 872, 872, 0, 674, 1026, 720 }, //
				{ BIG, BIG, BIG, 0, 867, 996 }, //
				{ BIG, BIG, BIG, 822, 0, 666 }, //
				{ BIG, BIG, BIG, 902, 753, 0 } };

		CustomLogger.log(graph);
		long start = System.nanoTime();
		littlesAlgorithm.findHamiltonianCycle(graph);
		long duration = System.nanoTime() - start;
		CustomLogger.log("Time spent: " + duration + "ns");

		int[][] pathCycle = littlesAlgorithm.getPathCycle();
		int pathLength = littlesAlgorithm.getPathLength();
		printBottomLineAndCycle(pathCycle, pathLength);

		Assert.assertEquals(pathLength, 1113943, "Incorrect length of Cycle for graph.");
		verifyCycle(graph, pathCycle, pathLength);
	}

	@Test(invocationTimeOut = 10000)
	public void testRealWorldScenario3() {
		int BIG = LittlesAlgorithm.INF3;
		int[][] graph = { //
				{ 0, 872, 0, 515, 1026, 782 }, //
				{ 747, 0, 747, 601, 392, 887 }, //
				{ 0, 872, 0, 515, 1026, 782 }, //
				{ BIG, BIG, BIG, 0, 928, 497 }, //
				{ BIG, BIG, BIG, 844, 0, 866 }, //
				{ BIG, BIG, BIG, 490, 947, 0 } };

		CustomLogger.log(graph);
		long start = System.nanoTime();
		littlesAlgorithm.findHamiltonianCycle(graph);
		long duration = System.nanoTime() - start;
		CustomLogger.log("Time spent: " + duration + "ns");

		int[][] pathCycle = littlesAlgorithm.getPathCycle();
		int pathLength = littlesAlgorithm.getPathLength();
		printBottomLineAndCycle(pathCycle, pathLength);

		Assert.assertEquals(pathLength, 1113716, "Incorrect length of Cycle for graph.");
		verifyCycle(graph, pathCycle, pathLength);
	}

	@Test(invocationTimeOut = 10000)
	public void testPredefinedTrickyGraph() {
		int[][] graph = { //
				{ 0, 20, 18, 12, 8 }, //
				{ 5, 0, 14, 7, 11 }, //
				{ 12, 18, 0, 6, 11 }, //
				{ 11, 17, 11, 0, 12 }, //
				{ 5, 5, 5, 5, 0 } };

		CustomLogger.log(graph);
		long start = System.nanoTime();
		littlesAlgorithm.findHamiltonianCycle(graph);
		long duration = System.nanoTime() - start;
		CustomLogger.log("Time spent: " + duration + "ns");

		int[][] pathCycle = littlesAlgorithm.getPathCycle();
		int pathLength = littlesAlgorithm.getPathLength();
		printBottomLineAndCycle(pathCycle, pathLength);

		Assert.assertTrue(duration < 1500000, "Time taken for tricky graph is too long: " + duration);
		Assert.assertEquals(pathLength, 41, "Incorrect length of Cycle for tricky graph.");
		verifyCycle(graph, pathCycle, pathLength);
	}

	private void verifyCycle(int[][] graph, int[][] pathCycle, int pathLength) {
		Assert.assertTrue(pathLength > 0, "Cycle length must be > 0.");
		Assert.assertTrue(pathLength < 10000000, "Cycle length must be < infinity.");
		Assert.assertEquals(pathCycle.length, graph.length, "Incorrect number of edges.");

		Arrays.stream(pathCycle).forEach(edge -> //
		Assert.assertNotEquals(edge[0], edge[1], "Edge cannot conect the same node with itself."));

		IntStream.range(0, graph.length).forEach(i -> Assert.assertEquals( //
				Arrays.stream(pathCycle).mapToInt(e -> e[0]).filter(j -> j == i).count(), 1,
				"This node either isn't visited or visited twice: " + i));

		IntStream.range(0, graph.length).forEach(i -> Assert.assertEquals( //
				Arrays.stream(pathCycle).mapToInt(e -> e[1]).filter(j -> j == i).count(), 1,
				"This node either isn't visited or visited twice: " + i));

		int start = pathCycle[0][0];
		int count = 0, length = 0;
		for (int finish = start; finish != start || count == 0; count++)
			for (int i = 0; i < pathCycle.length; i++)
				if (pathCycle[i][0] == finish) {
					finish = pathCycle[i][1];
					length += graph[pathCycle[i][0]][pathCycle[i][1]];
					break;
				}

		Assert.assertEquals(count, graph.length, "Early shortage of Cycle detected.");
		Assert.assertEquals(pathLength, length, "Cycle length is inconsistent.");
	}

	private void printBottomLineAndCycle(int[][] pathCycle, int pathLength) {
		CustomLogger.log("Cycle length: " + pathLength);
		StringBuilder sb = new StringBuilder("Cycle edges:\n");
		for (int j = 0; j < pathCycle.length; sb.append(++j % 5 == 0 ? "\n" : "\t\t"))
			sb.append("[").append(pathCycle[j][0]).append(" ").append(pathCycle[j][1]).append("]");
		CustomLogger.log(sb);
	}

}
