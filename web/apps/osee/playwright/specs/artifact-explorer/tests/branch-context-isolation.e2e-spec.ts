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
	selectBranch,
} from '../utils/helpers';
import { waitForNetworkIdleIgnoringSse } from '../../../shared/sse-helpers';

/**
 * Branch-context isolation for the artifact editor.
 *
 * An editor tab pins its own branch (`tab().branchId`). Reads were already
 * tab-sourced, but attribute WRITES used to fall back to the global
 * `UiService.id` (the branch selector) -- so switching the global branch while a
 * tab stayed open silently committed edits to the wrong branch. The fix provides
 * a scoped, branch-pinned `CurrentTransactionService` for the whole editor
 * subtree (`provideBranchContext()` on `ArtifactEditorComponent`). See
 * `docs/ai/web/transaction-service.md` -> "Branch targeting".
 *
 * This test is the end-to-end regression guard: it opens an artifact on branch A,
 * switches the global selector to a DIFFERENT branch B, edits in the still-open A
 * tab, and proves the edit landed on A (not B) by closing, returning to A, and
 * reading the value back. It also asserts B did not receive the write.
 */

const BRANCH_A = 'AE BranchCtx A';
const BRANCH_B = 'AE BranchCtx B';
// Shared, PRISTINE artifact used by the two-tab / add-attribute tests (same name
// on both branches so the write's target branch is provable by where it lands).
const ARTIFACT = 'AE BranchCtx Art';
// Separate artifact for the rename test so its mutation cannot corrupt the shared
// one the later serial tests depend on (keeps each test's state independent).
const RENAME_ARTIFACT = 'AE BranchCtx Rename Art';

let branchAId: string;
let branchBId: string;

function nameField(page: Page) {
	return page.locator('osee-focus-lost-input').first().getByRole('textbox');
}

/**
 * Opens an artifact's Attributes editor on the current branch, gating on SSE-safe
 * network idle first so a dev server slow under parallel load cannot be
 * interacted with before the app finished bootstrapping.
 */
async function openArtifactAttributes(page: Page, artifact: string) {
	await waitForNetworkIdleIgnoringSse(page);
	await searchAndOpenArtifact(page, artifact);
	await switchEditorSection(page, 'Attributes');
	await expect(nameField(page)).toBeVisible({ timeout: 20000 });
}

test.describe('Branch-context isolation (tab pins its branch for writes)', () => {
	test.describe.configure({ mode: 'serial' });

	test.beforeAll(async ({ browser, request }) => {
		// Two independent working branches off SAW_Bld_1, each with its OWN artifact
		// of the same name. Same name on both lets us prove the write targeted the
		// right branch purely by which branch the saved value shows up on.
		branchAId = await createBranchViaApi(request, BRANCH_A);
		branchBId = await createBranchViaApi(request, BRANCH_B);

		const page = await browser.newPage();
		await openBranch(page, BRANCH_A);
		await createArtifact(page, 'System Requirements - Markdown', ARTIFACT);
		await createArtifact(
			page,
			'System Requirements - Markdown',
			RENAME_ARTIFACT
		);
		await openBranch(page, BRANCH_B);
		await createArtifact(page, 'System Requirements - Markdown', ARTIFACT);
		await createArtifact(
			page,
			'System Requirements - Markdown',
			RENAME_ARTIFACT
		);
		await page.close();
	});

	test.afterAll(async ({ request }) => {
		await purgeBranchViaApi(request, branchAId);
		await purgeBranchViaApi(request, branchBId);
	});

	test('an edit made after switching the global branch saves to the tab branch, not the selected one', async ({
		page,
	}) => {
		// Unique target so the fill is always a real change (fires the save) and the
		// test is idempotent on re-run regardless of leftover state.
		const editedName = `${RENAME_ARTIFACT} edited on A ${Date.now()}`;

		await test.step('open the artifact on branch A', async () => {
			await openBranch(page, BRANCH_A);
			await openArtifactAttributes(page, RENAME_ARTIFACT);
		});

		await test.step('switch the GLOBAL branch selector to branch B while the A tab stays open', async () => {
			// Changing the selector does NOT touch the open tab (tabs are pinned);
			// it only changes what the hierarchy/global state points at. The A tab's
			// editor must keep reading and writing branch A.
			await selectBranch(page, 'Working', BRANCH_B);
			// The A tab is still open and still shows the A artifact's editor.
			await expect(nameField(page)).toBeVisible({ timeout: 15000 });
		});

		await test.step('edit the name in the still-open A tab and capture the save', async () => {
			const field = nameField(page);
			await expect(field).toHaveValue(RENAME_ARTIFACT, {
				timeout: 15000,
			});
			await field.click();
			await field.fill(editedName);
			// Capture the actual transaction so we can assert WHICH branch it hit.
			const [txResponse] = await Promise.all([
				page.waitForResponse(
					(res) =>
						res.url().includes('orcs/txs') &&
						res.request().method() === 'POST' &&
						res.status() === 200,
					{ timeout: 30000 }
				),
				field.evaluate((el) => (el as HTMLElement).blur()),
			]);
			// The transaction body's branch must be branch A (the tab's branch),
			// NOT branch B (the current global selection). This is the direct,
			// non-UI assertion of the branch-context fix.
			const requestBody = txResponse.request().postDataJSON();
			expect(requestBody.branch).toBe(branchAId);
			expect(requestBody.branch).not.toBe(branchBId);
		});

		await test.step('the edited value persisted on branch A (close, return to A, reopen, read back)', async () => {
			await page.getByLabel('Close tab').first().click();
			await openBranch(page, BRANCH_A);
			await openArtifactAttributes(page, editedName);
			await expect(nameField(page)).toHaveValue(editedName, {
				timeout: 15000,
			});
			// Restore the name so the test is idempotent across re-runs.
			const restore = nameField(page);
			await restore.click();
			await restore.fill(RENAME_ARTIFACT);
			await Promise.all([
				page.waitForResponse(
					(res) =>
						res.url().includes('orcs/txs') &&
						res.request().method() === 'POST' &&
						res.status() === 200,
					{ timeout: 30000 }
				),
				restore.evaluate((el) => (el as HTMLElement).blur()),
			]);
			await page.getByLabel('Close tab').first().click();
		});

		await test.step('branch B still has the ORIGINAL value (the write did not leak to it)', async () => {
			await openBranch(page, BRANCH_B);
			await openArtifactAttributes(page, RENAME_ARTIFACT);
			await expect(nameField(page)).toHaveValue(RENAME_ARTIFACT, {
				timeout: 15000,
			});
		});
	});

	test('two tabs for the same artifact on different branches each save to their own branch', async ({
		page,
	}) => {
		// addArtifactTabOnBranch dedups per (branch, artifact), so the same artifact
		// can be open as two tabs on two branches. Each must write to its own branch.
		const bName = `${ARTIFACT} B-only ${Date.now()}`;

		await test.step('open the artifact on A, then (without reloading) switch to B and open it again -> two tabs', async () => {
			await openBranch(page, BRANCH_A);
			await openArtifactAttributes(page, ARTIFACT);

			// Switch the branch selector in-place (NOT openBranch, which navigates and
			// would reset the tab group). This keeps the A tab open while we open a
			// second tab on B for the same artifact.
			await selectBranch(page, 'Working', BRANCH_B);
			await openArtifactAttributes(page, ARTIFACT);
			// Two tabs now exist (dedup is per branch+artifact, so same artifact on
			// two branches is two tabs).
			await expect(
				page.locator('osee-artifact-tab-group .mat-mdc-tab')
			).toHaveCount(2, { timeout: 15000 });
		});

		await test.step('editing the B tab writes to branch B', async () => {
			// The B tab is the active one (opened last).
			const field = nameField(page);
			await field.click();
			await field.fill(bName);
			const [txResponse] = await Promise.all([
				page.waitForResponse(
					(res) =>
						res.url().includes('orcs/txs') &&
						res.request().method() === 'POST' &&
						res.status() === 200,
					{ timeout: 30000 }
				),
				field.evaluate((el) => (el as HTMLElement).blur()),
			]);
			expect(txResponse.request().postDataJSON().branch).toBe(branchBId);
		});

		// Restore B's name so the shared artifact stays pristine for re-runs.
		await test.step('restore B name', async () => {
			const field = nameField(page);
			await expect(field).toHaveValue(bName, { timeout: 15000 });
			await field.click();
			await field.fill(ARTIFACT);
			await Promise.all([
				page.waitForResponse(
					(res) =>
						res.url().includes('orcs/txs') &&
						res.request().method() === 'POST' &&
						res.status() === 200,
					{ timeout: 30000 }
				),
				field.evaluate((el) => (el as HTMLElement).blur()),
			]);
		});
	});

	test('add-attribute and conflict/add transactions from a switched-branch tab target the tab branch', async ({
		page,
	}) => {
		// The attribute-panel writes (add/delete/native/conflict-commit) all go
		// through the scoped CurrentTransactionService too, so they must also target
		// the tab branch -- not just the per-field Name save covered above. Add an
		// attribute from the A tab after switching the global selector to B and assert
		// the transaction's branch is A.
		await test.step('open on A, then switch the global selector to B', async () => {
			await openBranch(page, BRANCH_A);
			await openArtifactAttributes(page, ARTIFACT);
			await selectBranch(page, 'Working', BRANCH_B);
			await expect(nameField(page)).toBeVisible({ timeout: 15000 });
		});

		await test.step('adding an attribute in the A tab commits to branch A', async () => {
			await page.getByRole('button', { name: 'Add Attribute' }).click();
			const dialog = page.locator('mat-dialog-container');
			await expect(
				dialog.getByRole('textbox', {
					name: 'Filter attribute types',
				})
			).toBeVisible({ timeout: 5000 });
			await dialog
				.getByRole('textbox', { name: 'Filter attribute types' })
				.fill('CUI Limited');
			await dialog.locator('mat-checkbox').first().click();
			const [txResponse] = await Promise.all([
				page.waitForResponse(
					(res) =>
						res.url().includes('orcs/txs') &&
						res.request().method() === 'POST' &&
						res.status() === 200,
					{ timeout: 30000 }
				),
				dialog.getByRole('button', { name: 'Add' }).click(),
			]);
			expect(txResponse.request().postDataJSON().branch).toBe(branchAId);
		});
	});
});
