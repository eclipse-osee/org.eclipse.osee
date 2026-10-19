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
import { test, expect, type Page, type Locator } from '@ngx-playwright/test';
import {
	createBranchViaApi,
	purgeBranchViaApi,
	createArtifact,
	openBranch,
	searchAndOpenArtifact,
	switchEditorSection,
	switchToHierarchy,
	expandArtifact,
} from '../utils/helpers';
import { waitForNetworkIdleIgnoringSse } from '../../../shared/sse-helpers';

/**
 * Drag-and-drop artifact linking in the markdown editor, plus preview link
 * opening. Dragging an artifact from the hierarchy onto the markdown editor
 * inserts an `<artifact-link>` (or `<image-link>` for image artifacts) at the
 * cursor; clicking the resolved link in the preview opens that artifact in a new,
 * editable tab. Mirrors the desktop OmeEditTabDrop behavior.
 *
 * Critical regressions this guards (all introduced/fixed this effort):
 *  - the drop must auto-SAVE (it bypasses the textarea focus/blur cycle, so the
 *    field would otherwise stay dirty and the tab could not close without an
 *    unsaved-changes prompt),
 *  - the tab opened via a link must be EDITABLE (branch-type-derived editability,
 *    not the raw endpoint's non-branch-aware flag),
 *  - BOTH the original tab and the link-opened tab must close without a dirty
 *    prompt.
 */

const BRANCH = 'AE Artifact Link Tests';
let branchId: string;

// The artifact whose markdown we drop a link INTO.
const HOST = 'AE Link Host Art';
// The artifact we drag onto the editor to create a link TO.
const TARGET = 'AE Link Target Art';

function markdownTextarea(page: Page): Locator {
	return page.locator('osee-markdown-editor textarea');
}

/**
 * Drives a CDK drag of a hierarchy artifact button onto a drop target element.
 * CDK drag is pointer-based: hover the source, press, hover the target TWICE
 * (two moves let CDK register the drop-list enter before release), then release.
 * This mirrors the proven CDK drag pattern used in the MIM traceability spec
 * (hierarchy -> relation side), which uses the same `cdkDrag`/`cdkDropList` wiring.
 */
async function dragArtifactOnto(
	page: Page,
	source: Locator,
	target: Locator
): Promise<void> {
	await source.hover();
	await page.mouse.down();
	await target.hover();
	await target.hover();
	await page.mouse.up();
}

test.describe('Markdown artifact linking (drag + preview open)', () => {
	test.describe.configure({ mode: 'serial' });

	test.beforeAll(async ({ browser, request }) => {
		branchId = await createBranchViaApi(request, BRANCH);
		const page = await browser.newPage();
		await openBranch(page, BRANCH);
		await createArtifact(page, 'System Requirements - Markdown', HOST);
		await createArtifact(page, 'System Requirements - Markdown', TARGET);
		await page.close();
	});

	test.afterAll(async ({ request }) => {
		await purgeBranchViaApi(request, branchId);
	});

	test('dragging an artifact into the editor inserts a link, auto-saves, and both tabs close clean', async ({
		page,
	}) => {
		// Fail loudly if ANY unsaved-changes prompt appears — the whole point is
		// that a dropped link does not leave a field dirty.
		let dialogText: string | null = null;
		page.on('dialog', (dialog) => {
			dialogText = dialog.message();
			void dialog.dismiss();
		});

		await openBranch(page, BRANCH);

		await test.step('open the host artifact markdown editor', async () => {
			// Gate on SSE-safe network idle so a dev server slow under parallel load
			// can't be interacted with before the app finished bootstrapping.
			await waitForNetworkIdleIgnoringSse(page);
			await searchAndOpenArtifact(page, HOST);
			await switchEditorSection(page, 'Attributes');
			await expect(markdownTextarea(page)).toBeVisible({
				timeout: 20000,
			});
		});

		await test.step('drag the target artifact from the hierarchy onto the editor', async () => {
			// Opening via search left the left panel on the Search section; the drag
			// SOURCE is a hierarchy node, so switch to the hierarchy and expand the
			// root (System Requirements - Markdown) to reveal both child artifacts.
			await switchToHierarchy(page);
			await expandArtifact(page, 'System Requirements - Markdown');
			const targetNode = page
				.locator('osee-artifact-hierarchy button')
				.filter({ hasText: TARGET })
				.first();
			await expect(targetNode).toBeVisible({ timeout: 15000 });

			const editorBody = page.locator(
				'osee-markdown-editor [data-testid="editor-container"]'
			);
			// The drop auto-saves; capture that transaction.
			await Promise.all([
				page.waitForResponse(
					(res) =>
						res.url().includes('orcs/txs') &&
						res.request().method() === 'POST' &&
						res.status() === 200,
					{ timeout: 30000 }
				),
				dragArtifactOnto(page, targetNode, editorBody),
			]);
		});

		await test.step('the editor content now contains an artifact-link tag', async () => {
			await expect(markdownTextarea(page)).toHaveValue(
				/<artifact-link>\d+<\/artifact-link>/,
				{ timeout: 15000 }
			);
		});

		await test.step('the host tab closes without an unsaved-changes prompt', async () => {
			await page.getByLabel('Close tab').first().click();
			await expect(
				page
					.locator('osee-artifact-tab-group')
					.getByText(HOST, { exact: true })
			).not.toBeVisible({ timeout: 10000 });
			expect(
				dialogText,
				'drop should have auto-saved; no dirty prompt expected'
			).toBeNull();
		});

		await test.step('reopening shows the link persisted', async () => {
			await waitForNetworkIdleIgnoringSse(page);
			await searchAndOpenArtifact(page, HOST);
			await switchEditorSection(page, 'Attributes');
			await expect(markdownTextarea(page)).toHaveValue(
				/<artifact-link>\d+<\/artifact-link>/,
				{ timeout: 15000 }
			);
		});

		await test.step('clicking the link in the preview opens the target in a new, editable tab', async () => {
			// The preview resolves <artifact-link> to an anchor with data-artifact-id.
			const previewLink = page
				.locator('osee-markdown-editor a[data-artifact-id]')
				.first();
			await expect(previewLink).toBeVisible({ timeout: 15000 });
			// Click the link's TEXT (position 'center' targets the inner text node),
			// reproducing a real user click. A naive handler reading event.target
			// gets the text node (no closest()) and falls through to the native
			// href="#" navigation; this guards that regression.
			await previewLink.click({ position: { x: 5, y: 5 } });

			// The native href="#" must NOT have been followed (would append '#').
			expect(page.url()).not.toContain('/#');

			// A new tab for the TARGET opens.
			await expect(
				page
					.locator('osee-artifact-tab-group')
					.getByText(TARGET, { exact: true })
					.first()
			).toBeVisible({ timeout: 15000 });

			// The opened tab must be EDITABLE: its Name field is enabled (not disabled
			// because the raw endpoint returned a non-branch-aware editable=false).
			await switchEditorSection(page, 'Attributes');
			const targetName = page
				.locator('osee-focus-lost-input')
				.first()
				.getByRole('textbox');
			await expect(targetName).toBeEnabled({ timeout: 15000 });
		});

		await test.step('both tabs close without a dirty prompt', async () => {
			// Close the link-opened (target) tab — it was only viewed, never edited.
			const closeButtons = page.getByLabel('Close tab');
			await closeButtons.first().click();
			await closeButtons.first().click();
			await expect(
				page.locator('osee-artifact-tab-group .mat-mdc-tab')
			).toHaveCount(0, { timeout: 10000 });
			expect(
				dialogText,
				'neither tab should be dirty; no prompt expected'
			).toBeNull();
		});
	});
});
