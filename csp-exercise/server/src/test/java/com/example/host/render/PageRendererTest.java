package com.example.host.render;

import com.example.host.request.RequestContext;

import java.io.IOException;
import java.io.Writer;

import java.util.Map;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

/**
 * Covers the rendering seams. Green on a fresh clone; keep it that way.
 */
public class PageRendererTest {

	@Test
	public void testExpandsDynamicIncludes() throws Exception {
		DynamicIncludeRegistry dynamicIncludeRegistry =
			new DynamicIncludeRegistry();

		dynamicIncludeRegistry.register(
			new DynamicInclude() {

				@Override
				public String getKey() {
					return "top_head#post";
				}

				@Override
				public void include(
						RequestContext requestContext, Writer writer)
					throws IOException {

					writer.write("<meta name=\"contributed\">");
				}

			});

		String html = _render(dynamicIncludeRegistry, Map.of());

		Assertions.assertTrue(html.contains("<meta name=\"contributed\">"));
		Assertions.assertFalse(html.contains("<!--#include:top_head#post-->"));
	}

	@Test
	public void testFlushesScriptDataLate() throws Exception {
		RequestContext requestContext = new RequestContext("acme", "/dashboard");

		ScriptData scriptData = requestContext.getScriptData();

		scriptData.append("window.X = 1;");

		PageRenderer pageRenderer = new PageRenderer(
			new DynamicIncludeRegistry());

		String html = pageRenderer.render(
			"test-page.html", requestContext, Map.of("title", "t"));

		Assertions.assertTrue(html.contains("window.X = 1;"));
		Assertions.assertTrue(
			html.indexOf("window.X = 1;") > html.indexOf("<body>"),
			"Script data must flush after the body has been produced");
	}

	@Test
	public void testSubstitutesModelValues() throws Exception {
		String html = _render(
			new DynamicIncludeRegistry(), Map.of("title", "Acme"));

		Assertions.assertTrue(html.contains("<title>Acme</title>"));
	}

	private String _render(
			DynamicIncludeRegistry dynamicIncludeRegistry,
			Map<String, String> model)
		throws Exception {

		PageRenderer pageRenderer = new PageRenderer(dynamicIncludeRegistry);

		return pageRenderer.render(
			"test-page.html", new RequestContext("acme", "/dashboard"), model);
	}

}
