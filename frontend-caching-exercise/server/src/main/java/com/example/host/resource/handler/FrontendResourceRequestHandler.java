package com.example.host.resource.handler;

import com.example.host.request.RequestContext;
import com.example.host.resource.FrontendResource;

import jakarta.servlet.http.HttpServletRequest;

import java.io.IOException;

/**
 * Claims a request and turns it into a {@link FrontendResource}.
 *
 * <p>
 * The filter walks the registered handlers in order. The first one whose
 * {@link #canHandleRequest} returns <code>true</code> serves the request; if
 * none does, the request carries on down the chain.
 * </p>
 */
public interface FrontendResourceRequestHandler {

	public boolean canHandleRequest(HttpServletRequest httpServletRequest);

	public FrontendResource handleRequest(
			HttpServletRequest httpServletRequest,
			RequestContext requestContext)
		throws IOException;

}
