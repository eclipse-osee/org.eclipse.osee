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
	await page.setViewportSize({ width: 1600, height: 1000 });
	await page.goto('http://localhost:4200/ple');
	await page.getByRole('link', { name: 'MIM' }).click();
	await page
		.getByRole('link', { name: 'Enumeration List Configuration' })
		.click();
	await page.getByLabel('Working').check();
	await page.getByText('Select a Branch').click();
	await page.getByText('TW2 - New MIM ICD').click();

	await page.screenshot({
		animations: 'disabled',
		path: 'screenshots/enum-list-config.png',
	});

	await page.getByRole('button', { name: 'Units' }).click();
	await page.locator('button').filter({ hasText: /^add$/ }).click();
	await page.getByLabel('Name').fill('seconds');
	await page
		.locator('div')
		.filter({ hasText: /^Measurement$/ })
		.nth(2)
		.click();
	await page.getByLabel('Measurement').fill('time');
	await page.getByRole('button', { name: 'Ok' }).click();
	await page.waitForTimeout(500);
	await page.locator('button').filter({ hasText: /^add$/ }).click();
	await page.getByLabel('Name').fill('meters');
	await page
		.locator('div')
		.filter({ hasText: /^Measurement$/ })
		.nth(2)
		.click();
	await page.getByLabel('Measurement').fill('distance');
	await page.getByRole('button', { name: 'Ok' }).click();
	await page.waitForTimeout(500);
	await page.locator('button').filter({ hasText: /^add$/ }).click();
	await page.getByLabel('Name').fill('hertz');
	await page.getByLabel('Measurement').click();
	await page.getByLabel('Measurement').fill('frequency');
	await page.getByRole('button', { name: 'Ok' }).click();
	await page.waitForTimeout(500);
	await page.getByRole('button', { name: 'Units' }).click();
	await page.getByRole('button', { name: 'Rates' }).click();
	await page.getByRole('button', { name: 'Add New Rates' }).click();
	await page.getByLabel('Name', { exact: true }).click();
	await page.getByLabel('Name', { exact: true }).fill('1');
	await page.getByRole('button', { name: 'Save Changes' }).click();
	await page.getByRole('button', { name: 'Add New Rates' }).click();
	await page.getByLabel('Name', { exact: true }).fill('5');
	await page.getByRole('button', { name: 'Save Changes' }).click();
	await page.getByRole('button', { name: 'Add New Rates' }).click();
	await page.getByLabel('Name', { exact: true }).fill('10');
	await page.getByRole('button', { name: 'Save Changes' }).click();
	await page.getByRole('button', { name: 'Add New Rates' }).click();
	await page.getByLabel('Name', { exact: true }).fill('25');
	await page.getByRole('button', { name: 'Save Changes' }).click();
	await page.getByRole('button', { name: 'Add New Rates' }).click();
	await page.getByLabel('Name', { exact: true }).fill('Aperiodic');
	await page.getByRole('button', { name: 'Save Changes' }).click();
	await page.getByRole('button', { name: 'Rates', exact: true }).click();
	await page.getByRole('button', { name: 'Message Types' }).click();
	await page.getByRole('button', { name: 'Add New Message Types' }).click();
	await page.getByLabel('Name', { exact: true }).click();
	await page.getByLabel('Name', { exact: true }).fill('Operational');
	await page.getByRole('button', { name: 'Save Changes' }).click();
	await page.getByRole('button', { name: 'Add New Message Types' }).click();
	await page.getByLabel('Name', { exact: true }).fill('Connection');
	await page.getByRole('button', { name: 'Save Changes' }).click();
	await page
		.getByRole('button', { name: 'Message Types', exact: true })
		.click();
	await page.getByRole('button', { name: 'Structure Categories' }).click();
	await page.getByPlaceholder('Filter Structure Categories').click();
	await page.getByPlaceholder('Filter Structure Categories').fill('');
	await page
		.getByRole('button', { name: 'Add New Structure Categories' })
		.click();
	await page.getByLabel('Name', { exact: true }).fill('Misc');
	await page.getByRole('button', { name: 'Save Changes' }).click();
	await page
		.getByRole('button', { name: 'Add New Structure Categories' })
		.click();
	await page.getByLabel('Name', { exact: true }).fill('Test');
	await page.getByRole('button', { name: 'Save Changes' }).click();
	await page
		.getByRole('button', { name: 'Structure Categories', exact: true })
		.click();
	await page.getByRole('button', { name: 'Message Periodicities' }).click();
	await page
		.getByRole('button', { name: 'Add New Message Periodicities' })
		.click();
	await page.getByLabel('Name', { exact: true }).fill('Periodic');
	await page.getByRole('button', { name: 'Save Changes' }).click();
	await page
		.getByRole('button', { name: 'Add New Message Periodicities' })
		.click();
	await page.getByLabel('Name', { exact: true }).fill('Aperiodic');
	await page.getByRole('button', { name: 'Save Changes' }).click();
	await page
		.getByRole('button', { name: 'Message Periodicities', exact: true })
		.click();
});
