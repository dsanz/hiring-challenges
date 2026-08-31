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
public class ClientExtension {

	public Map<String, List<String>> getCsp() {
		if (_csp == null) {
			return Map.of();
		}

		return _csp;
	}

	public String getHtmlElementName() {
		return _htmlElementName;
	}

	public String getName() {
		return _name;
	}

	public String getType() {
		return _type;
	}

	public List<String> getUrls() {
		if (_urls == null) {
			return List.of();
		}

		return _urls;
	}

	public boolean isUseESM() {
		return _useESM;
	}

	public void setCsp(Map<String, List<String>> csp) {
		_csp = csp;
	}

	public void setHtmlElementName(String htmlElementName) {
		_htmlElementName = htmlElementName;
	}

	public void setName(String name) {
		_name = name;
	}

	public void setType(String type) {
		_type = type;
	}

	public void setUrls(List<String> urls) {
		_urls = urls;
	}

	public void setUseESM(boolean useESM) {
		_useESM = useESM;
	}

	private Map<String, List<String>> _csp;
	private String _htmlElementName;
	private String _name;
	private String _type;
	private List<String> _urls;
	private boolean _useESM;

}
