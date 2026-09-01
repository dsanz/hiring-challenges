import {expect, test} from '@playwright/test';

// Enable these and make them pass.

test.skip('approving a claim updates what the table shows', async ({page}) => {
	await page.goto('/claims');

	const row = page.locator('tr[data-item-id="CLM-1001"]');

	await row.getByRole('button', {name: 'Approve'}).click();

	await expect(page.locator('tr[data-item-id="CLM-1001"]')).toHaveCount(0);
});

test.skip('a refused approval tells the user why', async ({page}) => {
	await page.goto('/claims');

	const row = page.locator('tr[data-item-id="CLM-1003"]');

	await row.getByRole('button', {name: 'Approve'}).click();

	await expect(row).toContainText(/second approver/i);
	await expect(row.locator('.claim-status')).toContainText('Pending');
});

test.skip('a decision cannot be sent twice', async ({page}) => {
	const patches: string[] = [];

	page.on('request', (request) => {
		if (request.method() === 'PATCH') {
			patches.push(request.url());
		}
	});

	await page.goto('/claims');

	const button = page
		.locator('tr[data-item-id="CLM-1002"]')
		.getByRole('button', {name: 'Approve'});

	await button.click({clickCount: 3, delay: 20});

	await page.waitForTimeout(1500);

	expect(patches.length).toBe(1);
});
