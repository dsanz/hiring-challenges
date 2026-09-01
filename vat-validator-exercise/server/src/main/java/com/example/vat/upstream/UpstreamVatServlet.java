package com.example.vat.upstream;

import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.io.PrintWriter;

import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Stands in for the external VAT registry. Runs on its own port, because it is
 * somebody else's service.
 *
 * <p>
 * Its behavior is fixed and reproducible, not random. Everything it does here,
 * a real registry does too.
 * </p>
 */
public class UpstreamVatServlet extends HttpServlet {

	/**
	 * The secret the microservice authenticates with. It is configured on the
	 * server and has no business ever reaching a browser.
	 */
	public static final String API_KEY = "vat-registry-6f2a91c4";

	/**
	 * Requests allowed per minute. The real quota is not generous either.
	 */
	public static final int RATE_LIMIT = 10;

	/**
	 * How long an ordinary lookup takes.
	 */
	public static final long LATENCY = 600;

	/**
	 * How long a lookup takes when the registry is having a bad day. German
	 * numbers route through a separate backend.
	 */
	public static final long SLOW_LATENCY = 5000;

	@Override
	protected void doGet(
			HttpServletRequest httpServletRequest,
			HttpServletResponse httpServletResponse)
		throws IOException {

		httpServletResponse.setContentType("application/json");

		if (!API_KEY.equals(httpServletRequest.getHeader("X-Api-Key"))) {
			httpServletResponse.setStatus(
				HttpServletResponse.SC_UNAUTHORIZED);

			_write(httpServletResponse, "{\"error\":\"BAD_CREDENTIALS\"}");

			return;
		}

		String vatId = httpServletRequest.getParameter("vatId");

		if ((vatId == null) || vatId.isBlank()) {
			httpServletResponse.setStatus(
				HttpServletResponse.SC_BAD_REQUEST);

			_write(httpServletResponse, "{\"error\":\"MISSING_VAT_ID\"}");

			return;
		}

		vatId = vatId.trim(
		).toUpperCase();

		if (!_withinRateLimit()) {
			httpServletResponse.setStatus(429);
			httpServletResponse.setHeader("Retry-After", "60");

			_write(httpServletResponse, "{\"error\":\"RATE_LIMIT_EXCEEDED\"}");

			return;
		}

		_sleep(vatId.startsWith("DE") ? SLOW_LATENCY : LATENCY);

		// The registry cannot always reach the member state that owns the
		// number. When that happens it says so, and says nothing about whether
		// the number is good.

		if (vatId.endsWith("9")) {
			_write(
				httpServletResponse,
				"{\"valid\":null,\"reason\":\"MEMBER_STATE_UNAVAILABLE\"}");

			return;
		}

		if (_VALID_VAT_IDS.contains(vatId)) {
			_write(
				httpServletResponse,
				"{\"valid\":true,\"name\":\"" + _nameFor(vatId) +
					"\",\"address\":\"1 Example Street, Springfield\"}");

			return;
		}

		_write(httpServletResponse, "{\"valid\":false}");
	}

	private String _nameFor(String vatId) {
		return "Registered Trader " + vatId.substring(0, 2);
	}

	private void _sleep(long millis) {
		try {
			Thread.sleep(millis);
		}
		catch (InterruptedException interruptedException) {
			Thread.currentThread(
			).interrupt();
		}
	}

	private boolean _withinRateLimit() {
		long minute = System.currentTimeMillis() / 60000;

		_counters.keySet(
		).removeIf(
			key -> key < minute
		);

		AtomicInteger counter = _counters.computeIfAbsent(
			minute, key -> new AtomicInteger());

		return counter.incrementAndGet() <= RATE_LIMIT;
	}

	private void _write(HttpServletResponse httpServletResponse, String body)
		throws IOException {

		PrintWriter printWriter = httpServletResponse.getWriter();

		printWriter.write(body);
	}

	private static final Set<String> _VALID_VAT_IDS = Set.of(
		"ESB12345678", "FR40303265045", "IE6388047V", "DE811907980",
		"NL004495445B01");

	private final Map<Long, AtomicInteger> _counters =
		new ConcurrentHashMap<>();

}
