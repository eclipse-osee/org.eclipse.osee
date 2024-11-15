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
import { createWorkingBranchFromPL, enableEditMode } from '../utils/helpers';

test.describe.configure({ mode: 'parallel' });

test('screenshot', async ({ page }) => {
	await page.goto('http://localhost:4200/ple');
	await page.getByRole('link', { name: 'MIM' }).click();
	await page.getByRole('link', { name: 'Connections' }).click();
	await page.getByLabel('Working').check();

	await Promise.all([
		page.waitForResponse(
			(res) => res.url().includes('branch') && res.status() === 200
		),
		page.getByText('Select a Branch').click(),
	]);

	await Promise.all([
		page.waitForResponse(
			(res) => res.url().includes('graph') && res.status() === 200,
			{ timeout: 60000 }
		),
		page.getByText('TW2 - MIM Demo').click({ timeout: 60000 }),
	]);

	await enableEditMode(page);

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

test('remove elements', async ({ page }) => {
	await page.goto('http://localhost:4200/ple');
	await page.getByRole('link', { name: 'MIM' }).click();
	await page.getByRole('link', { name: 'Connections' }).click();
	await createWorkingBranchFromPL(page, 'Test Multi-Select Remove');
	await enableEditMode(page);
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

	await expect(
		page.getByTestId('element-table-row-Boolean Element')
	).toBeVisible();

	await expect(
		page.getByTestId('element-table-row-Float Element')
	).toBeVisible();

	await page
		.getByTestId('element-table-row-Boolean Element')
		.getByTestId('element-table-cell-rowControls')
		.getByLabel('')
		.check();
	await page
		.getByTestId('element-table-row-Float Element')
		.getByTestId('element-table-cell-rowControls')
		.getByLabel('')
		.check();
	await page
		.getByTestId('element-table-row-Float Element')
		.getByTestId('element-table-cell-name')
		.click({
			button: 'right',
		});
	await page
		.getByRole('menuitem', { name: 'Remove elements from structure' })
		.click();

	await Promise.all([
		page.waitForResponse(
			(res) => res.url().includes('structures') && res.status() === 200
		),
		page.getByRole('button', { name: 'Yes' }).click(),
	]);

	await expect(
		page.getByTestId('element-table-row-Boolean Element')
	).toBeHidden();

	await expect(
		page.getByTestId('element-table-row-Float Element')
	).toBeHidden();
});

test('delete elements', async ({ page }) => {
	await page.goto('http://localhost:4200/ple');
	await page.getByRole('link', { name: 'MIM' }).click();
	await page.getByRole('link', { name: 'Connections' }).click();
	await createWorkingBranchFromPL(page, 'Test Multi-Select Delete');
	await enableEditMode(page);
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

	await expect(
		page.getByTestId('element-table-row-Boolean Element')
	).toBeVisible();

	await expect(
		page.getByTestId('element-table-row-Float Element')
	).toBeVisible();

	await page
		.getByTestId('element-table-row-Boolean Element')
		.getByTestId('element-table-cell-rowControls')
		.getByLabel('')
		.check();
	await page
		.getByTestId('element-table-row-Float Element')
		.getByTestId('element-table-cell-rowControls')
		.getByLabel('')
		.check();
	await page
		.getByTestId('element-table-row-Float Element')
		.getByTestId('element-table-cell-name')
		.click({
			button: 'right',
		});
	await page
		.getByRole('menuitem', { name: 'Delete elements globally' })
		.click();

	await Promise.all([
		page.waitForResponse(
			(res) => res.url().includes('structures') && res.status() === 200
		),
		page.getByRole('button', { name: 'Yes' }).click(),
	]);

	await expect(
		page.getByTestId('element-table-row-Boolean Element')
	).toBeHidden();

	await expect(
		page.getByTestId('element-table-row-Float Element')
	).toBeHidden();
});
