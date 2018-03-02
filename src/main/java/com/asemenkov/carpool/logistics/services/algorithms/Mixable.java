package com.asemenkov.carpool.logistics.services.algorithms;

/**
 * Generic argument for Kernighan-Lin algorithm
 * 
 * @author asemenkov
 * @since Feb 14, 2018
 */
public interface Mixable<T extends Mixable<T>> {

	/**
	 * Execute in case of only 1 Mixable passed to Kernighan-Lin algorithm
	 */
	public void mixOneItem();

	/**
	 * Swaps items between this Mixable and Mixable arg, looking for a swap
	 * which leads to better result
	 */
	public void mixTwoItems(T mixable);

	/**
	 * @return better result which is achieved during mix() operation
	 */
	public int getMixResult();

}
