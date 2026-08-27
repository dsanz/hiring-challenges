package com.example.host.request;

import com.example.host.render.ScriptData;

import java.util.HashMap;
import java.util.Map;

/**
 * Per-request state. Created by {@link RequestContextFilter} at the start of
 * every request and discarded at the end of it.
 *
 * <p>
 * This is the request-scoped holder the platform gives you. Anything that has
 * to be unique per response belongs here.
 * </p>
 */
public final class RequestContext {

	public static final String ATTRIBUTE_NAME = RequestContext.class.getName();

	/**
	 * The context bound to the thread currently serving a request, or
	 * <code>null</code> outside a request.
	 */
	public static RequestContext current() {
		return _current.get();
	}

	public RequestContext(String tenantId, String route) {
		_tenantId = tenantId;
		_route = route;
	}

	public Object getAttribute(String name) {
		return _attributes.get(name);
	}

	public String getRoute() {
		return _route;
	}

	public ScriptData getScriptData() {
		return _scriptData;
	}

	public String getTenantId() {
		return _tenantId;
	}

	public void setAttribute(String name, Object value) {
		_attributes.put(name, value);
	}

	protected static void clearCurrent() {
		_current.remove();
	}

	protected static void setCurrent(RequestContext requestContext) {
		_current.set(requestContext);
	}

	private static final ThreadLocal<RequestContext> _current = new ThreadLocal<>();

	private final Map<String, Object> _attributes = new HashMap<>();
	private final String _route;
	private final ScriptData _scriptData = new ScriptData();
	private final String _tenantId;

}
