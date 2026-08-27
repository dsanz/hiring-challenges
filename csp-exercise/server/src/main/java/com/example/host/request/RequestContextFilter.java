package com.example.host.request;

import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;

import java.io.IOException;

/**
 * Creates the {@link RequestContext} and guarantees it is torn down, including
 * on the error path. Runs before every other filter.
 *
 * <p>
 * Threads are pooled and reused across requests. The <code>finally</code> block
 * below is what stops one request from seeing another request's state.
 * </p>
 */
public class RequestContextFilter implements Filter {

	@Override
	public void doFilter(
			ServletRequest servletRequest, ServletResponse servletResponse,
			FilterChain filterChain)
		throws IOException, ServletException {

		HttpServletRequest httpServletRequest =
			(HttpServletRequest)servletRequest;

		RequestContext requestContext = new RequestContext(
			_getTenantId(httpServletRequest), httpServletRequest.getPathInfo());

		httpServletRequest.setAttribute(
			RequestContext.ATTRIBUTE_NAME, requestContext);

		RequestContext.setCurrent(requestContext);

		try {
			filterChain.doFilter(servletRequest, servletResponse);
		}
		finally {
			RequestContext.clearCurrent();
		}
	}

	private String _getTenantId(HttpServletRequest httpServletRequest) {
		String requestURI = httpServletRequest.getRequestURI();

		if (!requestURI.startsWith("/t/")) {
			return "default";
		}

		String[] parts = requestURI.split("/");

		if (parts.length < 3) {
			return "default";
		}

		return parts[2];
	}

}
