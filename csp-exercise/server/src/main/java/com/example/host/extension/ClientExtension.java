package com.example.host.extension;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.List;
import java.util.Map;

/**
 * A client extension descriptor, as published by the extension's own origin.
 *
 * <p>
 * The <code>csp</code> map is the extension telling us what it needs in order
 * to function: directive name to source list. It is the extension author's
 * claim about itself, not a fact.
 * </p>
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public record ClientExtension(
	String name, String type, String htmlElementName, List<String> urls,
	boolean useESM, Map<String, List<String>> csp) {

	public ClientExtension {
		urls = (urls == null) ? List.of() : List.copyOf(urls);
		csp = (csp == null) ? Map.of() : Map.copyOf(csp);
	}

}
