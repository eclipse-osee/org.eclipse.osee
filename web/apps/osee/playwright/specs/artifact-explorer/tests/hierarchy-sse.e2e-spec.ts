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
import { test, expect, Page } from '@ngx-playwright/test';
import {
	DEMO_USERS,
	newUserPage,
	createBranchViaApi,
	purgeBranchViaApi,
	createArtifact,
	openBranch,
	expandArtifact,
	searchAndOpenArtifact,
	switchEditorSection,
	switchToHierarchy,
} from '../utils/helpers';
import { waitForPageReadyForSse } from '../../../shared/sse-helpers';

/**
 * Real-time (SSE) behavior for the artifact-explorer HIERARCHY TREE with two demo
 * users: a create / rename / delete by one user is reflected in the OTHER user's
 * tree without a manual refresh. This exercises the hierarchy's own refetch
 * triggers -- structural changes (create/delete) via `structuralChangesForBranch`,
 * and a Name change via `forChangedAttributeType` (the `changedAttributeTypeIds`
 * path) which updates a node label without reacting to every `attribute_modified`.
 *
 * The three behaviors run as labeled steps in one test over a single self-contained
 * branch (created + purged in-test), chaining create -> rename -> delete on the same
 * node to avoid re-paying the two-user + tree-expand setup three times.
 */

const ROOT = 'System Requirements - Markdown';

/** The hierarchy tree node (button) for an artifact name, scoped to the tree panel. */
function treeNode(page: Page, name: string) {
	return page
		.locator('osee-artifact-hierarchy')
		.getByRole('button', { name, exact: true });
}

/** Opens the branch and expands the root so the child node is visible in the tree. */
async function openTreeExpanded(page: Page, branchName: string) {
	await openBranch(page, branchName);
	await expandArtifact(page, ROOT);
}

test.describe('Artifact hierarchy real-time (SSE, two users)', () => {
	// The create/rename/delete behaviors share one expensive setup (a branch + two
	// open, tree-expanded editors) and chain naturally (create a node, rename it,
	// delete it), so they run as labeled steps in a single test rather than three
	// tests each re-paying that setup (per the perf guidance: consolidate shared-
	// setup steps). Joe acts; Jason (idle) must observe each change over SSE.
	test('create, rename, and delete by one user propagate to another user hierarchy tree', async ({
		browser,
		request,
	}) => {
		test.setTimeout(90000);
		const suffix = `${Date.now()}-${Math.floor(Math.random() * 1e6)}`;
		const branchName = `AE Hier SSE ${suffix}`;
		const child = `AE Hier SSE Child ${suffix}`;
		const renamed = child + ' Renamed';
		const branchId = await createBranchViaApi(request, branchName);

		const joe = await newUserPage(browser, DEMO_USERS.joe);
		const jason = await newUserPage(browser, DEMO_USERS.jason);
		try {
			await openTreeExpanded(joe, branchName);
			await openTreeExpanded(jason, branchName);
			// Both tabs must have settled their loads AND connected their SSE stream before any
			// cross-user step: acting before Joe is ready can no-op the action, and asserting
			// before Jason is subscribed misses the event this test is about.
			await Promise.all([
				waitForPageReadyForSse(joe),
				waitForPageReadyForSse(jason),
			]);

			await test.step('a created child appears in the other tree', async () => {
				// Structural change (artifact_created/relation_added) -> hierarchy refetch.
				await expect(treeNode(jason, child)).toHaveCount(0);
				await createArtifact(joe, ROOT, child);
				await expect(treeNode(jason, child)).toBeVisible({
					timeout: 20000,
				});
			});

			await test.step('a rename updates the label in the other tree', async () => {
				// The regression guard for the hierarchy-not-updating-on-rename fix: a Name
				// edit propagates via the changedAttributeTypeIds path, not a structural change.
				await searchAndOpenArtifact(joe, child);
				await switchEditorSection(joe, 'Attributes');
				const nameField = joe
					.locator('osee-focus-lost-input')
					.first()
					.getByRole('textbox');
				await nameField.click();
				await nameField.fill(renamed);
				await Promise.all([
					joe.waitForResponse(
						(res) =>
							res.url().includes('orcs/txs') &&
							res.status() === 200
					),
					nameField.evaluate((el) => el.blur()),
				]);
				// searchAndOpenArtifact switched Joe's sidebar to Search; switch him back so his
				// tree is visible for the delete step (and let Jason settle his rename refetch).
				await switchToHierarchy(joe);
				await Promise.all([
					waitForPageReadyForSse(joe),
					waitForPageReadyForSse(jason),
				]);
				await expect(treeNode(jason, renamed)).toBeVisible({
					timeout: 20000,
				});
				await expect(treeNode(jason, child)).toHaveCount(0);
			});

			await test.step('a delete removes the node from the other tree', async () => {
				await treeNode(joe, renamed).click({ button: 'right' });
				await joe
					.getByRole('menuitem', { name: 'Delete Artifact' })
					.click();
				const deleteDialog = joe.getByRole('dialog', {
					name: 'Delete Artifact',
				});
				await expect(deleteDialog).toBeVisible({ timeout: 10000 });
				await Promise.all([
					joe.waitForResponse(
						(res) =>
							res.url().includes('orcs/txs') &&
							res.status() === 200
					),
					deleteDialog
						.getByRole('button', { name: 'Delete' })
						.click(),
				]);
				await expect(treeNode(jason, renamed)).toHaveCount(0, {
					timeout: 20000,
				});
			});
		} finally {
			await joe.context().close();
			await jason.context().close();
			await purgeBranchViaApi(request, branchId);
		}
	});
});
