package com.example.host.web;

import java.util.List;
import java.util.Map;

/**
 * The deployed web contexts. Discovered from the OSGi runtime in the real
 * platform; declared here.
 */
public class WebContextRegistry {

	public WebContext getWebContext(String name) {
		return _webContexts.get(name);
	}

	public List<WebContext> getWebContexts() {
		return List.copyOf(_webContexts.values());
	}

	private final Map<String, WebContext> _webContexts = Map.of(
		"app-web",
		new WebContext("app-web", List.of("app.js", "app.js.map")),
		"widget-web",
		new WebContext("widget-web", List.of("widget.js")));

}
