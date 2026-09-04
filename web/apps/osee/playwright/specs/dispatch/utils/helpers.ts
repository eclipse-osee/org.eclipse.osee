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

/** Navigate to the Dispatch index page. */
export const navigateToDispatch = async (page: Page) => {
	await page.goto('/ple/dispatch');
};

/**
 * Navigate to a specific dispatch page by name.
 * If only one page exists, the index auto-redirects.
 * If multiple exist, clicks the page link from the index.
 */
export const navigateToDispatchPage = async (page: Page, pageName: string) => {
	await page.goto('/ple/dispatch');
	// Wait for either auto-redirect or index to load
	await expect(page.getByText('Loading Dispatch Pages...')).toBeHidden({
		timeout: 15000,
	});

	// If we're already on the page (auto-redirect for single page), done
	if (page.url().includes('/ple/dispatch/')) {
		await waitForConfigLoad(page);
		return;
	}

	// Otherwise click the page link from the index
	await page
		.getByRole('link', { name: pageName, exact: true })
		.click({ force: true });
	await waitForConfigLoad(page);
};

/** Wait for a dispatch page's configuration to load. */
export const waitForConfigLoad = async (page: Page) => {
	await expect(page.getByText('Loading Configuration...')).toBeHidden({
		timeout: 15000,
	});
};

/** Select a branch by type and name using the branch picker within a tab. */
export const selectBranch = async (
	page: Page,
	branchType: 'Working' | 'Baseline',
	branchName: string
) => {
	await page
		.locator(`mat-button-toggle[data-cy="${branchType.toLowerCase()}"]`)
		.click();
	const branchInput = page.getByRole('combobox', {
		name: 'Select a Branch',
	});
	await expect(branchInput).toBeEnabled({ timeout: 5000 });
	await branchInput.click();
	await branchInput.fill(branchName);
	await expect(
		page.locator('mat-option').filter({ hasText: branchName }).first()
	).toBeVisible({ timeout: 15000 });
	await page
		.locator('mat-option')
		.filter({ hasText: branchName })
		.first()
		.click();
};

/** Click a specific tab by its label text. */
export const selectTab = async (page: Page, tabLabel: string) => {
	await page
		.locator('.mat-mdc-tab-labels .mdc-tab')
		.filter({ hasText: tabLabel })
		.click();
};

/**
 * Select the first available option from the "Artifact Type" autocomplete.
 * Assumes a branch has already been selected so the dropdown is rendered.
 */
export const selectArtifactType = async (page: Page) => {
	await expect(page.getByText('Artifact Type', { exact: true })).toBeVisible({
		timeout: 10000,
	});
	const artTypeInput = page.getByRole('combobox', { name: 'Artifact Type' });
	await artTypeInput.click();
	await expect(page.locator('mat-option').first()).toBeVisible({
		timeout: 15000,
	});
	await page.locator('mat-option').first().click();
};

/**
 * Upload one or more files to a drag-and-drop file input.
 *
 * When `label` is provided, the file input is located within the wrapping
 * block whose heading matches the label (the label heading is a sibling of
 * the upload component, not a descendant, so we scope by the parent block).
 * Otherwise the first file input on the page is used.
 */
export const uploadFile = async (
	page: Page,
	filePath: string | string[],
	label?: string
) => {
	const fileInput = label
		? // The label <h4> and the upload component are siblings inside the
			// per-input wrapper div, so scope to the heading's parent div and
			// take the file input within it.
			page
				.getByRole('heading', { name: label })
				.locator('xpath=..')
				.locator('input[type="file"]')
		: page.locator('osee-drag-and-drop-upload input[type="file"]').first();
	await fileInput.setInputFiles(filePath);
};
