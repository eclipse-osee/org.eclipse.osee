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
	await page.setViewportSize({ width: 1600, height: 1000 });
	await page.goto('http://localhost:4200/ple');
	await page.getByRole('link', { name: 'MIM' }).click();
	await page.getByRole('link', { name: 'Connections' }).click();
	await page.getByLabel('Working').check();
	await page.getByText('Select a Branch').click();
	await page.getByText('TW2 - New MIM ICD').click();
	await page.getByText('Connection A-B', { exact: true }).click();

	await page.screenshot({
		animations: 'disabled',
		path: 'screenshots/message-table-add.png',
	});

	await page.locator('button').filter({ hasText: 'add' }).click();
	await page.getByLabel('Name').fill('Message 1');
	await page.getByPlaceholder('Rates').click();
	await page.getByRole('option', { name: '5', exact: true }).click();
	await page.getByPlaceholder('Message Periodicity').click();
	await page.getByText('Periodic', { exact: true }).click();
	await page.getByPlaceholder('Message Types').click();
	await page.getByText('Operational').click();
	await page
		.getByLabel('Create New Message')
		.getByText('Message Number')
		.click();
	await page.getByLabel('Message Number').fill('1');
	await page.getByText('Select Publisher Node(s)').click();
	await page.getByRole('option', { name: 'Node A' }).click();
	await page.getByText('Select Subscriber Node(s)').click();
	await page.getByText('Node B').click();

	await page.screenshot({
		animations: 'disabled',
		path: 'screenshots/create-message.png',
	});

	await page.getByRole('button', { name: 'Ok' }).click();
	await page
		.locator('button')
		.filter({ hasText: /^expand_more$/ })
		.click();

	await page.screenshot({
		animations: 'disabled',
		path: 'screenshots/submessage-table-add.png',
	});

	await page.getByRole('button', { name: 'Add Submessage to:' }).click();
	await page.getByRole('menuitem', { name: 'Message' }).click();

	await page.screenshot({
		animations: 'disabled',
		path: 'create-submessage-dialog.png',
	});

	await page.getByRole('button', { name: 'Create new Submessage' }).click();
	await page.locator('#mat-mdc-form-field-label-54 span').click();
	await page.getByLabel('Name').fill('Submessage 1');
	await page.getByText('Sub Message Number', { exact: true }).click();
	await page.getByLabel('Sub Message Number').fill('1');

	await page.screenshot({
		animations: 'disabled',
		path: 'create-submessage-dialog-new.png',
	});

	await page.getByRole('button', { name: 'Next' }).click();
	await page.getByRole('button', { name: 'Ok' }).click();

	await page.waitForTimeout(500);
	await expect(
		page.getByRole('row', {
			name: 'Submessage 1 1 Go To Message Details Base',
			exact: true,
		})
	).toBeVisible();
});
