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
import { createWorkingBranchFromPL } from '../utils/helpers';
import { APP_BASE } from '../../../shared/test-config';

test('test', async ({ page }) => {
	await page.goto(`${APP_BASE}/ple`);
	await page.getByRole('link', { name: 'MIM' }).click();
	await page.getByRole('link', { name: 'Connections' }).click();
	await page.getByLabel('Working').check();
	await page.getByText('Select a Branch').click();
	await page.getByText('MIM Demo').click();
	await page.getByRole('button', { name: 'In Work' }).click();
	await page.getByRole('menuitem', { name: 'Transition to Review' }).click();
	await page.getByRole('button', { name: 'Review', exact: true }).click();
	await page.getByRole('menuitem', { name: 'Commit Branch' }).click();

	await expect(page.getByText('SAW Product Line')).toBeVisible();

	await page.goto(`${APP_BASE}/ple/messaging/connections`);
	await createWorkingBranchFromPL(page, 'MIM Demo');
});
