package com.asemenkov.carpool.logistics.services.logistics;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import com.asemenkov.carpool.logistics.services.exceptions.LogisticsProcessNotFoundException;
import com.asemenkov.carpool.logistics.utils.Factories.DuoFactory;

/**
 * @author asemenkov
 * @since Feb 14, 2018
 */
@Service
@CacheConfig(cacheNames = "logistics")
public class LogisticsService {

	@Autowired
	private DuoFactory<String, long[], LogisticsProcess> logisticsProcessFactory;

	@Cacheable(key = "#id")
	public LogisticsProcess startLogisticsProcessWithId(String id, long[] tasks) {
		LogisticsProcess logisticsProcess = logisticsProcessFactory.get(id, tasks);
		logisticsProcess.startLogisticsProcess();
		return logisticsProcess;
	}

	@Cacheable(key = "#id")
	public LogisticsProcess getLogisticsProcessById(String id) throws LogisticsProcessNotFoundException {
		throw new LogisticsProcessNotFoundException();
	}
}
