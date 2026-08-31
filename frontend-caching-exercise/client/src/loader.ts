import {resolve} from './modules';

/**
 * Loads a module by name, through whatever `resolve` says its URL is.
 */
export function loadModule<T>(name: string): Promise<T> {
	const url = resolve(name);

	const existing = _loaded.get(url);

	if (existing) {
		return existing as Promise<T>;
	}

	const promise = import(/* @vite-ignore */ url);

	_loaded.set(url, promise);

	return promise as Promise<T>;
}

const _loaded = new Map<string, Promise<unknown>>();
