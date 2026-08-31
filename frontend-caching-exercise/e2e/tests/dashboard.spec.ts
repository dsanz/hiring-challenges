import {expect, test} from '@playwright/test';

// Green on a fresh clone. If these go red, something you changed broke the app.

test('the app loads its modules and renders', async ({page}) => {
	await page.goto('/t/acme/dashboard');

	await expect(page.getByRole('heading', {name: 'Dashboard'})).toBeVisible();
	await expect(page.getByText('Widgets: 3 active')).toBeVisible();
	await expect(page.getByRole('button', {name: 'Refresh'})).toBeVisible();
});

test('labels follow the user locale', async ({page}) => {
	await page.goto('/t/acme/dashboard?locale=es_ES');

	await expect(
		page.getByRole('heading', {name: 'Panel de control'})
	).toBeVisible();
	await expect(page.getByText('Componentes: 3 active')).toBeVisible();
});
