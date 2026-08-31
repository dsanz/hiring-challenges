package com.example.host.servlet.filter;

import com.example.host.request.RequestContext;
import com.example.host.resource.FrontendResource;
import com.example.host.resource.handler.FrontendResourceRequestHandler;

import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import java.util.List;

/**
 * Finds the handler for a request and writes the resource out.
 *
 * <p>
 * TODO (T2, T3): {@link #send} writes the bytes and the content type and stops
 * there. Every response this server produces is therefore uncacheable, and a
 * client that already holds an identical copy downloads it again.
 * </p>
 */
public class FrontendResourceFilter implements Filter {

	public FrontendResourceFilter(
		List<FrontendResourceRequestHandler> frontendResourceRequestHandlers) {

		_frontendResourceRequestHandlers = frontendResourceRequestHandlers;
	}

	@Override
	public void doFilter(
			ServletRequest servletRequest, ServletResponse servletResponse,
			FilterChain filterChain)
		throws IOException, ServletException {

		HttpServletRequest httpServletRequest =
			(HttpServletRequest)servletRequest;
		HttpServletResponse httpServletResponse =
			(HttpServletResponse)servletResponse;

		RequestContext requestContext =
			(RequestContext)httpServletRequest.getAttribute(
				RequestContext.ATTRIBUTE_NAME);

		for (FrontendResourceRequestHandler frontendResourceRequestHandler :
				_frontendResourceRequestHandlers) {

			if (!frontendResourceRequestHandler.canHandleRequest(
					httpServletRequest)) {

				continue;
			}

			send(
				frontendResourceRequestHandler.handleRequest(
					httpServletRequest, requestContext),
				httpServletRequest, httpServletResponse);

			return;
		}

		filterChain.doFilter(servletRequest, servletResponse);
	}

	public void send(
			FrontendResource frontendResource,
			HttpServletRequest httpServletRequest,
			HttpServletResponse httpServletResponse)
		throws IOException {

		if (frontendResource == null) {
			httpServletResponse.sendError(HttpServletResponse.SC_NOT_FOUND);

			return;
		}

		httpServletResponse.setCharacterEncoding("UTF-8");
		httpServletResponse.setContentType(frontendResource.getContentType());

		try (InputStream inputStream = frontendResource.getInputStream()) {
			OutputStream outputStream = httpServletResponse.getOutputStream();

			inputStream.transferTo(outputStream);
		}
	}

	private final List<FrontendResourceRequestHandler>
		_frontendResourceRequestHandlers;

}
