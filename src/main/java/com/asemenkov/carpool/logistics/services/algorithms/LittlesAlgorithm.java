package com.asemenkov.carpool.logistics.services.algorithms;


/**
 * An algorithm for the traveling salesman problem
 * 
 * @author asemenkov
 * @since Jan 28, 2018
 */
public class LittlesAlgorithm {

    public static final int INF1 = 1111111111;
    public static final int INF2 = 111111111;
    public static final int INF3 = 1111111;
    
    public static final int TENDS_TO_INF1 = 1000000000;
    public static final int TENDS_TO_INF2 = 100000000;
    public static final int TENDS_TO_INF3 = 1000000;

    private int[][] bestCycle;
    private int bestPath;
    private int stepDelta;

    /**
     * Recursively builds Hamiltonian Cycle for copy of argument matrix. Reduces
     * matrix size on every iteration. Initial matrix is not altered. Diagonal
     * elements are set to INF, so preset is not required.
     * 
     * @param graph
     *            -- square matrix at least 2x2
     * @param size
     *            -- size of matrix, must be less or equals origin matrix sizes
     */
    public void findHamiltonianCycle(int[][] graph) {
        findHamiltonianCycle(graph, graph.length);
    }

    /**
     * Recursively builds Hamiltonian Cycle for copy of argument matrix. Reduces
     * matrix size on every iteration. Initial matrix is not altered. Diagonal
     * elements are set to INF, so preset is not required.
     * 
     * @param graph
     *            -- square matrix at least 2x2
     * @param size
     *            -- size of matrix, must be less or equals origin matrix sizes
     */
    public void findHamiltonianCycle(int[][] graph, int size) {
        bestCycle = null;
        bestPath = INF1;
        int[][] indexedGraph = getIndexedGraph(graph, size);
        recursiveHamiltonianCycle(indexedGraph, new int[0][2]);
    }

    /**
     * @return length of the shortest possible path after invocation of</br>
     *         {@link #findHamiltonianCycle(int[][], int)}
     */
    public int getPathLength() {
        return bestPath;
    }

    /**
     * @return the shortest possible path after invocation of</br>
     *         {@link #findHamiltonianCycle(int[][], int)}</br>
     *         in format of integer matrix of size nx2, where n = number of edges
     */
    public int[][] getPathCycle() {
        return bestCycle;
    }

    /**
     * @param graph
     *            -- square matrix at least 2x2
     * @param size
     *            -- size of original matrix, must be less or equals of graph sizes
     * @return square copy of matrix [(size + 1) x (size + 1)]</br>
     *         the first row and column contains indexes of matrix row/column</br>
     *         for example: {0, 0, 1, 2, 3, 4, ..., (size - 1)} </br>
     *         diagonal elements are set to INF
     */
    private int[][] getIndexedGraph(int[][] graph, int size) {
        int sizePlusOne = size + 1;
        int[][] toReturn = new int[sizePlusOne][sizePlusOne];

        for (int i = 1; i < sizePlusOne; i++)
            System.arraycopy(graph[i - 1], 0, toReturn[i], 1, size);

        for (int i = 1; i < sizePlusOne; i++) {
            toReturn[0][i] = toReturn[i][0] = i - 1;
            toReturn[i][i] = INF1;
        }

        return toReturn;
    }

    /**
     * 1. In every row of the graph takes the minimum element and subtracts it from
     * every element of this row, making at least one '0' in the row.</br>
     * 
     * 2. In every column of the graph takes the minimum element and subtracts it
     * from every element of this column, making at least one '0' in the
     * column.</br>
     * 
     * 3. All of the subtracted elements are added to the graph[0][0], which
     * represents overall length of the Cycle.</br>
     * 
     * 4. If overall length of the Cycle is bigger than the best previously seen,
     * the recursive calculations are stopped in this branch.</br>
     * 
     * 5. Looks for '0' elements.</br>
     * 
     * 6. For each '0' element, looks for the minimum element of its row and the
     * minimum element of its column, not including this '0' element, and sums this
     * pair up.</br>
     * 
     * 7. Takes the maximum element of the Little's Sums (if there are 2 or more max
     * elements, the algorithm forks away and steps 8-12 are executed for each
     * element).</br>
     * 
     * 8. Takes '0' element, which corresponds the Little's Sum from the step 7, and
     * removes its row and column from the graph.</br>
     * 
     * 9. If the new smaller matrix contains row and column without INF, it sets INF
     * at the intersection to avoid early shortage of the Cycle.</br>
     * 
     * 10. Takes '0' element, which corresponds the Little's Sum from the step 7,
     * and sets this element to INF, discarding this edge and saving graph
     * size.</br>
     * 
     * 11. Recursively repeats steps 1-10 for each new graph until the size becomes
     * 3x3(including indexes) with an obvious path.</br>
     * 
     * 12. Updates 'bestPath' and 'bestCycle' variables, if and only if the branch
     * provide better ones.
     * 
     * @param graph
     *            -- square matrix at least 3x3 with indexes and accumulated length
     * @param cycle
     *            -- matrix of size nx2 where n = number of accumulated edges
     */
    private void recursiveHamiltonianCycle(int[][] graph, int[][] cycle) {

        if ((stepDelta = subtractMinElementInEachRow(graph)) == INF2)
            return;

        graph[0][0] += stepDelta;
        if (graph[0][0] >= bestPath)
            return;

        if ((stepDelta = subtractMinElementInEachCol(graph)) == INF2)
            return;

        graph[0][0] += stepDelta;
        if (graph[0][0] >= bestPath)
            return;

        int[][] zerosCoords = findZerosCoordinates(graph);

        if (graph.length == 3) {
            if (graph[0][0] < bestPath) {
                bestCycle = appendToCycle(graph, cycle, zerosCoords[0], zerosCoords[1]);
                bestPath = graph[0][0];
            }
            return;
        }

        int[] littlesSums = findLittlesSums(graph, zerosCoords);
        int[][] coords = filterCoordsLessThanMaxSum(zerosCoords, littlesSums);
        int[][][] branchedGraph = branchOutGraph(graph, coords);

        for (int i = 0; i < coords.length; i++) {
            int[][] newCycle = appendToCycle(graph, cycle, coords[i]);
            recursiveHamiltonianCycle(branchedGraph[i], newCycle);
        }

        for (int i = coords.length; i < branchedGraph.length; i++)
            recursiveHamiltonianCycle(branchedGraph[i], cycle);
    }

    /**
     * Adds one more edge to the previously accumulated ones. Must be invoked only
     * in case of removal of row and column from the graph.
     * 
     * @param graph
     *            -- square matrix at least 4x4 with indexes and accumulated length
     * @param cycle
     *            -- matrix of size nx2 where n = number of accumulated edges
     * @param coord
     *            -- row and column which are removed in current recursive step
     * @return matrix of size (n+1)x2, where n = length of cycle.</br>
     */
    private int[][] appendToCycle(int[][] graph, int[][] cycle, int[] coord) {
        int[][] toReturn = new int[cycle.length + 1][2];
        System.arraycopy(cycle, 0, toReturn, 1, cycle.length);
        toReturn[0][0] = graph[coord[0]][0];
        toReturn[0][1] = graph[0][coord[1]];
        return toReturn;
    }

    /**
     * Adds two final edges to the previously accumulated ones. Must be invoked in
     * the end of the branch, when matrix 3x3 is reached.
     * 
     * @param graph
     *            -- square matrix 3x3 with indexes and accumulated length
     * @param cycle
     *            -- matrix of size nx2 where n = number of accumulated edges
     * @param coord0
     *            -- row and column of the first '0' element
     * @param coord1
     *            -- row and column of the second '0' element
     * @return matrix of size (n+2)x2, where n = length of cycle.</br>
     */
    private int[][] appendToCycle(int[][] graph, int[][] cycle, int[] coord0, int[] coord1) {
        int[][] toReturn = new int[cycle.length + 2][2];
        System.arraycopy(cycle, 0, toReturn, 2, cycle.length);
        toReturn[0][0] = graph[coord0[0]][0];
        toReturn[0][1] = graph[0][coord0[1]];
        toReturn[1][0] = graph[coord1[0]][0];
        toReturn[1][1] = graph[0][coord1[1]];
        return toReturn;
    }

    /**
     * Looks for minimum element in each row.</br>
     * If it isn't zero, it's subtracted from each element of the row, including
     * this min element.
     * 
     * @param graph
     *            -- square matrix at least 3x3 with indexes and accumulated length
     * 
     * @return sum of the minimum elements of each row
     */
    private int subtractMinElementInEachRow(int[][] graph) {
        int min, toReturn = 0;

        for (int i = 1; i < graph.length; toReturn += min, i++) {
            min = searchMinElementInRow(graph, i);
            if (min == 0)
                continue;
            if (min > TENDS_TO_INF2)
                return INF2;
            for (int j = 1; j < graph.length; j++)
                graph[i][j] -= min;
        }

        return toReturn;
    }

    /**
     * Looks for minimum element in each column. </br>
     * If it isn't zero, it's subtracted from each element of the column, including
     * this min element.
     * 
     * @param graph
     *            -- square matrix at least 3x3 with indexes and accumulated length
     * 
     * @return sum of the minimum elements of each column
     */
    private int subtractMinElementInEachCol(int[][] graph) {
        int min, toReturn = 0;

        for (int i = 1; i < graph.length; toReturn += min, i++) {
            min = searchMinElementInCol(graph, i);
            if (min == 0)
                continue;
            if (min > TENDS_TO_INF2)
                return INF2;
            for (int j = 1; j < graph.length; j++)
                graph[j][i] -= min;
        }

        return toReturn;
    }

    /**
     * Searches the minimum element in particular row of the graph.
     * 
     * @param graph
     *            -- square matrix at least 3x3 with indexes and accumulated length
     * @param row
     *            -- row to search in, must be less than the graph size
     * @return value of the minimum element in the row
     */
    private int searchMinElementInRow(int[][] graph, int row) {
        int toReturn = graph[row][1];
        for (int i = 2; i < graph.length; i++)
            if (graph[row][i] == 0)
                return 0;
            else if (graph[row][i] < toReturn)
                toReturn = graph[row][i];
        return toReturn;
    }

    /**
     * Searches the minimum element in particular column of the graph.
     * 
     * @param graph
     *            -- square matrix at least 3x3 with indexes and accumulated length
     * @param col
     *            -- column to search in, must be less than the graph size
     * @return value of the minimum element in the column
     */
    private int searchMinElementInCol(int[][] graph, int col) {
        int toReturn = graph[1][col];
        for (int i = 2; i < graph.length; i++)
            if (graph[i][col] == 0)
                return 0;
            else if (graph[i][col] < toReturn)
                toReturn = graph[i][col];
        return toReturn;
    }

    /**
     * Looks for '0' elements in the graph matrix.
     * 
     * @param graph
     *            -- square matrix at least 3x3 with indexes and accumulated length
     * @return matrix of size nx2, where n = number of '0' elements found
     */
    private int[][] findZerosCoordinates(int[][] graph) {
        int zeros = 0;
        for (int i = 1; i < graph.length; i++)
            for (int j = 1; j < graph.length; j++)
                if (graph[i][j] == 0)
                    zeros++;

        int[][] toReturn = new int[zeros][2];
        for (int i = 1; i < graph.length; i++)
            for (int j = 1; j < graph.length; j++)
                if (graph[i][j] == 0) {
                    toReturn[--zeros][0] = i;
                    toReturn[zeros][1] = j;
                }

        return toReturn;
    }

    /**
     * Calculates Little's sum for each '0' element. This is sum of the minimum
     * element in row and the minimum element in column of '0' element, excluding
     * this '0' element.
     * 
     * @param graph
     *            -- square matrix at least 3x3 with indexes and accumulated length
     * @param coords
     *            -- array of size nx2, where n = number of '0' elements</br>
     *            element[i][0] represents row of '0'</br>
     *            element[i][1] represents column of '0'</br>
     *            each element must be not negative and less than the graph size
     * @return Little's sums array of size n, where n = number of '0' elements
     */
    private int[] findLittlesSums(int[][] graph, int[][] coords) {
        int[] toReturn = new int[coords.length];
        int minInRowI, minInColJ;

        for (int c = 0; c < coords.length; c++) {
            minInRowI = minInColJ = INF1;

            for (int k = 1; k < graph.length; k++)
                if (k != coords[c][1] && graph[coords[c][0]][k] < minInRowI)
                    minInRowI = graph[coords[c][0]][k];

            for (int k = 1; k < graph.length; k++)
                if (k != coords[c][0] && graph[k][coords[c][1]] < minInColJ)
                    minInColJ = graph[k][coords[c][1]];

            toReturn[c] = minInRowI + minInColJ;
        }

        return toReturn;
    }

    /**
     * Looks for maximum sum and corresponding '0' coordinates. All the coordinates,
     * which don't correspond these maximum sums, are removed.
     * 
     * @param coords
     *            -- coordinates of each '0' element
     * @param sums
     *            -- Little's sums for each '0' element
     * @return coordinates matrix of size nx2, where n = number of maximum sums
     */
    private int[][] filterCoordsLessThanMaxSum(int[][] coords, int[] sums) {
        int max = sums[0];
        int count = 1;

        for (int i = 1; i < sums.length; i++)
            if (sums[i] > max) {
                max = sums[i];
                count = 1;
            } else if (sums[i] < max) {
                continue;
            } else {
                count++;
            }

        int[][] toReturn = new int[count][2];
        for (int i = 0; i < sums.length; i++)
            if (sums[i] == max)
                toReturn[--count] = coords[i];

        return toReturn;
    }

    /**
     * Creates altered copies of current graph. Each copy represents its own branch
     * 
     * @param graph
     *            -- square matrix at least 3x3 with indexes and accumulated length
     * @param coords
     *            -- coordinates of '0' elements, that correspond to maximum sum
     * @return 3-dimensional array - collection of graph alterations.</br>
     *         Total size is 2n, where n = number of coords.</br>
     *         The 1st n matrices have size (m-1)x(m-1), where m = graph size. These
     *         matrices represents original graph after row and column removal.</br>
     *         The 2nd n matrices have size mxm, where m = graph size. These
     *         matrices represents original graph with the element at row and column
     *         intersection set to INF.</br>
     */
    private int[][][] branchOutGraph(int[][] graph, int[][] coords) {
        int[][][] branchedGraph = new int[coords.length + coords.length][][];
        for (int i = 0, j = coords.length; i < coords.length; i++, j++) {
            branchedGraph[i] = removeRowAndColumnByCoords(graph, coords[i]);
            branchedGraph[j] = setInfToDiscardEdge(graph, coords[i]);
        }
        return branchedGraph;
    }

    /**
     * @param graph
     *            -- square matrix at least 3x3 with indexes and accumulated length
     * @param coord
     *            -- matrix of size 1x2</br>
     *            element[0] - index of row which will be removed</br>
     *            element[1] - index of column which will be removed</br>
     *            both elements must be not negative less than the graph size
     * @return altered copy of graph after row and column removal
     */
    private int[][] removeRowAndColumnByCoords(int[][] graph, int[] coord) {
        int[][] newgraph = new int[graph.length - 1][graph.length - 1];
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

        setInfToPreventFromEarlyShortage(newgraph);
        return newgraph;
    }

    /**
     * @param graph
     *            -- square matrix at least 3x3 with indexes and accumulated length
     * @param coord
     *            -- coordinates of element which must be set to INF
     * @return copy of argument graph with INF at [coord[0]][coord[1]]
     */
    private int[][] setInfToDiscardEdge(int[][] graph, int[] coord) {
        int[][] newgraph = new int[graph.length][graph.length];

        for (int i = 0; i < graph.length; i++)
            System.arraycopy(graph[i], 0, newgraph[i], 0, graph.length);

        newgraph[coord[0]][coord[1]] = INF2;
        return newgraph;
    }

    /**
     * If the argument graph contains row and column without INF1, it will be set at
     * the intersection. That helps to avoid early shortage of the Cycle.
     * 
     * @param graph
     *            -- square matrix at least 3x3 with indexes and accumulated length
     */
    private void setInfToPreventFromEarlyShortage(int[][] graph) {

        int row = 1;
        rows: for (; row < graph.length; row++) {
            for (int i = 1; i < graph.length; i++)
                if (graph[row][i] > TENDS_TO_INF1)
                    continue rows;
            break;
        }

        if (row == graph.length)
            return;

        int col = 1;
        cols: for (; col < graph.length; col++) {
            for (int i = 1; i < graph.length; i++)
                if (graph[i][col] > TENDS_TO_INF1)
                    continue cols;
            break;
        }

        if (col == graph.length)
            return;

        graph[row][col] = INF1;
    }

}