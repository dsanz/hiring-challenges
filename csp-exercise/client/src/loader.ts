export interface ClientExtension {
	csp: Record<string, string[]>;
	htmlElementName: string;
	name: string;
	type: string;
	urls: string[];
	useESM: boolean;
}

/**
 * The runtime module loader.
 *
 * Modules are not bundled with the application. They are fetched at runtime by
 * creating a script element and appending it to the document, which is how the
 * platform's own loader resolves modules and how every client extension gets
 * onto the page.
 */
export function loadModule(url: string, esm: boolean): Promise<void> {
	const existing = _loaded.get(url);

	if (existing) {
		return existing;
	}

	const promise = new Promise<void>((resolve, reject) => {
		const script = document.createElement('script');

		script.async = true;
		script.src = url;
		script.type = esm ? 'module' : 'text/javascript';

		script.onerror = () => reject(new Error(`Unable to load ${url}`));
		script.onload = () => resolve();

		document.head.appendChild(script);
	});

	_loaded.set(url, promise);

	return promise;
}

export async function loadExtensions(
	clientExtensions: ClientExtension[]
): Promise<string[]> {
	const failed: string[] = [];

	await Promise.all(
		clientExtensions.flatMap((clientExtension) =>
			clientExtension.urls.map((url) =>
				loadModule(url, clientExtension.useESM).catch(() => {
					failed.push(clientExtension.name);
				})
			)
		)
	);

	return failed;
}

const _loaded = new Map<string, Promise<void>>();
