package com.example.vat.web;

import com.example.vat.cx.ClientExtension;
import com.example.vat.cx.ClientExtensionRegistry;
import com.example.vat.cx.ClientExtensionServlet;

import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;

import java.nio.charset.StandardCharsets;

import java.util.List;

/**
 * Renders the checkout page and places the client extension on it, the way a
 * page would after someone dropped the widget into two regions.
 */
public class CheckoutServlet extends HttpServlet {

	public CheckoutServlet(ClientExtensionRegistry clientExtensionRegistry) {
		_clientExtensionRegistry = clientExtensionRegistry;
	}

	@Override
	protected void doGet(
			HttpServletRequest httpServletRequest,
			HttpServletResponse httpServletResponse)
		throws IOException {

		ClientExtension clientExtension =
			_clientExtensionRegistry.getClientExtension();

		StringBuilder head = new StringBuilder();

		for (String fileName : _clientExtensionRegistry.resolveCssURLs()) {
			head.append("<link rel=\"stylesheet\" href=\"");
			head.append(ClientExtensionServlet.URI_PREFIX);
			head.append(fileName);
			head.append("\">\n\t");
		}

		List<String> urls = _clientExtensionRegistry.resolveURLs();

		for (String fileName : urls) {
			head.append("<script src=\"");
			head.append(ClientExtensionServlet.URI_PREFIX);
			head.append(fileName);
			head.append("\"");

			if (clientExtension.isUseESM()) {
				head.append(" type=\"module\"");
			}

			head.append("></script>\n\t");
		}

		String html = _readTemplate();

		html = html.replace("${clientExtensionHead}", head.toString());
		html = html.replace(
			"${clientExtensionName}", clientExtension.getName());
		html = html.replace(
			"${htmlElementName}", clientExtension.getHtmlElementName());
		html = html.replace(
			"${instanceable}",
			String.valueOf(clientExtension.isInstanceable()));

		httpServletResponse.setContentType("text/html; charset=UTF-8");

		PrintWriter printWriter = httpServletResponse.getWriter();

		printWriter.write(html);
	}

	private String _readTemplate() throws IOException {
		try (InputStream inputStream =
				CheckoutServlet.class.getResourceAsStream(
					"/templates/checkout.html")) {

			return new String(
				inputStream.readAllBytes(), StandardCharsets.UTF_8);
		}
	}

	private final ClientExtensionRegistry _clientExtensionRegistry;

}
