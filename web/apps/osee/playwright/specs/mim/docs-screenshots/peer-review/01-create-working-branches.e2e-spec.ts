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
	await page.goto('http://localhost:4200/ple');

	// Commit MIM Demo branch to create baseline
	await page.getByRole('link', { name: 'MIM' }).click();
	await page.getByRole('link', { name: 'Connections' }).click();
	await page.getByLabel('Working').check();
	await page.getByText('Select a Branch').click();
	await page.getByText('MIM Demo').click();
	await page.getByRole('button', { name: 'In Work' }).click();
	await page.getByRole('menuitem', { name: 'Transition to Review' }).click();
	await page.getByRole('button', { name: 'Review', exact: true }).click();
	await page.getByRole('menuitem', { name: 'Approve Transition to' }).click();
	await page.getByRole('button', { name: 'Review', exact: true }).click();
	await page.getByRole('menuitem', { name: 'Commit Branch' }).click();

	await expect(page.getByText('SAW Product Line')).toBeVisible();

	// Create first working branch
	await page.getByRole('button', { name: 'Create Action' }).click();
	await page.getByLabel('Title').fill('MIM Changes');
	await page.getByLabel('Actionable Item').click();
	await page.getByRole('combobox', { name: 'Actionable Item' }).fill('mim');
	await page.getByRole('option', { name: 'SAW PL MIM' }).click();
	await page.locator('#mat-mdc-form-field-label-14 span').click();
	await page.getByLabel('Description').fill('Make changes');
	await page.getByLabel('Change Type').locator('span').click();
	await page.getByText('Improvement').click();
	await page.getByLabel('Title').click();
	await page.getByLabel('Title').fill('Edit Message Description');
	await page.getByRole('button', { name: 'Create Action' }).click();
	await page.getByText('Connection A-B', { exact: true }).click();
	await page.getByText('account_circle').click();
	await page.getByRole('menuitem', { name: 'Settings' }).click();
	await page.getByLabel('Edit Mode').check();
	await page.getByRole('button', { name: 'Ok' }).click();
	await page.locator('#mat-input-12').click();
	await page.locator('#mat-input-12').fill('This is the first message');

	let responsePromise = page.waitForResponse(
		'http://localhost:4200/orcs/txs'
	);
	await page.locator('#mat-input-12').press('Tab');
	let response = await responsePromise;
	expect(response.status()).toBe(200);

	await page.getByRole('link', { name: 'working' }).click();

	// Create second working branch
	await page.getByLabel('Product Line').check();
	await page.getByText('Select a Branch').click();
	await page.getByText('SAW Product Line').click();
	await page.getByRole('button', { name: 'Create Action' }).click();
	await page.getByLabel('Title').fill('Edit Submessage Description');
	await page.getByLabel('Actionable Item').click();
	await page.getByRole('combobox', { name: 'Actionable Item' }).fill('mim');
	await page.getByText('SAW PL MIM').click();
	await page.getByLabel('Change Type').locator('span').click();
	await page.getByRole('option', { name: 'Improvement' }).click();
	await page.getByLabel('Description').click();
	await page
		.getByLabel('Description')
		.fill('Update the submessage description');
	await page.getByRole('button', { name: 'Create Action' }).click();
	await page.getByText('Connection A-B', { exact: true }).click();
	await page.getByText('account_circle').click();
	await page.getByRole('menuitem', { name: 'Settings' }).click();
	await page.getByLabel('Edit Mode').check();
	await page.getByRole('button', { name: 'Ok' }).click();
	await page
		.locator('button')
		.filter({ hasText: /^expand_more$/ })
		.click();
	await page.locator('#mat-input-39').click();
	await page.locator('#mat-input-39').fill('This is a new description');

	responsePromise = page.waitForResponse('http://localhost:4200/orcs/txs');
	await page.locator('#mat-input-39').press('Tab');
	response = await responsePromise;
	expect(response.status()).toBe(200);

	await page.getByRole('link', { name: 'working' }).click();

	// Create third working branch
	await page.getByLabel('Product Line').check();
	await page.getByText('Select a Branch').click();
	await page.getByText('SAW Product Line').click();
	await page.getByText('Connection A-B', { exact: true }).click();
	await page.getByRole('link', { name: 'baseline' }).click();
	await page.getByText('Select a Branch').click();
	await page.getByText('SAW Product Line').click();
	await page.getByRole('button', { name: 'Create Action' }).click();
	await page.getByLabel('Title').fill('Add an Element');
	await page.getByLabel('Description').click();
	await page.getByLabel('Description').fill('Adding an element');
	await page.getByText('Actionable Item').click();
	await page.getByRole('combobox', { name: 'Actionable Item' }).fill('mim');
	await page.getByText('SAW PL MIM').click();
	await page.getByLabel('Change Type').locator('span').click();
	await page.getByText('Improvement').click();
	await page.getByRole('button', { name: 'Create Action' }).click();
	await page.getByText('account_circle').click();
	await page.getByRole('menuitem', { name: 'Settings' }).click();
	await page.getByLabel('Edit Mode').check();
	await page.getByRole('button', { name: 'Ok' }).click();
	await page.locator('rect').nth(1).click();
	await page.getByText('Connection A-B', { exact: true }).click();
	await page
		.locator('button')
		.filter({ hasText: /^expand_more$/ })
		.click();
	await page
		.getByRole('row', {
			name: 'Submessage 1 1 Go To Message Details Base',
			exact: true,
		})
		.getByRole('link')
		.click();
	await page
		.getByRole('row', { name: 'Structure 1 1 1 0' })
		.getByRole('button')
		.click();
	await page.getByRole('button', { name: 'Add Element to:' }).click();
	await page.getByRole('menuitem', { name: 'Structure' }).click();
	await page.getByRole('button', { name: 'Create new Element' }).click();
	await page.getByLabel('Name').click();
	await page.getByLabel('Name').fill('New Element');
	await page.getByLabel('2Define element').getByText('Platform Type').click();
	await page.getByText('Float', { exact: true }).click();
	await page.getByRole('button', { name: 'Next' }).click();

	responsePromise = page.waitForResponse('http://localhost:4200/orcs/txs');
	await page.getByRole('button', { name: 'Ok' }).click();
	response = await responsePromise;
	expect(response.status()).toBe(200);
});
