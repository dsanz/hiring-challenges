/**
 * Renders the status cell of the claims data set.
 *
 * For a claim that is still pending it offers Approve and Reject. Pressing one
 * sends a PATCH to the claim.
 *
 * TODO (T2, T3): after that PATCH, nothing happens. The row keeps showing what
 * it showed before, because nothing has told the data set that anything
 * changed and nothing has looked at what the server said. Some of those PATCHes
 * do not do what the button says they do.
 */

interface Claim {
	amount: number;
	description: string;
	employee: string;
	id: string;
	status: string;
	version: number;
}

interface CellRendererArgs {
	itemData: Claim;
	value: unknown;
}

function patch(claim: Claim, status: string) {
	fetch(`/o/c/claims/${claim.id}`, {
		body: JSON.stringify({status}),
		headers: {'Content-Type': 'application/json'},
		method: 'PATCH',
	});
}

const cellRenderer = ({itemData, value}: CellRendererArgs): HTMLElement => {
	const element = document.createElement('div');

	element.className = 'claim-decision';

	const label = document.createElement('span');

	label.className = 'claim-status';
	label.textContent = String(value);

	element.appendChild(label);

	if (itemData.status !== 'Pending') {
		return element;
	}

	for (const [text, status] of [
		['Approve', 'Approved'],
		['Reject', 'Rejected'],
	]) {
		const button = document.createElement('button');

		button.textContent = text;
		button.type = 'button';

		button.addEventListener('click', () => patch(itemData, status));

		element.appendChild(button);
	}

	return element;
};

export default cellRenderer;
