package com.example.host.render;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Holds the dynamic includes registered against each insertion point, in
 * registration order.
 */
public class DynamicIncludeRegistry {

	public List<DynamicInclude> getDynamicIncludes(String key) {
		return _dynamicIncludes.getOrDefault(key, List.of());
	}

	public void register(DynamicInclude dynamicInclude) {
		List<DynamicInclude> dynamicIncludes = _dynamicIncludes.computeIfAbsent(
			dynamicInclude.getKey(), key -> new ArrayList<>());

		dynamicIncludes.add(dynamicInclude);
	}

	private final Map<String, List<DynamicInclude>> _dynamicIncludes =
		new LinkedHashMap<>();

}
