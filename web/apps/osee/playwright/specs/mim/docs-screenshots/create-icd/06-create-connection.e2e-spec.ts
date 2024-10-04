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
	await page.locator('rect').nth(1).click({
		button: 'right',
	});
	await page.getByRole('menuitem', { name: 'Create New Connection' }).click();
	await page.getByLabel('Add a Name').fill('Connection A-B');
	await page.getByLabel('Select a Transport Type').locator('span').click();
	await page.getByText('Ethernet').click();
	await page.getByText('Select Nodes').click();
	await page.getByLabel('Select Nodes').getByText('Node A').click();
	await page.getByPlaceholder('Add a node').click();
	await page.getByLabel('Select Nodes').getByText('Node B').click();
	await page.getByRole('button', { name: 'Ok' }).click();

	await expect(
		page.getByText('Connection A-B', { exact: true })
	).toBeVisible();
});
