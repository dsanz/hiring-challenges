package com.example.host.resource.handler;

import com.example.host.configuration.FrontendCachingConfiguration;
import com.example.host.configuration.TenantConfigStore;
import com.example.host.hashed.HashedFilesRegistry;
import com.example.host.request.RequestContext;
import com.example.host.resource.ByteArrayFrontendResource;
import com.example.host.resource.FrontendResource;

import jakarta.servlet.http.HttpServletRequest;

import java.io.IOException;

/**
 * Serves the files in a web context.
 *
 * <p>
 * TODO (T1, T2): this hands back every file with the same caching intent
 * regardless of how it was published. A URI that carries a content hash and one
 * that does not are not the same case, and a source map is not the same case as
 * a module.
 * </p>
 */
public class JavaScriptFrontendResourceRequestHandler
	implements FrontendResourceRequestHandler {

	public JavaScriptFrontendResourceRequestHandler(
		HashedFilesRegistry hashedFilesRegistry,
		TenantConfigStore tenantConfigStore) {

		_hashedFilesRegistry = hashedFilesRegistry;
		_tenantConfigStore = tenantConfigStore;
	}

	@Override
	public boolean canHandleRequest(HttpServletRequest httpServletRequest) {
		String requestURI = httpServletRequest.getRequestURI();

		if (requestURI.startsWith(HashedFilesRegistry.URI_PREFIX) &&
			!requestURI.startsWith(
				LanguageFrontendResourceRequestHandler.URI_PREFIX)) {

			return true;
		}

		return false;
	}

	@Override
	public FrontendResource handleRequest(
			HttpServletRequest httpServletRequest,
			RequestContext requestContext)
		throws IOException {

		String requestURI = httpServletRequest.getRequestURI();

		byte[] content = _hashedFilesRegistry.getResource(requestURI);

		if (content == null) {
			return null;
		}

		FrontendCachingConfiguration frontendCachingConfiguration =
			_tenantConfigStore.getFrontendCachingConfiguration(
				requestContext.getTenantId());

		return new ByteArrayFrontendResource(
			content, _getContentType(requestURI), false,
			frontendCachingConfiguration.jsFilesMaxAge(),
			frontendCachingConfiguration.sendNoCacheForJSFiles(), false, false);
	}

	private String _getContentType(String requestURI) {
		if (requestURI.endsWith(".map")) {
			return "application/json";
		}

		if (requestURI.endsWith(".css")) {
			return "text/css";
		}

		return "text/javascript";
	}

	private final HashedFilesRegistry _hashedFilesRegistry;
	private final TenantConfigStore _tenantConfigStore;

}
