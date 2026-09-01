package com.example.fds.web;

import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class StaticServlet extends HttpServlet {

	@Override
	protected void doGet(
			HttpServletRequest httpServletRequest,
			HttpServletResponse httpServletResponse)
		throws IOException {

		String pathInfo = httpServletRequest.getPathInfo();

		if ((pathInfo == null) || pathInfo.contains("..")) {
			httpServletResponse.sendError(HttpServletResponse.SC_NOT_FOUND);

			return;
		}

		try (InputStream inputStream = StaticServlet.class.getResourceAsStream(
				"/static" + pathInfo)) {

			if (inputStream == null) {
				httpServletResponse.sendError(HttpServletResponse.SC_NOT_FOUND);

				return;
			}

			httpServletResponse.setContentType("text/javascript");

			OutputStream outputStream = httpServletResponse.getOutputStream();

			inputStream.transferTo(outputStream);
		}
	}

}
