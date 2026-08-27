// Feature-detection shim. Runs before anything else and never varies: the same
// bytes for every user, on every request, for the lifetime of the process.
window.__CAPS__ = {
	customElements: typeof window.customElements !== 'undefined',
	intl: typeof Intl !== 'undefined',
	modules: 'noModule' in document.createElement('script')
};
