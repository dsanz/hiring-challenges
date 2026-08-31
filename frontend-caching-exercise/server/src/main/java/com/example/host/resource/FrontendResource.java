package com.example.host.resource;

import java.io.IOException;
import java.io.InputStream;

/**
 * Something servable, together with its caching intent.
 *
 * <p>
 * A resource describes what it is and how long it is good for. Turning that
 * into response headers is the filter's job, not the resource's.
 * </p>
 */
public interface FrontendResource {

	public String getContentType();

	/**
	 * An entity tag that unambiguously identifies these bytes, or
	 * <code>null</code>.
	 *
	 * <p>
	 * Returning <code>null</code> is allowed, and for some resources it is the
	 * right answer.
	 * </p>
	 */
	public String getETag();

	public InputStream getInputStream() throws IOException;

	/**
	 * Seconds this resource stays fresh. May be ignored for immutable
	 * resources.
	 */
	public long getMaxAge();

	/**
	 * Whether this URI can only ever return these bytes.
	 */
	public boolean isImmutable();

	/**
	 * Whether a shared cache must not store this response.
	 */
	public boolean isPrivate();

	/**
	 * Whether the client should revalidate before every reuse, rather than only
	 * once the resource has gone stale.
	 */
	public boolean isSendNoCache();

}
