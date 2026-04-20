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
import { expect, test } from '@ngx-playwright/test';
import { createWorkingBranchFromPL, enableEditMode } from '../utils/helpers';

test('test', async ({ page }) => {
	await page.setViewportSize({ width: 1200, height: 900 });
	await page.goto('http://localhost:4200/ple/messaging/connections');
	await page.waitForTimeout(500);
	await createWorkingBranchFromPL(page, 'Validation Impact');
	await page.waitForTimeout(500);
	await enableEditMode(page);
	await page.waitForTimeout(500);

	await page
		.getByTestId('link-Connection A-B')
		.getByText('Connection A-B')
		.click();
	await page.waitForTimeout(500);
	await page.getByRole('combobox', { name: 'Rates' }).click();
	await page.getByRole('combobox', { name: 'Rates' }).fill('10');
	await page.getByRole('option', { name: '10' }).click();
	await page.getByRole('button').filter({ hasText: 'menu' }).click();
	await page.getByText('Product Line Engineering').click();
	await page.getByText('MIM').click();
	await page.getByRole('link', { name: 'Reports' }).click();
	await page
		.getByRole('link', { name: 'Validate Connection Impact' })
		.click();
	await expect(
		page.getByRole('cell', { name: 'Connection A-B - Assumed' })
	).toBeVisible();
});
