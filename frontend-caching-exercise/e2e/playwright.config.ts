import {defineConfig} from '@playwright/test';

const port = process.env.PORT ?? '8080';

export default defineConfig({
	reporter: 'list',
	testDir: './tests',
	timeout: 60000,
	use: {
		baseURL: `http://localhost:${port}`,
	},
	webServer: {
		command: `../gradlew --project-dir .. :server:run -Pport=${port}`,
		reuseExistingServer: true,
		timeout: 240000,
		url: `http://localhost:${port}/t/acme/dashboard`,
	},
});
