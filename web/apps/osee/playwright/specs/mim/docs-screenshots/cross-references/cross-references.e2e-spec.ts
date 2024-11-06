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
import { test } from '@ngx-playwright/test';

test('test', async ({ page }) => {
	await page.setViewportSize({ width: 1200, height: 800 });
	await page.goto('http://localhost:4200/ple');
	await page.getByRole('link', { name: 'MIM' }).click();
	await page.getByRole('link', { name: 'Cross-References' }).click();
	await page.getByLabel('Working').check();
	await page.getByText('Select a Branch').click();
	await page.getByText('MIM Demo').click();
	await page.getByLabel('Select a Connection').locator('span').click();
	await page.getByText('Connection A-B').click();

	// Enable edit mode
	await page.getByText('account_circle').click();
	await page.getByRole('menuitem', { name: 'Settings' }).click();
	await page.getByLabel('Edit Mode').check();
	await page.getByRole('button', { name: 'Ok' }).click();

	// Create cross-reference
	await page.locator('button').filter({ hasText: 'add' }).click();
	await page.getByLabel('Name').click();
	await page.getByLabel('Name').fill('C1');
	await page.getByLabel('Name').press('Tab');
	await page.getByLabel('Value').fill('Private Array');
	await page.locator('mat-dialog-content').getByRole('button').click();
	await page.getByText('Key', { exact: true }).click();
	await page.getByLabel('Key').fill('1');
	await page
		.locator('#mat-mdc-form-field-label-26')
		.getByText('Value')
		.click();
	await page.locator('#mat-input-10').fill('FIRST_VAL');
	await page.locator('mat-dialog-content').getByRole('button').click();
	await page.locator('#mat-input-11').click();
	await page.locator('#mat-input-11').fill('2');
	await page
		.locator(
			'div:nth-child(3) > mat-form-field:nth-child(2) > .mat-mdc-text-field-wrapper > .mat-mdc-form-field-flex > .mat-mdc-form-field-infix'
		)
		.click();
	await page.locator('#mat-input-12').fill('SECOND_VAL');

	await page.screenshot({
		animations: 'disabled',
		path: 'screenshots/create-cross-reference.png',
	});

	await page.getByRole('button', { name: 'Ok' }).click();
});
