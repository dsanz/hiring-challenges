package com.example.host.tenant;

import java.util.List;
import java.util.Map;

/**
 * Per-tenant configuration. The <code>csp</code> map is tenant-supplied: an
 * administrator types these values into a form.
 */
public record TenantConfig(
	String tenantId, String displayName, Map<String, List<String>> csp) {
}
