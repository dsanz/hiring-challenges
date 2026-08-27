// The extension itself is fine. Its origin is not.
class SlowWidget extends HTMLElement {
	connectedCallback() {
		this.textContent = 'slow-widget is running';
		this.style.display = 'block';
		this.style.padding = '.5rem';
		this.style.background = '#f2f0ff';
	}
}

if (!customElements.get('slow-widget')) {
	customElements.define('slow-widget', SlowWidget);
}
