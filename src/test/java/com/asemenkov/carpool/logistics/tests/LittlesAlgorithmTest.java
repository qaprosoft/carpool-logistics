package com.asemenkov.carpool.logistics.tests;

import static com.asemenkov.carpool.logistics.RealWorldData.REAL_DURATIONS;

import java.util.Arrays;
import java.util.Random;
import java.util.stream.IntStream;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.testng.AbstractTestNGSpringContextTests;
import org.testng.Assert;
import org.testng.annotations.Test;

import com.asemenkov.carpool.logistics.config.ApplicationConfiguration;
import com.asemenkov.carpool.logistics.services.algorithms.LittlesAlgorithm;
import com.asemenkov.carpool.logistics.utils.io.CustomLogger;

/**
 * @author asemenkov
 * @since Feb 1, 2018
 */

@Test
@ContextConfiguration(classes = ApplicationConfiguration.class)
public class LittlesAlgorithmTest extends AbstractTestNGSpringContextTests {

	@Autowired
	private LittlesAlgorithm littlesAlgorithm;
	private Random random = new Random();

	@Test(invocationTimeOut = 10000)
	public void testPredefinedGraph2x2() {
		int[][] graph = { //
				{ 0, 12 }, //
				{ 25, 0 } };

		CustomLogger.log(littlesAlgorithm.withGraph(graph));

		long start = System.nanoTime();
		int[][] result = littlesAlgorithm.findHamiltonianCycle();
		long duration = System.nanoTime() - start;

		CustomLogger.log("Time spent: " + duration + "ns");
		printBottomLineAndCycle(result);

		Assert.assertTrue(duration < 400000, "Time taken for graph 2x2 is too long: " + duration);
		Assert.assertEquals(result[0][0], 37, "Incorrect length of Cycle for graph 2x2.");
		verifyCycle(graph, result);
	}

	@Test(invocationTimeOut = 10000)
	public void testPredefinedGraph3x3() {
		int[][] graph = { //
				{ 0, 98, 172 }, //
				{ 250, 0, 116 }, //
				{ 234, 300, 0 } };

		CustomLogger.log(littlesAlgorithm.withGraph(graph));

		long start = System.nanoTime();
		int[][] result = littlesAlgorithm.findHamiltonianCycle();
		long duration = System.nanoTime() - start;

		CustomLogger.log("Time spent: " + duration + "ns");
		printBottomLineAndCycle(result);

		Assert.assertTrue(duration < 500000, "Time taken for graph 3x3 is too long: " + duration);
		Assert.assertEquals(result[0][0], 448, "Incorrect length of Cycle for graph 3x3.");
		verifyCycle(graph, result);
	}

	@Test(invocationTimeOut = 10000)
	public void testPredefinedGraph4x4() {
		int[][] graph = { //
				{ 0, 14, 7, 100 }, //
				{ 18, 0, 6, 11 }, //
				{ 17, 11, 0, 12 }, //
				{ 5, 5, 5, 0 } };

		CustomLogger.log(littlesAlgorithm.withGraph(graph));

		long start = System.nanoTime();
		int[][] result = littlesAlgorithm.findHamiltonianCycle();
		long duration = System.nanoTime() - start;

		CustomLogger.log("Time spent: " + duration + "ns");
		printBottomLineAndCycle(result);

		Assert.assertTrue(duration < 600000, "Time taken for graph 4x4 is too long: " + duration);
		Assert.assertEquals(result[0][0], 34, "Incorrect length of Cycle for graph 4x4.");
		verifyCycle(graph, result);
	}

	@Test(invocationTimeOut = 10000)
	public void testPredefinedGraph5x5() {
		int[][] graph = { //
				{ 0, 35, 45, 20, 11 }, //
				{ 9, 0, 17, 6, 8 }, //
				{ 21, 31, 0, 2, 11 }, //
				{ 30, 15, 40, 0, 10 }, //
				{ 10, 9, 8, 7, 0 } };

		CustomLogger.log(littlesAlgorithm.withGraph(graph));

		long start = System.nanoTime();
		int[][] result = littlesAlgorithm.findHamiltonianCycle();
		long duration = System.nanoTime() - start;

		CustomLogger.log("Time spent: " + duration + "ns");
		printBottomLineAndCycle(result);

		Assert.assertTrue(duration < 1000000, "Time taken for graph 5x5 is too long: " + duration);
		Assert.assertEquals(result[0][0], 45, "Incorrect length of Cycle for graph 5x5.");
		verifyCycle(graph, result);
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

		CustomLogger.log(littlesAlgorithm.withGraph(graph));

		long start = System.nanoTime();
		int[][] result = littlesAlgorithm.findHamiltonianCycle();
		long duration = System.nanoTime() - start;

		CustomLogger.log("Time spent: " + duration + "ns");
		printBottomLineAndCycle(result);

		Assert.assertTrue(duration < 10000000, "Time taken for graph 6x6 is too long: " + duration);
		Assert.assertEquals(result[0][0], 74, "Incorrect length of Cycle for graph 6x6.");
		verifyCycle(graph, result);
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

		CustomLogger.log(littlesAlgorithm.withGraph(graph));

		long start = System.nanoTime();
		int[][] result = littlesAlgorithm.findHamiltonianCycle();
		long duration = System.nanoTime() - start;

		CustomLogger.log("Time spent: " + duration + "ns");
		printBottomLineAndCycle(result);

		Assert.assertTrue(duration < 10000000, "Time taken for graph 7x7 is too long: " + duration);
		Assert.assertEquals(result[0][0], 121, "Incorrect length of Cycle for graph 7x7.");
		verifyCycle(graph, result);
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

		CustomLogger.log(littlesAlgorithm.withGraph(graph));

		long start = System.nanoTime();
		int[][] result = littlesAlgorithm.findHamiltonianCycle();
		long duration = System.nanoTime() - start;

		CustomLogger.log("Time spent: " + duration + "ns");
		printBottomLineAndCycle(result);

		Assert.assertTrue(duration < 50000000, "Time taken for graph 9x9 is too long: " + duration);
		Assert.assertEquals(result[0][0], 97, "Incorrect length of Cycle for graph 9x9.");
		verifyCycle(graph, result);
	}

	@Test(invocationTimeOut = 10000)
	public void testRandomGraph10x10() {
		int[][] graph = new int[10][10];
		for (int j = 0; j < 10; j++)
			for (int k = 0; k < 10; k++)
				graph[j][k] = random.nextInt(999) + 1;

		CustomLogger.log(littlesAlgorithm.withGraph(graph));

		long start = System.nanoTime();
		int[][] result = littlesAlgorithm.findHamiltonianCycle();
		long duration = System.nanoTime() - start;

		CustomLogger.log("Time spent: " + duration + "ns");
		printBottomLineAndCycle(result);

		Assert.assertTrue(duration < 15000000, "Time taken for graph 10x10 is too long: " + duration);
		verifyCycle(graph, result);
	}

	@Test(invocationTimeOut = 10000)
	public void testRealWorldScenario() {
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
				graph[i][j] = LittlesAlgorithm.BIG;

		CustomLogger.log(littlesAlgorithm.withGraph(graph));

		long start = System.nanoTime();
		int[][] result = littlesAlgorithm.findHamiltonianCycle();
		long duration = System.nanoTime() - start;

		CustomLogger.log("Time spent: " + duration + "ns");
		printBottomLineAndCycle(result);
		verifyCycle(graph, result);
	}

	private void verifyCycle(int[][] graph, int[][] bottomLineAndCycle) {
		Assert.assertTrue(bottomLineAndCycle[0][0] > 0, "Cycle length must be > 0.");
		Assert.assertTrue(bottomLineAndCycle[0][0] < 10000000, "Cycle length must be < infinity.");
		Assert.assertEquals(bottomLineAndCycle.length - 1, graph.length, "Incorrect number of edges.");

		Arrays.stream(bottomLineAndCycle).forEach(edge -> //
		Assert.assertNotEquals(edge[0], edge[1], "Edge cannot conect the same		 node to itself."));

		IntStream.range(0, graph.length)
				.forEach(i -> Assert.assertEquals( //
						Arrays.stream(bottomLineAndCycle).skip(1) //
								.mapToInt(array -> array[0]) //
								.filter(j -> j == i) //
								.count(),
						1, "This node either isn't visited or visited twice: " + i));

		IntStream.range(0, graph.length)
				.forEach(i -> Assert.assertEquals( //
						Arrays.stream(bottomLineAndCycle).skip(1) //
								.mapToInt(array -> array[1]) //
								.filter(j -> j == i) //
								.count(),
						1, "This node either isn't visited or visited twice: " + i));

		int start = bottomLineAndCycle[1][0];
		int count = 0, length = 0;
		for (int finish = start; finish != start || count == 0; count++)
			for (int i = 1; i < bottomLineAndCycle.length; i++)
				if (bottomLineAndCycle[i][0] == finish) {
					finish = bottomLineAndCycle[i][1];
					length += graph[bottomLineAndCycle[i][0]][bottomLineAndCycle[i][1]];
					break;
				}

		Assert.assertEquals(count, graph.length, "Early shortage of Cycle detected.");
		Assert.assertEquals(bottomLineAndCycle[0][0], length, "Cycle length is inconsistent.");
	}

	private void printBottomLineAndCycle(int[][] bottomLineAndCycle) {
		CustomLogger.log("Cycle length: " + bottomLineAndCycle[0][0]);
		StringBuilder sb = new StringBuilder("Cycle edges:\n");
		for (int j = 1; j < bottomLineAndCycle.length; sb.append(j++ % 5 == 0 ? "\n" : "\t\t"))
			sb.append("[").append(bottomLineAndCycle[j][0]).append(" ") //
					.append(bottomLineAndCycle[j][1]).append("]");
		CustomLogger.log(sb);
	}

}
