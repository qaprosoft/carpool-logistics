package com.asemenkov.carpool.logistics.tests;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.testng.AbstractTestNGSpringContextTests;

import com.asemenkov.carpool.logistics.config.ApplicationConfiguration;
import com.asemenkov.carpool.logistics.config.CacheConfiguration;
import com.asemenkov.carpool.logistics.config.GoogleMapsConfiguration;
import com.asemenkov.carpool.logistics.config.JpaConfiguration;
import com.asemenkov.carpool.logistics.repositories.TaskRepository;
import com.asemenkov.carpool.logistics.services.algorithms.Car;
import com.asemenkov.carpool.logistics.services.algorithms.KernighanLinAlgorithm;
import com.asemenkov.carpool.logistics.services.algorithms.LittlesAlgorithm;
import com.asemenkov.carpool.logistics.services.googlemaps.GoogleMapsPoint;
import com.asemenkov.carpool.logistics.services.googlemaps.PointsNeighbourship;
import com.asemenkov.carpool.logistics.services.logistics.LogisticsService;
import com.asemenkov.carpool.logistics.utils.Factories.DuoFactory;
import com.asemenkov.carpool.logistics.utils.Factories.MonoFactory;

/**
 * @author asemenkov
 * @since Apr 25, 2018
 */
@ContextConfiguration(classes = { //
		ApplicationConfiguration.class, //
		GoogleMapsConfiguration.class, //
		JpaConfiguration.class, //
		CacheConfiguration.class })
public abstract class AbstractTest extends AbstractTestNGSpringContextTests {

	protected @Autowired TaskRepository taskRepository;

	protected @Autowired LittlesAlgorithm littlesAlgorithm;

	protected @Autowired LogisticsService logisticsService;

	protected @Autowired DuoFactory<Integer, int[][], Car> carFactory;

	protected @Autowired MonoFactory<Car[], KernighanLinAlgorithm<Car>> kernighanLinForCarsFactory;

	protected @Autowired DuoFactory<GoogleMapsPoint[], GoogleMapsPoint[], PointsNeighbourship> pointsNeighbourshipFactory;

}
