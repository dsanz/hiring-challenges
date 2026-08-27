package com.example.host.web;

import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * Serves the client bundle and other static assets from the classpath.
 */
public class StaticServlet extends HttpServlet {

	@Override
	protected void doGet(
			HttpServletRequest httpServletRequest,
			HttpServletResponse httpServletResponse)
		throws IOException {

		String pathInfo = httpServletRequest.getPathInfo();

		if ((pathInfo == null) || pathInfo.contains("..")) {
			httpServletResponse.sendError(HttpServletResponse.SC_NOT_FOUND);

			return;
		}

		try (InputStream inputStream =
				StaticServlet.class.getResourceAsStream("/static" + pathInfo)) {

			if (inputStream == null) {
				httpServletResponse.sendError(HttpServletResponse.SC_NOT_FOUND);

				return;
			}

			httpServletResponse.setContentType(_getContentType(pathInfo));

			OutputStream outputStream = httpServletResponse.getOutputStream();

			inputStream.transferTo(outputStream);
		}
	}

	private String _getContentType(String pathInfo) {
		if (pathInfo.endsWith(".css")) {
			return "text/css";
		}

		if (pathInfo.endsWith(".js")) {
			return "text/javascript";
		}

		if (pathInfo.endsWith(".svg")) {
			return "image/svg+xml";
		}

		return "application/octet-stream";
	}

}
