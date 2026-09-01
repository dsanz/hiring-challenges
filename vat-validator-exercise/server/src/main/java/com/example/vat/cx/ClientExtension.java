package com.example.vat.cx;

import java.util.List;

/**
 * A client extension, as declared in <code>client-extension.yaml</code>.
 *
 * <p>
 * The harness reads the same descriptor the platform reads, so a mistake in
 * that file shows up here rather than being discovered on a real server later.
 * </p>
 */
public class ClientExtension {

	public ClientExtension(
		String id, String name, String type, String htmlElementName,
		boolean instanceable, boolean useESM, List<String> urlPatterns,
		List<String> cssURLPatterns) {

		_id = id;
		_name = name;
		_type = type;
		_htmlElementName = htmlElementName;
		_instanceable = instanceable;
		_useESM = useESM;
		_urlPatterns = List.copyOf(urlPatterns);
		_cssURLPatterns = List.copyOf(cssURLPatterns);
	}

	public List<String> getCssURLPatterns() {
		return _cssURLPatterns;
	}

	public String getHtmlElementName() {
		return _htmlElementName;
	}

	public String getId() {
		return _id;
	}

	public String getName() {
		return _name;
	}

	public String getType() {
		return _type;
	}

	public List<String> getUrlPatterns() {
		return _urlPatterns;
	}

	public boolean isInstanceable() {
		return _instanceable;
	}

	public boolean isUseESM() {
		return _useESM;
	}

	private final List<String> _cssURLPatterns;
	private final String _htmlElementName;
	private final String _id;
	private final boolean _instanceable;
	private final String _name;
	private final String _type;
	private final List<String> _urlPatterns;
	private final boolean _useESM;

}
