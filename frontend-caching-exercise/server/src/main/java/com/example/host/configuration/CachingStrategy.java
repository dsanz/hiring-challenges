package com.example.host.configuration;

/**
 * How public URIs are derived for the files in a web context.
 */
public enum CachingStrategy {

	/**
	 * URIs are the plain file names. Nothing can be cached indefinitely,
	 * because any URI may start returning different bytes after a deploy.
	 */
	DO_NOT_USE_HASHES("do-not-use-hashes"),

	/**
	 * Each file's URI carries a hash of that file's own content.
	 */
	USE_ONE_HASH_PER_FILE("use-one-hash-per-file"),

	/**
	 * Every file in a web context carries the same hash, derived from the
	 * context as a whole.
	 */
	USE_ONE_HASH_PER_WEB_CONTEXT("use-one-hash-per-web-context");

	public static CachingStrategy parse(String value) {
		for (CachingStrategy cachingStrategy : values()) {
			if (cachingStrategy._value.equals(value)) {
				return cachingStrategy;
			}
		}

		return DO_NOT_USE_HASHES;
	}

	public String getValue() {
		return _value;
	}

	private CachingStrategy(String value) {
		_value = value;
	}

	private final String _value;

}
