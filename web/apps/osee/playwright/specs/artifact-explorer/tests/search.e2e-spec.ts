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
import { test, expect } from '@ngx-playwright/test';
import {
	createBranchViaApi,
	purgeBranchViaApi,
	createArtifact,
	openBranch,
	switchToSearch,
	switchToHierarchy,
	searchAndShowInHierarchy,
} from '../utils/helpers';

const BRANCH = 'AE Search Tests';
let branchId: string;

test.describe('Search & Show in Hierarchy', () => {
	test.describe.configure({ mode: 'serial' });

	test.beforeAll(async ({ browser, request }) => {
		branchId = await createBranchViaApi(request, BRANCH);
		const page = await browser.newPage();
		await openBranch(page, BRANCH);
		await createArtifact(
			page,
			'System Requirements - Markdown',
			'AE Search Parent',
			'Folder'
		);
		await expect(page.getByText('AE Search Parent')).toBeVisible({
			timeout: 10000,
		});
		await createArtifact(page, 'AE Search Parent', 'AE Search Child A');
		await page.close();
	});

	test.afterAll(async ({ request }) => {
		await purgeBranchViaApi(request, branchId);
	});

	test('should return results when searching', async ({ page }) => {
		await openBranch(page, BRANCH);
		await switchToSearch(page);
		const searchInput = page.getByRole('textbox', {
			name: 'Search for Artifact',
		});
		await searchInput.click({ force: true });
		await searchInput.fill('');
		await searchInput.type('AE Search');
		await searchInput.press('Enter');

		await expect(
			page
				.locator('osee-artifact-search button')
				.filter({ hasText: 'AE Search Parent' })
				.first()
		).toBeVisible({ timeout: 15000 });
		await expect(
			page
				.locator('osee-artifact-search button')
				.filter({ hasText: 'AE Search Child A' })
				.first()
		).toBeVisible();
	});

	test('should open artifact in editor tab when clicking a search result', async ({
		page,
	}) => {
		await openBranch(page, BRANCH);
		await switchToSearch(page);
		const searchInput = page.getByRole('textbox', {
			name: 'Search for Artifact',
		});
		await searchInput.click({ force: true });
		await searchInput.fill('');
		await searchInput.type('AE Search Parent');
		await searchInput.press('Enter');

		const resultButton = page
			.locator('osee-artifact-search button')
			.filter({ hasText: 'AE Search Parent' })
			.first();
		await expect(resultButton).toBeVisible({ timeout: 15000 });
		await resultButton.click({ force: true });

		await expect(
			page
				.locator('osee-artifact-tab-group')
				.getByText('AE Search Parent')
				.first()
		).toBeVisible({ timeout: 10000 });
	});

	test('should navigate to hierarchy when using "Show in Hierarchy"', async ({
		page,
	}) => {
		await openBranch(page, BRANCH);
		await searchAndShowInHierarchy(page, 'AE Search Child A');

		const hierarchyTree = page.locator(
			'osee-artifact-hierarchy-panel osee-artifact-hierarchy'
		);
		await expect(
			hierarchyTree.getByText('AE Search Child A')
		).toBeVisible({ timeout: 20000 });
	});

	test('should preserve search state when switching back from hierarchy', async ({
		page,
	}) => {
		await openBranch(page, BRANCH);
		await switchToSearch(page);
		const searchInput = page.getByRole('textbox', {
			name: 'Search for Artifact',
		});
		await searchInput.click({ force: true });
		await searchInput.fill('');
		await searchInput.type('AE Search');
		await searchInput.press('Enter');

		const resultButton = page
			.locator('osee-artifact-search button')
			.filter({ hasText: 'AE Search Parent' })
			.first();
		await expect(resultButton).toBeVisible({ timeout: 15000 });

		await switchToHierarchy(page);
		await switchToSearch(page);
		await expect(resultButton).toBeVisible();
	});
});
