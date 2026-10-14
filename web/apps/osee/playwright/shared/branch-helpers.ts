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
	await page
		.locator(`mat-button-toggle[data-cy="${branchType.toLowerCase()}"]`)
		.click();
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
	await expect(branchCombobox).toBeEnabled({ timeout: 15000 });

	const option = page
		.locator('mat-option')
		.filter({ hasText: branchName })
		.first();

	// The branch picker is a mat-autocomplete whose options come from a debounced search driven by
	// the input's valueChanges. Type per-character with `pressSequentially` rather than `.fill()`:
	// `.fill()` sets the value in one shot and emits a single input event, which the debounce
	// pipeline can coalesce or mis-time so the search never re-fires and the listbox stays empty.
	// Per-character input drives valueChanges the way the control expects, so the query reliably
	// runs. (`pressSequentially` is the non-deprecated replacement for `locator.type()`.)
	await branchCombobox.click({ force: true });
	await branchCombobox.fill('');
	await branchCombobox.pressSequentially(branchName);

	// Assert the option is present before clicking so a failure points here (search never
	// populated) rather than at a downstream step.
	await expect(option).toBeVisible({ timeout: 15000 });
	await option.click();
};
