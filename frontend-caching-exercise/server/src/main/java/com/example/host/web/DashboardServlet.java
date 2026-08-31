package com.example.host.web;

import com.example.host.configuration.FrontendCachingConfiguration;
import com.example.host.configuration.TenantConfigStore;
import com.example.host.hashed.HashedFilesRegistry;
import com.example.host.request.RequestContext;
import com.example.host.resource.handler.LanguageFrontendResourceRequestHandler;

import com.fasterxml.jackson.databind.ObjectMapper;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;

import java.nio.charset.StandardCharsets;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Renders the dashboard, and with it the module manifest the client resolves
 * its imports through.
 */
public class DashboardServlet extends HttpServlet {

	public DashboardServlet(
		HashedFilesRegistry hashedFilesRegistry,
		TenantConfigStore tenantConfigStore,
		WebContextRegistry webContextRegistry) {

		_hashedFilesRegistry = hashedFilesRegistry;
		_tenantConfigStore = tenantConfigStore;
		_webContextRegistry = webContextRegistry;
	}

	@Override
	protected void doGet(
			HttpServletRequest httpServletRequest,
			HttpServletResponse httpServletResponse)
		throws IOException {

		RequestContext requestContext =
			(RequestContext)httpServletRequest.getAttribute(
				RequestContext.ATTRIBUTE_NAME);

		_addCookie(
			httpServletResponse, "tenant", requestContext.getTenantId());
		_addCookie(
			httpServletResponse, "locale", requestContext.getLanguageId());

		Map<String, String> modules = new LinkedHashMap<>();

		for (WebContext webContext : _webContextRegistry.getWebContexts()) {
			for (String fileName : webContext.getFileNames()) {
				if (fileName.endsWith(".map")) {
					continue;
				}

				String unhashedURI =
					HashedFilesRegistry.URI_PREFIX + webContext.getName() + "/" +
						fileName;

				modules.put(
					webContext.getName() + "/" + fileName,
					_hashedFilesRegistry.getPublicURI(
						unhashedURI, requestContext));
			}
		}

		modules.put(
			"language/app-web",
			LanguageFrontendResourceRequestHandler.URI_PREFIX +
				"app-web/all.js");

		FrontendCachingConfiguration frontendCachingConfiguration =
			_tenantConfigStore.getFrontendCachingConfiguration(
				requestContext.getTenantId());

		Map<String, String> model = new LinkedHashMap<>();

		model.put(
			"appModuleURI", modules.get("app-web/app.js"));
		model.put(
			"cachingStrategy",
			frontendCachingConfiguration.cachingStrategy(
			).getValue());
		model.put("languageId", requestContext.getLanguageId());
		model.put(
			"modulesJson", _objectMapper.writeValueAsString(modules));
		model.put("tenantId", requestContext.getTenantId());

		String html = _readTemplate();

		for (Map.Entry<String, String> entry : model.entrySet()) {
			html = html.replace(
				"${" + entry.getKey() + "}", entry.getValue());
		}

		httpServletResponse.setContentType("text/html; charset=UTF-8");

		PrintWriter printWriter = httpServletResponse.getWriter();

		printWriter.write(html);
	}

	private void _addCookie(
		HttpServletResponse httpServletResponse, String name, String value) {

		Cookie cookie = new Cookie(name, value);

		cookie.setPath("/");

		httpServletResponse.addCookie(cookie);
	}

	private String _readTemplate() throws IOException {
		try (InputStream inputStream =
				DashboardServlet.class.getResourceAsStream(
					"/templates/dashboard.html")) {

			return new String(
				inputStream.readAllBytes(), StandardCharsets.UTF_8);
		}
	}

	private final HashedFilesRegistry _hashedFilesRegistry;
	private final ObjectMapper _objectMapper = new ObjectMapper();
	private final TenantConfigStore _tenantConfigStore;
	private final WebContextRegistry _webContextRegistry;

}
