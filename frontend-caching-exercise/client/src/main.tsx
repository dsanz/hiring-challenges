import {StrictMode, useEffect, useState} from 'react';
import {createRoot} from 'react-dom/client';

import {loadModule} from './loader';

type Labels = Record<string, string>;

type WidgetModule = {
	default: (element: HTMLElement, labels: Labels) => void;
};

function Dashboard() {
	const [labels, setLabels] = useState<Labels | null>(null);

	useEffect(() => {
		loadModule<{default: Labels}>('language/app-web').then((module) =>
			setLabels(module.default)
		);
	}, []);

	useEffect(() => {
		if (!labels) {
			return;
		}

		loadModule<WidgetModule>('widget-web/widget.js').then((module) => {
			const element = document.getElementById('widget');

			if (element) {
				module.default(element, labels);
			}
		});
	}, [labels]);

	if (!labels) {
		return <p>Loading...</p>;
	}

	return (
		<>
			<h2>{labels.dashboard}</h2>
			<div id="widget" />
			<p>
				<button type="button">{labels.refresh}</button>
			</p>
		</>
	);
}

const manifest = document.getElementById('manifest');

if (manifest) {
	manifest.textContent = JSON.stringify(window.__MODULES__ ?? {}, null, 2);
}

const container = document.getElementById('app');

if (container) {
	container.textContent = '';

	createRoot(container).render(
		<StrictMode>
			<Dashboard />
		</StrictMode>
	);
}
