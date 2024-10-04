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
	await page.waitForTimeout(500);
	await page.getByTestId('add-structure').click();
	await page.getByRole('button', { name: 'Create new Structure' }).click();
	await page.getByLabel('Name', { exact: true }).click();
	await page.getByLabel('Name', { exact: true }).fill('Structure 1');
	await page.getByText('Max Simultaneity', { exact: true }).click();
	await page.getByLabel('Max Simultaneity').fill('1');
	await page.getByText('Min Simultaneity', { exact: true }).click();
	await page.getByLabel('Min Simultaneity').fill('1');
	await page.getByPlaceholder('Structure Categories').click();
	await page.getByRole('option', { name: 'Misc' }).click();
	await page.getByRole('button', { name: 'Next' }).click();
	await page.getByRole('button', { name: 'Ok' }).click();

	await expect(page.getByText('Structure 1')).toBeVisible();
});
