package com.example.host.resource.handler;

import com.example.host.configuration.FrontendCachingConfiguration;
import com.example.host.configuration.TenantConfigStore;
import com.example.host.request.RequestContext;
import com.example.host.resource.ByteArrayFrontendResource;
import com.example.host.resource.FrontendResource;
import com.example.host.web.Language;

import jakarta.servlet.http.HttpServletRequest;

import java.io.IOException;

import java.nio.charset.StandardCharsets;

import java.util.List;

/**
 * Serves a web context's translated labels as a JavaScript module, at
 * <code>/o/js/language/{webContextName}/all.js</code>.
 *
 * <p>
 * TODO (T4): this is correct for one user at a time.
 * </p>
 */
public class LanguageFrontendResourceRequestHandler
	implements FrontendResourceRequestHandler {

	public static final String URI_PREFIX = "/o/js/language/";

	public LanguageFrontendResourceRequestHandler(
		Language language, TenantConfigStore tenantConfigStore) {

		_language = language;
		_tenantConfigStore = tenantConfigStore;
	}

	@Override
	public boolean canHandleRequest(HttpServletRequest httpServletRequest) {
		String requestURI = httpServletRequest.getRequestURI();

		if (requestURI.startsWith(URI_PREFIX) &&
			requestURI.endsWith("/all.js")) {

			return true;
		}

		return false;
	}

	@Override
	public FrontendResource handleRequest(
			HttpServletRequest httpServletRequest,
			RequestContext requestContext)
		throws IOException {

		String path = httpServletRequest.getRequestURI(
		).substring(
			URI_PREFIX.length()
		);

		String[] parts = path.split("/");

		if (parts.length != 2) {
			return null;
		}

		String webContextName = parts[0];

		String languageId = requestContext.getLanguageId();

		List<String> keys = _language.getKeys(webContextName);

		if (keys.isEmpty()) {
			return null;
		}

		StringBuilder sb = new StringBuilder();

		sb.append("export default {\n");

		for (String key : keys) {
			sb.append("\t'");
			sb.append(key);
			sb.append("': '");
			sb.append(_language.get(languageId, key).replace("'", "\\'"));
			sb.append("',\n");
		}

		sb.append("};\n");

		FrontendCachingConfiguration frontendCachingConfiguration =
			_tenantConfigStore.getFrontendCachingConfiguration(
				requestContext.getTenantId());

		return new ByteArrayFrontendResource(
			sb.toString(
			).getBytes(
				StandardCharsets.UTF_8
			),
			"text/javascript", false,
			frontendCachingConfiguration.labelsModulesMaxAge(),
			frontendCachingConfiguration.sendNoCacheForLabelsModules(), false,
			false);
	}

	private final Language _language;
	private final TenantConfigStore _tenantConfigStore;

}
