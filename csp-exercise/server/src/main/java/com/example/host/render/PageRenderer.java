package com.example.host.render;

import com.example.host.request.RequestContext;

import java.io.IOException;
import java.io.InputStream;
import java.io.StringWriter;
import java.io.Writer;

import java.nio.charset.StandardCharsets;

import java.util.List;
import java.util.Map;

/**
 * Renders a template into the final document.
 *
 * <p>
 * Three constructs are expanded, in this order:
 * </p>
 *
 * <ul>
 * <li><code>${name}</code> &mdash; replaced with the model value, verbatim. No
 * escaping is applied; a contributor that emits untrusted data is responsible
 * for escaping it.</li>
 * <li><code>&lt;!--#include:key--&gt;</code> &mdash; replaced with the output of
 * every {@link DynamicInclude} registered against that key.</li>
 * <li><code>&lt;!--#scriptData--&gt;</code> &mdash; replaced with the flushed
 * {@link ScriptData} buffer.</li>
 * </ul>
 */
public class PageRenderer {

	public PageRenderer(DynamicIncludeRegistry dynamicIncludeRegistry) {
		_dynamicIncludeRegistry = dynamicIncludeRegistry;
	}

	public String render(
			String templateName, RequestContext requestContext,
			Map<String, String> model)
		throws IOException {

		String template = _readTemplate(templateName);

		for (Map.Entry<String, String> entry : model.entrySet()) {
			template = template.replace(
				"${" + entry.getKey() + "}", entry.getValue());
		}

		for (String key : _KEYS) {
			template = template.replace(
				"<!--#include:" + key + "-->",
				_expandDynamicIncludes(key, requestContext));
		}

		StringWriter stringWriter = new StringWriter();

		ScriptData scriptData = requestContext.getScriptData();

		scriptData.writeTo(stringWriter);

		return template.replace(
			"<!--#scriptData-->", stringWriter.toString());
	}

	private String _expandDynamicIncludes(
			String key, RequestContext requestContext)
		throws IOException {

		List<DynamicInclude> dynamicIncludes =
			_dynamicIncludeRegistry.getDynamicIncludes(key);

		Writer writer = new StringWriter();

		for (DynamicInclude dynamicInclude : dynamicIncludes) {
			dynamicInclude.include(requestContext, writer);

			writer.write("\n");
		}

		return writer.toString();
	}

	private String _readTemplate(String templateName) throws IOException {
		try (InputStream inputStream =
				PageRenderer.class.getResourceAsStream(
					"/templates/" + templateName)) {

			if (inputStream == null) {
				throw new IOException("No such template " + templateName);
			}

			return new String(
				inputStream.readAllBytes(), StandardCharsets.UTF_8);
		}
	}

	private static final String[] _KEYS = {
		"top_head#pre", "top_head#post", "bottom#post"
	};

	private final DynamicIncludeRegistry _dynamicIncludeRegistry;

}
