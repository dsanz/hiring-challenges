package com.example.host.csp;

/**
 * Composes a {@link CspPolicy} for the page being rendered.
 *
 * <p>
 * TODO (T1): compose from four layers &mdash; the platform ceiling, the tenant
 * configuration, the client extensions actually placed on this page, and any
 * per-route override. Nothing below the platform layer may widen what the
 * ceiling permits. Every source that reaches you from configuration or from a
 * third-party descriptor is untrusted input.
 * </p>
 */
public class CspPolicyBuilder {
}
