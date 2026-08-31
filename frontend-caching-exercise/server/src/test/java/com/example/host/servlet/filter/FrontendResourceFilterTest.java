package com.example.host.servlet.filter;

import com.example.host.HostServer;

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
 * Runs against a real server on its own port, because what matters here is the
 * response a browser actually receives.
 *
 * <p>
 * The first three tests are green on a fresh clone. The last three are disabled
 * because the behavior they describe does not exist yet. Enable them, write
 * them, and make them pass.
 * </p>
 */
public class FrontendResourceFilterTest {

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
	 * A resource published under a URI that carries a content hash can be
	 * cached indefinitely, and should say so.
	 */
	@Disabled("T2")
	@Test
	public void testHashedResourceIsImmutable() {
		Assertions.fail("Not implemented");
	}

	@Test
	public void testLabelsModuleIsJavaScript() throws Exception {
		HttpResponse<String> httpResponse = _get(
			"/o/js/language/app-web/all.js");

		Assertions.assertEquals(200, httpResponse.statusCode());
		Assertions.assertTrue(
			httpResponse.body(
			).startsWith(
				"export default"
			));
	}

	/**
	 * A client that already holds an identical copy should be told so, not sent
	 * the bytes again.
	 */
	@Disabled("T3")
	@Test
	public void testMatchingETagReturns304() {
		Assertions.fail("Not implemented");
	}

	@Test
	public void testServesModule() throws Exception {
		HttpResponse<String> httpResponse = _get("/o/js/widget-web/widget.js");

		Assertions.assertEquals(200, httpResponse.statusCode());
		Assertions.assertTrue(
			httpResponse.body(
			).contains(
				"export default function render"
			));

		String contentType = httpResponse.headers(
		).firstValue(
			"Content-Type"
		).orElse(
			""
		);

		Assertions.assertTrue(contentType.contains("javascript"));
	}

	/**
	 * A resource published under its plain name may start returning different
	 * bytes after any deploy, so a client must check back.
	 */
	@Disabled("T2")
	@Test
	public void testUnhashedResourceRevalidates() {
		Assertions.fail("Not implemented");
	}

	@Test
	public void testUnknownModuleIsNotFound() throws Exception {
		HttpResponse<String> httpResponse = _get("/o/js/app-web/nope.js");

		Assertions.assertEquals(404, httpResponse.statusCode());
	}

	private static HttpResponse<String> _get(String path) throws Exception {
		HttpRequest httpRequest = HttpRequest.newBuilder(
			URI.create("http://localhost:" + _PORT + path)
		).GET(
		).build();

		return _httpClient.send(
			httpRequest, HttpResponse.BodyHandlers.ofString());
	}

	private static final int _PORT = 8089;

	private static final HttpClient _httpClient = HttpClient.newHttpClient();

	private static Tomcat _tomcat;

}
