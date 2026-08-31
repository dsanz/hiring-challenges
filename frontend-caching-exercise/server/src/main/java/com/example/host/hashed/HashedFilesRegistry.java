package com.example.host.hashed;

import com.example.host.configuration.CachingStrategy;
import com.example.host.configuration.TenantConfigStore;
import com.example.host.request.RequestContext;
import com.example.host.web.WebContext;
import com.example.host.web.WebContextRegistry;

import java.io.IOException;

/**
 * Maps between the URI a file is published under and the bytes behind it.
 *
 * <p>
 * TODO (T1): as shipped this publishes every file under its plain name and
 * resolves it straight off the classpath, which is why nothing on this server
 * can be cached for longer than a deploy cycle. Walk the web contexts at
 * startup, work out what each file's public URI should be under the tenant's
 * caching strategy, and hold the mapping so that both directions are a lookup
 * rather than a file read.
 * </p>
 *
 * <p>
 * All three values of {@link CachingStrategy} have to work, and the strategy is
 * per tenant, so two tenants may be looking at different URIs for the same file
 * at the same time.
 * </p>
 */
public class HashedFilesRegistry {

	public static final String URI_PREFIX = "/o/js/";

	public HashedFilesRegistry(
		WebContextRegistry webContextRegistry,
		TenantConfigStore tenantConfigStore) {

		_webContextRegistry = webContextRegistry;
		_tenantConfigStore = tenantConfigStore;
	}

	public CachingStrategy getCachingStrategy(RequestContext requestContext) {
		return CachingStrategy.DO_NOT_USE_HASHES;
	}

	/**
	 * The URI a file should be published under, for this tenant.
	 *
	 * @param unhashedURI for example <code>/o/js/app-web/app.js</code>
	 */
	public String getPublicURI(
		String unhashedURI, RequestContext requestContext) {

		return unhashedURI;
	}

	/**
	 * The bytes published under a URI, or <code>null</code> if nothing is.
	 *
	 * @param publicURI whatever {@link #getPublicURI} handed out
	 */
	public byte[] getResource(String publicURI) throws IOException {
		if (!publicURI.startsWith(URI_PREFIX)) {
			return null;
		}

		String path = publicURI.substring(URI_PREFIX.length());

		int i = path.indexOf('/');

		if (i == -1) {
			return null;
		}

		WebContext webContext = _webContextRegistry.getWebContext(
			path.substring(0, i));

		if (webContext == null) {
			return null;
		}

		return webContext.read(path.substring(i + 1));
	}

	private final TenantConfigStore _tenantConfigStore;
	private final WebContextRegistry _webContextRegistry;

}
