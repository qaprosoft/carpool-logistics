package com.asemenkov.carpool.logistics.services.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * @author asemenkov
 * @since Feb 27, 2018
 */
@ResponseStatus(value = HttpStatus.NOT_FOUND, reason = "Cache doesn't contain requested logistics process")
public class LogisticsProcessNotFoundException extends Exception {

	private static final long serialVersionUID = 6216849667997828381L;

}
