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
@JsonPropertyOrder({ "task", "id", "name", "phone", "address", "lat", "lon", "time" })
public class UserLocationDto extends LocationDto {

	private static final long serialVersionUID = 1805676244509849402L;

	private Long task;
	private String phone;

	public Long getTask() {
		return task;
	}

	public void setTask(Long task) {
		this.task = task;
	}

	public String getPhone() {
		return phone;
	}

	public void setPhone(String phone) {
		this.phone = phone;
	}

	@Override
	public String toString() {
		return ToStringBuilder.reflectionToString(this, ToStringStyle.SHORT_PREFIX_STYLE);
	}

}
