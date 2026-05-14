/*********************************************************************
 * Copyright (c) 2025 Boeing
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

test('test', async ({ page }) => {
	await page.setViewportSize({ width: 1200, height: 800 });
	await page.goto('http://localhost:8089/coverage/ui/index.html#/');
	await page.waitForLoadState('networkidle');

	const atsDropdown = page.locator('select[ng-model="programSelection"]');
	await expect(atsDropdown).toBeVisible({ timeout: 15000 });

	// Retry selecting the program and waiting for set options to load
	const setDropdown = page.locator('select[ng-model="setSelection"]');
	await expect(async () => {
		await atsDropdown.selectOption({ label: '' });
		await atsDropdown.selectOption({ label: 'Dispo Demo' });
		await expect(setDropdown).toBeVisible();
		await expect(
			setDropdown.locator('option[label="Dispo_Demo_Set"]')
		).toHaveCount(1);
	}).toPass({ timeout: 60000, intervals: [1000, 2000, 5000] });

	await setDropdown.selectOption({ label: 'Dispo_Demo_Set' });

	await expect(page.getByText('Dispo_Demo_Item')).toBeVisible({
		timeout: 15000,
	});
	await page.getByText('Dispo_Demo_Item').dblclick();

	await expect(page.getByText('Resolution_1')).toBeVisible({
		timeout: 15000,
	});
	await page.getByText('Resolution_1').dblclick();
	await page.keyboard.type('Resolution_1_Updated');
	await page.keyboard.press('Enter');

	await expect(page.getByText('Resolution_2')).toBeVisible({
		timeout: 15000,
	});
	await page.getByText('Resolution_2').dblclick();
	await page.keyboard.type('Resolution_2_Updated');
	await page.keyboard.press('Enter');
});
