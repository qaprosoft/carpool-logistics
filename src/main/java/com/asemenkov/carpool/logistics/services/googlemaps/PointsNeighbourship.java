package com.asemenkov.carpool.logistics.services.googlemaps;

import java.util.function.Predicate;

import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;

import com.asemenkov.carpool.logistics.services.enums.Status;
import com.asemenkov.carpool.logistics.utils.WaitUtil;
import com.asemenkov.carpool.logistics.utils.io.QuerySender;

/**
 * Composes distance and duration matrices via requests to Google Maps Distance
 * Matrices API service
 * 
 * @author asemenkov
 * @since Feb 5, 2018
 */
public class PointsNeighbourship {

	private final GoogleMapsPoint[] picks;
	private final GoogleMapsPoint[] drops;
	private final int[][] distanceMatrix;
	private final int[][] durationMatrix;
	private final int matrixSize;

	private static long latestRequestTime = 1500000000000L;

	private Status status = Status.RUNNING_REQUESTS_TO_GOOGLE_MAPS;
	private String message = "OK";

	private @Value("${googlemaps.limit}") int limit;
	private @Value("${googlemaps.interval}") int interval;

	private @Autowired DistanceMatrixRequest distanceMatrixRequest;
	private @Autowired QuerySender googleMapsQuerySender;

	private Predicate<Long> waitLimit = (rqtime) -> System.currentTimeMillis() - rqtime > interval + 50;

	public PointsNeighbourship(GoogleMapsPoint[] picks, GoogleMapsPoint[] drops) {
		this.drops = drops;
		this.picks = picks;

		matrixSize = drops.length + picks.length;
		distanceMatrix = new int[matrixSize][matrixSize];
		durationMatrix = new int[matrixSize][matrixSize];
	}

	public int[][] getDistanceMatrix() {
		return distanceMatrix;
	}

	public int[][] getDurationMatrix() {
		return durationMatrix;
	}

	public String getMessage() {
		return message;
	}

	public Status getStatus() {
		return status;
	}

	/**
	 * Updates static field with request time</br>
	 * Each instance must check this field before request to Google Maps and
	 * update it after request
	 */
	public static void updateLastRequestTime() {
		latestRequestTime = System.currentTimeMillis();
	}

	/**
	 * If sum of pick up and drop off Locations is greater than 10, matrices
	 * cannot be built with one request. The reason is Google Map limitation:
	 * 100 elements per second. In this case all Locations are grouped into
	 * clusters 10x10. For each cluster:</br>
	 * 1. Sends request to Google Maps</br>
	 * 2. Parses response (if status != OK, the process is stopped)</br>
	 * 3. Writes data into corresponding part of matrix</br>
	 * 4. Waits for 1 second
	 */
	public void requestGoogleMapsForDistanceMatrices() {

		for (int i = 0; i < matrixSize; i += limit)
			for (int j = 0; j < matrixSize; j += limit, distanceMatrixRequest.freeAllArrays()) {
				for (int row = i; row < i + limit && row < matrixSize; row++)
					for (int col = j; col < j + limit && col < matrixSize; col++) {
						distanceMatrixRequest.addOriginPoint(row < drops.length ? //
								drops[row] : picks[row - drops.length], row % limit);
						distanceMatrixRequest.addDestinationsPoint(col < drops.length ? //
								drops[col] : picks[col - drops.length], col % limit);
					}

				parseGoogleMapsResponse(sendRequest(distanceMatrixRequest), i, j);
				if (status != Status.RUNNING_REQUESTS_TO_GOOGLE_MAPS)
					return;
			}
	}

	/**
	 * Waits for 1 second since the previous request and sends the new one
	 */
	private JSONObject sendRequest(DistanceMatrixRequest request) {
		WaitUtil.waitUntilAny(waitLimit, 2000, 20, latestRequestTime);
		JSONObject response = googleMapsQuerySender.sendQuery(request.toQueryString());
		return response;
	}

	/**
	 * 1. Verifies response status and status of each element is OK</br>
	 * 2. Reads JSON data and builds pieces of distance / duration matrices</br>
	 * 3. Calculates corresponding position of these pieces</br>
	 * 4. Writes distance / duration pieces to Global Matrices
	 */
	private void parseGoogleMapsResponse(JSONObject response, int startFromRow, int startFromCol) {

		if (response == null) {
			status = Status.ERROR_REQUESTS_TO_GOOGLE_MAPS_NO_RESPONSE;
			return;
		}

		if (!(message = (String) response.get("status")).equals("OK")) {
			status = Status.ERROR_REQUESTS_TO_GOOGLE_MAPS_ENCOUNTER_PROBLEM;
			return;
		}

		JSONArray rows = response.getJSONArray("rows");
		JSONArray elements = rows.getJSONObject(0).getJSONArray("elements");
		JSONObject element = elements.getJSONObject(0);

		rows: for (int i = 0; i < limit; //
				elements = rows.getJSONObject(i).getJSONArray("elements"), //
				element = elements.getJSONObject(0))

			for (int j = 0; j < limit; element = elements.getJSONObject(j)) {
				if (!(message = (String) element.get("status")).equals("OK")) {
					status = Status.ERROR_REQUESTS_TO_GOOGLE_MAPS_FAIL_TO_BUILD_ROUTE;
					return;
				}

				distanceMatrix[startFromRow + i][startFromCol + j] = element.getJSONObject("distance").getInt("value");
				durationMatrix[startFromRow + i][startFromCol + j] = element.getJSONObject("duration").getInt("value");

				if (++j == matrixSize - startFromCol || j == limit)
					if (++i == matrixSize - startFromRow || i == limit)
						break rows;
					else
						continue rows;
			}
	}

	/**
	 * @return distance and duration matrices in string format</br>
	 *         <b>For debug only</b>
	 */
	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder("\n===== PASSENGERS ============== HUBS ==========\n");
		for (int i = 0; i < drops.length || i < picks.length; i++, sb.append("\n")) {
			if (drops.length > i)
				sb.append(drops[i]);
			if (picks.length > i)
				sb.append("\t").append(picks[i]);
		}

		sb.append("\n=============== DISTANCE MATRIX ===============\n");
		for (int i = 0; i < matrixSize; i++, sb.append("\n"))
			for (int j = 0; j < matrixSize; j++, sb.append("\t"))
				sb.append(distanceMatrix[i][j]);

		sb.append("\n=============== DURATION MATRIX ===============\n");
		for (int i = 0; i < matrixSize; i++, sb.append("\n"))
			for (int j = 0; j < matrixSize; j++, sb.append("\t"))
				sb.append(durationMatrix[i][j]);

		return sb.append("===============================================").toString();
	}

}