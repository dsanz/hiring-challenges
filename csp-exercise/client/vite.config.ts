import react from '@vitejs/plugin-react';
import {defineConfig} from 'vite';

// Builds a single self-contained bundle straight into the server's static
// resources, so that `./gradlew run` is the only command needed.

export default defineConfig({
	define: {'process.env.NODE_ENV': '"production"'},
	build: {
		emptyOutDir: false,
		lib: {
			entry: 'src/main.tsx',
			fileName: () => 'app.js',
			formats: ['es'],
		},
		outDir: '../server/src/main/resources/static',
		rollupOptions: {
			output: {inlineDynamicImports: true},
		},
	},
	plugins: [react()],
});
