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
import { test } from '@playwright/test';

test('test', async ({ page }) => {
	await page.goto('http://localhost:4200/ple');
	await page.getByRole('link', { name: 'MIM' }).click();
	await page.getByRole('link', { name: 'Connections' }).click();
	await page.getByLabel('Working').check();
	await page.getByText('Select a Branch').click();
	await page.getByText('MIM Demo').click();
	await page.getByText('account_circle').click();
	await page.getByRole('menuitem', { name: 'Settings' }).click();
	await page.getByLabel('Edit Mode').check();
	await page.getByRole('button', { name: 'Ok' }).click();
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
	await page.getByTestId('structure-table').getByRole('button').click();
	await page
		.getByTestId('element-table-row-Integer Element')
		.getByTestId('element-table-cell-rowControls')
		.getByLabel('')
		.check();
	await page
		.getByTestId('element-table-row-Boolean Element')
		.getByTestId('element-table-cell-rowControls')
		.getByLabel('')
		.check();
	await page
		.getByTestId('element-table-row-Boolean Element')
		.getByTestId('element-table-cell-name')
		.click({
			button: 'right',
		});

	await page.screenshot({
		animations: 'disabled',
		path: 'screenshots/edit-icd/multi-select-context.png',
	});
});
