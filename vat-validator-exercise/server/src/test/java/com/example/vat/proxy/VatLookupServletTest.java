package com.example.vat.proxy;

import com.example.vat.HostServer;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

import java.time.Duration;

import org.apache.catalina.startup.Tomcat;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

/**
 * Runs against a real server, because the thing under test is a response a
 * browser receives.
 *
 * <p>
 * The first two are green on a fresh clone. The rest describe behavior that
 * does not exist yet.
 * </p>
 */
public class VatLookupServletTest {

	@BeforeAll
	public static void setUpClass() throws Exception {
		_tomcat = HostServer.start(_PORT);
	}

	@AfterAll
	public static void tearDownClass() throws Exception {
		if (_tomcat != null) {
			_tomcat.stop();
			_tomcat.destroy();
		}
	}

	/**
	 * Asking twice for the same number must not cost two lookups against a
	 * registry that allows ten a minute.
	 */
	@Disabled("T3")
	@Test
	public void testRepeatedLookupHitsUpstreamOnce() {
		Assertions.fail("Not implemented");
	}

	@Test
	public void testRegisteredNumber() throws Exception {
		HttpResponse<String> httpResponse = _get("ESB12345678");

		Assertions.assertEquals(200, httpResponse.statusCode());
		Assertions.assertTrue(
			httpResponse.body(
			).contains(
				"\"valid\":true"
			));
	}

	/**
	 * A number the registry could not check is not an invalid number, and the
	 * browser has to be able to tell the two apart without parsing somebody
	 * else's error format.
	 */
	@Disabled("T1")
	@Test
	public void testUncheckableNumberIsNotReportedAsInvalid() {
		Assertions.fail("Not implemented");
	}

	@Test
	public void testUnregisteredNumber() throws Exception {
		HttpResponse<String> httpResponse = _get("ESB00000000");

		Assertions.assertEquals(200, httpResponse.statusCode());
		Assertions.assertTrue(
			httpResponse.body(
			).contains(
				"\"valid\":false"
			));
	}

	/**
	 * A registry that stops answering must not become a request that never
	 * ends.
	 */
	@Disabled("T2")
	@Test
	public void testUpstreamStallIsBounded() {
		Assertions.fail("Not implemented");
	}

	private static HttpResponse<String> _get(String vatId) throws Exception {
		HttpRequest httpRequest = HttpRequest.newBuilder(
			URI.create(
				"http://localhost:" + _PORT + "/o/vat/lookup?vatId=" + vatId)
		).timeout(
			Duration.ofSeconds(30)
		).GET(
		).build();

		return _httpClient.send(
			httpRequest, HttpResponse.BodyHandlers.ofString());
	}

	private static final int _PORT = 8099;

	private static final HttpClient _httpClient = HttpClient.newHttpClient();

	private static Tomcat _tomcat;

}
