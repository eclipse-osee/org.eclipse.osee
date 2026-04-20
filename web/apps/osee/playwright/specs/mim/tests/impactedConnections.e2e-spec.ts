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
	await createWorkingBranchFromPL(page, 'Validation Impact');
	await enableEditMode(page);
	await page.getByRole('button').filter({ hasText: 'menu' }).click();
	await page.getByRole('link', { name: 'Structures' }).click();
	await page.getByRole('button', { name: 'Structure' }).click();
	await page.getByRole('link', { name: 'Message 1 > Submessage' }).click();
	await page.locator('#mat-input-49').click();
	await page.locator('#mat-input-49').fill('Test Desc');
	await page.getByRole('button').filter({ hasText: 'menu' }).click();
	await page.getByRole('link', { name: 'Reports' }).click();
	await page
		.getByRole('link', { name: 'Validate Connection Impact' })
		.click();
	await expect(page.locator('mat-row')).toContainText(
		'Connection A-B - Assumed Target Connection'
	);
});
