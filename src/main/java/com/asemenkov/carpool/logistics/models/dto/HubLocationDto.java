package com.asemenkov.carpool.logistics.models.dto;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

/**
 * @author asemenkov
 * @since Feb 23, 2018
 */
@JsonInclude(Include.NON_NULL)
@JsonPropertyOrder({ "id", "name", "color", "address", "lat", "lon", "time" })
public class HubLocationDto extends LocationDto {

	private static final long serialVersionUID = 6276170157320864956L;
	private String color;

	public String getColor() {
		return color;
	}

	public void setColor(String color) {
		this.color = color;
	}

	@Override
	public String toString() {
		return ToStringBuilder.reflectionToString(this, ToStringStyle.SHORT_PREFIX_STYLE);
	}

}
