export interface AppConfig {
	features: string[];
	locale: string;
	user: string;
}

/**
 * Reads the per-user bootstrap data the server put on the page.
 *
 * TODO (T4): today the server writes this into an executable inline script and
 * this function picks it off the global object. Where the data lives is your
 * call; this function is where the client reads it from.
 */
export function readAppConfig(): AppConfig {
	const appConfig = window.__APP_CONFIG__;

	if (!appConfig) {
		throw new Error('No bootstrap configuration on the page');
	}

	return appConfig as AppConfig;
}
