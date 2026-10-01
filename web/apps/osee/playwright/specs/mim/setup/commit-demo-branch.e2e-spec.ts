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
import { selectBranch } from '../../../shared/branch-helpers';

test('test', async ({ page }) => {
	await page.goto('/ple');
	await page.getByRole('link', { name: 'MIM' }).click();
	await page.getByRole('link', { name: 'Connections' }).click();
	await selectBranch(page, 'Working', 'MIM Demo');

	// Drive the branch In Work -> Review, waiting for each state control to render before
	// opening its menu so a click never lands before the prior transition applied.
	await page.getByRole('button', { name: 'In Work' }).click();
	await page.getByRole('menuitem', { name: 'Transition to Review' }).click();
	const reviewButton = page.getByRole('button', {
		name: 'Review',
		exact: true,
	});
	await expect(reviewButton).toBeVisible({ timeout: 15000 });
	await reviewButton.click();
	await page.getByRole('menuitem', { name: 'Commit Branch' }).click();

	// Commit opens the commit-manager flow against the parent PL, which is a server
	// round-trip; wait on the concrete "SAW Product Line" target label with a budget that
	// covers the commit load rather than the 5s default.
	await expect(page.getByText('SAW Product Line')).toBeVisible({
		timeout: 20000,
	});

	await page.goto('/ple/messaging/connections');
	await createWorkingBranchFromPL(page, 'MIM Demo');
});
