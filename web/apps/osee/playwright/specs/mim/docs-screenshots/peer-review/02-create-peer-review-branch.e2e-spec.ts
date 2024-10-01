/*********************************************************************
 * Copyright (c) 2024 Boeing
 *
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     Boeing - initial API and implementation
 **********************************************************************/
import { test, expect } from '@ngx-playwright/test';

test('test', async ({ page }) => {
	await page.setViewportSize({ width: 1200, height: 800 });
	await page.goto('http://localhost:4200/ple');
	await page.getByRole('link', { name: 'MIM' }).click();
	await page.getByRole('link', { name: 'Connection View' }).click();
	await page.getByLabel('Product Line').check();
	await page.getByText('Select a Branch').click();
	await page.getByText('SAW Product Line').click();

	await page.screenshot({
		animations: 'disabled',
		path: 'screenshots/peer-review-button.png',
		clip: { x: 0, y: 0, width: 1200, height: 375 },
	});

	await page.getByRole('button', { name: 'Peer Review' }).click();
	await page.getByRole('button', { name: 'Create Peer Review' }).click();
	await page.getByLabel('Title').fill('MIM Peer Review');
	await page.getByLabel('Actionable Item').click();
	await page.getByRole('combobox', { name: 'Actionable Item' }).fill('mim');
	await page.getByText('SAW PL MIM').click();
	await page.locator('#mat-mdc-form-field-label-16 span').click();
	await page.getByLabel('Description').fill('Peer review');
	await page.getByLabel('Change Type').locator('span').click();
	await page.getByText('Improvement').click();

	let requestPromise = page.waitForResponse((response) =>
		response.url().startsWith('http://localhost:4200/ats/ple/branches/pr')
	);
	await page.getByRole('button', { name: 'Create Action' }).click();
	let response = await requestPromise;
	expect(response.status()).toBe(200);

	await page
		.getByRole('option', { name: 'TW2 - Edit Message Description' })
		.locator('div')
		.first()
		.click();
	await page
		.getByRole('option', { name: 'TW3 - Edit Submessage' })
		.locator('div')
		.first()
		.click();

	await page.screenshot({
		animations: 'disabled',
		path: 'screenshots/peer-review-added-selections.png',
	});

	requestPromise = page.waitForResponse((response) =>
		response.url().startsWith('http://localhost:4200/ats/ple/branches/pr')
	);
	await page.getByRole('button', { name: 'Apply Selected' }).click();
	response = await requestPromise;
	expect(response.status()).toBe(200);

	await page.screenshot({
		animations: 'disabled',
		path: 'screenshots/peer-review-applied.png',
	});

	await page
		.getByRole('option', { name: 'TW3 - Edit Submessage' })
		.locator('span')
		.first()
		.click();
	await page
		.getByRole('option', { name: 'TW4 - Add an Element' })
		.locator('span')
		.first()
		.click();

	await page.screenshot({
		animations: 'disabled',
		path: 'screenshots/peer-review-add-remove.png',
	});

	await page.getByRole('button', { name: 'Apply Selected' }).click();
	await page.getByRole('button', { name: 'Close' }).click();
});
