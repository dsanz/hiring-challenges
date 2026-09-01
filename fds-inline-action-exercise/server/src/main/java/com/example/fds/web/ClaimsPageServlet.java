package com.example.fds.web;

import com.example.fds.cx.ClientExtensionRegistry;
import com.example.fds.cx.ClientExtensionServlet;

import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;

import java.nio.charset.StandardCharsets;

import java.util.List;

/**
 * Renders the claims page and points the data set at the cell renderer client
 * extension.
 */
public class ClaimsPageServlet extends HttpServlet {

	public ClaimsPageServlet(ClientExtensionRegistry clientExtensionRegistry) {
		_clientExtensionRegistry = clientExtensionRegistry;
	}

	@Override
	protected void doGet(
			HttpServletRequest httpServletRequest,
			HttpServletResponse httpServletResponse)
		throws IOException {

		List<String> urls = _clientExtensionRegistry.resolveURLs();

		String html = _readTemplate();

		html = html.replace(
			"${cellRendererName}",
			_clientExtensionRegistry.getClientExtension(
			).getName());
		html = html.replace(
			"${cellRendererURL}",
			ClientExtensionServlet.URI_PREFIX + urls.get(0));

		httpServletResponse.setContentType("text/html; charset=UTF-8");

		PrintWriter printWriter = httpServletResponse.getWriter();

		printWriter.write(html);
	}

	private String _readTemplate() throws IOException {
		try (InputStream inputStream =
				ClaimsPageServlet.class.getResourceAsStream(
					"/templates/claims.html")) {

			return new String(
				inputStream.readAllBytes(), StandardCharsets.UTF_8);
		}
	}

	private final ClientExtensionRegistry _clientExtensionRegistry;

}
