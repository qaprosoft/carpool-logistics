package com.asemenkov.carpool.logistics.utils;

import java.time.LocalTime;

import com.asemenkov.carpool.logistics.services.algorithms.LittlesAlgorithm;

/**
 * @author asemenkov
 * @since Feb 4, 2018
 */
public class CustomLogger {

	public static void log(Object message) {
		log(message.toString(), "INFO");
	}

	public static void log(int[][] matrix) {
		log(matrixToString(matrix), "INFO");
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

	private static String matrixToString(int[][] matrix) {
		StringBuilder sb = new StringBuilder("\n==================== MATRIX ====================\n");
		for (int i = 0; i < matrix.length; i++, sb.append("\n"))
			for (int j = 0; j < matrix[0].length; j++, sb.append("\t"))
				if (matrix[i][j] > LittlesAlgorithm.TENDS_TO_INF1)
					sb.append("INF1");
				else if (matrix[i][j] > LittlesAlgorithm.TENDS_TO_INF2)
					sb.append("INF2");
				else if (matrix[i][j] > LittlesAlgorithm.TENDS_TO_INF3)
					sb.append("BIG");
				else
					sb.append(matrix[i][j]);
		return sb.append("================================================").toString();
	}
}
