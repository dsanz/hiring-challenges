package com.example.host.tenant;

import java.util.List;
import java.util.Map;

/**
 * Per-tenant configuration. The CSP map is tenant-supplied: an administrator
 * types these values into a form.
 */
public class TenantConfig {

	public TenantConfig(
		String tenantId, String displayName, Map<String, List<String>> csp) {

		_tenantId = tenantId;
		_displayName = displayName;
		_csp = Map.copyOf(csp);
	}

	public Map<String, List<String>> getCsp() {
		return _csp;
	}

	public String getDisplayName() {
		return _displayName;
	}

	public String getTenantId() {
		return _tenantId;
	}

	private final Map<String, List<String>> _csp;
	private final String _displayName;
	private final String _tenantId;

}
