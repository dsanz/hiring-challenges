package com.example.host.configuration;

/**
 * Per-tenant caching configuration. An OSGi configuration scoped to the company
 * in the real platform, which is why the accessors carry no <code>get</code>
 * prefix.
 */
public interface FrontendCachingConfiguration {

	/**
	 * How public URIs are derived for the files in a web context.
	 */
	public CachingStrategy cachingStrategy();

	/**
	 * Seconds a non-hashed JavaScript file stays fresh.
	 */
	public long jsFilesMaxAge();

	/**
	 * Seconds a labels module stays fresh.
	 */
	public long labelsModulesMaxAge();

	/**
	 * Whether JavaScript responses should be revalidated before every reuse
	 * rather than only once stale.
	 */
	public boolean sendNoCacheForJSFiles();

	/**
	 * The same, for labels modules.
	 */
	public boolean sendNoCacheForLabelsModules();

}
