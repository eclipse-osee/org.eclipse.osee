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
import { expect, Page } from '@ngx-playwright/test';
import { APIRequestContext } from '@playwright/test';
import { API_BASE, AUTH_HEADER } from '../../../shared/test-config';

/** SAW_Bld_1 branch ID used as the parent for test working branches. */
const SAW_BLD_1_BRANCH_ID = '3';

/**
 * Create a working branch via the OSEE REST API.
 * Defaults to SAW_Bld_1 as parent. Pass a different parentBranchId for other parents (e.g. '8' for SAW_PL).
 * Returns the new branch ID as a string.
 */
export const createBranchViaApi = async (
	request: APIRequestContext,
	branchName: string,
	parentBranchId: string = SAW_BLD_1_BRANCH_ID
): Promise<string> => {
	const response = await request.post(`${API_BASE}/orcs/branches`, {
		data: {
			branchName,
			parentBranch: parentBranchId,
			associatedArtifact: '-1',
			branchType: '0',
			sourceTransaction: { id: '-1', branchId: '-1' },
			mergeBaselineTransaction: { id: '-1', branchId: '-1' },
			creationComment: `Playwright test branch: ${branchName}`,
			mergeAddressingQueryId: '0',
			mergeDestinationBranchId: '-1',
			txCopyBranchType: false,
		},
		headers: {
			...AUTH_HEADER,
			'Content-Type': 'application/json',
		},
	});
	if (response.status() !== 200) {
		const body = await response.text();
		throw new Error(
			`createBranchViaApi failed (${response.status()}): ${body}`
		);
	}
	const body = await response.json();
	return String(body.id);
};

/**
 * Purge (permanently delete) a branch via the OSEE REST API.
 * Silently succeeds if the branch was already deleted.
 */
export const purgeBranchViaApi = async (
	request: APIRequestContext,
	branchId: string
): Promise<void> => {
	const response = await request.delete(
		`${API_BASE}/orcs/branches/${branchId}`,
		{ headers: AUTH_HEADER }
	);
	expect([200, 404]).toContain(response.status());
};

/** Navigate to the Artifact Explorer page. */
export const navigateToArtifactExplorer = async (page: Page) => {
	await page.goto('/ple/artifact/explorer');
};

/** Select a branch by type and name using the branch picker. */
export const selectBranch = async (
	page: Page,
	branchType: 'Working' | 'Product Line',
	branchName: string
) => {
	await page.getByRole('radio', { name: branchType }).check();
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

/**
 * Create a working branch off SAW_Bld_1 via the UI.
 * Navigates to artifact explorer, selects baseline, creates branch.
 */
export const createTestBranch = async (page: Page, branchName: string) => {
	await navigateToArtifactExplorer(page);
	await page.getByRole('radio', { name: 'Product Line' }).check();
	const branchCombobox = page.getByRole('combobox', {
		name: 'Select a Branch',
	});
	await branchCombobox.click();
	await branchCombobox.fill('SAW_Bld_1');
	await page.getByRole('option', { name: 'SAW_Bld_1' }).click();

	await page
		.getByRole('button', { name: 'Branch Management' })
		.click();
	await page.getByRole('button', { name: 'Create Branch' }).click();
	await page
		.getByRole('textbox', { name: 'Branch Name' })
		.fill(branchName);

	await Promise.all([
		page.waitForResponse(
			(res) =>
				res.url().includes('orcs/branches') &&
				res.status() === 200
		),
		page.getByRole('button', { name: 'Submit' }).click(),
	]);
};

/**
 * Create an artifact via the hierarchy context menu.
 * Assumes the parent artifact is already visible in the hierarchy.
 */
export const createArtifact = async (
	page: Page,
	parentName: string,
	childName: string,
	typeName = 'Software Requirement - Markdown'
) => {
	await page.getByText(parentName, { exact: true }).click({
		button: 'right',
	});
	await page
		.getByRole('menuitem', { name: 'Create Child Artifact' })
		.click();

	await page
		.getByRole('textbox', { name: 'Enter a Name' })
		.fill(childName);
	await page.getByRole('combobox', { name: 'Select a Type' }).click();
	await page
		.getByRole('combobox', { name: 'Select a Type' })
		.fill(typeName);
	await page.getByRole('option', { name: typeName }).first().click();

	await Promise.all([
		page.waitForResponse(
			(res) =>
				res.url().includes('orcs/txs') && res.status() === 200
		),
		page.getByRole('button', { name: 'Ok' }).click(),
	]);
};

/**
 * Navigate to artifact explorer, select the given working branch,
 * and wait for hierarchy to load.
 */
export const openBranch = async (page: Page, branchName: string) => {
	await navigateToArtifactExplorer(page);
	await selectBranch(page, 'Working', branchName);
	await expect(
		page.getByText('System Requirements - Markdown', { exact: true })
	).toBeVisible({ timeout: 15000 });
};

/** Ensure the hierarchy section is visible in the left panel. */
export const switchToHierarchy = async (page: Page) => {
	const hierarchyTree = page.locator(
		'osee-artifact-hierarchy-panel osee-artifact-hierarchy'
	);
	if (
		(await hierarchyTree.count()) > 0 &&
		(await hierarchyTree.first().isVisible())
	) {
		return;
	}
	await page
		.getByRole('button', { name: 'Artifact Hierarchy' })
		.click();
};

/** Ensure the search section is visible in the left panel. */
export const switchToSearch = async (page: Page) => {
	const searchInput = page.getByLabel('Search for Artifact');
	if (await searchInput.isVisible()) return;
	await page
		.getByRole('button', { name: 'Artifact Search' })
		.click();
};

/** Ensure the branch management section is visible in the left panel. */
export const switchToBranchManagement = async (page: Page) => {
	const branchPanel = page.locator('osee-branch-management-panel');
	if (await branchPanel.isVisible()) return;
	await page
		.getByRole('button', { name: 'Branch Management' })
		.click();
};

/**
 * Expand an artifact in the hierarchy by clicking its expand icon.
 */
export const expandArtifact = async (page: Page, artifactName: string) => {
	const artifactButton = page
		.getByRole('button', { name: artifactName, exact: true })
		.first();
	await expect(artifactButton).toBeVisible({ timeout: 10000 });
	const row = artifactButton.locator('..');
	await row.locator('button').first().click();
};

/** Search for an artifact by name and click the first result to open it. */
export const searchAndOpenArtifact = async (
	page: Page,
	artifactName: string
) => {
	await switchToSearch(page);
	const searchInput = page.getByRole('textbox', {
		name: 'Search for Artifact',
	});
	await searchInput.click({ force: true });
	await searchInput.fill('');
	await searchInput.type(artifactName);
	await searchInput.press('Enter');

	const resultButton = page
		.locator('osee-artifact-search button')
		.filter({ hasText: artifactName })
		.first();
	await expect(resultButton).toBeVisible({ timeout: 15000 });
	await resultButton.click({ force: true });
};

/** Search for an artifact and use "Show in Hierarchy" from context menu. */
export const searchAndShowInHierarchy = async (
	page: Page,
	artifactName: string
) => {
	await switchToSearch(page);
	const searchInput = page.getByRole('textbox', {
		name: 'Search for Artifact',
	});
	await searchInput.click({ force: true });
	await searchInput.fill('');
	await searchInput.type(artifactName);
	await searchInput.press('Enter');

	const resultButton = page
		.locator('osee-artifact-search button')
		.filter({ hasText: artifactName })
		.first();
	await expect(resultButton).toBeVisible({ timeout: 15000 });
	await resultButton.click({ button: 'right', force: true });
	await page
		.getByRole('menuitem', { name: 'Show in Artifact Hierarchy' })
		.click();
};

/** Switch to a specific editor section. */
export const switchEditorSection = async (
	page: Page,
	section: 'Attributes' | 'Relations' | 'History' | 'Artifact Info'
) => {
	await page.getByRole('button', { name: section }).click();
};


/**
 * @deprecated Use searchAndOpenArtifact instead.
 */
export const searchForArtifact = async (page: Page, artifactName: string) => {
	await searchAndOpenArtifact(page, artifactName);
};
