import {expect, test} from '@playwright/test';

// Green on a fresh clone: the extension is declared, loaded, and used to render
// the status column.

test('the data set renders through the cell renderer', async ({page}) => {
	await page.goto('/claims');

	await expect(page.locator('table tr')).toHaveCount(6);
	await expect(
		page.locator('.claim-decision').first()
	).toBeVisible();
	await expect(
		page.getByRole('button', {name: 'Approve'}).first()
	).toBeVisible();
});

test('paging works', async ({page}) => {
	await page.goto('/claims');

	await expect(page.getByText('Page 1 of 3')).toBeVisible();

	await page.getByRole('button', {name: 'Next'}).click();

	await expect(page.getByText('Page 2 of 3')).toBeVisible();
});
