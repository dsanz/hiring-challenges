/**
 * A VAT number field.
 *
 * It renders an input, asks the microservice whether the number is registered,
 * and shows the answer.
 *
 * TODO (T4): it does that on every keystroke, with nothing bounding how many
 * requests are in the air, nothing deciding which answer is still relevant by
 * the time it arrives, and nothing distinguishing "we asked and the number is
 * not registered" from "we could not ask".
 */

interface LookupResponse {
	address?: string;
	error?: string;
	name?: string;
	reason?: string;
	valid?: boolean | null;
}

// The status element the next response should be written into.

let activeStatusElement: HTMLElement | null = null;

class VatField extends HTMLElement {
	connectedCallback() {
		const field = this.getAttribute('data-field') ?? 'billing';

		this.innerHTML = `
			<label for="vat-${field}">VAT number</label>
			<input autocomplete="off" id="vat-${field}" type="text" placeholder="ESB12345678">
			<div class="vat-status" data-state="idle">Enter a VAT number</div>
		`;

		const input = this.querySelector('input') as HTMLInputElement;

		activeStatusElement = this.querySelector(
			'.vat-status'
		) as HTMLElement;

		input.addEventListener('input', () => this._lookup(input.value));
	}

	private async _lookup(vatId: string) {
		if (!vatId.trim()) {
			this._render('idle', 'Enter a VAT number');

			return;
		}

		this._render('checking', 'Checking...');

		const response = await fetch(
			`/o/vat/lookup?vatId=${encodeURIComponent(vatId)}`
		);

		const body: LookupResponse = await response.json();

		if (body.valid === true) {
			this._render('valid', `Registered: ${body.name}`);
		}
		else {
			this._render('invalid', 'Not a registered VAT number');
		}
	}

	private _render(state: string, message: string) {
		if (!activeStatusElement) {
			return;
		}

		activeStatusElement.setAttribute('data-state', state);
		activeStatusElement.textContent = message;
	}
}

if (!customElements.get('vat-field')) {
	customElements.define('vat-field', VatField);
}
