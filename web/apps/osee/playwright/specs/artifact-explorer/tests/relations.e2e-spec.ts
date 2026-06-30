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
	searchAndOpenArtifact,
	switchEditorSection,
} from '../utils/helpers';

const BRANCH = 'AE Relations Tests';
let branchId: string;

test.describe('Relations Panel', () => {
	test.describe.configure({ mode: 'serial' });

	test.beforeAll(async ({ browser, request }) => {
		branchId = await createBranchViaApi(request, BRANCH);
		const page = await browser.newPage();
		await openBranch(page, BRANCH);
		await createArtifact(
			page,
			'System Requirements - Markdown',
			'AE Rel Parent',
			'Folder'
		);
		// Parent auto-expands after create — wait for it
		await expect(page.getByText('AE Rel Parent')).toBeVisible({
			timeout: 10000,
		});
		await createArtifact(page, 'AE Rel Parent', 'AE Rel Child A');
		await page.close();
	});

	test.afterAll(async ({ request }) => {
		await purgeBranchViaApi(request, branchId);
	});

	test('should display relation types as expandable rows', async ({
		page,
	}) => {
		await openBranch(page, BRANCH);
		await searchAndOpenArtifact(page, 'AE Rel Parent');
		await switchEditorSection(page, 'Relations');

		await expect(
			page.getByText('Default Hierarchical')
		).toBeVisible({ timeout: 10000 });
	});

	test('should expand relation type to show sides', async ({ page }) => {
		await openBranch(page, BRANCH);
		await searchAndOpenArtifact(page, 'AE Rel Parent');
		await switchEditorSection(page, 'Relations');

		const panel = page.locator('osee-relations-editor-panel');
		const expandBtn = panel
			.getByText('Default Hierarchical')
			.locator('..')
			.locator('button')
			.first();
		await expandBtn.click();

		await expect(
			panel.getByText('parent', { exact: true }).first()
		).toBeVisible({ timeout: 10000 });
		await expect(
			panel.getByText('child', { exact: true }).first()
		).toBeVisible();
	});

	test('should expand a relation side to show artifacts', async ({
		page,
	}) => {
		await openBranch(page, BRANCH);
		await searchAndOpenArtifact(page, 'AE Rel Parent');
		await switchEditorSection(page, 'Relations');

		const panel = page.locator('osee-relations-editor-panel');
		await panel
			.getByText('Default Hierarchical')
			.locator('..')
			.locator('button')
			.first()
			.click();
		await expect(
			panel.getByText('child', { exact: true }).first()
		).toBeVisible({ timeout: 10000 });

		await panel
			.getByText('child', { exact: true })
			.first()
			.locator('..')
			.locator('button')
			.first()
			.click();

		await expect(
			panel.getByText('AE Rel Child A')
		).toBeVisible({ timeout: 10000 });
	});

	test('should open related artifact in a new tab when clicked', async ({
		page,
	}) => {
		await openBranch(page, BRANCH);
		await searchAndOpenArtifact(page, 'AE Rel Parent');
		await switchEditorSection(page, 'Relations');

		const panel = page.locator('osee-relations-editor-panel');
		await panel
			.getByText('Default Hierarchical')
			.locator('..')
			.locator('button')
			.first()
			.click();
		await panel
			.getByText('child', { exact: true })
			.first()
			.locator('..')
			.locator('button')
			.first()
			.click();
		await expect(
			panel.getByText('AE Rel Child A')
		).toBeVisible({ timeout: 10000 });

		await panel.getByText('AE Rel Child A').click();

		await expect(
			page
				.locator('osee-artifact-tab-group')
				.getByText('AE Rel Child A')
				.first()
		).toBeVisible({ timeout: 10000 });
	});

	test('should collapse relation type to hide sides', async ({ page }) => {
		await openBranch(page, BRANCH);
		await searchAndOpenArtifact(page, 'AE Rel Parent');
		await switchEditorSection(page, 'Relations');

		const panel = page.locator('osee-relations-editor-panel');
		const expandBtn = panel
			.getByText('Default Hierarchical')
			.locator('..')
			.locator('button')
			.first();
		await expandBtn.click();
		await expect(
			panel.getByText('child', { exact: true }).first()
		).toBeVisible({ timeout: 10000 });

		await expandBtn.click();
		await expect(
			panel.getByText('child', { exact: true }).first()
		).not.toBeVisible();
	});
});
