package com.example.host.web;

import com.example.host.render.DynamicInclude;
import com.example.host.request.RequestContext;

import java.io.IOException;
import java.io.InputStream;
import java.io.Writer;

import java.nio.charset.StandardCharsets;

/**
 * Inlines the feature-detection shim into the head.
 *
 * <p>
 * The shim is the same bytes on every request, for every user, for the lifetime
 * of the process.
 * </p>
 */
public class ShimDynamicInclude implements DynamicInclude {

	public ShimDynamicInclude() throws IOException {
		try (InputStream inputStream =
				ShimDynamicInclude.class.getResourceAsStream(
					"/static/shim.js")) {

			_script = new String(
				inputStream.readAllBytes(), StandardCharsets.UTF_8);
		}
	}

	@Override
	public String getKey() {
		return "top_head#post";
	}

	/**
	 * The exact bytes this include emits between the script tags.
	 */
	public String getScript() {
		return _script;
	}

	@Override
	public void include(RequestContext requestContext, Writer writer)
		throws IOException {

		writer.write("<script>");
		writer.write(_script);
		writer.write("</script>");
	}

	private final String _script;

}
