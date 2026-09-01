import {defineConfig} from 'vite';

// Assembles into build/static under a hashed name, which is what the
// `index.*.js` pattern in client-extension.yaml resolves against.

export default defineConfig({
	build: {
		emptyOutDir: true,
		outDir: 'build/static',
		rollupOptions: {
			// The entry's default export is the contract. Without this,
			// Rollup treats it as an application entry and shakes it away.
			input: 'src/index.ts',
			preserveEntrySignatures: 'strict',
			output: {
				entryFileNames: 'index.[hash].js',
				format: 'es',
			},
		},
	},
});
