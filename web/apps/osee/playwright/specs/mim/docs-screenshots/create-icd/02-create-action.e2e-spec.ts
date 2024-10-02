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
	await page.getByRole('link', { name: 'Connection View' }).click();
	await page.getByLabel('Product Line').check();
	await page.getByText('Select a Branch').click();
	await page.getByText('SAW Product Line').click();
	await page.waitForTimeout(500);

	await page.screenshot({
		animations: 'disabled',
		path: 'screenshots/create-action-button.png',
	});

	await page.getByRole('button', { name: 'Create Action' }).click();
	await page.getByLabel('Title').click();
	await page.getByLabel('Title').fill('New MIM ICD');
	await page.getByText('Actionable Item').click();
	await page.getByRole('combobox', { name: 'Actionable Item' }).fill('mim');
	await page.getByText('SAW PL MIM').click();
	await page.getByLabel('Description').click();
	await page.getByLabel('Description').fill('Creating a new MIM ICD');
	await page.getByLabel('Change Type').locator('span').click();
	await page.getByText('Improvement').click();

	await page.screenshot({
		animations: 'disabled',
		path: 'screenshots/create-action-dialog.png',
	});

	await page.getByRole('button', { name: 'Create Action' }).click();

	await expect(page.getByText('TW2 - New MIM ICD')).toBeVisible();
});
