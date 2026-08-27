package com.example.host.csp;

import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;

/**
 * Emits the Content Security Policy header. Runs after
 * {@link com.example.host.request.RequestContextFilter}.
 *
 * <p>
 * TODO (T1, T3): this is the policy the penetration test flagged.
 * </p>
 */
public class CspFilter implements Filter {

	@Override
	public void doFilter(
			ServletRequest servletRequest, ServletResponse servletResponse,
			FilterChain filterChain)
		throws IOException, ServletException {

		HttpServletResponse httpServletResponse =
			(HttpServletResponse)servletResponse;

		httpServletResponse.setHeader(
			"Content-Security-Policy",
			"default-src *; script-src * 'unsafe-inline' 'unsafe-eval'; " +
				"style-src * 'unsafe-inline'");

		filterChain.doFilter(servletRequest, servletResponse);
	}

}
