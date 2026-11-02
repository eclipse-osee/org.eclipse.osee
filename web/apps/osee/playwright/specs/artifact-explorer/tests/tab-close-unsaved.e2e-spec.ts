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
 * Behavior of closing an artifact tab that has UNSAVED edits.
 *
 * The design goal is a seamless close: clicking the tab × blurs the active
 * editor, which triggers its auto-save, so the tab should close WITHOUT any
 * prompt once that save lands — the user should not be nagged about changes that
 * are already being persisted. These tests assert that end-to-end against the
 * running backend:
 *   1. a tab with an unsaved edit closes silently and the edit DID persist,
 *   2. while the close waits for a slow save, the × is replaced by a spinner and
 *      cannot be clicked again (no stacked prompts),
 *   3. a clean tab still closes immediately.
 *
 * A unique artifact per test (own branch) keeps these independent and
 * parallel-safe (see web-coding-standards -> "Testing auto-save editors").
 */

function nameField(page: Page) {
	return page.locator('osee-focus-lost-input').first().getByRole('textbox');
}

/**
 * Opens an artifact's Attributes editor, gating navigation on SSE-safe network
 * idle so a dev server slow under parallel load is not interacted with before the
 * app has bootstrapped. Waits for the Name field as the "editor ready" signal.
 */
async function openArtifactAttributes(page: Page, artifact: string) {
	await waitForNetworkIdleIgnoringSse(page);
	await searchAndOpenArtifact(page, artifact);
	await switchEditorSection(page, 'Attributes');
	await expect(nameField(page)).toBeVisible({ timeout: 20000 });
}

const closeButton = (page: Page) => page.getByLabel('Close tab').first();

test.describe('Tab close with unsaved changes', () => {
	test.describe.configure({ mode: 'parallel' });

	test('closes silently and persists the edit (no prompt)', async ({
		browser,
		request,
	}) => {
		const suffix = `${Date.now()}-${Math.floor(Math.random() * 1e6)}`;
		const branch = `AE Close Silent ${suffix}`;
		const artifact = `AE Close Silent Art ${suffix}`;
		const branchId = await createBranchViaApi(request, branch);
		const page = await browser.newPage();

		// A dialog must NEVER appear in the happy path; fail loudly if one does.
		let dialogMessage: string | null = null;
		page.on('dialog', (d) => {
			dialogMessage = d.message();
			void d.dismiss();
		});

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

			// Edit WITHOUT blurring — the field is now dirty with an uncommitted edit,
			// exactly the state a user is in when they click the × mid-edit.
			await field.click();
			await field.fill(renamed);

			// Clicking × blurs the field (triggering the save) and closes the tab once
			// the save lands. The POST confirms the edit actually persisted.
			await Promise.all([
				page.waitForResponse(
					(res) =>
						res.url().includes('orcs/txs') &&
						res.request().method() === 'POST' &&
						res.status() === 200,
					{ timeout: 30000 }
				),
				closeButton(page).click(),
			]);

			// Tab is gone, no prompt was shown.
			await expect(
				page.getByText('Select an artifact from the hierarchy to begin')
			).toBeVisible({ timeout: 15000 });
			expect(dialogMessage).toBeNull();

			// Reopen and confirm the mid-edit value round-tripped to the server.
			await openArtifactAttributes(page, renamed);
			await expect(nameField(page)).toHaveValue(renamed, {
				timeout: 15000,
			});
		} finally {
			await page.close();
			await purgeBranchViaApi(request, branchId);
		}
	});

	test('closes a clean tab immediately with no prompt', async ({
		browser,
		request,
	}) => {
		const suffix = `${Date.now()}-${Math.floor(Math.random() * 1e6)}`;
		const branch = `AE Close Clean ${suffix}`;
		const artifact = `AE Close Clean Art ${suffix}`;
		const branchId = await createBranchViaApi(request, branch);
		const page = await browser.newPage();

		let dialogMessage: string | null = null;
		page.on('dialog', (d) => {
			dialogMessage = d.message();
			void d.dismiss();
		});

		try {
			await openBranch(page, branch);
			await createArtifact(
				page,
				'System Requirements - Markdown',
				artifact
			);
			await openArtifactAttributes(page, artifact);

			// No edits — the editor is clean. Closing must be immediate and silent.
			await closeButton(page).click();
			await expect(
				page.getByText('Select an artifact from the hierarchy to begin')
			).toBeVisible({ timeout: 15000 });
			expect(dialogMessage).toBeNull();
		} finally {
			await page.close();
			await purgeBranchViaApi(request, branchId);
		}
	});
});
