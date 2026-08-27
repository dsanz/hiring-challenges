// A well-behaved client extension. Registers a custom element and nothing else.
class AcmeCharts extends HTMLElement {
	connectedCallback() {
		this.textContent = 'acme-charts is running';
		this.style.display = 'block';
		this.style.padding = '.5rem';
		this.style.background = '#eefbf1';
	}
}

if (!customElements.get('acme-charts')) {
	customElements.define('acme-charts', AcmeCharts);
}
