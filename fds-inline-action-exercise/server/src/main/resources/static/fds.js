// A small frontend data set.
//
// It does what the real one does, in the places that matter to a cell
// renderer: it loads a page of items from a collection endpoint, renders a
// table, and for one column it hands off to a client extension. It reloads
// when something fires `fds-update-display` with its id, and announces
// `fds-display-updated` when it has.
//
// You are not asked to change this file, but you will want to read it.

const EVENTS = {
	DISPLAY_UPDATED: 'fds-display-updated',
	UPDATE_DISPLAY: 'fds-update-display',
};

const FIELDS = [
	{fieldName: 'id', label: 'Claim'},
	{fieldName: 'employee', label: 'Employee'},
	{fieldName: 'description', label: 'Description'},
	{fieldName: 'amount', label: 'Amount'},
	{contentRendererClientExtension: true, fieldName: 'status', label: 'Status'},
];

export async function mount(root) {
	const id = root.dataset.fdsId;
	const apiURL = root.dataset.apiUrl;

	const state = {page: 1, pageSize: 5, status: root.dataset.status ?? ''};

	// The cell renderer client extension. Its default export is the
	// htmlElementBuilder the platform calls with {itemData, value}.

	const module = await import(root.dataset.cellRendererUrl);

	const htmlElementBuilder = module.default;

	if (typeof htmlElementBuilder !== 'function') {
		throw new Error(
			'The cell renderer client extension must default-export a function'
		);
	}

	async function load() {
		const url = new URL(apiURL, window.location.origin);

		url.searchParams.set('page', String(state.page));
		url.searchParams.set('pageSize', String(state.pageSize));

		if (state.status) {
			url.searchParams.set('status', state.status);
		}

		const response = await fetch(url);
		const body = await response.json();

		render(body);

		Liferay.fire(EVENTS.DISPLAY_UPDATED, {id});
	}

	function render(body) {
		root.textContent = '';

		const table = document.createElement('table');

		const head = document.createElement('tr');

		for (const field of FIELDS) {
			const th = document.createElement('th');

			th.textContent = field.label;

			head.appendChild(th);
		}

		table.appendChild(head);

		for (const itemData of body.items) {
			const row = document.createElement('tr');

			row.dataset.itemId = itemData.id;

			for (const field of FIELDS) {
				const td = document.createElement('td');

				const value = itemData[field.fieldName];

				if (field.contentRendererClientExtension) {
					td.appendChild(htmlElementBuilder({itemData, value}));
				}
				else {
					td.textContent = String(value);
				}

				row.appendChild(td);
			}

			table.appendChild(row);
		}

		root.appendChild(table);
		root.appendChild(pagination(body));
	}

	function pagination(body) {
		const nav = document.createElement('div');

		nav.className = 'fds-pagination';

		const pages = Math.max(1, Math.ceil(body.totalCount / body.pageSize));

		const label = document.createElement('span');

		label.textContent = `Page ${body.page} of ${pages} — ${body.totalCount} claims`;

		nav.appendChild(label);

		for (const [text, delta] of [
			['Previous', -1],
			['Next', 1],
		]) {
			const button = document.createElement('button');

			button.textContent = text;
			button.type = 'button';
			button.disabled =
				body.page + delta < 1 || body.page + delta > pages;

			button.addEventListener('click', () => {
				state.page += delta;

				load();
			});

			nav.appendChild(button);
		}

		return nav;
	}

	Liferay.on(EVENTS.UPDATE_DISPLAY, (payload) => {
		if (payload && payload.id === id) {
			load();
		}
	});

	await load();
}
