import {expect, test} from '@playwright/test';

// Green on a fresh clone: the extension is declared, loaded, and upgraded.

test('the client extension is placed twice and upgrades', async ({page}) => {
	await page.goto('/checkout');

	await expect(page.locator('vat-field')).toHaveCount(2);
	await expect(page.locator('vat-field input')).toHaveCount(2);
});

// Deliberately does not say *which* instance reports it. As shipped, that is
// not the one you typed into. See behavior.spec.ts.

test('a registered number reaches the registry and comes back', async ({
	page,
}) => {
	await page.goto('/checkout');

	await page
		.locator('vat-field[data-field="billing"] input')
		.fill('ESB12345678');

	await expect(page.getByText('Registered:')).toBeVisible({timeout: 15000});
});
