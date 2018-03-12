package com.asemenkov.carpool.logistics.services.algorithms;

import java.util.Arrays;

/**
 * Algorithm for partition of graph
 * 
 * @author asemenkov
 * @since Feb 14, 2018
 */
@SuppressWarnings({ "rawtypes", "unchecked" })
public class KernighanLinAlgorithm<T> {

	private static final int MAX_ITERATION = 100;

	private final Mixable[] mixables;

	private int latestResult;
	private int standstillResult;
	private int biggerResult;

	public KernighanLinAlgorithm(Mixable[] mixables) {
		this.mixables = mixables;
		this.latestResult = Integer.MAX_VALUE;
	}

	/**
	 * If only 1 Mixable provided, finds its Hamiltonian path and returns</br>
	 * 
	 * If 2+ Mixables provided:</br>
	 * 1. Splits them into all possible pairs not counting rearrangements</br>
	 * 2. For each pair, swaps items between these 2 Mixables</br>
	 * 3. While total result after swaps is better, continues iterations</br>
	 * 4. If total result isn't being changed for 2+ iterations, breaks it</br>
	 * 5. If total result is becoming bigger too often, throws exception
	 */
	public void mix() {
		if (mixables.length == 1) {
			mixables[0].mixOneItem();
			return;
		}

		Mixable[][] pairs = splitIntoPairs();

		for (int i = 0, newResult; i < MAX_ITERATION; i++) {
			Arrays.stream(pairs).parallel().forEach(p -> p[0].mixTwoItems(p[1]));
			newResult = Arrays.stream(mixables).mapToInt(Mixable::getMixResult).sum();

			if (newResult - latestResult < 0) {
				biggerResult = standstillResult = 0;
				latestResult = newResult;
				continue;
			}

			if (newResult - latestResult == 0)
				if (standstillResult++ < 2)
					continue;
				else
					break;

			if (biggerResult++ >= 4)
				throw new IllegalStateException("IllegalStateException in KernighanLinAlgorithm.mix()");
		}
	}

	/**
	 * Splits all Mixables into pairs,</br>
	 * without rearrangements and without pairing Mixable with itself
	 * 
	 * @return Mixables matrix of size [num_of_pairs x 2]</br>
	 *         where num_of_pairs = ((num_of_Mixables)^2 - num_of_Mixables) / 2
	 */
	private Mixable[][] splitIntoPairs() {
		int length = mixables.length;
		Mixable[][] toReturn = new Mixable[(length * length - length) / 2][2];

		for (int i = 0, index = 0; i < length; i++)
			for (int j = i + 1; j < length; j++, index++) {
				toReturn[index][0] = mixables[i];
				toReturn[index][1] = mixables[j];
			}

		return toReturn;
	}
}