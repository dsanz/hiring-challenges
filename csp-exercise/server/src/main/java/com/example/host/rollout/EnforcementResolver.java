package com.example.host.rollout;

/**
 * Decides whether a given response is enforced, reported, or left alone.
 *
 * <p>
 * TODO (T5, optional): resolve per tenant and per route, changeable at runtime
 * without a deploy, with a kill switch. Support a staged percentage rollout in
 * which a user does not flip between modes from one request to the next, and
 * lands in the same bucket on every server instance.
 * </p>
 */
public class EnforcementResolver {
}
