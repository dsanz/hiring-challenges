package com.example.host.web;

import java.util.List;
import java.util.Map;

/**
 * Translated labels. A properties-file hierarchy in the real platform; two
 * locales in a map here.
 */
public class Language {

	public static final String DEFAULT_LANGUAGE_ID = "en_US";

	public String get(String languageId, String key) {
		Map<String, String> labels = _labels.getOrDefault(
			languageId, _labels.get(DEFAULT_LANGUAGE_ID));

		return labels.getOrDefault(key, key);
	}

	/**
	 * The keys a web context declares it needs. Read from
	 * <code>language.json</code> alongside the module in the real platform.
	 */
	public List<String> getKeys(String webContextName) {
		return _keys.getOrDefault(webContextName, List.of());
	}

	public boolean isAvailable(String languageId) {
		return _labels.containsKey(languageId);
	}

	private final Map<String, List<String>> _keys = Map.of(
		"app-web", List.of("dashboard", "loading", "refresh", "widgets"),
		"widget-web", List.of("widgets"));

	private final Map<String, Map<String, String>> _labels = Map.of(
		"en_US",
		Map.of(
			"dashboard", "Dashboard", "loading", "Loading...", "refresh",
			"Refresh", "widgets", "Widgets"),
		"es_ES",
		Map.of(
			"dashboard", "Panel de control", "loading", "Cargando...",
			"refresh", "Actualizar", "widgets", "Componentes"));

}
