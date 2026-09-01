package com.example.vat.proxy;

import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.io.PrintWriter;

import java.net.http.HttpResponse;

/**
 * The microservice endpoint the client extension calls. Mapped at
 * <code>/o/vat/lookup</code>.
 *
 * <p>
 * TODO (T1, T2, T3): this forwards every call straight through and hands the
 * registry's answer back verbatim, status code and all. It holds no state, so
 * two people checking the same number are two calls to a service that allows
 * ten a minute, and a browser can tell the difference between "not a valid
 * number" and "we could not find out" only by reading somebody else's error
 * format.
 * </p>
 */
public class VatLookupServlet extends HttpServlet {

	public VatLookupServlet(UpstreamVatClient upstreamVatClient) {
		_upstreamVatClient = upstreamVatClient;
	}

	@Override
	protected void doGet(
			HttpServletRequest httpServletRequest,
			HttpServletResponse httpServletResponse)
		throws IOException {

		String vatId = httpServletRequest.getParameter("vatId");

		httpServletResponse.setContentType("application/json");

		if ((vatId == null) || vatId.isBlank()) {
			httpServletResponse.setStatus(
				HttpServletResponse.SC_BAD_REQUEST);

			_write(httpServletResponse, "{\"error\":\"MISSING_VAT_ID\"}");

			return;
		}

		try {
			HttpResponse<String> httpResponse = _upstreamVatClient.lookup(
				vatId);

			httpServletResponse.setStatus(httpResponse.statusCode());

			_write(httpServletResponse, httpResponse.body());
		}
		catch (Exception exception) {
			httpServletResponse.setStatus(
				HttpServletResponse.SC_INTERNAL_SERVER_ERROR);

			_write(
				httpServletResponse,
				"{\"error\":\"" + exception.getMessage() + "\"}");
		}
	}

	private void _write(HttpServletResponse httpServletResponse, String body)
		throws IOException {

		PrintWriter printWriter = httpServletResponse.getWriter();

		printWriter.write(body);
	}

	private final UpstreamVatClient _upstreamVatClient;

}
