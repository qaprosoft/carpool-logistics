package com.asemenkov.carpool.logistics.utils;

/**
 * @author asemenkov
 * @since Feb 12, 2018
 */
public class Factories {

	@FunctionalInterface
	public interface MonoFactory<T1, R> {
		public R get(T1 t1);
	}

	@FunctionalInterface
	public interface DuoFactory<T1, T2, R> {
		public R get(T1 t1, T2 t2);
	}

	@FunctionalInterface
	public interface TriFactory<T1, T2, T3, R> {
		public R get(T1 t1, T2 t2, T3 t3);
	}

	@FunctionalInterface
	public interface TetraFactory<T1, T2, T3, T4, R> {
		public R get(T1 t1, T2 t2, T3 t3, T4 t4);
	}

	@FunctionalInterface
	public interface PentaFactory<T1, T2, T3, T4, T5, R> {
		public R get(T1 t1, T2 t2, T3 t3, T4 t4, T5 t5);
	}

}
