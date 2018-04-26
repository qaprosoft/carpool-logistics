package com.asemenkov.carpool.logistics.services.googlemaps;

import java.util.Arrays;

import com.asemenkov.carpool.logistics.utils.QuerySender;

/**
 * @author asemenkov
 * @since Feb 5, 2018
 */
public class DistanceMatrixRequest {

	private String units;
	private String key;
	private GoogleMapsPoint[] origins;
	private GoogleMapsPoint[] destinations;

	public String getUnits() {
		return units;
	}

	public void setUnits(String units) {
		this.units = units;
	}

	public String getKey() {
		return key;
	}

	public void setKey(String key) {
		this.key = key;
	}

	public GoogleMapsPoint[] getOrigins() {
		return origins;
	}

	public void setOrigins(GoogleMapsPoint[] origins) {
		this.origins = origins;
	}

	public GoogleMapsPoint[] getDestinations() {
		return destinations;
	}

	public void setDestinations(GoogleMapsPoint[] destinations) {
		this.destinations = destinations;
	}

	/**
	 * Puts GoogleMapsPoint into origins array at specified index
	 * 
	 * @param index
	 *            -- can be >= array length, the array will be extended
	 */
	public void addOriginPoint(GoogleMapsPoint point, int index) {
		if (index >= origins.length) {
			GoogleMapsPoint[] newOrigins = new GoogleMapsPoint[index + 1];
			System.arraycopy(origins, 0, newOrigins, 0, origins.length);
			origins = newOrigins;
		}
		origins[index] = point;
	}

	/**
	 * Puts GoogleMapsPoint into destinations array at specified index
	 * 
	 * @param index
	 *            -- can be >= array length, the array will be extended
	 */
	public void addDestinationsPoint(GoogleMapsPoint point, int index) {
		if (index >= destinations.length) {
			GoogleMapsPoint[] newDestinations = new GoogleMapsPoint[index + 1];
			System.arraycopy(destinations, 0, newDestinations, 0, destinations.length);
			destinations = newDestinations;
		}
		destinations[index] = point;
	}

	/**
	 * Creates new Origins GoogleMapsPoint array of specified size
	 */
	public void allocOriginsArray(int size) {
		origins = new GoogleMapsPoint[size];
	}

	/**
	 * Creates new Destinations GoogleMapsPoint array of specified size
	 */
	public void allocDestinationsArray(int size) {
		destinations = new GoogleMapsPoint[size];
	}

	/**
	 * Unlinks objects from origins and destinations arrays
	 */
	public void freeAllArrays() {
		Arrays.fill(destinations, null);
		Arrays.fill(origins, null);
	}

	/**
	 * @return Query String which can be passed to {@link QuerySender}
	 */
	public String toQueryString() {
		StringBuilder sb = new StringBuilder();
		sb.append("key=").append(key);

		sb.append("&origins=");
		Arrays.stream(origins) //
				.filter(origin -> origin != null).forEach(origin -> sb //
						.append(origin.getLatitude()).append(',') //
						.append(origin.getLongtitude()).append("|"));
		sb.deleteCharAt(sb.length() - 1);

		sb.append("&destinations=");
		Arrays.stream(destinations) //
				.filter(destination -> destination != null).forEach(destination -> sb //
						.append(destination.getLatitude()).append(',') //
						.append(destination.getLongtitude()).append("|"));
		sb.deleteCharAt(sb.length() - 1);

		if (units != null)
			sb.append("&units=").append(units);

		return sb.toString();
	}

}
