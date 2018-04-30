package com.asemenkov.carpool.logistics.tests;

import java.text.ParseException;
import java.util.List;

import org.testng.Assert;
import org.testng.annotations.Test;

import com.asemenkov.carpool.logistics.models.db.Task;
import com.asemenkov.carpool.logistics.utils.CustomLogger;

/**
 * @author asemenkov
 * @since Feb 17, 2018
 */
@Test
public class RepositoryTest extends AbstractTest {

	@Test
	public void testJpaTaskRepository() throws ParseException {
		Assert.assertTrue(taskRepository.count() > 0, "Either 'TASKS' table is empty or something went wrong.");

		int realTasksSize = 12;
		long realTaskId = 57L;
		String realHubName = "Eshak";
		Task task = taskRepository.getOne(realTaskId);
		Assert.assertEquals(task.getHub().getName(), realHubName, "Wrong association with 'HUBS' table.");

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
