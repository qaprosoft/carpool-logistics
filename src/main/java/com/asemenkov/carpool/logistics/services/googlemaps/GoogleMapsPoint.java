package com.asemenkov.carpool.logistics.services.googlemaps;

import java.util.Comparator;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author asemenkov
 * @since Feb 5, 2018
 */
public class GoogleMapsPoint {

	public static final Comparator<GoogleMapsPoint> CMP_BY_INDEX = (p1, p2) -> p1.index - p2.index;
	private static final Pattern PATTERN = Pattern.compile("\\d+.\\d+");

	private final double latitude;
	private final double longtitude;

	private String dbRepresentation;
	private int index;

	public GoogleMapsPoint(double latitude, double longtitude) {
		this.latitude = latitude;
		this.longtitude = longtitude;
	}

	/**
	 * @param dbRepresentation
	 *            -- String representation of MySQL Point - POINT(1.123,0.789)
	 */
	public GoogleMapsPoint(String dbRepresentation) {
		this.dbRepresentation = dbRepresentation;
		Matcher matcher = PATTERN.matcher(dbRepresentation);
		matcher.find();
		latitude = Double.valueOf(matcher.group(0));
		matcher.find();
		longtitude = Double.valueOf(matcher.group(0));
	}

	public double getLatitude() {
		return latitude;
	}

	public double getLongtitude() {
		return longtitude;
	}

	public String getDbRepresentation() {
		return dbRepresentation;
	}

	public int getIndex() {
		return index;
	}

	public void setIndex(int index) {
		this.index = index;
	}

	@Override
	public String toString() {
		return latitude + ", " + longtitude;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		long temp;
		temp = Double.doubleToLongBits(latitude);
		result = prime * result + (int) (temp ^ (temp >>> 32));
		temp = Double.doubleToLongBits(longtitude);
		result = prime * result + (int) (temp ^ (temp >>> 32));
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (!(obj instanceof GoogleMapsPoint))
			return false;
		GoogleMapsPoint other = (GoogleMapsPoint) obj;
		if (Double.doubleToLongBits(latitude) != Double.doubleToLongBits(other.latitude))
			return false;
		if (Double.doubleToLongBits(longtitude) != Double.doubleToLongBits(other.longtitude))
			return false;
		return true;
	}

}
