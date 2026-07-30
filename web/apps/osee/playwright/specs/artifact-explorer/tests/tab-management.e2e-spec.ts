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
	searchAndOpenArtifact,
} from '../utils/helpers';

const BRANCH = 'AE Tab Tests';
let branchId: string;

test.describe('Tab Management', () => {
	test.describe.configure({ mode: 'serial' });

	test.beforeAll(async ({ browser, request }) => {
		branchId = await createBranchViaApi(request, BRANCH);
		const page = await browser.newPage();
		await openBranch(page, BRANCH);
		await createArtifact(
			page,
			'System Requirements - Markdown',
			'AE Tab Parent',
			'Folder'
		);
		await expect(page.getByText('AE Tab Parent')).toBeVisible({
			timeout: 10000,
		});
		await createArtifact(page, 'AE Tab Parent', 'AE Tab Child A');
		await page.close();
	});

	test.afterAll(async ({ request }) => {
		await purgeBranchViaApi(request, branchId);
	});

	test('should close a tab when clicking the × button', async ({ page }) => {
		await openBranch(page, BRANCH);
		await searchAndOpenArtifact(page, 'AE Tab Parent');

		const tabGroup = page.locator('osee-artifact-tab-group');
		await expect(tabGroup.getByText('AE Tab Parent').first()).toBeVisible({
			timeout: 10000,
		});

		await page.getByRole('button', { name: 'Close tab' }).first().click();

		await expect(tabGroup.getByText('AE Tab Parent')).not.toBeVisible();
		await expect(
			page.getByText('Select an artifact from the hierarchy to begin')
		).toBeVisible();
	});

	test('should open multiple tabs for different artifacts', async ({
		page,
	}) => {
		await openBranch(page, BRANCH);
		await searchAndOpenArtifact(page, 'AE Tab Parent');
		const tabGroup = page.locator('osee-artifact-tab-group');
		await expect(tabGroup.getByText('AE Tab Parent').first()).toBeVisible({
			timeout: 10000,
		});

		await searchAndOpenArtifact(page, 'AE Tab Child A');
		await expect(tabGroup.getByText('AE Tab Child A').first()).toBeVisible({
			timeout: 10000,
		});

		// Both should be present
		await expect(tabGroup.getByText('AE Tab Parent').first()).toBeVisible();
		await expect(
			tabGroup.getByText('AE Tab Child A').first()
		).toBeVisible();
	});

	test('should not open duplicate tab for the same artifact', async ({
		page,
	}) => {
		await openBranch(page, BRANCH);
		await searchAndOpenArtifact(page, 'AE Tab Parent');
		const tabGroup = page.locator('osee-artifact-tab-group');
		await expect(tabGroup.getByText('AE Tab Parent').first()).toBeVisible({
			timeout: 10000,
		});

		// Try opening same artifact again
		await searchAndOpenArtifact(page, 'AE Tab Parent');

		const closeButtons = page.getByRole('button', { name: 'Close tab' });
		await expect(closeButtons).toHaveCount(1);
	});

	test('should switch between open tabs', async ({ page }) => {
		await openBranch(page, BRANCH);
		await searchAndOpenArtifact(page, 'AE Tab Parent');
		await searchAndOpenArtifact(page, 'AE Tab Child A');

		const tabGroup = page.locator('osee-artifact-tab-group');
		await expect(tabGroup.getByText('AE Tab Child A').first()).toBeVisible({
			timeout: 10000,
		});

		// Click first tab
		await tabGroup.getByText('AE Tab Parent').first().click();

		await expect(
			page.locator('[aria-selected="true"]').getByText('AE Tab Parent')
		).toBeVisible();
	});
});
