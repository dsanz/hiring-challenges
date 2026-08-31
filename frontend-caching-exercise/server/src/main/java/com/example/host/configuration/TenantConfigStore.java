package com.example.host.configuration;

import java.util.HashMap;
import java.util.Map;

/**
 * Where per-tenant configuration comes from. A database in the real platform,
 * a fixed map here.
 *
 * <p>
 * The two tenants are configured differently on purpose.
 * </p>
 */
public class TenantConfigStore {

	public TenantConfigStore() {
		_frontendCachingConfigurations.put(
			"acme",
			new FrontendCachingConfigurationImpl(
				CachingStrategy.USE_ONE_HASH_PER_FILE, 86400, 3600, false,
				false));
		_frontendCachingConfigurations.put(
			"globex",
			new FrontendCachingConfigurationImpl(
				CachingStrategy.USE_ONE_HASH_PER_WEB_CONTEXT, 600, 300, false,
				true));
	}

	public FrontendCachingConfiguration getFrontendCachingConfiguration(
		String tenantId) {

		return _frontendCachingConfigurations.getOrDefault(tenantId, _DEFAULT);
	}

	private static class FrontendCachingConfigurationImpl
		implements FrontendCachingConfiguration {

		public FrontendCachingConfigurationImpl(
			CachingStrategy cachingStrategy, long jsFilesMaxAge,
			long labelsModulesMaxAge, boolean sendNoCacheForJSFiles,
			boolean sendNoCacheForLabelsModules) {

			_cachingStrategy = cachingStrategy;
			_jsFilesMaxAge = jsFilesMaxAge;
			_labelsModulesMaxAge = labelsModulesMaxAge;
			_sendNoCacheForJSFiles = sendNoCacheForJSFiles;
			_sendNoCacheForLabelsModules = sendNoCacheForLabelsModules;
		}

		@Override
		public CachingStrategy cachingStrategy() {
			return _cachingStrategy;
		}

		@Override
		public long jsFilesMaxAge() {
			return _jsFilesMaxAge;
		}

		@Override
		public long labelsModulesMaxAge() {
			return _labelsModulesMaxAge;
		}

		@Override
		public boolean sendNoCacheForJSFiles() {
			return _sendNoCacheForJSFiles;
		}

		@Override
		public boolean sendNoCacheForLabelsModules() {
			return _sendNoCacheForLabelsModules;
		}

		private final CachingStrategy _cachingStrategy;
		private final long _jsFilesMaxAge;
		private final long _labelsModulesMaxAge;
		private final boolean _sendNoCacheForJSFiles;
		private final boolean _sendNoCacheForLabelsModules;

	}

	private static final FrontendCachingConfiguration _DEFAULT =
		new FrontendCachingConfigurationImpl(
			CachingStrategy.DO_NOT_USE_HASHES, 86400, 3600, false, false);

	private final Map<String, FrontendCachingConfiguration>
		_frontendCachingConfigurations = new HashMap<>();

}
