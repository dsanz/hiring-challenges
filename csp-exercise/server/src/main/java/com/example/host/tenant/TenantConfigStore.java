package com.example.host.tenant;

import java.util.List;
import java.util.Map;

/**
 * Where tenant configuration comes from. A database in the real platform, a
 * fixed map here.
 */
public class TenantConfigStore {

	public TenantConfig getTenantConfig(String tenantId) {
		return _tenantConfigs.getOrDefault(
			tenantId, new TenantConfig(tenantId, tenantId, Map.of()));
	}

	private final Map<String, TenantConfig> _tenantConfigs = Map.of(
		"acme",
		new TenantConfig(
			"acme", "Acme Corporation",
			Map.of(
				"img-src", List.of("https://images.acme.example"),
				"frame-src", List.of("https://analytics.vendor.example"))),
		"globex",
		new TenantConfig(
			"globex", "Globex", Map.of("img-src", List.of("data:"))));

}
