package com.example.host.render;

import java.io.IOException;
import java.io.Writer;
import java.util.ArrayList;
import java.util.List;

/**
 * A page-scoped buffer for script contributed while the page is being rendered.
 *
 * <p>
 * Contributors append to it at any point during rendering. The accumulated
 * script is flushed once, near the end of the document, at the
 * <code>&lt;!--#scriptData--&gt;</code> marker &mdash; which is to say, after
 * most of the body has already been produced.
 * </p>
 */
public final class ScriptData {

	public void append(String script) {
		_scripts.add(script);
	}

	public boolean isEmpty() {
		return _scripts.isEmpty();
	}

	public void writeTo(Writer writer) throws IOException {
		if (_scripts.isEmpty()) {
			return;
		}

		writer.write("<script>\n");

		for (String script : _scripts) {
			writer.write(script);
			writer.write("\n");
		}

		writer.write("</script>");
	}

	private final List<String> _scripts = new ArrayList<>();

}
