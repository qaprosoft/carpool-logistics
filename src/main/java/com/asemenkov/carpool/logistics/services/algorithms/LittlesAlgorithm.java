package com.asemenkov.carpool.logistics.services.algorithms;

/**
 * An algorithm for the traveling salesman problem
 * 
 * @author asemenkov
 * @since Jan 28, 2018
 */
public class LittlesAlgorithm {

	public static final int INF = Integer.MAX_VALUE;
	public static final int BIG = 1000000;

	private int[][] graph;
	private int size;

	/**
	 * Creates copy of matrix, avoiding alternation of the initial matrix. </br>
	 * Diagonal elements are set to INF, so preset is not required.
	 * 
	 * @param graph
	 *            -- square matrix at least 2x2
	 */
	public LittlesAlgorithm withGraph(int[][] graph) {
		this.graph = deepCopyOfMatrix(graph);
		this.size = this.graph.length;
		for (int i = 0; i < size; i++)
			this.graph[i][i] = INF;
		return this;
	}

	/**
	 * Creates truncated copy of matrix, avoiding alternation of the initial
	 * matrix. </br>
	 * Diagonal elements are set to INF, so preset is not required.
	 * 
	 * @param graph
	 *            -- square matrix at least 2x2
	 * @param size
	 *            -- size of matrix, must be less or equals origin matrix sizes
	 */
	public LittlesAlgorithm withGraph(int[][] graph, int size) {
		this.size = size;
		this.graph = deepCopyOfMatrix(graph, size);
		for (int i = 0; i < size; i++)
			this.graph[i][i] = INF;
		return this;
	}

	/**
	 * Recursively builds Hamiltonian Cycle, reducing matrix size on every
	 * iteration. The Cycle passes through every point and withdraws into the
	 * initial one. Calculates overall length of the Cycle, that must be as
	 * short as possible.
	 * 
	 * @return integer matrix of size (n+1)x2, where n = number of edges </br>
	 *         element[0][0] - contains overall length of cycle </br>
	 *         element[0][1] - is not used </br>
	 *         element[i][0] and element[i][1] - represents an edge which
	 *         connects two nodes
	 */
	public int[][] findHamiltonianCycle() {
		int[][] bottomLineAndCycle = new int[size + 1][2];
		int[][] toReturn = recursiveHamiltonianCycle(bottomLineAndCycle);
		toReturn = shiftBackCycle(toReturn);
		return toReturn;
	}

	/**
	 * 1. In every row of the graph takes the minimum element and subtracts it
	 * from every element of this row, making at least one '0' in the row.</br>
	 * 
	 * 2. In every column of the graph takes the minimum element and subtracts
	 * it from every element of this column, making at least one '0' in the
	 * column.</br>
	 * 
	 * 3. All of the subtracted elements are added to the Bottom Line, which
	 * represents overall length of the Cycle.</br>
	 * 
	 * 4. Looks for '0' elements.</br>
	 * 
	 * 5. For each '0' element, looks for the minimum element of its row and the
	 * minimum element of its column, exclusive of this '0' element, and sums
	 * this pair up.</br>
	 * 
	 * 6. Takes the maximum element of the Little's Sums (if there are 2 or more
	 * max elements, the algorithm forks away and steps 7-9 are executed for
	 * each element).</br>
	 * 
	 * 7. Takes '0' element, which corresponds the Little's Sum from the step 6,
	 * and removes its row and column from the graph.</br>
	 * 
	 * 8. If the new smaller matrix contains row and column without INF, it sets
	 * INF at the intersection to avoid early shortage of the Cycle.</br>
	 * 
	 * 9. Recursively repeats steps 1-8 until the graph size becomes 2x2 with an
	 * obvious path.</br>
	 * 
	 * 10. If the algorithm has more than one fork, chooses the shortest Cycle.
	 * 
	 * @param bottomLineAndCycle
	 *            -- zero-filled array of size (n+1)x2 where n = number of edges
	 * @return preliminary Hamiltonian Cycle and its length
	 */
	private int[][] recursiveHamiltonianCycle(int[][] bottomLineAndCycle) {

		bottomLineAndCycle[0][0] += subtractMinElementInEachRow();
		if (bottomLineAndCycle[0][0] > 1000000000)
			return bottomLineAndCycle;

		bottomLineAndCycle[0][0] += subtractMinElementInEachCol();
		if (bottomLineAndCycle[0][0] > 1000000000)
			return bottomLineAndCycle;

		int[][] zerosCoords = findZerosCoordinates();

		if (size == 2) {
			bottomLineAndCycle[2] = zerosCoords[1];
			bottomLineAndCycle[1] = zerosCoords[0];
			return bottomLineAndCycle;
		}

		int[] littlesSums = findLittlesSums(zerosCoords);
		sortLittlesSumsAndCoords(zerosCoords, littlesSums);

		if (littlesSums[1] < 0) {
			removeRowAndColumnByCoordinates(zerosCoords[0]);
			setInfToPreventFromEarlyShortage();
			bottomLineAndCycle[size + 1] = zerosCoords[0];
			return recursiveHamiltonianCycle(bottomLineAndCycle);
		}

		int[][] graphCopy = deepCopyOfMatrix(graph);
		int[][] toReturn = new int[bottomLineAndCycle.length][2];
		toReturn[0][0] = INF;

		for (int i = 0; i < littlesSums.length && littlesSums[i] != -1; graph = graphCopy, size = graph.length) {
			removeRowAndColumnByCoordinates(zerosCoords[i]);
			setInfToPreventFromEarlyShortage();

			int[][] bottomLineAndCycleCopy = deepCopyOfMatrix(bottomLineAndCycle);
			bottomLineAndCycleCopy[size + 1] = zerosCoords[i++];

			int[][] branchBottomLineAndCycle = recursiveHamiltonianCycle(bottomLineAndCycleCopy);
			if (branchBottomLineAndCycle[0][0] < toReturn[0][0])
				toReturn = branchBottomLineAndCycle;
		}

		return toReturn;
	}

	/**
	 * Looks for minimum element in each row.</br>
	 * If it isn't zero, it's subtracted from each element of the row, including
	 * this min element.
	 * 
	 * @return sum of the minimum elements of each row
	 */
	private int subtractMinElementInEachRow() {
		int min, toReturn = 0;

		for (int i = 0; i < size; toReturn += min, i++) {
			min = searchMinElementInRow(i);
			if (min == 0)
				continue;
			if (min > 1000000000)
				return INF;
			for (int j = 0; j < size; j++)
				graph[i][j] -= min;
		}

		return toReturn;
	}

	/**
	 * Looks for minimum element in each column. </br>
	 * If it isn't zero, it's subtracted from each element of the column,
	 * including this min element.
	 * 
	 * @return sum of the minimum elements of each column
	 */
	private int subtractMinElementInEachCol() {
		int min, toReturn = 0;

		for (int i = 0; i < size; toReturn += min, i++) {
			min = searchMinElementInCol(i);
			if (min == 0)
				continue;
			if (min > 1000000000)
				return INF;
			for (int j = 0; j < size; j++)
				graph[j][i] -= min;
		}

		return toReturn;
	}

	/**
	 * Searches the minimum element in particular row of the graph.
	 * 
	 * @param row
	 *            -- row to search in, must be not negative and less than the
	 *            graph size
	 * @return value of the minimum element in the row
	 */
	private int searchMinElementInRow(int row) {
		int toReturn = graph[row][0];
		for (int i = 1; i < size; i++)
			if (graph[row][i] == 0)
				return 0;
			else if (graph[row][i] < toReturn)
				toReturn = graph[row][i];
		return toReturn;
	}

	/**
	 * Searches the minimum element in particular column of the graph.
	 * 
	 * @param col
	 *            -- column to search in, must be not negative and less than the
	 *            graph size
	 * @return value of the minimum element in the column
	 */
	private int searchMinElementInCol(int col) {
		int toReturn = graph[0][col];
		for (int i = 1; i < size; i++)
			if (graph[col][i] == 0)
				return 0;
			else if (graph[i][col] < toReturn)
				toReturn = graph[i][col];
		return toReturn;
	}

	/**
	 * Looks for '0' elements in the graph matrix.
	 * 
	 * @return matrix of size nx2, where n = number of '0' elements found
	 */
	private int[][] findZerosCoordinates() {
		int zeros = 0;
		for (int i = 0; i < size; i++)
			for (int j = 0; j < size; j++)
				if (graph[i][j] == 0)
					zeros++;

		int[][] toReturn = new int[zeros][2];
		for (int i = 0; i < size; i++)
			for (int j = 0; j < size; j++)
				if (graph[i][j] == 0) {
					toReturn[--zeros][0] = i;
					toReturn[zeros][1] = j;
				}

		return toReturn;
	}

	/**
	 * Calculates Little's sum for each '0' element. This is sum of the minimum
	 * element in row and the minimum element in column of '0' element,
	 * excluding this '0' element.
	 * 
	 * @param coords
	 *            -- array of size nx2, where n = number of '0' elements</br>
	 *            element[i][0] represents row of '0'</br>
	 *            element[i][1] represents column of '0'</br>
	 *            each element must be not negative and less than the graph size
	 * @return Little's sums array of size n, where n = number of '0' elements
	 */
	private int[] findLittlesSums(int[][] coords) {
		int[] toReturn = new int[coords.length];
		int minInRowI, minInColJ;

		for (int c = 0; c < coords.length; c++) {
			minInRowI = minInColJ = INF;

			for (int k = 0; k < size; k++)
				if (k != coords[c][1] && graph[coords[c][0]][k] < minInRowI)
					minInRowI = graph[coords[c][0]][k];

			for (int k = 0; k < size; k++)
				if (k != coords[c][0] && graph[k][coords[c][1]] < minInColJ)
					minInColJ = graph[k][coords[c][1]];

			toReturn[c] = minInRowI + minInColJ;
		}

		return toReturn;
	}

	/**
	 * Actually, it is not sorting. The 2 maximums of sums and corresponding
	 * coordinates are moved to the beginning of their initial arrays. The rest
	 * of elements of sums array are set to zero: {5,14,11,14} -> {14,11,14,-1}.
	 * Coordinates are not reduced to zero, just shifted to the beginning.
	 * 
	 * @param coords
	 *            -- coordinates of each '0' element
	 * @param sums
	 *            -- Little's sums for each '0' element
	 */
	private void sortLittlesSumsAndCoords(int[][] coords, int[] sums) {
		int max1 = sums[0], max2 = -1;
		for (int i = 1; i < sums.length; i++)
			if (sums[i] > max1) {
				max2 = max1;
				max1 = sums[i];
			}

		int count = 0;
		for (int i = 0; i < sums.length; i++)
			if (sums[i] == max1 || sums[i] == max2) {
				sums[count] = sums[i];
				coords[count++] = coords[i];
			}

		for (; count < sums.length; count++)
			sums[count] = -1;
	}

	/**
	 * @param matrix
	 *            -- any matrix which we need to prevent from alteration
	 * @return new matrix with exact the same elements as the argument matrix
	 */
	private int[][] deepCopyOfMatrix(int[][] matrix) {
		int[][] toReturn = new int[matrix.length][matrix[0].length];
		for (int i = 0; i < matrix.length; i++)
			for (int j = 0; j < matrix[0].length; j++)
				toReturn[i][j] = matrix[i][j];
		return toReturn;
	}

	/**
	 * @param matrix
	 *            -- any matrix which we need to prevent from alteration
	 * @param size
	 *            -- copy size, must be less or equals origin matrix sizes
	 * @return new matrix size x size
	 */
	private int[][] deepCopyOfMatrix(int[][] matrix, int size) {
		int[][] toReturn = new int[size][size];
		for (int i = 0; i < size; i++)
			for (int j = 0; j < size; j++)
				toReturn[i][j] = matrix[i][j];
		return toReturn;
	}

	/**
	 * @param coord
	 *            -- matrix of size 1x2</br>
	 *            element[0] - index of row which will be removed</br>
	 *            element[1] - index of column which will be removed</br>
	 *            both elements must be not negative less than the graph size
	 */
	private void removeRowAndColumnByCoordinates(int[] coord) {
		int[][] newgraph = new int[size - 1][size - 1];
		int i, j;

		for (i = 0; i < coord[0]; i++)
			for (j = 0; j < coord[1]; j++)
				newgraph[i][j] = graph[i][j];

		if (coord[0] <= newgraph.length)
			for (i = coord[0]; i < newgraph.length; i++)
				for (j = 0; j < coord[1]; j++)
					newgraph[i][j] = graph[i + 1][j];

		if (coord[1] <= newgraph.length)
			for (i = 0; i < coord[0]; i++)
				for (j = coord[1]; j < newgraph.length; j++)
					newgraph[i][j] = graph[i][j + 1];

		if (coord[0] <= newgraph.length && coord[1] <= newgraph.length)
			for (i = coord[0]; i < newgraph.length; i++)
				for (j = coord[1]; j < newgraph.length; j++)
					newgraph[i][j] = graph[i + 1][j + 1];

		graph = newgraph;
		size = graph.length;
	}

	/**
	 * If the graph contains row and column without INF, it sets INF at the
	 * intersection to avoid early shortage of the Cycle.</br>
	 */
	private void setInfToPreventFromEarlyShortage() {
		int row = 0;
		rows: for (; row < size; row++) {
			for (int i = 0; i < size; i++)
				if (graph[row][i] > 1000000000)
					continue rows;
			break;
		}

		if (row == size)
			return;

		int col = 0;
		cols: for (; col < size; col++) {
			for (int i = 0; i < size; i++)
				if (graph[i][col] > 1000000000)
					continue cols;
			break;
		}

		if (col == size)
			return;

		graph[row][col] = INF;
	}

	/**
	 * This shift back is needed, because on every iteration, after row and
	 * column removal, some rows of the graph are shifted up and some columns
	 * are shifted right. Their indexes are decremented relatively the initial
	 * values. This method reverts these decrements.
	 * 
	 * @param bottomLineAndCycle
	 *            -- integer matrix of size (n+1)x2, where n = number of edges
	 * @return the argument matrix with shifted values
	 */
	private int[][] shiftBackCycle(int[][] bottomLineAndCycle) {
		int[] rows = new int[bottomLineAndCycle.length - 1];
		int[] cols = new int[rows.length];

		for (int i = 1; i < rows.length; i++)
			rows[i] = cols[i] = i;

		for (int i = rows.length; i > 1; i--) {
			int row = bottomLineAndCycle[i][0];
			int col = bottomLineAndCycle[i][1];

			bottomLineAndCycle[i][0] = rows[row];
			bottomLineAndCycle[i][1] = cols[col];

			for (int j = row + 1; j < rows.length; j++)
				rows[j - 1] = rows[j];

			for (int j = col + 1; j < cols.length; j++)
				cols[j - 1] = cols[j];
		}

		bottomLineAndCycle[1][0] = rows[0];
		bottomLineAndCycle[1][1] = cols[0];

		return bottomLineAndCycle;
	}

	/**
	 * @return graph matrix in string format</br>
	 *         <b>For debug only</b>
	 */
	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder("\n==================== GRAPH ====================\n");
		for (int i = 0; i < graph.length; i++, sb.append("\n"))
			for (int j = 0; j < graph[0].length; j++, sb.append("\t"))
				if (graph[i][j] > 1000000000)
					sb.append("INF");
				else
					sb.append(graph[i][j]);
		return sb.append("===============================================").toString();
	}

}