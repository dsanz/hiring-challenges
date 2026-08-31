declare global {
	interface Window {
		__MODULES__?: Record<string, string>;
	}
}

export {};
