package com.asemenkov.carpool.logistics.models.dto;

import java.io.Serializable;
import java.util.List;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

/**
 * @author asemenkov
 * @since Feb 14, 2018
 */
@JsonInclude(Include.NON_NULL)
public class RouteDto implements Serializable {

	private static final long serialVersionUID = -8578842638022111301L;

	private Integer length;
	private Integer seats;
	private List<HubLocationDto> hubs;
	private List<UserLocationDto> passengers;

	public Integer getLength() {
		return length;
	}

	public void setLength(Integer length) {
		this.length = length;
	}

	public Integer getSeats() {
		return seats;
	}

	public void setSeats(Integer seats) {
		this.seats = seats;
	}

	public List<HubLocationDto> getHubs() {
		return hubs;
	}

	public void setHubs(List<HubLocationDto> hubs) {
		this.hubs = hubs;
	}

	public List<UserLocationDto> getPassengers() {
		return passengers;
	}

	public void setPassengers(List<UserLocationDto> passengers) {
		this.passengers = passengers;
	}

	@Override
	public String toString() {
		return ToStringBuilder.reflectionToString(this, ToStringStyle.SHORT_PREFIX_STYLE);
	}

}
