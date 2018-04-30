package com.asemenkov.carpool.logistics.utils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.URL;
import java.nio.charset.Charset;

import org.json.JSONObject;

import com.asemenkov.carpool.logistics.services.googlemaps.PointsNeighbourship;

/**
 * @author asemenkov
 * @since Feb 5, 2018
 */
public class QuerySender {

	private final String url;
	private final String path;

	public QuerySender(String url, String path) {
		this.url = url;
		this.path = path;
	}

	/**
	 * @param queryParams
	 *            -- parameters must be concatenated and without '?'</br>
	 *            like this: key1=value1&key2=value2&key3=value3
	 */
	public JSONObject sendQuery(String queryParams) {
		InputStream is = null;
		try {
			is = new URL(url + path + '?' + queryParams).openStream();
			PointsNeighbourship.updateLastRequestTime();
			BufferedReader rd = new BufferedReader(new InputStreamReader(is, Charset.forName("UTF-8")));
			String jsonText = readAll(rd);
			JSONObject json = new JSONObject(jsonText);
			return json;
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		} finally {
			try {
				if (is != null)
					is.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	private String readAll(Reader rd) throws IOException {
		StringBuilder sb = new StringBuilder();
		int cp;
		while ((cp = rd.read()) != -1)
			sb.append((char) cp);
		return sb.toString();
	}
}
