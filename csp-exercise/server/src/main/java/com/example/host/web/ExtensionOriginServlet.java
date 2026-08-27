package com.example.host.web;

import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * Stands in for the third-party origins that publish client extensions. Runs on
 * its own port so that everything it serves is genuinely cross-origin.
 */
public class ExtensionOriginServlet extends HttpServlet {

	/**
	 * How long the <code>slow-widget</code> descriptor takes to respond. This is
	 * deliberate. One of the three extensions placed on the dashboard has an
	 * origin that is not healthy.
	 */
	public static final long SLOW_WIDGET_DELAY = 8000;

	@Override
	protected void doGet(
			HttpServletRequest httpServletRequest,
			HttpServletResponse httpServletResponse)
		throws IOException {

		String pathInfo = httpServletRequest.getRequestURI();

		if (pathInfo.contains("..")) {
			httpServletResponse.sendError(HttpServletResponse.SC_NOT_FOUND);

			return;
		}

		if (pathInfo.equals("/slow-widget/descriptor.json")) {
			try {
				Thread.sleep(SLOW_WIDGET_DELAY);
			}
			catch (InterruptedException interruptedException) {
				Thread.currentThread().interrupt();
			}
		}

		try (InputStream inputStream =
				ExtensionOriginServlet.class.getResourceAsStream(
					"/origin" + pathInfo)) {

			if (inputStream == null) {
				httpServletResponse.sendError(HttpServletResponse.SC_NOT_FOUND);

				return;
			}

			httpServletResponse.setHeader("Access-Control-Allow-Origin", "*");
			httpServletResponse.setContentType(_getContentType(pathInfo));

			OutputStream outputStream = httpServletResponse.getOutputStream();

			inputStream.transferTo(outputStream);
		}
	}

	private String _getContentType(String pathInfo) {
		if (pathInfo.endsWith(".html")) {
			return "text/html; charset=UTF-8";
		}

		if (pathInfo.endsWith(".js")) {
			return "text/javascript";
		}

		if (pathInfo.endsWith(".json")) {
			return "application/json";
		}

		if (pathInfo.endsWith(".svg")) {
			return "image/svg+xml";
		}

		return "application/octet-stream";
	}

}
