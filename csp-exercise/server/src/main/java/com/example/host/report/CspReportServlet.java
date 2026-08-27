package com.example.host.report;

import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;

/**
 * Receives violation reports. Mapped at <code>/o/csp-report</code>.
 *
 * <p>
 * TODO (T6, optional): accept both the legacy <code>application/csp-report</code>
 * body and the modern <code>application/reports+json</code> body, normalize them
 * into one internal model, and aggregate. This endpoint is unauthenticated and
 * publicly reachable, and everything that arrives here is attacker-influenced.
 * </p>
 */
public class CspReportServlet extends HttpServlet {

	@Override
	protected void doPost(
			HttpServletRequest httpServletRequest,
			HttpServletResponse httpServletResponse)
		throws IOException {

		httpServletResponse.setStatus(HttpServletResponse.SC_NO_CONTENT);
	}

}
