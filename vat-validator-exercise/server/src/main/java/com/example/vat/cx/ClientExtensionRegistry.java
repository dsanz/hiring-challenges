package com.example.vat.cx;

import java.io.IOException;
import java.io.InputStream;

import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import org.yaml.snakeyaml.Yaml;

/**
 * Reads <code>client-extension.yaml</code> and resolves the URL patterns it
 * declares against the assembled output.
 *
 * <p>
 * This is finished, and it is deliberately unforgiving: a descriptor that names
 * a file which is not there, or omits something the platform requires, fails
 * loudly at startup instead of producing a page with a silently missing
 * element.
 * </p>
 */
public class ClientExtensionRegistry {

	public ClientExtensionRegistry(Path clientExtensionDir) throws IOException {
		_clientExtensionDir = clientExtensionDir;

		_clientExtension = _read();
	}

	public ClientExtension getClientExtension() {
		return _clientExtension;
	}

	/**
	 * The assembled file for a resolved name, or <code>null</code>.
	 */
	public byte[] getStaticResource(String fileName) throws IOException {
		Path path = _staticDir(
		).resolve(
			fileName
		).normalize();

		if (!path.startsWith(_staticDir()) || !Files.isRegularFile(path)) {
			return null;
		}

		return Files.readAllBytes(path);
	}

	/**
	 * Every declared URL pattern, expanded against the assembled files.
	 */
	public List<String> resolveCssURLs() throws IOException {
		return _resolve(_clientExtension.getCssURLPatterns());
	}

	public List<String> resolveURLs() throws IOException {
		return _resolve(_clientExtension.getUrlPatterns());
	}

	@SuppressWarnings("unchecked")
	private ClientExtension _read() throws IOException {
		Path path = _clientExtensionDir.resolve("client-extension.yaml");

		if (!Files.isRegularFile(path)) {
			throw new IOException("Missing " + path);
		}

		Map<String, Object> document;

		try (InputStream inputStream = Files.newInputStream(path)) {
			document = new Yaml().load(inputStream);
		}

		if (document == null) {
			throw new IOException("Empty " + path);
		}

		for (Map.Entry<String, Object> entry : document.entrySet()) {
			if (entry.getKey(
				).equals(
					"assemble"
				) ||
				!(entry.getValue() instanceof Map)) {

				continue;
			}

			Map<String, Object> body = (Map<String, Object>)entry.getValue();

			String type = String.valueOf(body.get("type"));

			if (!type.equals("customElement")) {
				throw new IOException(
					"Expected a customElement client extension, found " + type);
			}

			return new ClientExtension(
				entry.getKey(), String.valueOf(body.get("name")), type,
				(String)body.get("htmlElementName"),
				Boolean.TRUE.equals(body.get("instanceable")),
				Boolean.TRUE.equals(body.get("useESM")),
				_list(body.get("urls")), _list(body.get("cssURLs")));
		}

		throw new IOException("No client extension declared in " + path);
	}

	@SuppressWarnings("unchecked")
	private List<String> _list(Object value) {
		if (value == null) {
			return List.of();
		}

		if (value instanceof List) {
			List<String> values = new ArrayList<>();

			for (Object item : (List<Object>)value) {
				values.add(String.valueOf(item));
			}

			return values;
		}

		return List.of(String.valueOf(value));
	}

	private List<String> _resolve(List<String> patterns) throws IOException {
		List<String> fileNames = new ArrayList<>();

		for (String pattern : patterns) {
			List<String> matches = new ArrayList<>();

			try (DirectoryStream<Path> directoryStream =
					Files.newDirectoryStream(_staticDir(), pattern)) {

				for (Path path : directoryStream) {
					matches.add(
						path.getFileName(
						).toString());
				}
			}

			if (matches.isEmpty()) {
				throw new IOException(
					"Nothing in " + _staticDir() + " matches \"" + pattern +
						"\" declared in client-extension.yaml");
			}

			Collections.sort(matches);

			fileNames.addAll(matches);
		}

		return fileNames;
	}

	private Path _staticDir() {
		return _clientExtensionDir.resolve("build/static");
	}

	private final ClientExtension _clientExtension;
	private final Path _clientExtensionDir;

}
