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

// Rename the osee-focus-lost-input and wait for the auto-save POST /orcs/txs.
// The field commits on blur; blur via a direct blur() to avoid side effects
// (Tab would open the adjacent select). fromValue guards against typing while
// the field is mid-resync from a previous save.
async function renameAndSave(
	page: import('@playwright/test').Page,
	input: import('@playwright/test').Locator,
	fromValue: string,
	value: string
) {
	await expect(input).toBeEnabled({ timeout: 15000 });
	await expect(input).toHaveValue(fromValue, { timeout: 15000 });
	await input.click();
	await input.fill(value);
	await expect(input).toHaveValue(value, { timeout: 10000 });
	const savePromise = page.waitForResponse(
		(res) =>
			res.url().includes('orcs/txs') &&
			res.request().method() === 'POST' &&
			res.status() === 200,
		{ timeout: 30000 }
	);
	await input.evaluate((el) => (el as HTMLElement).blur());
	await savePromise;
}

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
		await expect(page.getByLabel('Name')).toBeVisible({ timeout: 15000 });

		const nameInput = page
			.locator('osee-focus-lost-input')
			.first()
			.getByRole('textbox');
		await renameAndSave(
			page,
			nameInput,
			'AE Attr Test Art',
			'AE Attr Test Art Renamed'
		);

		await expect(
			page
				.locator('osee-artifact-tab-group')
				.getByText('AE Attr Test Art Renamed')
				.first()
		).toBeVisible({ timeout: 15000 });

		// Rename back in a fresh editor open (close + reopen): the auto-save only
		// re-fires reliably on the first blur after mount, not on a second edit
		// in the same open editor.
		await page.getByLabel('Close tab').first().click();
		await searchAndOpenArtifact(page, 'AE Attr Test Art Renamed');
		await switchEditorSection(page, 'Attributes');
		await expect(page.getByLabel('Name').first()).toBeVisible({
			timeout: 15000,
		});
		const nameInputAgain = page
			.locator('osee-focus-lost-input')
			.first()
			.getByRole('textbox');
		await renameAndSave(
			page,
			nameInputAgain,
			'AE Attr Test Art Renamed',
			'AE Attr Test Art'
		);
		await expect(
			page
				.locator('osee-artifact-tab-group')
				.getByText('AE Attr Test Art', { exact: true })
				.first()
		).toBeVisible({ timeout: 15000 });
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
		// The tab-close guard shows a native confirm() only when an editor is
		// dirty. With no unsaved changes the tab closes immediately, so fail if
		// any dialog fires and assert the tab actually closed.
		let dialogText: string | null = null;
		page.on('dialog', (dialog) => {
			dialogText = dialog.message();
			void dialog.dismiss();
		});
		const tabGroup = page.locator('osee-artifact-tab-group');

		await openBranch(page, BRANCH);
		await searchAndOpenArtifact(page, 'AE Attr Test Art');
		await switchEditorSection(page, 'Attributes');

		// --- Step 1: focus a text field and leave it unchanged, then close ---
		const nameInput = page
			.locator('osee-focus-lost-input')
			.first()
			.getByRole('textbox');
		await nameInput.click();
		await nameInput.evaluate((el) => (el as HTMLElement).blur());
		await page.getByLabel('Close tab').first().click();
		await expect(tabGroup.getByText('AE Attr Test Art')).not.toBeVisible({
			timeout: 10000,
		});
		expect(dialogText, 'no unsaved-changes dialog expected').toBeNull();

		// --- Step 2: edit + save (persisted, not dirty), then close ---
		await searchAndOpenArtifact(page, 'AE Attr Test Art');
		await switchEditorSection(page, 'Attributes');
		const nameInput2 = page
			.locator('osee-focus-lost-input')
			.first()
			.getByRole('textbox');
		await renameAndSave(
			page,
			nameInput2,
			'AE Attr Test Art',
			'AE Attr Dirty Test'
		);
		await page.getByLabel('Close tab').first().click();
		await expect(tabGroup.getByText('AE Attr Dirty Test')).not.toBeVisible({
			timeout: 10000,
		});
		expect(dialogText, 'no unsaved-changes dialog expected').toBeNull();

		// --- Step 3: focus the markdown editor and leave it unchanged, close ---
		await searchAndOpenArtifact(page, 'AE Attr Dirty Test');
		await switchEditorSection(page, 'Attributes');
		const markdownEditor = page.locator('osee-markdown-editor textarea');
		await expect(markdownEditor).toBeVisible({ timeout: 10000 });
		await markdownEditor.click();
		// Move focus off the markdown editor without changing anything.
		const nameInput3 = page
			.locator('osee-focus-lost-input')
			.first()
			.getByRole('textbox');
		await nameInput3.click();
		await nameInput3.evaluate((el) => (el as HTMLElement).blur());
		await page.getByLabel('Close tab').first().click();
		await expect(tabGroup.getByText('AE Attr Dirty Test')).not.toBeVisible({
			timeout: 10000,
		});
		expect(dialogText, 'no unsaved-changes dialog expected').toBeNull();
	});
});
