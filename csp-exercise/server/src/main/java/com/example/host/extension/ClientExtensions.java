package com.example.host.extension;

import com.example.host.request.RequestContext;

import java.util.List;

/**
 * Resolves the client extensions for the current request, once. Anything that
 * needs the placed extensions &mdash; the renderer, a filter &mdash; can call
 * this and will share the same result for the life of the request.
 */
public class ClientExtensions {

	public ClientExtensions(
		ExtensionPlacementService extensionPlacementService,
		ClientExtensionRegistry clientExtensionRegistry) {

		_extensionPlacementService = extensionPlacementService;
		_clientExtensionRegistry = clientExtensionRegistry;
	}

	@SuppressWarnings("unchecked")
	public List<ClientExtension> resolve(RequestContext requestContext) {
		Object attribute = requestContext.getAttribute(_ATTRIBUTE_NAME);

		if (attribute != null) {
			return (List<ClientExtension>)attribute;
		}

		List<String> descriptorURLs =
			_extensionPlacementService.getDescriptorURLs(
				requestContext.getTenantId(), requestContext.getRoute());

		List<ClientExtension> clientExtensions =
			_clientExtensionRegistry.getClientExtensions(descriptorURLs);

		requestContext.setAttribute(_ATTRIBUTE_NAME, clientExtensions);

		return clientExtensions;
	}

	private static final String _ATTRIBUTE_NAME =
		ClientExtensions.class.getName();

	private final ClientExtensionRegistry _clientExtensionRegistry;
	private final ExtensionPlacementService _extensionPlacementService;

}
