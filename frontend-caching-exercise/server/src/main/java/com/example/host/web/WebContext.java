package com.example.host.web;

import java.io.IOException;
import java.io.InputStream;

import java.util.List;

/**
 * A named bundle of files, mirroring the way modules are grouped and deployed.
 * Files are read from the classpath under <code>/webcontexts/{name}</code>.
 */
public class WebContext {

	public WebContext(String name, List<String> fileNames) {
		_name = name;
		_fileNames = List.copyOf(fileNames);
	}

	public List<String> getFileNames() {
		return _fileNames;
	}

	public String getName() {
		return _name;
	}

	public byte[] read(String fileName) throws IOException {
		try (InputStream inputStream = WebContext.class.getResourceAsStream(
				"/webcontexts/" + _name + "/" + fileName)) {

			if (inputStream == null) {
				return null;
			}

			return inputStream.readAllBytes();
		}
	}

	private final List<String> _fileNames;
	private final String _name;

}
