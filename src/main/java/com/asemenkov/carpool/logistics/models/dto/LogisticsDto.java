package com.asemenkov.carpool.logistics.models.dto;

import java.io.Serializable;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import com.asemenkov.carpool.logistics.services.enums.State;
import com.asemenkov.carpool.logistics.services.enums.Status;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

/**
 * @author asemenkov
 * @since Feb 14, 2018
 */
@JsonInclude(Include.NON_NULL)
public class LogisticsDto implements Serializable {

	private static final long serialVersionUID = -5270959170831504869L;

	private String id;
	private String state;
	private Integer code;
	private String message;
	private @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss", timezone = "Europe/Kiev") Date startTime;
	private @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss", timezone = "Europe/Kiev") Date endTime;

	private String optimization;
	private Integer hubs;
	private Integer passengers;
	private Integer cars;
	private Integer length;
	private List<RouteDto> routes;

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getState() {
		return state;
	}

	public void setState(String state) {
		this.state = state;
	}

	public void setState(State state) {
		this.state = state.name();
	}

	public String getMessage() {
		return message;
	}

	public Integer getCode() {
		return code;
	}

	public void setStatus(Status status) {
		this.code = status.getCode();
		this.message = status.getMessage();
	}

	public void appendMessage(String... messages) {
		message += Arrays.stream(messages).collect(Collectors.joining(",", " - ", ""));
	}

	public Date getStartTime() {
		return startTime;
	}

	public void setStartTime(Date startTime) {
		this.startTime = startTime;
	}

	public Date getEndTime() {
		return endTime;
	}

	public void setEndTime(Date endTime) {
		this.endTime = endTime;
	}

	public String getOptimization() {
		return optimization;
	}

	public void setOptimization(String optimization) {
		this.optimization = optimization;
	}

	public Integer getLength() {
		return length;
	}

	public void setLength(Integer length) {
		this.length = length;
	}

	public Integer getCars() {
		return cars;
	}

	public void setCars(Integer cars) {
		this.cars = cars;
	}

	public Integer getHubs() {
		return hubs;
	}

	public void setHubs(Integer hubs) {
		this.hubs = hubs;
	}

	public Integer getPassengers() {
		return passengers;
	}

	public void setPassengers(Integer passengers) {
		this.passengers = passengers;
	}

	public List<RouteDto> getRoutes() {
		return routes;
	}

	public void setRoutes(List<RouteDto> routes) {
		this.routes = routes;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public void setCode(Integer code) {
		this.code = code;
	}

	@Override
	public String toString() {
		return ToStringBuilder.reflectionToString(this, ToStringStyle.SHORT_PREFIX_STYLE);
	}

}
