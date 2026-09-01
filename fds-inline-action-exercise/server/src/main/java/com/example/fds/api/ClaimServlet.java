package com.example.fds.api;

import com.example.fds.model.Claim;
import com.example.fds.model.ClaimRepository;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.io.PrintWriter;

import java.nio.charset.StandardCharsets;

/**
 * The item endpoint, at <code>/o/c/claims/{id}</code>.
 *
 * <p>
 * TODO (T1): the only rule this enforces is the one about a second approver.
 * There is no check that the transition being asked for makes sense, nothing
 * that notices two people acting on the same claim, and on success it returns
 * <code>204 No Content</code> &mdash; which is a decision about what the caller
 * can do next, whether or not it was made deliberately.
 * </p>
 */
public class ClaimServlet extends HttpServlet {

	/**
	 * Claims above this need a second approver, and cannot be approved from the
	 * table.
	 */
	public static final int SECOND_APPROVER_THRESHOLD = 1000;

	public ClaimServlet(ClaimRepository claimRepository) {
		_claimRepository = claimRepository;
	}

	@Override
	protected void service(
			HttpServletRequest httpServletRequest,
			HttpServletResponse httpServletResponse)
		throws IOException {

		if (!"PATCH".equals(httpServletRequest.getMethod())) {
			httpServletResponse.sendError(
				HttpServletResponse.SC_METHOD_NOT_ALLOWED);

			return;
		}

		httpServletResponse.setContentType("application/json");

		String pathInfo = httpServletRequest.getPathInfo();

		if ((pathInfo == null) || (pathInfo.length() < 2)) {
			httpServletResponse.setStatus(HttpServletResponse.SC_NOT_FOUND);

			_write(httpServletResponse, "{\"error\":\"NOT_FOUND\"}");

			return;
		}

		Claim claim = _claimRepository.getClaim(pathInfo.substring(1));

		if (claim == null) {
			httpServletResponse.setStatus(HttpServletResponse.SC_NOT_FOUND);

			_write(httpServletResponse, "{\"error\":\"NOT_FOUND\"}");

			return;
		}

		JsonNode body = _objectMapper.readTree(
			new String(
				httpServletRequest.getInputStream(
				).readAllBytes(),
				StandardCharsets.UTF_8));

		JsonNode status = body.get("status");

		if (status == null) {
			httpServletResponse.setStatus(
				HttpServletResponse.SC_BAD_REQUEST);

			_write(httpServletResponse, "{\"error\":\"MISSING_STATUS\"}");

			return;
		}

		if ((claim.getAmount() > SECOND_APPROVER_THRESHOLD) &&
			Claim.APPROVED.equals(status.asText())) {

			httpServletResponse.setStatus(422);

			_write(
				httpServletResponse,
				"{\"error\":\"SECOND_APPROVER_REQUIRED\",\"message\":\"" +
					"Claims over " + SECOND_APPROVER_THRESHOLD +
						" need a second approver\"}");

			return;
		}

		claim.setStatus(status.asText());

		httpServletResponse.setStatus(HttpServletResponse.SC_NO_CONTENT);
	}

	private void _write(HttpServletResponse httpServletResponse, String body)
		throws IOException {

		PrintWriter printWriter = httpServletResponse.getWriter();

		printWriter.write(body);
	}

	private final ClaimRepository _claimRepository;
	private final ObjectMapper _objectMapper = new ObjectMapper();

}
