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
import { test, expect, type Page } from '@ngx-playwright/test';
import {
	createBranchViaApi,
	purgeBranchViaApi,
	createArtifact,
	openBranch,
	searchAndOpenArtifact,
	switchEditorSection,
} from '../utils/helpers';
import { waitForNetworkIdleIgnoringSse } from '../../../shared/sse-helpers';

/**
 * Auto-save round-trip for every attribute EDITOR KIND rendered by
 * `PersistedArtifactAttributeEditorComponent`: string, markdown, boolean toggle,
 * and enum dropdown. Each is edited, the tab is closed, the artifact is reopened,
 * and the value is asserted to have persisted.
 *
 * This exists because the commit model was reworked this effort (one `commit`
 * path with immediate vs. deferred wrappers). In particular the ENUM dropdown
 * previously never saved — it was routed through the deferred/stale-commit guard
 * and dropped because a select is not "dirty-first". The enum case here is the
 * direct regression guard for that bug; the others guard against the refactor
 * breaking the paths that already worked.
 *
 * A unique artifact per test (own branch) keeps these independent and
 * parallel-safe (see web-coding-standards -> "Testing auto-save editors").
 */

function nameField(page: Page) {
	return page.locator('osee-focus-lost-input').first().getByRole('textbox');
}

/**
 * Opens an artifact's Attributes editor, gating each navigation on SSE-safe
 * network idle so a dev server that is slow under parallel load cannot be
 * interacted with before the app has finished bootstrapping (the blank-app-under-
 * load failure mode). Waits for the Name field as the concrete "editor ready" signal.
 */
async function openArtifactAttributes(page: Page, artifact: string) {
	await waitForNetworkIdleIgnoringSse(page);
	await searchAndOpenArtifact(page, artifact);
	await switchEditorSection(page, 'Attributes');
	await expect(nameField(page)).toBeVisible({ timeout: 20000 });
}

/** Opens the Add Attribute dialog, selects the first type matching the filter, and saves. */
async function addAttribute(page: Page, typeFilter: string) {
	await page.getByRole('button', { name: 'Add Attribute' }).click();
	const dialog = page.locator('mat-dialog-container');
	await expect(
		dialog.getByRole('textbox', { name: 'Filter attribute types' })
	).toBeVisible({ timeout: 5000 });
	await dialog
		.getByRole('textbox', { name: 'Filter attribute types' })
		.fill(typeFilter);
	const checkbox = dialog.locator('mat-checkbox').first();
	await expect(checkbox).toBeVisible({ timeout: 5000 });
	await checkbox.click();
	await Promise.all([
		page.waitForResponse(
			(res) => res.url().includes('orcs/txs') && res.status() === 200
		),
		dialog.getByRole('button', { name: 'Add' }).click(),
	]);
	await expect(dialog).not.toBeVisible();
}

/** Waits for a POST /orcs/txs (an auto-save) triggered by `action`. */
async function expectSave(page: Page, action: () => Promise<void>) {
	await Promise.all([
		page.waitForResponse(
			(res) =>
				res.url().includes('orcs/txs') &&
				res.request().method() === 'POST' &&
				res.status() === 200,
			{ timeout: 30000 }
		),
		action(),
	]);
}

test.describe('Attribute save round-trip (every editor kind)', () => {
	test.describe.configure({ mode: 'parallel' });

	test('string attribute (Name) persists across close/reopen', async ({
		browser,
		request,
	}) => {
		const suffix = `${Date.now()}-${Math.floor(Math.random() * 1e6)}`;
		const branch = `AE Save String ${suffix}`;
		const artifact = `AE Save String Art ${suffix}`;
		const branchId = await createBranchViaApi(request, branch);
		const page = await browser.newPage();
		try {
			await openBranch(page, branch);
			await createArtifact(
				page,
				'System Requirements - Markdown',
				artifact
			);
			await openArtifactAttributes(page, artifact);

			const renamed = `${artifact} renamed`;
			const field = nameField(page);
			await expect(field).toHaveValue(artifact, { timeout: 15000 });
			await field.click();
			await field.fill(renamed);
			await expectSave(page, () =>
				field.evaluate((el) => (el as HTMLElement).blur())
			);

			await page.getByLabel('Close tab').first().click();
			await openArtifactAttributes(page, renamed);
			await expect(nameField(page)).toHaveValue(renamed, {
				timeout: 15000,
			});
		} finally {
			await page.close();
			await purgeBranchViaApi(request, branchId);
		}
	});

	test('markdown attribute persists across close/reopen', async ({
		browser,
		request,
	}) => {
		const suffix = `${Date.now()}-${Math.floor(Math.random() * 1e6)}`;
		const branch = `AE Save Md ${suffix}`;
		const artifact = `AE Save Md Art ${suffix}`;
		const branchId = await createBranchViaApi(request, branch);
		const page = await browser.newPage();
		try {
			await openBranch(page, branch);
			await createArtifact(
				page,
				'System Requirements - Markdown',
				artifact
			);
			await openArtifactAttributes(page, artifact);

			const textarea = page.locator('osee-markdown-editor textarea');
			await expect(textarea).toBeVisible({ timeout: 15000 });
			const content = `Round-trip body ${suffix}`;
			await textarea.click();
			await textarea.fill(content);
			// Markdown saves on focusout; blur the textarea (not Tab).
			await expectSave(page, () =>
				textarea.evaluate((el) => (el as HTMLElement).blur())
			);

			await page.getByLabel('Close tab').first().click();
			await openArtifactAttributes(page, artifact);
			await expect(
				page.locator('osee-markdown-editor textarea')
			).toHaveValue(content, { timeout: 15000 });
		} finally {
			await page.close();
			await purgeBranchViaApi(request, branchId);
		}
	});

	test('boolean toggle persists across close/reopen', async ({
		browser,
		request,
	}) => {
		const suffix = `${Date.now()}-${Math.floor(Math.random() * 1e6)}`;
		const branch = `AE Save Bool ${suffix}`;
		const artifact = `AE Save Bool Art ${suffix}`;
		const branchId = await createBranchViaApi(request, branch);
		const page = await browser.newPage();
		try {
			await openBranch(page, branch);
			await createArtifact(
				page,
				'System Requirements - Markdown',
				artifact
			);
			await openArtifactAttributes(page, artifact);

			// Potential Security Impact is a boolean type addable to this artifact;
			// it renders as a slide toggle (defaults false). Assert state via the
			// accessible `aria-checked` on the toggle's switch, not an internal MDC class.
			await addAttribute(page, 'Potential Security Impact');
			const toggleButton = page
				.locator('mat-slide-toggle button[role="switch"]')
				.first();
			await expect(toggleButton).toBeVisible({ timeout: 10000 });
			await expect(toggleButton).toHaveAttribute('aria-checked', 'false');
			// Toggling is atomic: the click is both the dirty signal and the save.
			await expectSave(page, () => toggleButton.click());
			await expect(toggleButton).toHaveAttribute('aria-checked', 'true', {
				timeout: 10000,
			});

			await page.getByLabel('Close tab').first().click();
			await openArtifactAttributes(page, artifact);
			await expect(
				page.locator('mat-slide-toggle button[role="switch"]').first()
			).toHaveAttribute('aria-checked', 'true', { timeout: 15000 });
		} finally {
			await page.close();
			await purgeBranchViaApi(request, branchId);
		}
	});

	test('enum dropdown persists across close/reopen (regression: enum never saved)', async ({
		browser,
		request,
	}) => {
		const suffix = `${Date.now()}-${Math.floor(Math.random() * 1e6)}`;
		const branch = `AE Save Enum ${suffix}`;
		const artifact = `AE Save Enum Art ${suffix}`;
		const branchId = await createBranchViaApi(request, branch);
		const page = await browser.newPage();
		try {
			await openBranch(page, branch);
			await createArtifact(
				page,
				'System Requirements - Markdown',
				artifact
			);
			await openArtifactAttributes(page, artifact);

			// Qualification Method is a required enum seeded at "Unspecified".
			const select = page.getByRole('combobox', {
				name: 'Qualification Method',
			});
			await expect(select).toBeVisible({ timeout: 15000 });
			await expect(select).toContainText('Unspecified');

			// Selecting a new enum value must fire a save. This is the exact action
			// that previously no-op'd (the select was routed through the deferred
			// stale-commit guard and dropped because it was not dirty-first).
			// Re-drive the open until the overlay reports expanded (a just-rendered
			// mat-select can swallow the first click), capped so it can't spin.
			await expect(async () => {
				if ((await select.getAttribute('aria-expanded')) !== 'true') {
					await select.click();
				}
				await expect(select).toHaveAttribute('aria-expanded', 'true', {
					timeout: 2000,
				});
			}).toPass({ timeout: 15000 });
			const option = page
				.locator('mat-option')
				.filter({ hasNotText: 'Unspecified' })
				.first();
			await expect(option).toBeVisible({ timeout: 10000 });
			const chosen = (await option.textContent())?.trim() ?? '';
			await expectSave(page, () => option.click());

			await page.getByLabel('Close tab').first().click();
			await openArtifactAttributes(page, artifact);
			await expect(
				page.getByRole('combobox', { name: 'Qualification Method' })
			).toContainText(chosen, { timeout: 15000 });
		} finally {
			await page.close();
			await purgeBranchViaApi(request, branchId);
		}
	});
});
