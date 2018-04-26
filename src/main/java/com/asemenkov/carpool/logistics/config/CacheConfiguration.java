package com.asemenkov.carpool.logistics.config;

import java.util.Arrays;
import java.util.concurrent.TimeUnit;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.guava.GuavaCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

import com.google.common.cache.CacheBuilder;

/**
 * @author asemenkov
 * @since Feb 24, 2018
 */
@Configuration
@EnableCaching
@PropertySource("classpath:aplication.properties")
public class CacheConfiguration {

	private @Value("${cache.expiration_time_hours}") int expireAfterWrite;
	private @Value("${cache.maximum_size}") int maximumSize;

	@Bean
	public CacheManager cacheManager() {

		GuavaCacheManager cacheManager = new GuavaCacheManager();
		cacheManager.setCacheBuilder(CacheBuilder.newBuilder() //
				.expireAfterWrite(expireAfterWrite, TimeUnit.HOURS) //
				.maximumSize(maximumSize));
		cacheManager.setCacheNames(Arrays.asList("logistics"));
		return cacheManager;
	}

}
