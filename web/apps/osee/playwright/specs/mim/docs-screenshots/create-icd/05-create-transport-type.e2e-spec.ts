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
	await page.getByRole('link', { name: 'Transport Type Manager' }).click();
	await page.getByLabel('Working').check();
	await page.getByPlaceholder('Branches').click();
	await page.getByText('TW2 - New MIM ICD').click();
	await page.locator('button').filter({ hasText: 'add' }).click();
	await page.getByLabel('Name').fill('Ethernet');
	await page.getByLabel('Byte Align Validation', { exact: true }).click();
	await page.getByLabel('Byte Align Validation Size').click();
	await page.getByLabel('Byte Align Validation Size').fill('8');
	await page.getByLabel('Message Generation', { exact: true }).click();
	await page.getByPlaceholder('Message Types').click();
	await page.getByText('Operational').click();
	await page
		.getByLabel('Create New Transport Type')
		.getByText('Message Generation Position')
		.click();
	await page.getByLabel('Message Generation Position').fill('0');
	await page.getByLabel('Minimum Publisher Multiplicity').click();
	await page.getByLabel('Minimum Publisher Multiplicity').fill('1');
	await page.getByLabel('Maximum Publisher Multiplicity').click();
	await page.getByLabel('Maximum Publisher Multiplicity').fill('1');
	await page.getByLabel('Minimum Subscriber').click();
	await page.getByLabel('Minimum Subscriber').fill('1');
	await page.getByLabel('Maximum Subscriber').click();
	await page.getByLabel('Maximum Subscriber').fill('1');
	await page.getByLabel('Select Available Message').locator('span').click();
	await page
		.getByRole('option', { name: 'Name' })
		.locator('mat-pseudo-checkbox')
		.click();
	await page
		.getByRole('combobox', { name: 'Select Available Message' })
		.press('Escape');
	await page
		.getByLabel('Select Available Submessage')
		.locator('span')
		.click();
	await page
		.getByRole('option', { name: 'SubMessage Name' })
		.locator('mat-pseudo-checkbox')
		.click();
	await page.getByLabel('SubMessage Name').press('Escape');

	await page.getByLabel('Select Available Structure').locator('span').click();
	await page
		.getByRole('option', { name: 'Name', exact: true })
		.locator('mat-pseudo-checkbox')
		.click();
	await page
		.getByRole('combobox', { name: 'Select Available Structure' })
		.press('Escape');

	await page.getByLabel('Select Available Element').locator('span').click();
	await page
		.getByRole('option', { name: 'Start Index' })
		.locator('mat-pseudo-checkbox')
		.click();
	await page
		.getByRole('combobox', { name: 'Select Available Element' })
		.press('Escape');
	await page.getByText('message, submessage,').click();
	await page.getByLabel('message, submessage,').press('Escape');
	await page.getByRole('button', { name: 'Ok' }).click();

	await expect(page.getByText('Ethernet')).toBeVisible();

	await expect(page.getByText('Select a Branch')).toBeVisible();
});
