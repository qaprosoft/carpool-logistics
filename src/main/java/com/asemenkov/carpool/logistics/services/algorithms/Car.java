package com.asemenkov.carpool.logistics.services.algorithms;

import java.util.Arrays;
import java.util.stream.IntStream;

import org.springframework.beans.factory.annotation.Autowired;

/**
 * Mixable implementation which will be used as argument for Kernighan-Lin. All
 * the calculations are performed with draft variables. If the result of
 * calculations is better than before, draft data are copied to fair. Otherwise
 * restores data from fair to draft.
 * 
 * @author asemenkov
 * @since Feb 11, 2018
 */
public class Car implements Mixable<Car> {

	private final int[][] globalMatrix;
	private final int[][] picksDropsGraph;

	private final int[] draftPicks;
	private final int[] fairPicks;

	private final int[] draftDrops;
	private final int[] fairDrops;
	private final int[] distinctPicks;

	private int distinctPicksLength;
	private int maxLength;
	private final int capacity;

	private int[][] draftRoute;
	private int[][] fairRoute;

	private int draftPathLength;
	private int fairPathLength;

	private int draftSeats;
	private int fairSeats;

	private @Autowired LittlesAlgorithm littlesAlgorithm;

	public Car(int capacity, int[][] globalMatrix) {
		this.capacity = capacity;
		this.globalMatrix = globalMatrix;
		this.picksDropsGraph = new int[2 * capacity][2 * capacity];

		draftRoute = new int[0][0];
		fairRoute = new int[0][0];
		draftPicks = new int[capacity];
		fairPicks = new int[capacity];
		draftDrops = new int[capacity];
		fairDrops = new int[capacity];
		distinctPicks = new int[capacity];

		Arrays.fill(draftPicks, -1);
		Arrays.fill(fairPicks, -1);
		Arrays.fill(draftDrops, -1);
		Arrays.fill(fairDrops, -1);
	}

	/**
	 * Parameters 'from' are 'to' must be Global Matrix indexes
	 */
	public synchronized Car putPassenger(int from, int to) {
		draftPicks[draftSeats] = from;
		draftDrops[draftSeats++] = to;
		return this;
	}

	/**
	 * @param maxLength
	 *            -- either distance or duration limitation</br>
	 *            values can be found in application.properties
	 */
	public synchronized void setMaxLength(int maxLength) {
		this.maxLength = maxLength;
	}

	/**
	 * @return whether any passenger lives too far from his hub</br>
	 *         if true, then the task cannot be solved because of MaxLength
	 *         limitation <b>(each car rides <= 60 min)</b>
	 */
	public synchronized boolean isAnyRouteGreaterThanMaxLength() {
		for (int i = 0; i < draftSeats; i++)
			if (globalMatrix[draftPicks[i]][draftDrops[i]] > maxLength)
				return true;
		return false;
	}

	/**
	 * @return Array of pick up Location indexes (relates to Global Matrix)
	 */
	public synchronized int[] getPicks() {
		return fairPicks;
	}

	/**
	 * @return Array of drop off Location indexes (relates to Global Matrix)
	 */
	public synchronized int[] getDrops() {
		return fairDrops;
	}

	/**
	 * @return number of passengers
	 */
	public synchronized int getSeatsOccupied() {
		return fairSeats;
	}

	/**
	 * @return integer matrix of size [n-1 x 2], where n is sum of pick up and drop
	 *         off Locations</br>
	 *         example: {1,1,2,3} -> [1,1] [1,2] [2,3]
	 */
	public synchronized int[][] getSuggestedRoute() {
		return fairRoute;
	}

	/**
	 * @return either distance in seconds or duration in meters
	 */
	@Override
	public synchronized int getMixResult() {
		return fairPathLength;
	}

	/**
	 * Must be used, if one car is enough to place all passengers
	 */
	@Override
	public synchronized void mixOneItem() {
		preMix();
		copyFromDraftToFair();
	}

	/**
	 * Swaps passengers between this and that car
	 */
	@Override
	public synchronized void mixTwoItems(Car that) {
		that.mixPassenegrs(this);
	}

	/**
	 * Finds Hamiltonian Path for this car
	 */
	private synchronized void preMix() {
		shiftEmptySeats();
		findPathLength();
	}

	/**
	 * 1. Combines passengers into pairs for both cars</br>
	 * 2. Swaps passengers pairs between two cars until better result's achieved
	 */
	private synchronized void mixPassenegrs(Car that) {
		if (this.draftPathLength == 0) {
			this.preMix();
			this.copyFromDraftToFair();
		}

		if (that.draftPathLength == 0) {
			that.preMix();
			that.copyFromDraftToFair();
		}

		int[][] thisPassengerPairs = this.getPassengerPairs();
		int[][] thatPassengerPairs = that.getPassengerPairs();

		for (int tmp, i = 0; i < thisPassengerPairs.length; i++)
			for (int j = 0; j < thatPassengerPairs.length; j++) {

				tmp = this.draftPicks[thisPassengerPairs[i][0]];
				this.draftPicks[thisPassengerPairs[i][0]] = that.draftPicks[thatPassengerPairs[j][0]];
				that.draftPicks[thatPassengerPairs[j][0]] = tmp;

				tmp = this.draftDrops[thisPassengerPairs[i][0]];
				this.draftDrops[thisPassengerPairs[i][0]] = that.draftDrops[thatPassengerPairs[j][0]];
				that.draftDrops[thatPassengerPairs[j][0]] = tmp;

				tmp = this.draftPicks[thisPassengerPairs[i][1]];
				this.draftPicks[thisPassengerPairs[i][1]] = that.draftPicks[thatPassengerPairs[j][1]];
				that.draftPicks[thatPassengerPairs[j][1]] = tmp;

				tmp = this.draftDrops[thisPassengerPairs[i][1]];
				this.draftDrops[thisPassengerPairs[i][1]] = that.draftDrops[thatPassengerPairs[j][1]];
				that.draftDrops[thatPassengerPairs[j][1]] = tmp;

				if (compareThisAndThat(that))
					return;
			}
	}

	/**
	 * @return passengers combined into unique pairs
	 */
	private int[][] getPassengerPairs() {
		int[][] possiblePairs = new int[capacity * capacity][2];
		int[] hashes = new int[possiblePairs.length];

		for (int index = 0, hash, i = 0; i < capacity; i++)
			loop: for (int j = 0; j < capacity; j++, index++) {

				possiblePairs[index][0] = i;
				possiblePairs[index][1] = j;
				hash = (31 + draftPicks[i]) * (31 + draftPicks[j]) * //
						(31 + draftDrops[i]) * (31 + draftDrops[j]);

				for (int k = 0; k < index; k++)
					if (hashes[k] == hash)
						continue loop;

				hashes[index] = hash;
			}

		int[][] toReturn = new int[(int) IntStream.of(hashes).filter(h -> h == 0).count()][2];
		for (int i = 0, index = 0; i < toReturn.length; i++)
			if (hashes[i] != 0)
				toReturn[index++] = possiblePairs[i];

		return toReturn;
	}

	/**
	 * 1. Calculate sum of this and that car path lengths with penalties</br>
	 * 2. Finds Hamiltonian Path for both cars</br>
	 * 3. If better result is achieved, copies data from draft to fair</br>
	 * 4. Otherwise, copies from fair to draft
	 * 
	 * @return whether better result is achieved
	 */
	private boolean compareThisAndThat(Car that) {
		int pathLengthBefore = this.getPathLengthWithPenalty() + that.getPathLengthWithPenalty();

		this.preMix();
		that.preMix();

		if (pathLengthBefore > this.getPathLengthWithPenalty() + that.getPathLengthWithPenalty()) {
			this.copyFromDraftToFair();
			that.copyFromDraftToFair();
			return true;

		} else {
			this.copyFromFairToDraft();
			that.copyFromFairToDraft();
			return false;
		}
	}

	/**
	 * Each second / meter beyond the limit is multiplied by 100
	 */
	private int getPathLengthWithPenalty() {
		int diff = draftPathLength - maxLength;
		return draftPathLength + (diff > 0 ? diff * 100 : 0);
	}

	/**
	 * Shifts empty cells of picks and drops arrays to the right:</br>
	 * {-1, 1, 2, -1} -> {1, 2, -1, -1}</br>
	 * Calculates number of passengers
	 */
	private void shiftEmptySeats() {
		for (int tmp, i = 0, j = 1; j < capacity; i++, j++)
			if (draftPicks[i] < 0 && draftPicks[j] >= 0) {

				tmp = draftPicks[i];
				draftPicks[i] = draftPicks[j];
				draftPicks[j] = tmp;

				tmp = draftDrops[i];
				draftDrops[i] = draftDrops[j];
				draftDrops[j] = tmp;
				shiftEmptySeats();
			}

		for (draftSeats = 0; draftSeats < capacity; draftSeats++)
			if (draftPicks[draftSeats] < 0)
				break;
	}

	/**
	 * Builds graph using Global Matrix data:</br>
	 * - from each pick up location to each pick up location</br>
	 * - from each pick up location to each drop off location</br>
	 * - from each drop off location to each pick up location</br>
	 * - from each drop off location to each drop off location</br>
	 * 
	 * Distance from drop off to pick up location is set to extremely high value. It
	 * helps to build route straight from picks to drops
	 */
	private void combinePicksAndDrops() {

		distinctPicks[0] = draftPicks[0];
		distinctPicksLength = 1;

		loop: for (int i = 1; i < draftPicks.length; i++) {
			if (draftPicks[i] < 0)
				continue;
			for (int j = 0; j < distinctPicksLength; j++)
				if (distinctPicks[j] == draftPicks[i])
					continue loop;
			distinctPicks[distinctPicksLength++] = draftPicks[i];
		}

		for (int i = 0; i < distinctPicksLength; i++)
			for (int j = 0; j < distinctPicksLength; j++)
				picksDropsGraph[i][j] = globalMatrix[distinctPicks[i]][distinctPicks[j]];

		for (int i = 0; i < distinctPicksLength; i++)
			for (int j = 0; j < draftSeats; j++)
				picksDropsGraph[i][j + distinctPicksLength] = globalMatrix[distinctPicks[i]][draftDrops[j]];

		for (int i = 0; i < draftSeats; i++)
			for (int j = 0; j < distinctPicksLength; j++)
				picksDropsGraph[i + distinctPicksLength][j] = LittlesAlgorithm.INF3;

		for (int i = 0; i < draftSeats; i++)
			for (int j = 0; j < draftSeats; j++)
				picksDropsGraph[i + distinctPicksLength][j + distinctPicksLength] = //
						globalMatrix[draftDrops[i]][draftDrops[j]];
	}

	/**
	 * Builds Hamiltonian Path with the help of Littles algorithm
	 */
	private void findPathLength() {
		if (draftSeats == 0) {
			draftPathLength = 0;
			draftRoute = new int[0][0];
		}

		else if (draftSeats == 1) {
			draftPathLength = globalMatrix[draftPicks[0]][draftDrops[0]];
			draftRoute = new int[][] { { draftPicks[0], draftDrops[0] } };

		} else {
			combinePicksAndDrops();
			littlesAlgorithm.findHamiltonianCycle(picksDropsGraph, draftSeats + distinctPicksLength);
			draftPathLength = littlesAlgorithm.getPathLength() - LittlesAlgorithm.INF3;
			draftRoute = cleanRoute(littlesAlgorithm.getPathCycle());
		}
	}

	/**
	 * @param dirtyRoute
	 *            -- result of Littles algorithm
	 * @return integer array of format [1,1] [1,2] [2,3]</br>
	 *         each pick up and drop off location is visited in proper order
	 */
	private int[][] cleanRoute(int[][] dirtyRoute) {
		int[][] cleanRoute = new int[dirtyRoute.length - 1][2];
		int start = -1, end = -1;

		for (int i = 0; i < dirtyRoute.length; i++)
			if (picksDropsGraph[dirtyRoute[i][0]][dirtyRoute[i][1]] == LittlesAlgorithm.INF3) {
				end = dirtyRoute[i][0];
				start = dirtyRoute[i][1];
				dirtyRoute[i][0] = dirtyRoute[i][1] = -1;
				break;
			}

		for (int i = 0; start != end; i++)
			for (int j = 0; j < dirtyRoute.length; j++)
				if (dirtyRoute[j][0] == start) {
					cleanRoute[i][0] = start < distinctPicksLength ? //
							distinctPicks[start] : draftDrops[start - distinctPicksLength];

					start = dirtyRoute[j][1];
					cleanRoute[i][1] = start < distinctPicksLength ? //
							distinctPicks[start] : draftDrops[start - distinctPicksLength];
					break;
				}

		return cleanRoute;
	}

	/**
	 * Copies pathLength, seats, picks, drops, route values from draft to fair</br>
	 * Must be called when better result is achieved during the mix operation
	 */
	private void copyFromDraftToFair() {
		fairPathLength = draftPathLength;
		fairSeats = draftSeats;

		System.arraycopy(draftPicks, 0, fairPicks, 0, capacity);
		System.arraycopy(draftDrops, 0, fairDrops, 0, capacity);

		fairRoute = new int[draftRoute.length][2];
		for (int i = 0; i < draftRoute.length; i++) {
			fairRoute[i][0] = draftRoute[i][0];
			fairRoute[i][1] = draftRoute[i][1];
		}
	}

	/**
	 * Copies pathLength, seats, picks, drops, route values from fair to draft </br>
	 * Must be called when better result isn't achieved during the mix operation
	 */
	private void copyFromFairToDraft() {
		draftPathLength = fairPathLength;
		draftSeats = fairSeats;

		System.arraycopy(fairPicks, 0, draftPicks, 0, capacity);
		System.arraycopy(fairDrops, 0, draftDrops, 0, capacity);

		draftRoute = new int[fairRoute.length][2];
		for (int i = 0; i < fairRoute.length; i++) {
			draftRoute[i][0] = fairRoute[i][0];
			draftRoute[i][1] = fairRoute[i][1];
		}
	}

	/**
	 * @return draft data in string format</br>
	 *         <b>For debug only</b>
	 */
	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder("\n===================== CAR =====================\n");
		sb.append("Capacity: " + capacity);
		sb.append("\nSeats occupied: " + draftSeats);
		sb.append("\nPath length: " + draftPathLength);

		sb.append("\nPick up locations indexes: ");
		for (int i = 0; i < capacity; i++, sb.append("  "))
			sb.append(draftPicks[i]);

		sb.append("\nDrop off locations indexes: ");
		for (int i = 0; i < capacity; i++, sb.append("  "))
			sb.append(draftDrops[i]);

		if (fairRoute != null) {
			sb.append("\nSuggested route: ");
			for (int i = 0; i < draftRoute.length; i++, sb.append("  "))
				sb.append("[").append(draftRoute[i][0]).append(" ").append(draftRoute[i][1]).append("]");
		}

		return sb.append("\n===============================================").toString();
	}

}
