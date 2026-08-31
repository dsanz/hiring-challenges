package com.example.host.resource;

import com.example.host.hashed.HashedFilesUtil;

import java.io.ByteArrayInputStream;
import java.io.InputStream;

/**
 * A resource backed by bytes already in memory.
 */
public class ByteArrayFrontendResource implements FrontendResource {

	public ByteArrayFrontendResource(
		byte[] content, String contentType, boolean immutable, long maxAge,
		boolean sendNoCache, boolean isPrivate, boolean computeETag) {

		_content = content;
		_contentType = contentType;
		_immutable = immutable;
		_maxAge = maxAge;
		_sendNoCache = sendNoCache;
		_private = isPrivate;

		_eTag = computeETag ?
			"\"" + HashedFilesUtil.computeHash(content) + "\"" : null;
	}

	@Override
	public String getContentType() {
		return _contentType;
	}

	@Override
	public String getETag() {
		return _eTag;
	}

	@Override
	public InputStream getInputStream() {
		return new ByteArrayInputStream(_content);
	}

	@Override
	public long getMaxAge() {
		return _maxAge;
	}

	@Override
	public boolean isImmutable() {
		return _immutable;
	}

	@Override
	public boolean isPrivate() {
		return _private;
	}

	@Override
	public boolean isSendNoCache() {
		return _sendNoCache;
	}

	private final byte[] _content;
	private final String _contentType;
	private final String _eTag;
	private final boolean _immutable;
	private final long _maxAge;
	private final boolean _private;
	private final boolean _sendNoCache;

}
