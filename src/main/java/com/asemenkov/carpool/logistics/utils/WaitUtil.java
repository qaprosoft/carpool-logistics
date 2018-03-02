package com.asemenkov.carpool.logistics.utils;

import java.util.concurrent.TimeUnit;
import java.util.function.Predicate;
import java.util.stream.Stream;

/**
 * Utility class to perform wait with condition
 *
 * @author asemenkov
 * @since Oct 27, 2017
 */
public class WaitUtil {

	/**
	 * @param miliseconds
	 *            -- time in milliseconds
	 */
	public static void pause(int milliseconds) {
		try {
			TimeUnit.MILLISECONDS.sleep(milliseconds);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Run a parallel thread and force the main thread to wait</br>
	 * The waiting stops </br>
	 * either when the condition is met for at least one of the arguments</br>
	 * or when the overall waiting time elapsed
	 * 
	 * @param predicate
	 *            -- condition which should be met to stop waiting
	 * @param overall
	 *            -- overall waiting time in milliseconds
	 * @param interval
	 *            -- time in milliseconds between condition checks
	 * @param args
	 *            -- arguments the condition is applied to
	 */
	@SafeVarargs
	public static <T> void waitUntilAll(Predicate<T> predicate, int overall, int interval, T... args) {
		final Lock lock = new Lock();

		new Thread(() -> {
			while (lock.getBool())
				if (Stream.of(args).allMatch(predicate))
					synchronized (lock) {
						lock.notify();
						break;
					}
				else
					pause(interval);
		}).start();

		synchronized (lock) {
			try {
				lock.wait(overall);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}

		lock.setBool(false);
	}

	/**
	 * Run a parallel thread and force the main thread to wait</br>
	 * The waiting stops </br>
	 * either when the condition is met for each argument</br>
	 * or when the overall waiting time elapsed
	 * 
	 * @param predicate
	 *            -- condition which should be met to stop waiting
	 * @param overall
	 *            -- overall waiting time in milliseconds
	 * @param interval
	 *            -- time in milliseconds between condition checks
	 * @param args
	 *            -- arguments the condition is applied to
	 */
	@SafeVarargs
	public static <T> void waitUntilAny(Predicate<T> predicate, int overall, int interval, T... args) {
		final Lock lock = new Lock();

		new Thread(() -> {
			while (lock.getBool())
				if (Stream.of(args).anyMatch(predicate))
					synchronized (lock) {
						lock.notify();
						break;
					}
				else
					pause(interval);
		}).start();

		synchronized (lock) {
			try {
				lock.wait(overall);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}

		lock.setBool(false);
	}

	public static class Lock {
		boolean bool = true;

		public boolean getBool() {
			return bool;
		}

		public void setBool(boolean bool) {
			this.bool = bool;
		}

	}

}
