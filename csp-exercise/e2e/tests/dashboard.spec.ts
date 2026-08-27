import {expect, test} from '@playwright/test';

// Green on a fresh clone. If these go red, something you changed broke the app.

test('the dashboard renders and the client hydrates', async ({page}) => {
	await page.goto('/t/acme/dashboard');

	await expect(page.getByText('Signed in as')).toBeVisible();
	await expect(page.getByText('u-1001')).toBeVisible();
});

test('client extensions load and upgrade their elements', async ({page}) => {
	await page.goto('/t/acme/dashboard');

	await expect(page.getByText('acme-charts is running')).toBeVisible();
	await expect(page.getByText('slow-widget is running')).toBeVisible();
});

test('the inline event handler works', async ({page}) => {
	await page.goto('/t/acme/dashboard');

	await page.getByRole('button', {name: 'Refresh'}).click();

	await expect(page.locator('#status')).toContainText('refreshed at');
});
