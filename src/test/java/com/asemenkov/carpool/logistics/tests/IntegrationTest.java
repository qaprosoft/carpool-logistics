package com.asemenkov.carpool.logistics.tests;

import java.util.stream.LongStream;

import org.testng.Assert;
import org.testng.annotations.Test;
import org.testng.asserts.Assertion;
import org.testng.asserts.SoftAssert;

import com.asemenkov.carpool.logistics.models.dto.LogisticsDto;
import com.asemenkov.carpool.logistics.models.dto.RouteDto;
import com.asemenkov.carpool.logistics.services.enums.State;
import com.asemenkov.carpool.logistics.services.exceptions.LogisticsProcessNotFoundException;
import com.asemenkov.carpool.logistics.utils.CustomLogger;
import com.asemenkov.carpool.logistics.utils.WaitUtil;

/**
 * @author asemenkov
 * @since Feb 18, 2018
 */
@Test
public class IntegrationTest extends AbstractTest {

	@Test
	public void testOnePassenger() throws LogisticsProcessNotFoundException {
		long[] tasks = new long[] { 37L };
		SoftAssert softAssert = new SoftAssert();

		String processId = "TEST-" + System.currentTimeMillis();
		logisticsService.startLogisticsProcessWithId(processId, tasks).getLogisticsDto().getId();
		WaitUtil.pause(10000);

		LogisticsDto dto = logisticsService.getLogisticsProcessById(processId).getLogisticsDto();
		Assert.assertNotNull(dto, "Failed to get LogisticsDto.");
		CustomLogger.log(dto);

		long duration = dto.getEndTime().getTime() - dto.getStartTime().getTime();
		CustomLogger.log("Time taken: " + duration + "ms");

		verifySuccessResponse(softAssert, dto, tasks, 1);
		softAssert.assertAll();
	}

	@Test
	public void testTwoCarsFromTheSameHub() throws LogisticsProcessNotFoundException {
		long[] tasks = new long[] { 37L, 38L, 39L };
		SoftAssert softAssert = new SoftAssert();

		String processId = "TEST-" + System.currentTimeMillis();
		logisticsService.startLogisticsProcessWithId(processId, tasks).getLogisticsDto().getId();
		WaitUtil.pause(10000);

		LogisticsDto dto = logisticsService.getLogisticsProcessById(processId).getLogisticsDto();
		Assert.assertNotNull(dto, "Failed to get LogisticsDto.");
		CustomLogger.log(dto);

		long duration = dto.getEndTime().getTime() - dto.getStartTime().getTime();
		CustomLogger.log("Time taken: " + duration + "ms");

		verifySuccessResponse(softAssert, dto, tasks, 2);
		softAssert.assertAll();
	}

	@Test
	public void testThreeCarsFromTheSameHubInDifferentTime() throws LogisticsProcessNotFoundException {
		long[] tasks = new long[] { 200L, 201L, 202L, 203L, 204L, 205L, 206L, 207L, 208L, 209L };
		SoftAssert softAssert = new SoftAssert();

		String processId = "TEST-" + System.currentTimeMillis();
		logisticsService.startLogisticsProcessWithId(processId, tasks).getLogisticsDto().getId();
		WaitUtil.pause(20000);

		LogisticsDto dto = logisticsService.getLogisticsProcessById(processId).getLogisticsDto();
		Assert.assertNotNull(dto, "Failed to get LogisticsDto.");
		CustomLogger.log(dto);

		long duration = dto.getEndTime().getTime() - dto.getStartTime().getTime();
		CustomLogger.log("Time taken: " + duration + "ms");

		verifySuccessResponse(softAssert, dto, tasks, 3);
		softAssert.assertAll();
	}

	@Test
	public void testErrorAbortedByUser() throws LogisticsProcessNotFoundException {
		long[] tasks = new long[] { 200L, 201L, 202L, 203L, 204L, 205L, 206L, 207L, 208L, 209L };
		SoftAssert softAssert = new SoftAssert();

		String processId = "TEST-" + System.currentTimeMillis();
		logisticsService.startLogisticsProcessWithId(processId, tasks).getLogisticsDto().getId();
		WaitUtil.pause(500);
		logisticsService.getLogisticsProcessById(processId).abortLogisticsProcess();

		LogisticsDto dto = logisticsService.getLogisticsProcessById(processId).getLogisticsDto();
		Assert.assertNotNull(dto, "Failed to get LogisticsDto.");
		CustomLogger.log(dto);

		softAssert.assertNotNull(dto.getStartTime(), "Start Time is empty.");
		softAssert.assertNotNull(dto.getEndTime(), "End Time is empty.");
		softAssert.assertEquals(dto.getId(), processId, "Wrong id.");
		softAssert.assertEquals(dto.getState(), State.ERROR.name(), "Wrong state.");
		softAssert.assertEquals(dto.getCode().intValue(), 50, "Wrong code.");
		softAssert.assertEquals(dto.getMessage(), "The process was aborted by user", "Wrong message.");
		softAssert.assertAll();
	}

	private void verifySuccessResponse(Assertion assertion, LogisticsDto dto, long[] tasks, int carsExpected) {
		assertion.assertEquals(dto.getMessage(), "Calculations were completed without errors", "Wrong message.");
		assertion.assertEquals(dto.getState(), State.SUCCESS.name(), "Wrong state.");
		assertion.assertEquals(dto.getCode().intValue(), 20, "Wrong code.");
		assertion.assertEquals(dto.getHubs().intValue(), 1, "Wrong hubs num.");
		assertion.assertEquals(dto.getPassengers().intValue(), tasks.length, "Wrong passegers num.");
		assertion.assertEquals(dto.getCars().intValue(), carsExpected, "Wrong cars num.");
		assertion.assertEquals(dto.getRoutes().size(), carsExpected, "Wrong royes num.");
		assertion.assertEquals(dto.getRoutes().stream().mapToInt(RouteDto::getSeats).sum(), tasks.length);

		long expectedTaskHash = LongStream.of(tasks).reduce(1L, (a, b) -> a * (31 + b));
		long actualTaskHash = dto.getRoutes().stream() //
				.flatMap(r -> r.getPassengers().stream().map(p -> p.getTask())) //
				.reduce(1L, (a, b) -> a * (31 + b));
		assertion.assertEquals(actualTaskHash, expectedTaskHash, "Wrong tasks in passengers' DTO.");
	}

}
