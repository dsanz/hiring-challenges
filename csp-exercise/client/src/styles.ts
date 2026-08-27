/**
 * A minimal CSS-in-JS runtime, shaped like the real ones.
 *
 * Rules are not in a stylesheet at build time. They are turned into a style
 * element and injected into the document as components render.
 */
export interface StyleCache {
	css(declarations: string): string;
}

export function createCache(options: {nonce?: string} = {}): StyleCache {
	let styleElement: HTMLStyleElement | null = null;

	const classNames = new Map<string, string>();

	function sheet(): HTMLStyleElement {
		if (!styleElement) {
			styleElement = document.createElement('style');

			if (options.nonce) {
				styleElement.nonce = options.nonce;
			}

			document.head.appendChild(styleElement);
		}

		return styleElement;
	}

	return {
		css(declarations: string): string {
			const cached = classNames.get(declarations);

			if (cached) {
				return cached;
			}

			const className = `c${classNames.size.toString(36)}`;

			sheet().appendChild(
				document.createTextNode(`.${className}{${declarations}}`)
			);

			classNames.set(declarations, className);

			return className;
		},
	};
}
