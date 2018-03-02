package com.asemenkov.carpool.logistics.services.enums;

import java.util.EnumSet;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

/**
 * @author asemenkov
 * @since Feb 18, 2018
 */
public enum Status {

	RUNNING_INITIALIZATION, //
	RUNNING_FETCH_DATABASE, //
	RUNNING_REQUESTS_TO_GOOGLE_MAPS, //
	RUNNING_KERNIGHAN_LIN_ALGORITHM,

	ERROR_ABORTED_BY_USER, //
	ERROR_FETCH_DATABASE_SQL_EXCEPTION, //
	ERROR_REQUESTS_TO_GOOGLE_MAPS_NO_RESPONSE, //
	ERROR_REQUESTS_TO_GOOGLE_MAPS_ENCOUNTER_PROBLEM, //
	ERROR_REQUESTS_TO_GOOGLE_MAPS_FAIL_TO_BUILD_ROUTE, //
	ERROR_KERNIGHAN_LIN_ALGORITHM_BIG_TASK, //
	ERROR_KERNIGHAN_LIN_ALGORITHM_BECOMING_BIGGER, //
	ERROR_KERNIGHAN_LIN_ALGORITHM_EXCEPTION, //

	SUCCESS;

	@Component
	@PropertySource("classpath:messages.properties")
	public static class CodeInjector {

		@Autowired
		private Environment environment;

		@PostConstruct
		public void postConstruct() {
			EnumSet.allOf(Status.class).stream() //
					.peek(code -> code.code = environment.getProperty(code.name() + "_CODE", Integer.class))
					.forEach(code -> code.message = environment.getProperty(code.name() + "_MESSAGE"));
		}
	}

	private String message;
	private Integer code;

	public String getMessage() {
		return message;
	}

	public Integer getCode() {
		return code;
	}
}
