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

	await page.getByText('account_circle').click();

	await page.screenshot({
		animations: 'disabled',
		path: 'screenshots/user-menu.png',
	});

	await page.getByRole('menuitem', { name: 'Settings' }).click();
	await page.getByLabel('Edit Mode').check();

	await page.screenshot({
		animations: 'disabled',
		path: 'screenshots/user-settings.png',
	});

	await page.getByRole('button', { name: 'Ok' }).click();
	await page.locator('rect').nth(1).click({
		button: 'right',
	});

	await page.screenshot({
		animations: 'disabled',
		path: 'screenshots/create-node-connection-menu.png',
	});

	await page.getByRole('menuitem', { name: 'Create New Node' }).click();
	await page.getByLabel('Add name', { exact: true }).fill('Node A');
	await page.getByText('Add node number').click();
	await page.getByLabel('Add node number').fill('A');

	await page.screenshot({
		animations: 'disabled',
		path: 'screenshots/create-node-dialog.png',
	});

	await page.getByText('Cancel Ok').click();
	await page.getByRole('button', { name: 'Ok' }).click();
	await page.locator('rect').nth(1).click({
		button: 'right',
	});
	await page.getByRole('menuitem', { name: 'Create New Node' }).click();
	await page.getByLabel('Add name', { exact: true }).click();
	await page.getByLabel('Add name', { exact: true }).fill('Node B');
	await page.getByText('Add node number').click();
	await page.getByLabel('Add node number').fill('B');
	await page.getByText('Cancel Ok').click();
	await page.getByRole('button', { name: 'Ok' }).click();

	await expect(page.getByText('Node A')).toBeVisible();
	await expect(page.getByText('Node B')).toBeVisible();
});
