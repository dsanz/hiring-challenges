package com.example.fds;

import com.example.fds.api.ClaimServlet;
import com.example.fds.api.ClaimsCollectionServlet;
import com.example.fds.cx.ClientExtensionRegistry;
import com.example.fds.cx.ClientExtensionServlet;
import com.example.fds.model.ClaimRepository;
import com.example.fds.web.ClaimsPageServlet;
import com.example.fds.web.StaticServlet;

import jakarta.servlet.Servlet;

import java.io.File;

import java.nio.file.Path;
import java.nio.file.Paths;

import org.apache.catalina.Context;
import org.apache.catalina.startup.Tomcat;

/**
 * Starts the claims host.
 *
 * <p>
 * Everything here is wiring. It is finished, and you should not need to change
 * it except to register something new.
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
		System.out.println("  Claims  http://localhost:" + PORT + "/claims");
		System.out.println();

		tomcat.getServer(
		).await();
	}

	public static Tomcat start(int port) throws Exception {
		ClientExtensionRegistry clientExtensionRegistry =
			new ClientExtensionRegistry(_clientExtensionDir());

		ClaimRepository claimRepository = new ClaimRepository();

		Tomcat tomcat = new Tomcat();

		File baseDir = new File(
			System.getProperty("java.io.tmpdir"), "fds-host-" + port);

		baseDir.mkdirs();

		tomcat.setBaseDir(baseDir.getAbsolutePath());
		tomcat.setPort(port);
		tomcat.getConnector();

		Context context = tomcat.addContext("", baseDir.getAbsolutePath());

		_addServlet(
			context, "claimsPage",
			new ClaimsPageServlet(clientExtensionRegistry), "/claims");
		_addServlet(
			context, "clientExtension",
			new ClientExtensionServlet(clientExtensionRegistry),
			ClientExtensionServlet.URI_PREFIX + "*");
		_addServlet(context, "static", new StaticServlet(), "/static/*");
		_addServlet(
			context, "claimsCollection",
			new ClaimsCollectionServlet(claimRepository), "/o/c/claims");
		_addServlet(
			context, "claim", new ClaimServlet(claimRepository),
			"/o/c/claims/*");

		tomcat.start();

		return tomcat;
	}

	private static void _addServlet(
		Context context, String name, Servlet servlet, String urlPattern) {

		Tomcat.addServlet(context, name, servlet);

		context.addServletMappingDecoded(urlPattern, name);
	}

	private static Path _clientExtensionDir() {
		Path path = Paths.get("client-extension");

		if (path.toFile(
			).isDirectory()) {

			return path.toAbsolutePath();
		}

		return Paths.get("..", "client-extension"
		).toAbsolutePath(
		).normalize();
	}

}
