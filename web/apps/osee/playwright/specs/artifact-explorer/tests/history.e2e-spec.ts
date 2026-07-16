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
	expandArtifact,
	switchEditorSection,
} from '../utils/helpers';

const BRANCH = 'AE Hist Tests';
let branchId: string;

test.describe('History & Revert', () => {
	test.describe.configure({ mode: 'serial' });

	test.beforeAll(async ({ browser, request }) => {
		branchId = await createBranchViaApi(request, BRANCH);
		const page = await browser.newPage();
		await openBranch(page, BRANCH);
		await createArtifact(
			page,
			'System Requirements - Markdown',
			'AE Hist Art'
		);
		await expect(page.getByText('AE Hist Art')).toBeVisible({
			timeout: 10000,
		});
		await page.getByRole('button', { name: 'AE Hist Art' }).click();

		// Modify the name to generate a modification transaction
		await switchEditorSection(page, 'Attributes');
		const nameInput = page
			.locator('osee-focus-lost-input')
			.first()
			.getByRole('textbox');
		await nameInput.click();
		await nameInput.fill('AE Hist Art v2');
		await Promise.all([
			page.waitForResponse(
				(res) => res.url().includes('orcs/txs') && res.status() === 200
			),
			page.keyboard.press('Tab'),
		]);
		await expect(
			page
				.locator('osee-artifact-tab-group')
				.getByText('AE Hist Art v2')
				.first()
		).toBeVisible({ timeout: 10000 });
		await page.close();
	});

	test.afterAll(async ({ request }) => {
		await purgeBranchViaApi(request, branchId);
	});

	test('should display history panel with transaction data', async ({
		page,
	}) => {
		await openBranch(page, BRANCH);
		// Open the renamed artifact from hierarchy
		await expandArtifact(page, 'System Requirements - Markdown');
		const artButton = page.getByRole('button', {
			name: 'AE Hist Art v2',
		});
		await expect(artButton).toBeVisible({ timeout: 10000 });
		await artButton.click();
		await expect(
			page
				.locator('osee-artifact-tab-group')
				.getByText('AE Hist Art v2')
				.first()
		).toBeVisible({ timeout: 10000 });

		await switchEditorSection(page, 'History');
		await expect(page.locator('tbody tr').first()).toBeVisible({
			timeout: 20000,
		});
		await expect(page.locator('th').getByText('Tx')).toBeVisible();
		await expect(page.locator('th').getByText('Comment')).toBeVisible();
	});

	test('should open diff dialog when clicking a history row', async ({
		page,
	}) => {
		await openBranch(page, BRANCH);
		await expandArtifact(page, 'System Requirements - Markdown');
		const artButton = page.getByRole('button', {
			name: 'AE Hist Art v2',
		});
		await expect(artButton).toBeVisible({ timeout: 10000 });
		await artButton.click();
		await expect(
			page
				.locator('osee-artifact-tab-group')
				.getByText('AE Hist Art v2')
				.first()
		).toBeVisible({ timeout: 10000 });

		await switchEditorSection(page, 'History');
		const firstRow = page.locator('tbody tr').first();
		await expect(firstRow).toBeVisible({ timeout: 20000 });
		await firstRow.click();

		await expect(
			page.getByRole('heading', { name: /Transaction/ })
		).toBeVisible({
			timeout: 5000,
		});
		await expect(page.getByText('Before:')).toBeVisible();
		await expect(page.getByText('After:')).toBeVisible();

		await page.getByRole('button', { name: 'Cancel' }).click();
		await expect(
			page.getByRole('heading', { name: /Transaction/ })
		).not.toBeVisible();
	});
});
