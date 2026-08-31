package com.example.host.hashed;

import java.nio.charset.StandardCharsets;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import java.util.Base64;

/**
 * Puts a content hash into a file name and reads it back out.
 *
 * <p>
 * The hash goes between <code>.(</code> and <code>)</code> immediately before
 * the extension, so <code>app.js</code> becomes <code>app.(aB3$xY7@).js</code>.
 * </p>
 */
public class HashedFilesUtil {

	public static String addHash(String uri, String hash) {
		if (getHash(uri) != null) {
			throw new IllegalArgumentException("URI already hashed: " + uri);
		}

		int i = uri.lastIndexOf('.');

		if (i == -1) {
			throw new IllegalArgumentException(
				"URI has no file extension: " + uri);
		}

		return uri.substring(0, i) + ".(" + hash + ")" + uri.substring(i);
	}

	/**
	 * Digests the content and keeps the leading 8 bytes, base64 encoded with
	 * the characters that are awkward in a URI swapped out.
	 */
	public static String computeHash(byte[] content) {
		try {
			MessageDigest messageDigest = MessageDigest.getInstance("SHA-256");

			byte[] digest = messageDigest.digest(content);

			byte[] truncated = new byte[8];

			System.arraycopy(digest, 0, truncated, 0, truncated.length);

			String encoded = Base64.getEncoder().encodeToString(truncated);

			encoded = encoded.replace('+', '$');
			encoded = encoded.replace('/', '@');

			return encoded.replace("=", "");
		}
		catch (NoSuchAlgorithmException noSuchAlgorithmException) {
			throw new IllegalStateException(noSuchAlgorithmException);
		}
	}

	public static String computeHash(String content) {
		return computeHash(content.getBytes(StandardCharsets.UTF_8));
	}

	/**
	 * The hash in a URI, or <code>null</code> if it carries none.
	 */
	public static String getHash(String uri) {
		int start = uri.lastIndexOf(".(");

		if (start == -1) {
			return null;
		}

		int end = uri.indexOf(')', start);

		if (end == -1) {
			return null;
		}

		return uri.substring(start + 2, end);
	}

	/**
	 * The URI with any hash taken back out.
	 */
	public static String removeHash(String uri) {
		int start = uri.lastIndexOf(".(");

		if (start == -1) {
			return uri;
		}

		int end = uri.indexOf(')', start);

		if (end == -1) {
			return uri;
		}

		return uri.substring(0, start) + uri.substring(end + 1);
	}

}
