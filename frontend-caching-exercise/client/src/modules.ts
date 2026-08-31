/**
 * Turns a module name into the URL it is published at.
 *
 * TODO (T5): these URLs are hardcoded, so the client always asks for the plain
 * name no matter what the server decided to publish. The server already puts a
 * manifest on the page — see `window.__MODULES__` — saying where each module
 * actually lives. Resolve through that instead.
 */
export function resolve(name: string): string {
	const url = _HARDCODED[name];

	if (!url) {
		throw new Error(`No URL known for module ${name}`);
	}

	return url;
}

const _HARDCODED: Record<string, string> = {
	'app-web/app.js': '/o/js/app-web/app.js',
	'language/app-web': '/o/js/language/app-web/all.js',
	'widget-web/widget.js': '/o/js/widget-web/widget.js',
};
