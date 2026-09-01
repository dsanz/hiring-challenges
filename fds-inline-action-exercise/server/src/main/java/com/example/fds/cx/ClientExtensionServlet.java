package com.example.fds.cx;

import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.io.OutputStream;

/**
 * Serves the assembled client extension files, the way the platform serves them
 * from a deployed extension's static directory.
 */
public class ClientExtensionServlet extends HttpServlet {

	public static final String URI_PREFIX = "/o/cx/";

	public ClientExtensionServlet(
		ClientExtensionRegistry clientExtensionRegistry) {

		_clientExtensionRegistry = clientExtensionRegistry;
	}

	@Override
	protected void doGet(
			HttpServletRequest httpServletRequest,
			HttpServletResponse httpServletResponse)
		throws IOException {

		String pathInfo = httpServletRequest.getPathInfo();

		if (pathInfo == null) {
			httpServletResponse.sendError(HttpServletResponse.SC_NOT_FOUND);

			return;
		}

		byte[] content = _clientExtensionRegistry.getStaticResource(
			pathInfo.substring(1));

		if (content == null) {
			httpServletResponse.sendError(HttpServletResponse.SC_NOT_FOUND);

			return;
		}

		if (pathInfo.endsWith(".css")) {
			httpServletResponse.setContentType("text/css");
		}
		else {
			httpServletResponse.setContentType("text/javascript");
		}

		OutputStream outputStream = httpServletResponse.getOutputStream();

		outputStream.write(content);
	}

	private final ClientExtensionRegistry _clientExtensionRegistry;

}
