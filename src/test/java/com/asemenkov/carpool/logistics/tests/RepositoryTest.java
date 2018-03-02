package com.asemenkov.carpool.logistics.tests;

import java.text.ParseException;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.testng.AbstractTestNGSpringContextTests;
import org.springframework.transaction.annotation.Transactional;
import org.testng.Assert;
import org.testng.annotations.Test;

import com.asemenkov.carpool.logistics.config.JpaConfiguration;
import com.asemenkov.carpool.logistics.models.db.Task;
import com.asemenkov.carpool.logistics.repositories.TaskRepository;
import com.asemenkov.carpool.logistics.utils.io.CustomLogger;

/**
 * @author asemenkov
 * @since Feb 17, 2018
 */
@Test
@Transactional(readOnly = true)
@ContextConfiguration(classes = JpaConfiguration.class)
public class RepositoryTest extends AbstractTestNGSpringContextTests {

	@Autowired
	private TaskRepository taskRepository;

	@Test
	public void testJpaTaskRepository() throws ParseException {
		Assert.assertTrue(taskRepository.count() > 0, "Either 'TASKS' table is empty or something went wrong.");

		long realTaskId = 57L;
		String realHubName = "Eshak";
		String realUserName = "Евгений";
		Task task = taskRepository.getOne(realTaskId);
		Assert.assertEquals(task.getHub().getName(), realHubName, "Wrong association with 'HUBS' table.");
		Assert.assertEquals(task.getUser().getFirstName(), realUserName, "Wrong association with 'USERS' table.");

		int realTasksSize = 12;

		long time1 = System.currentTimeMillis();
		List<Task> tasks = taskRepository.findByIdIn(37L, 38L, 39L, 41L, 42L, 43L, 45L, 46L, 47L, 48L, 49L, 50L);
		tasks.stream().forEach(CustomLogger::log);
		long time2 = System.currentTimeMillis();

		CustomLogger.log("Total number of tasks: " + tasks.size());
		CustomLogger.log("Time taken: " + (time2 - time1) + "ms");

		Assert.assertEquals(tasks.size(), realTasksSize, "Wrong number of tasks.");
		Assert.assertTrue(time2 - time1 < 5000, "Entities fething was lasting for too long.");
	}

}
