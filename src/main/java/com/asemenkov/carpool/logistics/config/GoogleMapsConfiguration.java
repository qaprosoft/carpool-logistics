package com.asemenkov.carpool.logistics.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.context.annotation.Scope;

import com.asemenkov.carpool.logistics.services.googlemaps.DistanceMatrixRequest;
import com.asemenkov.carpool.logistics.services.googlemaps.GoogleMapsPoint;
import com.asemenkov.carpool.logistics.services.googlemaps.PointsNeighbourship;
import com.asemenkov.carpool.logistics.utils.Factories.DuoFactory;
import com.asemenkov.carpool.logistics.utils.io.QuerySender;

/**
 * @author asemenkov
 * @since Feb 7, 2018
 */
@Configuration
@PropertySource("classpath:googlemaps.properties")
public class GoogleMapsConfiguration {

	@Value("${googlemaps.key}")
	private String key;

	@Value("${googlemaps.url}")
	private String url;

	@Value("${googlemaps.path}")
	private String path;

	@Value("${googlemaps.units}")
	private String units;

	@Value("${googlemaps.limit}")
	private int limit;

	@Value("${googlemaps.interval}")
	private int interval;

	@Bean
	public QuerySender googleMapsQuerySender() {
		return new QuerySender(url, path);
	}

	@Bean
	public DuoFactory<GoogleMapsPoint[], GoogleMapsPoint[], PointsNeighbourship> pointsNeighbourshipFactory() {
		return this::getPointsNeighbourship;
	}

	@Bean
	@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
	protected PointsNeighbourship getPointsNeighbourship(GoogleMapsPoint[] picks, GoogleMapsPoint[] drops) {
		return new PointsNeighbourship(picks, drops);
	}

	@Bean
	@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
	public DistanceMatrixRequest distanceMatrixRequest() {
		DistanceMatrixRequest distanceMatrixRequest = new DistanceMatrixRequest();
		distanceMatrixRequest.setKey(key);
		distanceMatrixRequest.setUnits(units);
		distanceMatrixRequest.allocDestinationsArray(limit);
		distanceMatrixRequest.allocOriginsArray(limit);
		return distanceMatrixRequest;
	}
}
