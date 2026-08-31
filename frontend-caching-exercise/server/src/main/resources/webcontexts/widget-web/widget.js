// A module in a second web context, loaded at runtime by the app.

export default function render(element, labels) {
	element.textContent = labels.widgets + ': 3 active';
	element.style.padding = '.5rem';
	element.style.background = '#eefbf1';
	element.style.borderRadius = '6px';
}
