package com.asemenkov.carpool.logistics.controllers;

import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.asemenkov.carpool.logistics.models.dto.LogisticsDto;
import com.asemenkov.carpool.logistics.services.exceptions.LogisticsProcessNotFoundException;
import com.asemenkov.carpool.logistics.services.logistics.LogisticsService;

import io.swagger.annotations.ApiOperation;

/**
 * @author asemenkov
 * @since Feb 25, 2018
 */
@CrossOrigin
@RestController
@RequestMapping("/logistics")
public class LogisticsWebService {

	@Autowired
	private LogisticsService logisticsService;

	@ResponseStatus(HttpStatus.OK)
	@RequestMapping(value = "/process", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	@ApiOperation(value = "Create Logistics Process")
	public @ResponseBody LogisticsDto createLogisticsProcess(@RequestParam("tasks") long[] tasks) {
		String id = UUID.randomUUID().toString();
		return logisticsService.startLogisticsProcessWithId(id, tasks).getLogisticsDto();
	}

	@ResponseStatus(HttpStatus.OK)
	@RequestMapping(value = "/result", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	@ApiOperation(value = "Get Result of Logistics Process")
	public @ResponseBody LogisticsDto getResultOfLogisticsProcess(@RequestParam("id") String id)
			throws LogisticsProcessNotFoundException {
		return logisticsService.getLogisticsProcessById(id).getLogisticsDto();
	}

	@ResponseStatus(HttpStatus.OK)
	@RequestMapping(value = "/abort", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	@ApiOperation(value = "Abort Logistics Process")
	public void abortLogisticsProcess(@RequestParam("id") String id) throws LogisticsProcessNotFoundException {
		logisticsService.getLogisticsProcessById(id).abortLogisticsProcess();
	}

}
