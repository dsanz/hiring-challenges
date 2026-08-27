// Evaluates a small expression language so that editors can write formulas
// like "value * 1.2". The extension author is not going to rewrite this.
function compile(expression) {
	return new Function('value', 'return (' + expression + ');');
}

class LegacyTicker extends HTMLElement {
	connectedCallback() {
		this.style.display = 'block';
		this.style.padding = '.5rem';

		try {
			const format = compile('value * 1.2');

			this.textContent = 'legacy-ticker: ' + format(100).toFixed(2);
			this.style.background = '#fff8e6';
		}
		catch (error) {
			this.textContent = 'legacy-ticker failed: ' + error.message;
			this.style.background = '#fdecec';
		}
	}
}

if (!customElements.get('legacy-ticker')) {
	customElements.define('legacy-ticker', LegacyTicker);
}
