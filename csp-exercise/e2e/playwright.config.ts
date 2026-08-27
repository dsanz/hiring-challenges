import {defineConfig} from '@playwright/test';

export default defineConfig({
	reporter: 'list',
	testDir: './tests',
	timeout: 60000,
	use: {
		baseURL: 'http://localhost:8080',
	},
	webServer: {
		command: '../gradlew --project-dir .. :server:run',
		reuseExistingServer: true,
		timeout: 240000,
		url: 'http://localhost:8080/t/acme/dashboard',
	},
});
