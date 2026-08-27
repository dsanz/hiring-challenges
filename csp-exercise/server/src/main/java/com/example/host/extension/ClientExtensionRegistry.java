package com.example.host.extension;

import java.util.ArrayList;
import java.util.List;

/**
 * Resolves the client extensions placed on a page into their descriptors.
 *
 * <p>
 * TODO (T2): this implementation renders the page correctly and is otherwise
 * indefensible. It holds no state, so every render re-fetches every descriptor
 * from a third party while the request thread waits. Give the registry the
 * state it needs, and decide what a page renders when a descriptor is stale,
 * unreachable, slow, or malformed.
 * </p>
 */
public class ClientExtensionRegistry {

	public ClientExtensionRegistry(DescriptorFetcher descriptorFetcher) {
		_descriptorFetcher = descriptorFetcher;
	}

	public List<ClientExtension> getClientExtensions(
		List<String> descriptorURLs) {

		List<ClientExtension> clientExtensions = new ArrayList<>();

		for (String descriptorURL : descriptorURLs) {
			try {
				clientExtensions.add(_descriptorFetcher.fetch(descriptorURL));
			}
			catch (Exception exception) {
				System.err.println(
					"Unable to fetch " + descriptorURL + ": " +
						exception.getMessage());
			}
		}

		return clientExtensions;
	}

	private final DescriptorFetcher _descriptorFetcher;

}
