package com.example.host.request;

/**
 * Per-request state: which tenant is being served, and which locale the user
 * has chosen.
 */
public final class RequestContext {

	public static final String ATTRIBUTE_NAME = RequestContext.class.getName();

	public RequestContext(String tenantId, String languageId) {
		_tenantId = tenantId;
		_languageId = languageId;
	}

	/**
	 * The locale this user picked, from their profile. Not the
	 * <code>Accept-Language</code> header.
	 */
	public String getLanguageId() {
		return _languageId;
	}

	public String getTenantId() {
		return _tenantId;
	}

	private final String _languageId;
	private final String _tenantId;

}
