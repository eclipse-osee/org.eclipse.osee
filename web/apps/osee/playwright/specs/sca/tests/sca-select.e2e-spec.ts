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

	const atsDropdown = page.locator('select[ng-model="programSelection"]');
	await atsDropdown.click();
	await expect(atsDropdown).toBeVisible();
	await atsDropdown.selectOption({ label: 'Dispo Demo' });

	const setDropdown = page.locator('select[ng-model="setSelection"]');
	await setDropdown.click();
	await expect(setDropdown).toBeVisible();
	await setDropdown.selectOption({ label: 'Dispo_Demo_Set' });

	await page.getByText('Dispo_Demo_Item').dblclick();

	await page.getByText('Resolution_1').dblclick();
	await page.keyboard.type('Resolution_1_Updated');
	await page.keyboard.press('Enter');

	await page.getByText('Resolution_2').dblclick();
	await page.keyboard.type('Resolution_2_Updated');
	await page.keyboard.press('Enter');
});
