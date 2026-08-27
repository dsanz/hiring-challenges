package com.example.host.csp;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

/**
 * The two tests below are disabled because the code they describe does not
 * exist yet. Enable them, write them, and make them pass.
 */
public class CspPolicyTest {

	/**
	 * A tenant, a client extension descriptor, and a route override must not be
	 * able to widen the policy beyond what the platform ceiling permits, no
	 * matter what they ask for.
	 */
	@Disabled("T1")
	@Test
	public void testLowerLayersCannotEscapeTheCeiling() {
		Assertions.fail("Not implemented");
	}

	/**
	 * Two responses must never carry the same nonce.
	 */
	@Disabled("T3")
	@Test
	public void testNoncesAreUniquePerResponse() {
		Assertions.fail("Not implemented");
	}

}
