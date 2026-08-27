package com.example.host.extension;

import com.fasterxml.jackson.databind.ObjectMapper;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

/**
 * Retrieves a client extension descriptor over HTTP.
 *
 * <p>
 * As shipped this makes an unbounded request: no connect timeout, no read
 * timeout, no cap on the response body, and any failure propagates to whoever
 * called it.
 * </p>
 */
public class DescriptorFetcher {

	public ClientExtension fetch(String descriptorURL) throws Exception {
		HttpRequest httpRequest = HttpRequest.newBuilder(
			URI.create(descriptorURL)
		).GET(
		).build();

		HttpResponse<String> httpResponse = _httpClient.send(
			httpRequest, HttpResponse.BodyHandlers.ofString());

		if (httpResponse.statusCode() != 200) {
			throw new IllegalStateException(
				"Descriptor " + descriptorURL + " returned " +
					httpResponse.statusCode());
		}

		return _objectMapper.readValue(
			httpResponse.body(), ClientExtension.class);
	}

	private final HttpClient _httpClient = HttpClient.newHttpClient();
	private final ObjectMapper _objectMapper = new ObjectMapper();

}
