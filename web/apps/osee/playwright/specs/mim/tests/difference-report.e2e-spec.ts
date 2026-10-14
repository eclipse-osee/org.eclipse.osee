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
import { expect, test } from '@ngx-playwright/test';
import { createWorkingBranchFromPL, enableEditMode } from '../utils/helpers';

test('test', async ({ page }) => {
	await page.setViewportSize({ width: 1200, height: 900 });
	await page.goto('/ple/messaging/connections');
	await createWorkingBranchFromPL(page, 'Difference Report');
	await enableEditMode(page);

	await page.getByText('Connection A-B', { exact: true }).click();

	await page
		.locator('button')
		.filter({ hasText: /^expand_more$/ })
		.click();
	await page.getByTestId('submessage-details-btnSubmessage 1').click();
	await page
		.getByRole('row', { name: 'Structure 1 1 1 0' })
		.getByTestId('structure-table-expand-button')
		.click();
	await page
		.getByRole('cell', { name: 'Float Element', exact: true })
		.click();
	await page.keyboard.press('Control+a');
	await page.keyboard.type('Float');
	// Click a stable toolbar control to blur/commit the cell edit.
	await page.getByRole('combobox', { name: 'Select a View' }).click();
	await page
		.getByRole('cell', { name: 'Integer Element', exact: true })
		.click({
			button: 'right',
		});
	await page
		.getByRole('menuitem', { name: 'Remove element from structure' })
		.click();
	await page.getByRole('button', { name: 'Yes' }).click();

	// Create new element
	await page.getByRole('button', { name: 'Add Element to:' }).click();
	await page.getByRole('menuitem', { name: 'Structure' }).click();
	await page.getByRole('button', { name: 'Create new Element' }).click();
	await expect(page.getByLabel('Name')).toBeVisible();
	await page.getByLabel('Name').fill('New Element');
	// Open the Platform Type autocomplete by typing into it (a bare click focuses the input but does
	// not reliably fire the type-ahead options fetch), then assert the option before clicking.
	const platformTypeCombobox = page
		.getByLabel('2Define element')
		.getByRole('combobox', { name: 'Platform Type' });
	await expect(platformTypeCombobox).toBeVisible();
	// Type per-character (not fill): options come from a debounced search on the input's
	// valueChanges; fill emits a single coalesced event the debounce can mis-time so the query never
	// fires. pressSequentially drives valueChanges as the control expects.
	await platformTypeCombobox.click({ force: true });
	await platformTypeCombobox.fill('');
	await platformTypeCombobox.pressSequentially('Integer');
	const integerOption = page
		.locator('mat-option')
		.filter({ hasText: 'Integer' })
		.first();
	await expect(integerOption).toBeVisible({ timeout: 60000 });
	await integerOption.click();
	const defineElementNext = page.getByRole('button', { name: 'Next' });
	await expect(defineElementNext).toBeEnabled();
	await defineElementNext.click();
	await page.getByRole('button', { name: 'Ok' }).click();

	// Go to difference report
	await page.locator('button').filter({ hasText: 'menu' }).click();
	await page.getByText('Product Line Engineering').click();
	await page.getByText('MIM').click();
	await page.getByRole('link', { name: 'Reports' }).click();
	await page.getByRole('link', { name: 'Difference Report' }).click();

	await expect(
		page.getByRole('heading', { name: 'Structures' })
	).toBeVisible();

	await page.keyboard.press('End');
	await page.waitForTimeout(500);

	await page.screenshot({
		path: 'screenshots/reports/difference-report.png',
		animations: 'disabled',
	});
});
