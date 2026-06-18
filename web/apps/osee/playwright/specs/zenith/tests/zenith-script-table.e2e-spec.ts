/*********************************************************************
 * Copyright (c) 2026 Boeing
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
import { navigateToScriptTable } from '../utils/helpers';

test('script table loads with data', async ({ page }) => {
	await navigateToScriptTable(page);

	await expect(
		page.locator('[data-cy^="script-def-table-row-"]').first()
	).toBeVisible();
});

test('name filter narrows results', async ({ page }) => {
	await navigateToScriptTable(page);

	await expect(
		page.locator('[data-cy^="script-def-table-row-"]').first()
	).toBeVisible();

	const rowsBefore = await page
		.locator('[data-cy^="script-def-table-row-"]')
		.count();

	await page.locator('[data-cy^="script-def-table-row-"]').first().waitFor();
	await page.locator('mat-form-field input').first().fill('EmptyTestScript');

	await expect(
		page.locator('[data-cy="script-def-table-row-EmptyTestScript"]')
	).toBeVisible();

	const rowsAfter = await page
		.locator('[data-cy^="script-def-table-row-"]')
		.count();
	expect(rowsAfter).toBeLessThanOrEqual(rowsBefore);
});

test('clearing a filter restores results', async ({ page }) => {
	await navigateToScriptTable(page);

	await expect(
		page.locator('[data-cy^="script-def-table-row-"]').first()
	).toBeVisible();

	const rowsBefore = await page
		.locator('[data-cy^="script-def-table-row-"]')
		.count();

	await page.locator('mat-form-field input').first().fill('xyznonexistent');

	await expect(
		page.locator('[data-cy^="script-def-table-row-"]')
	).toHaveCount(0);

	await page.locator('mat-form-field input').first().fill('');

	await expect(
		page.locator('[data-cy^="script-def-table-row-"]')
	).toHaveCount(rowsBefore);
});

test('paginator is visible', async ({ page }) => {
	await navigateToScriptTable(page);

	await expect(page.locator('mat-paginator')).toBeVisible();
});
