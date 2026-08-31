import react from '@vitejs/plugin-react';
import {defineConfig} from 'vite';

// Builds a single self-contained module straight into the app-web context, so
// that `./gradlew :server:run` is the only command needed. Source maps are on
// because the server publishes them too.

export default defineConfig({
	build: {
		emptyOutDir: false,
		lib: {
			entry: 'src/main.tsx',
			fileName: () => 'app.js',
			formats: ['es'],
		},
		outDir: '../server/src/main/resources/webcontexts/app-web',
		rollupOptions: {
			output: {inlineDynamicImports: true},
		},
		sourcemap: true,
	},
	define: {'process.env.NODE_ENV': '"production"'},
	plugins: [react()],
});
