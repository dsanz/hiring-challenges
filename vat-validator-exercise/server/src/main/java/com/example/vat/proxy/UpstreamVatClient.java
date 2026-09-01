package com.example.vat.proxy;

import java.net.URI;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

import java.nio.charset.StandardCharsets;

/**
 * Talks to the external VAT registry.
 *
 * <p>
 * TODO (T2): this call is unbounded. No connect timeout, no read timeout, and
 * nothing that gives up. The registry is a third party and it will not always
 * answer.
 * </p>
 */
public class UpstreamVatClient {

	public UpstreamVatClient(String baseURL, String apiKey) {
		_baseURL = baseURL;
		_apiKey = apiKey;
	}

	public HttpResponse<String> lookup(String vatId) throws Exception {
		HttpRequest httpRequest = HttpRequest.newBuilder(
			URI.create(
				_baseURL + "/vat/check?vatId=" +
					URLEncoder.encode(vatId, StandardCharsets.UTF_8))
		).header(
			"X-Api-Key", _apiKey
		).GET(
		).build();

		return _httpClient.send(
			httpRequest, HttpResponse.BodyHandlers.ofString());
	}

	private final String _apiKey;
	private final String _baseURL;
	private final HttpClient _httpClient = HttpClient.newHttpClient();

}
