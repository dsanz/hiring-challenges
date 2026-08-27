package com.example.host.render;

import com.example.host.request.RequestContext;

import java.io.IOException;
import java.io.Writer;

/**
 * Contributes markup at a named insertion point in the document.
 *
 * <p>
 * The platform renders the page and expands each insertion point in turn. A
 * dynamic include never sees the whole document and cannot modify markup that
 * another contributor emitted &mdash; it only appends its own.
 * </p>
 *
 * <p>
 * Known keys: <code>top_head#pre</code>, <code>top_head#post</code>,
 * <code>bottom#post</code>.
 * </p>
 */
public interface DynamicInclude {

	public String getKey();

	public void include(RequestContext requestContext, Writer writer)
		throws IOException;

}
