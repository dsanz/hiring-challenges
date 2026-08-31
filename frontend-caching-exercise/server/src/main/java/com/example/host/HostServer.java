package com.example.host;

import com.example.host.configuration.TenantConfigStore;
import com.example.host.hashed.HashedFilesRegistry;
import com.example.host.request.RequestContextFilter;
import com.example.host.resource.handler.FrontendResourceRequestHandler;
import com.example.host.resource.handler.JavaScriptFrontendResourceRequestHandler;
import com.example.host.resource.handler.LanguageFrontendResourceRequestHandler;
import com.example.host.servlet.filter.FrontendResourceFilter;
import com.example.host.web.DashboardServlet;
import com.example.host.web.Language;
import com.example.host.web.RootServlet;
import com.example.host.web.WebContextRegistry;

import jakarta.servlet.Filter;
import jakarta.servlet.Servlet;

import java.io.File;

import java.util.List;

import org.apache.catalina.Context;
import org.apache.catalina.startup.Tomcat;
import org.apache.tomcat.util.descriptor.web.FilterDef;
import org.apache.tomcat.util.descriptor.web.FilterMap;

/**
 * Starts the resource host on 8080.
 *
 * <p>
 * Everything here is wiring. It is finished, and you should not need to change
 * it except to register a new handler.
 * </p>
 */
public class HostServer {

	/**
	 * Defaults to 8080. Override with <code>-Pport=8090</code> when something
	 * else already owns it.
	 */
	public static final int PORT = Integer.getInteger("port", 8080);

	public static void main(String[] args) throws Exception {
		Tomcat tomcat = start(PORT);

		System.out.println();
		System.out.println("  Dashboard  http://localhost:" + PORT + "/t/acme/dashboard");
		System.out.println("  Spanish    http://localhost:" + PORT + "/t/acme/dashboard?locale=es_ES");
		System.out.println("  Globex     http://localhost:" + PORT + "/t/globex/dashboard");
		System.out.println();

		tomcat.getServer(
		).await();
	}

	/**
	 * Starts the server and returns it. Tests use this to run on their own
	 * port.
	 */
	public static Tomcat start(int port) throws Exception {
		WebContextRegistry webContextRegistry = new WebContextRegistry();
		TenantConfigStore tenantConfigStore = new TenantConfigStore();

		HashedFilesRegistry hashedFilesRegistry = new HashedFilesRegistry(
			webContextRegistry, tenantConfigStore);

		List<FrontendResourceRequestHandler> frontendResourceRequestHandlers =
			List.of(
				new LanguageFrontendResourceRequestHandler(
					new Language(), tenantConfigStore),
				new JavaScriptFrontendResourceRequestHandler(
					hashedFilesRegistry, tenantConfigStore));

		Tomcat tomcat = new Tomcat();

		tomcat.setPort(port);
		tomcat.getConnector();

		File baseDir = new File(
			System.getProperty("java.io.tmpdir"),
			"frontend-caching-exercise-" + port);

		baseDir.mkdirs();

		tomcat.setBaseDir(baseDir.getAbsolutePath());

		Context context = tomcat.addContext("", baseDir.getAbsolutePath());

		_addFilter(
			context, "requestContext", new RequestContextFilter(), "/*");
		_addFilter(
			context, "frontendResource",
			new FrontendResourceFilter(frontendResourceRequestHandlers), "/*");

		_addServlet(
			context, "dashboard",
			new DashboardServlet(
				hashedFilesRegistry, tenantConfigStore, webContextRegistry),
			"/t/*");
		_addServlet(context, "root", new RootServlet(), "/");

		tomcat.start();

		return tomcat;
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

}
