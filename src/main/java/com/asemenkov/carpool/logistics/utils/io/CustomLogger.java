package com.asemenkov.carpool.logistics.utils.io;

import java.time.LocalTime;

/**
 * @author asemenkov
 * @since Feb 4, 2018
 */
public class CustomLogger {

	public static void log(Object message) {
		log(message.toString(), "INFO");
	}

	public static void warn(Object message) {
		log(message.toString(), "WARNING");
	}

	public static void error(Object message) {
		log(message.toString(), "ERROR");
	}

	private static void log(String message, String infoOrWarning) {
		Exception ex = new Exception();
		System.out.println(new StringBuilder(LocalTime.now().toString())
				.append(" [" + Thread.currentThread().getName() + "] [" + infoOrWarning + "] ")
				.append(ex.getStackTrace()[2].getClassName().replaceAll("\\w+\\.", ""))
				.append(":" + ex.getStackTrace()[2].getLineNumber() + " - ") //
				.append(message));
	}
}
