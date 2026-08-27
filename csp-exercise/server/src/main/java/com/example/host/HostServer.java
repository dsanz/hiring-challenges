package com.example.host;

import com.example.host.csp.CspFilter;
import com.example.host.extension.ClientExtensionRegistry;
import com.example.host.extension.ClientExtensions;
import com.example.host.extension.DescriptorFetcher;
import com.example.host.extension.ExtensionPlacementService;
import com.example.host.render.DynamicIncludeRegistry;
import com.example.host.render.PageRenderer;
import com.example.host.report.CspReportServlet;
import com.example.host.request.RequestContextFilter;
import com.example.host.tenant.TenantConfigStore;
import com.example.host.web.DashboardServlet;
import com.example.host.web.ExtensionOriginServlet;
import com.example.host.web.RootServlet;
import com.example.host.web.ShimDynamicInclude;
import com.example.host.web.StaticServlet;

import jakarta.servlet.Filter;
import jakarta.servlet.Servlet;

import java.io.File;

import org.apache.catalina.Context;
import org.apache.catalina.startup.Tomcat;
import org.apache.tomcat.util.descriptor.web.FilterDef;
import org.apache.tomcat.util.descriptor.web.FilterMap;

/**
 * Starts the page host on 8080 and the third-party extension origins on 8081.
 *
 * <p>
 * Everything here is wiring. It is finished, and you should not need to change
 * it except to register something new.
 * </p>
 */
public class HostServer {

	public static final int ORIGIN_PORT = 8081;

	public static final int PORT = 8080;

	public static void main(String[] args) throws Exception {
		_startExtensionOrigins();
		_startHost();

		System.out.println();
		System.out.println("  Dashboard  http://localhost:" + PORT + "/t/acme/dashboard");
		System.out.println("  Origins    http://localhost:" + ORIGIN_PORT + "/acme-charts/descriptor.json");
		System.out.println();
	}

	private static Context _createContext(Tomcat tomcat, String name) {
		File baseDir = new File(System.getProperty("java.io.tmpdir"), name);

		baseDir.mkdirs();

		tomcat.setBaseDir(baseDir.getAbsolutePath());

		return tomcat.addContext("", baseDir.getAbsolutePath());
	}

	private static void _addFilter(
		Context context, String name, Filter filter, String urlPattern) {

		FilterDef filterDef = new FilterDef();

		filterDef.setFilterName(name);
		filterDef.setFilter(filter);

		context.addFilterDef(filterDef);

		FilterMap filterMap = new FilterMap();

		filterMap.setFilterName(name);
		filterMap.addURLPattern(urlPattern);

		context.addFilterMap(filterMap);
	}

	private static void _addServlet(
		Context context, String name, Servlet servlet, String urlPattern) {

		Tomcat.addServlet(context, name, servlet);

		context.addServletMappingDecoded(urlPattern, name);
	}

	private static void _startExtensionOrigins() throws Exception {
		Tomcat tomcat = new Tomcat();

		tomcat.setPort(ORIGIN_PORT);
		tomcat.getConnector();

		Context context = _createContext(tomcat, "csp-exercise-origins");

		_addServlet(context, "origin", new ExtensionOriginServlet(), "/*");

		tomcat.start();
	}

	private static void _startHost() throws Exception {
		String originBaseURL = "http://localhost:" + ORIGIN_PORT;

		DynamicIncludeRegistry dynamicIncludeRegistry =
			new DynamicIncludeRegistry();

		dynamicIncludeRegistry.register(new ShimDynamicInclude());

		PageRenderer pageRenderer = new PageRenderer(dynamicIncludeRegistry);

		ClientExtensions clientExtensions = new ClientExtensions(
			new ExtensionPlacementService(originBaseURL),
			new ClientExtensionRegistry(new DescriptorFetcher()));

		Tomcat tomcat = new Tomcat();

		tomcat.setPort(PORT);
		tomcat.getConnector();

		Context context = _createContext(tomcat, "csp-exercise-host");

		// Order matters. The request context has to exist before anything else
		// runs, and has to outlive everything else.

		_addFilter(
			context, "requestContext", new RequestContextFilter(), "/*");
		_addFilter(context, "csp", new CspFilter(), "/*");

		_addServlet(
			context, "dashboard",
			new DashboardServlet(
				pageRenderer, clientExtensions, new TenantConfigStore(),
				originBaseURL),
			"/t/*");
		_addServlet(context, "static", new StaticServlet(), "/static/*");
		_addServlet(
			context, "cspReport", new CspReportServlet(), "/o/csp-report");
		_addServlet(context, "root", new RootServlet(), "/");

		tomcat.start();

		Thread thread = new Thread(() -> tomcat.getServer().await());

		thread.setDaemon(false);

		thread.start();
	}

}
