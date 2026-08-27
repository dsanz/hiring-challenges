import {expect, test} from '@playwright/test';

// Enable these and make them pass. A test that asserts on the header string
// proves the header exists, not that the browser did anything about it.

test.skip('an injected inline script does not execute', async ({page}) => {
	await page.goto('/t/acme/dashboard');

	await page.evaluate(() => {
		const script = document.createElement('script');

		script.textContent = 'window.__PWNED__ = true;';

		document.body.appendChild(script);
	});

	expect(await page.evaluate(() => '__PWNED__' in window)).toBe(false);
});

test.skip('the legitimate page still works under enforcement', async ({
	page,
}) => {
	const violations: string[] = [];

	page.on('console', (message) => {
		if (message.text().includes('Content Security Policy')) {
			violations.push(message.text());
		}
	});

	await page.goto('/t/acme/dashboard');

	await expect(page.getByText('acme-charts is running')).toBeVisible();

	expect(violations).toEqual([]);
});
