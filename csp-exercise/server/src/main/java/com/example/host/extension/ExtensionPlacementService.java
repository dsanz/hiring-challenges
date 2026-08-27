package com.example.host.extension;

import java.util.List;
import java.util.Map;

/**
 * Says which client extensions are placed on a given page. In the real platform
 * this is editorial configuration; here it is a fixed map.
 */
public class ExtensionPlacementService {

	public ExtensionPlacementService(String originBaseURL) {
		_placements = Map.of(
			"acme",
			List.of(
				originBaseURL + "/acme-charts/descriptor.json",
				originBaseURL + "/legacy-ticker/descriptor.json",
				originBaseURL + "/slow-widget/descriptor.json"),
			"globex",
			List.of(originBaseURL + "/acme-charts/descriptor.json"));
	}

	public List<String> getDescriptorURLs(String tenantId, String route) {
		return _placements.getOrDefault(tenantId, List.of());
	}

	private final Map<String, List<String>> _placements;

}
