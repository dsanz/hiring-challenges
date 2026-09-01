package com.example.fds.api;

import com.example.fds.HostServer;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

import org.apache.catalina.startup.Tomcat;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

/**
 * Runs against a real server. The first three are green on a fresh clone; the
 * rest describe behavior that does not exist yet.
 */
public class ClaimServletTest {

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

	@Test
	public void testApproveLargeClaimIsRefused() throws Exception {
		HttpResponse<String> httpResponse = _patch("CLM-1003", "Approved");

		Assertions.assertEquals(422, httpResponse.statusCode());
		Assertions.assertTrue(
			httpResponse.body(
			).contains(
				"SECOND_APPROVER_REQUIRED"
			));
	}

	@Test
	public void testApproveSmallClaim() throws Exception {
		HttpResponse<String> httpResponse = _patch("CLM-1001", "Approved");

		Assertions.assertTrue(httpResponse.statusCode() < 300);
	}

	/**
	 * Approving a claim that somebody else already rejected is not something
	 * the server should quietly do.
	 */
	@Disabled("T1")
	@Test
	public void testApprovingAnAlreadyDecidedClaimIsRefused() {
		Assertions.fail("Not implemented");
	}

	/**
	 * Two people looking at the same table, both pressing a button. The second
	 * one to arrive is acting on what the table showed before the first one
	 * landed.
	 */
	@Disabled("T1")
	@Test
	public void testStaleUpdateIsDetected() {
		Assertions.fail("Not implemented");
	}

	@Test
	public void testUnknownClaim() throws Exception {
		HttpResponse<String> httpResponse = _patch("CLM-9999", "Approved");

		Assertions.assertEquals(404, httpResponse.statusCode());
	}

	private static HttpResponse<String> _patch(String id, String status)
		throws Exception {

		HttpRequest httpRequest = HttpRequest.newBuilder(
			URI.create("http://localhost:" + _PORT + "/o/c/claims/" + id)
		).header(
			"Content-Type", "application/json"
		).method(
			"PATCH",
			HttpRequest.BodyPublishers.ofString(
				"{\"status\":\"" + status + "\"}")
		).build();

		return _httpClient.send(
			httpRequest, HttpResponse.BodyHandlers.ofString());
	}

	private static final int _PORT = 8098;

	private static final HttpClient _httpClient = HttpClient.newHttpClient();

	private static Tomcat _tomcat;

}
