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
import { Page, expect } from '@ngx-playwright/test';

/**
 * Select a branch type using the branch-type toggle.
 * Clicks the toggle button matching the given type.
 */
export const selectBranchType = async (
	page: Page,
	branchType: 'Working' | 'Baseline'
) => {
	await page.getByRole('radio', { name: branchType }).click();
};

/**
 * Select a branch by type and name using the branch picker.
 * Clicks the branch type toggle, waits for the combobox to enable,
 * types the branch name, and selects the matching autocomplete option.
 */
export const selectBranch = async (
	page: Page,
	branchType: 'Working' | 'Baseline',
	branchName: string
) => {
	await selectBranchType(page, branchType);
	const branchCombobox = page.getByRole('combobox', {
		name: 'Select a Branch',
	});
	// Wait for combobox to be enabled (disabled while no type selected)
	await expect(branchCombobox).toBeEnabled({ timeout: 5000 });
	await branchCombobox.click({ force: true });
	await branchCombobox.fill('');
	await branchCombobox.type(branchName);
	// Wait for and click the matching option
	await expect(
		page.locator('mat-option').filter({ hasText: branchName }).first()
	).toBeVisible({ timeout: 15000 });
	await page
		.locator('mat-option')
		.filter({ hasText: branchName })
		.first()
		.click();
};
