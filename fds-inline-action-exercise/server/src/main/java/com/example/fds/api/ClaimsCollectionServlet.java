package com.example.fds.api;

import com.example.fds.model.Claim;
import com.example.fds.model.ClaimRepository;

import com.fasterxml.jackson.databind.ObjectMapper;

import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.io.PrintWriter;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * The collection endpoint the data set reads from, at
 * <code>/o/c/claims</code>.
 *
 * <p>
 * Paged and filterable, the way a real collection endpoint is. This is
 * finished.
 * </p>
 */
public class ClaimsCollectionServlet extends HttpServlet {

	public ClaimsCollectionServlet(ClaimRepository claimRepository) {
		_claimRepository = claimRepository;
	}

	@Override
	protected void doGet(
			HttpServletRequest httpServletRequest,
			HttpServletResponse httpServletResponse)
		throws IOException {

		List<Claim> claims = _claimRepository.getClaims(
			httpServletRequest.getParameter("status"));

		int page = _intParameter(httpServletRequest, "page", 1);
		int pageSize = _intParameter(httpServletRequest, "pageSize", 5);

		int from = Math.min((page - 1) * pageSize, claims.size());
		int to = Math.min(from + pageSize, claims.size());

		List<Map<String, Object>> items = new ArrayList<>();

		for (Claim claim : claims.subList(from, to)) {
			items.add(ClaimSerializer.toMap(claim));
		}

		Map<String, Object> body = new LinkedHashMap<>();

		body.put("items", items);
		body.put("page", page);
		body.put("pageSize", pageSize);
		body.put("totalCount", claims.size());

		httpServletResponse.setContentType("application/json");

		PrintWriter printWriter = httpServletResponse.getWriter();

		printWriter.write(_objectMapper.writeValueAsString(body));
	}

	private int _intParameter(
		HttpServletRequest httpServletRequest, String name, int defaultValue) {

		String value = httpServletRequest.getParameter(name);

		if (value == null) {
			return defaultValue;
		}

		try {
			return Integer.parseInt(value);
		}
		catch (NumberFormatException numberFormatException) {
			return defaultValue;
		}
	}

	private final ClaimRepository _claimRepository;
	private final ObjectMapper _objectMapper = new ObjectMapper();

}
