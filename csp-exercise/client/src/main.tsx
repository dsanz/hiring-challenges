import {StrictMode, useEffect, useState} from 'react';
import {createRoot} from 'react-dom/client';

import {readAppConfig} from './config';
import {loadExtensions} from './loader';
import {createCache} from './styles';

// TODO (T4): the style cache has somewhere to put a nonce and nothing is
// putting one there.

const {css} = createCache();

function Dashboard() {
	const [failed, setFailed] = useState<string[]>([]);
	const [ready, setReady] = useState(false);

	const config = readAppConfig();

	useEffect(() => {
		loadExtensions(window.__EXTENSIONS__ ?? []).then((names) => {
			setFailed(names);
			setReady(true);
		});
	}, []);

	const panel = css(
		'border-left:3px solid #5b8def;padding:.25rem .75rem;margin:.5rem 0'
	);

	return (
		<div className={panel}>
			<div>
				Signed in as <strong>{config.user}</strong> ({config.locale})
			</div>
			<div>Features: {config.features.join(', ')}</div>
			<div>
				Extensions: {ready ? 'loaded' : 'loading...'}
				{failed.length > 0 ? ` (failed: ${failed.join(', ')})` : ''}
			</div>
		</div>
	);
}

window.refreshWidgets = () => {
	const status = document.getElementById('status');

	if (status) {
		status.textContent = `refreshed at ${new Date().toLocaleTimeString()}`;
	}
};

const container = document.getElementById('app');

if (container) {
	createRoot(container).render(
		<StrictMode>
			<Dashboard />
		</StrictMode>
	);
}
