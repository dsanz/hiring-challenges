package com.example.host.web;

import com.example.host.extension.ClientExtension;
import com.example.host.extension.ClientExtensions;
import com.example.host.render.PageRenderer;
import com.example.host.render.ScriptData;
import com.example.host.request.RequestContext;
import com.example.host.tenant.TenantConfig;
import com.example.host.tenant.TenantConfigStore;

import com.fasterxml.jackson.databind.ObjectMapper;

import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.io.PrintWriter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Renders <code>/t/{tenant}/dashboard</code>.
 */
public class DashboardServlet extends HttpServlet {

	public DashboardServlet(
		PageRenderer pageRenderer, ClientExtensions clientExtensions,
		TenantConfigStore tenantConfigStore, String originBaseURL) {

		_pageRenderer = pageRenderer;
		_clientExtensions = clientExtensions;
		_tenantConfigStore = tenantConfigStore;
		_originBaseURL = originBaseURL;
	}

	@Override
	protected void doGet(
			HttpServletRequest httpServletRequest,
			HttpServletResponse httpServletResponse)
		throws IOException {

		RequestContext requestContext =
			(RequestContext)httpServletRequest.getAttribute(
				RequestContext.ATTRIBUTE_NAME);

		TenantConfig tenantConfig = _tenantConfigStore.getTenantConfig(
			requestContext.getTenantId());

		List<ClientExtension> clientExtensions = _clientExtensions.resolve(
			requestContext);

		ScriptData scriptData = requestContext.getScriptData();

		scriptData.append(
			"window.__EXTENSIONS__ = " +
				_objectMapper.writeValueAsString(clientExtensions) + ";");

		Map<String, String> model = new HashMap<>();

		model.put("analyticsSrc", _originBaseURL + "/analytics/widget.html");
		model.put("appConfigJson", _getAppConfigJson(httpServletRequest));
		model.put("cdnBase", _originBaseURL + "/cdn");
		model.put(
			"extensionElements", _getExtensionElements(clientExtensions));
		model.put("tenantDisplayName", tenantConfig.displayName());

		httpServletResponse.setContentType("text/html; charset=UTF-8");

		PrintWriter printWriter = httpServletResponse.getWriter();

		printWriter.write(
			_pageRenderer.render("dashboard.html", requestContext, model));
	}

	private String _getAppConfigJson(HttpServletRequest httpServletRequest)
		throws IOException {

		String userId = httpServletRequest.getParameter("uid");

		if (userId == null) {
			userId = "u-1001";
		}

		return _objectMapper.writeValueAsString(
			Map.of(
				"features", List.of("charts", "ticker"), "locale", "en_US",
				"user", userId));
	}

	private String _getExtensionElements(
		List<ClientExtension> clientExtensions) {

		List<String> elements = new ArrayList<>();

		for (ClientExtension clientExtension : clientExtensions) {
			if (!"customElement".equals(clientExtension.type())) {
				continue;
			}

			elements.add(
				"\t\t\t<" + clientExtension.htmlElementName() + "></" +
					clientExtension.htmlElementName() + ">");
		}

		return String.join("\n", elements);
	}

	private final ClientExtensions _clientExtensions;
	private final ObjectMapper _objectMapper = new ObjectMapper();
	private final String _originBaseURL;
	private final PageRenderer _pageRenderer;
	private final TenantConfigStore _tenantConfigStore;

}
