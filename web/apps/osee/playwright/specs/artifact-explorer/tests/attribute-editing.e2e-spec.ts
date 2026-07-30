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
		await expect(page.getByLabel('Name')).toBeVisible({ timeout: 10000 });
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
				(res) => res.url().includes('orcs/txs') && res.status() === 200
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
				(res) => res.url().includes('orcs/txs') && res.status() === 200
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

		await expect(page.getByLabel('Name')).toBeVisible({ timeout: 10000 });

		await switchEditorSection(page, 'Relations');
		await expect(page.getByLabel('Name')).not.toBeVisible();

		await switchEditorSection(page, 'History');
		await switchEditorSection(page, 'Artifact Info');
		await expect(page.getByText('Artifact Id:')).toBeVisible();

		await switchEditorSection(page, 'Attributes');
		await expect(page.getByLabel('Name')).toBeVisible();
	});

	test('should not show dirty warning when focus enters and leaves editors without changes', async ({
		page,
	}) => {
		await openBranch(page, BRANCH);
		await searchAndOpenArtifact(page, 'AE Attr Test Art');
		await switchEditorSection(page, 'Attributes');

		// --- Step 1: Click into text field, click out, no change ---
		const nameInput = page
			.locator('osee-focus-lost-input')
			.first()
			.getByRole('textbox');
		await nameInput.click();
		await page.keyboard.press('Tab');
		await page.waitForTimeout(600);

		let dialogAppeared = false;
		const dialogHandler = (dialog: import('@playwright/test').Dialog) => {
			dialogAppeared = true;
			dialog.dismiss();
		};
		page.on('dialog', dialogHandler);
		await page.getByLabel('Close tab').first().click();
		await page.waitForTimeout(300);
		expect(dialogAppeared).toBe(false);
		page.off('dialog', dialogHandler);

		// --- Step 2: Re-open artifact, edit + save text field, then close ---
		await searchAndOpenArtifact(page, 'AE Attr Test Art');
		await switchEditorSection(page, 'Attributes');

		const nameInput2 = page
			.locator('osee-focus-lost-input')
			.first()
			.getByRole('textbox');
		await nameInput2.click();
		await nameInput2.fill('AE Attr Dirty Test');
		await Promise.all([
			page.waitForResponse(
				(res) => res.url().includes('orcs/txs') && res.status() === 200
			),
			page.keyboard.press('Tab'),
		]);

		dialogAppeared = false;
		page.on('dialog', dialogHandler);
		await page.getByLabel('Close tab').first().click();
		await page.waitForTimeout(300);
		expect(dialogAppeared).toBe(false);
		page.off('dialog', dialogHandler);

		// --- Step 3: Re-open artifact, click into markdown, click out, close ---
		await searchAndOpenArtifact(page, 'AE Attr Dirty Test');
		await switchEditorSection(page, 'Attributes');

		const markdownEditor = page.locator('osee-markdown-editor textarea');
		await expect(markdownEditor).toBeVisible({ timeout: 10000 });
		await markdownEditor.click();
		await page.waitForTimeout(200);
		// Click away from markdown editor
		await page
			.locator('osee-focus-lost-input')
			.first()
			.getByRole('textbox')
			.click();
		await page.keyboard.press('Tab');
		await page.waitForTimeout(600);

		dialogAppeared = false;
		page.on('dialog', dialogHandler);
		await page.getByLabel('Close tab').first().click();
		await page.waitForTimeout(300);
		expect(dialogAppeared).toBe(false);
		page.off('dialog', dialogHandler);
	});
});
