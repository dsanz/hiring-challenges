import type {ClientExtension} from './loader';

declare global {
	interface Window {
		__APP_CONFIG__?: unknown;
		__CAPS__?: Record<string, boolean>;
		__EXTENSIONS__?: ClientExtension[];
		refreshWidgets?: () => void;
	}
}

export {};
