package com.asemenkov.carpool.logistics.tests;

import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Predicate;
import java.util.stream.Stream;

import org.testng.Assert;
import org.testng.annotations.Test;

import com.asemenkov.carpool.logistics.utils.CustomLogger;
import com.asemenkov.carpool.logistics.utils.WaitUtil;

/**
 * @author asemenkov
 * @since Feb 11, 2018
 */
@Test
public class WaitUtilTest extends AbstractTest {

	private AtomicInteger passedTests = new AtomicInteger();

	@Test(invocationTimeOut = 3000)
	public void testPause() {
		long start = System.currentTimeMillis();
		WaitUtil.pause(500);
		long finish = System.currentTimeMillis();

		CustomLogger.log("Waiting time: expected = 500ms, actual = " + (finish - start) + "ms");
		Assert.assertTrue(finish - start >= 500, "Waiting had stopped before the first event happened.");
		Assert.assertTrue(finish - start < 520, "Waiting was lasting for too long.");
		passedTests.getAndIncrement();
	}

	@Test(invocationTimeOut = 3000)
	public void testWaitUntilAnyEventHappens() {
		long start = System.currentTimeMillis();
		Predicate<Long> predicate = getWaitingPredicate(start);
		WaitUtil.waitUntilAny(predicate, 800, 10, 200L, 300L, 400L);
		long finish = System.currentTimeMillis();

		CustomLogger.log("Waiting time: expected = 200ms, actual = " + (finish - start) + "ms");
		Assert.assertTrue(finish - start >= 200, "Waiting had stopped before the first event happened.");
		Assert.assertTrue(finish - start < 225, "Waiting was lasting for too long.");
		passedTests.getAndIncrement();
	}

	@Test(invocationTimeOut = 3000)
	public void testWaitUntilAllEventsHappen() {
		long start = System.currentTimeMillis();
		Predicate<Long> predicate = getWaitingPredicate(start);
		WaitUtil.waitUntilAll(predicate, 800, 10, 200L, 300L, 400L);
		long finish = System.currentTimeMillis();

		CustomLogger.log("Waiting time: expected = 400ms, actual = " + (finish - start) + "ms");
		Assert.assertTrue(finish - start >= 400, "Waiting had stopped before the last event happened.");
		Assert.assertTrue(finish - start < 425, "Waiting was lasting for too long.");
		passedTests.getAndIncrement();
	}

	@Test(invocationTimeOut = 3000)
	public void testWaitUntilAnyEventHappensButOverallWaitingTimeElapsed() {
		long start = System.currentTimeMillis();
		Predicate<Long> predicate = getWaitingPredicate(start);
		WaitUtil.waitUntilAny(predicate, 500, 10, 2000L, 3000L, 4000L);
		long finish = System.currentTimeMillis();

		CustomLogger.log("Waiting time: expected = 500ms, actual = " + (finish - start) + "ms");
		Assert.assertTrue(finish - start >= 500, "Waiting had stopped before the last event happened.");
		Assert.assertTrue(finish - start < 800, "Waiting was lasting for too long.");
		passedTests.getAndIncrement();
	}

	@Test(invocationTimeOut = 3000)
	public void testWaitUntilAllEventsHappenButOverallWaitingTimeElapsed() {
		long start = System.currentTimeMillis();
		Predicate<Long> predicate = getWaitingPredicate(start);
		WaitUtil.waitUntilAll(predicate, 500, 10, 2000L, 3000L, 4000L);
		long finish = System.currentTimeMillis();

		CustomLogger.log("Waiting time: expected = 500ms, actual = " + (finish - start) + "ms");
		Assert.assertTrue(finish - start >= 500, "Waiting had stopped before the last event happened.");
		Assert.assertTrue(finish - start < 800, "Waiting was lasting for too long.");
		passedTests.getAndIncrement();
	}

	@Test(invocationTimeOut = 3000)
	public void testSeveralWaitingsSimultaneously() {
		passedTests.set(0);
		Thread t1 = new Thread(this::testPause);
		Thread t2 = new Thread(this::testWaitUntilAnyEventHappens);
		Thread t3 = new Thread(this::testWaitUntilAllEventsHappen);
		Thread t4 = new Thread(this::testWaitUntilAnyEventHappensButOverallWaitingTimeElapsed);
		Thread t5 = new Thread(this::testWaitUntilAllEventsHappenButOverallWaitingTimeElapsed);
		Thread[] threads = Stream.of(t1, t2, t3, t4, t5).peek(Thread::start).toArray(Thread[]::new);
		WaitUtil.waitUntilAll((thread) -> !thread.isAlive(), 2500, 250, threads);
		Assert.assertEquals(passedTests.get(), threads.length, "Some tests failid in parallel run.");
	}

	private Predicate<Long> getWaitingPredicate(long value) {
		return (interval) -> System.currentTimeMillis() - value > interval;
	}
}
