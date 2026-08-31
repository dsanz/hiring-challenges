package com.example.host.request;

import com.example.host.web.Language;

import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;

import java.io.IOException;

/**
 * Establishes the {@link RequestContext}. Runs before everything else.
 *
 * <p>
 * The locale comes from a <code>locale</code> cookie, which stands in for a
 * stored user preference. Append <code>?locale=es_ES</code> to any page to set
 * it.
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

		httpServletRequest.setAttribute(
			RequestContext.ATTRIBUTE_NAME,
			new RequestContext(
				_getTenantId(httpServletRequest),
				_getLanguageId(httpServletRequest)));

		filterChain.doFilter(servletRequest, servletResponse);
	}

	private String _getLanguageId(HttpServletRequest httpServletRequest) {
		String languageId = httpServletRequest.getParameter("locale");

		if (languageId != null) {
			return languageId;
		}

		Cookie[] cookies = httpServletRequest.getCookies();

		if (cookies != null) {
			for (Cookie cookie : cookies) {
				if ("locale".equals(cookie.getName())) {
					return cookie.getValue();
				}
			}
		}

		return Language.DEFAULT_LANGUAGE_ID;
	}

	private String _getTenantId(HttpServletRequest httpServletRequest) {
		String requestURI = httpServletRequest.getRequestURI();

		if (requestURI.startsWith("/t/")) {
			String[] parts = requestURI.split("/");

			if (parts.length >= 3) {
				return parts[2];
			}
		}

		Cookie[] cookies = httpServletRequest.getCookies();

		if (cookies != null) {
			for (Cookie cookie : cookies) {
				if ("tenant".equals(cookie.getName())) {
					return cookie.getValue();
				}
			}
		}

		return "acme";
	}

}
