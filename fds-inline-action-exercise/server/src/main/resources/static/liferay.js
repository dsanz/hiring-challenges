// The bits of the global the platform provides that a data set and a cell
// renderer actually use. Same names, same semantics, so code written against
// this works unchanged against the real thing.

const target = new EventTarget();

window.Liferay = {
	detach(name, handler) {
		target.removeEventListener(name, handler._wrapped ?? handler);
	},

	fire(name, payload) {
		target.dispatchEvent(new CustomEvent(name, {detail: payload}));
	},

	on(name, handler) {
		const wrapped = (event) => handler(event.detail);

		handler._wrapped = wrapped;

		target.addEventListener(name, wrapped);
	},
};
