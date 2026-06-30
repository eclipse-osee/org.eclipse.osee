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

const BRANCH = 'AE Attribute Tests';
let branchId: string;

test.describe('Attribute Editing (Auto-Save)', () => {
	test.describe.configure({ mode: 'serial' });

	test.beforeAll(async ({ browser, request }) => {
		branchId = await createBranchViaApi(request, BRANCH);
		const page = await browser.newPage();
		await openBranch(page, BRANCH);
		await createArtifact(
			page,
			'System Requirements - Markdown',
			'AE Attr Test Art'
		);
		await page.close();
	});

	test.afterAll(async ({ request }) => {
		await purgeBranchViaApi(request, branchId);
	});

	test('should display artifact name in tab after opening', async ({
		page,
	}) => {
		await openBranch(page, BRANCH);
		await searchAndOpenArtifact(page, 'AE Attr Test Art');
		await expect(
			page
				.locator('osee-artifact-tab-group')
				.getByText('AE Attr Test Art')
				.first()
		).toBeVisible({ timeout: 10000 });
	});

	test('should display attribute editor with Name field', async ({
		page,
	}) => {
		await openBranch(page, BRANCH);
		await searchAndOpenArtifact(page, 'AE Attr Test Art');
		await switchEditorSection(page, 'Attributes');
		await expect(
			page.getByText('Name:', { exact: true })
		).toBeVisible({ timeout: 10000 });
	});

	test('should auto-save name attribute on blur and update tab title', async ({
		page,
	}) => {
		await openBranch(page, BRANCH);
		await searchAndOpenArtifact(page, 'AE Attr Test Art');
		await switchEditorSection(page, 'Attributes');

		const nameInput = page
			.locator('osee-focus-lost-input')
			.first()
			.getByRole('textbox');
		await nameInput.click();
		await nameInput.fill('AE Attr Test Art Renamed');

		await Promise.all([
			page.waitForResponse(
				(res) =>
					res.url().includes('orcs/txs') && res.status() === 200
			),
			page.keyboard.press('Tab'),
		]);

		await expect(
			page
				.locator('osee-artifact-tab-group')
				.getByText('AE Attr Test Art Renamed')
				.first()
		).toBeVisible({ timeout: 10000 });

		// Rename back
		const nameInputAgain = page
			.locator('osee-focus-lost-input')
			.first()
			.getByRole('textbox');
		await nameInputAgain.click();
		await nameInputAgain.fill('AE Attr Test Art');
		await Promise.all([
			page.waitForResponse(
				(res) =>
					res.url().includes('orcs/txs') && res.status() === 200
			),
			page.keyboard.press('Tab'),
		]);
		await expect(
			page
				.locator('osee-artifact-tab-group')
				.getByText('AE Attr Test Art', { exact: true })
				.first()
		).toBeVisible({ timeout: 10000 });
	});

	test('should switch between editor sections via toolbar', async ({
		page,
	}) => {
		await openBranch(page, BRANCH);
		await searchAndOpenArtifact(page, 'AE Attr Test Art');

		await expect(
			page.getByText('Name:', { exact: true })
		).toBeVisible({ timeout: 10000 });

		await switchEditorSection(page, 'Relations');
		await expect(
			page.getByText('Name:', { exact: true })
		).not.toBeVisible();

		await switchEditorSection(page, 'History');
		await switchEditorSection(page, 'Artifact Info');
		await expect(page.getByText('Artifact Id:')).toBeVisible();

		await switchEditorSection(page, 'Attributes');
		await expect(
			page.getByText('Name:', { exact: true })
		).toBeVisible();
	});
});
