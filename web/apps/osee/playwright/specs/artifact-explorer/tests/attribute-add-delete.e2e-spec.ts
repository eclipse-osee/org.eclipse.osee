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

const BRANCH = 'AE Add Delete Attr Tests';
let branchId: string;

test.describe('Attribute Add/Delete and Grouping', () => {
	test.beforeAll(async ({ browser, request }) => {
		branchId = await createBranchViaApi(request, BRANCH);
		const page = await browser.newPage();
		await openBranch(page, BRANCH);
		await createArtifact(
			page,
			'System Requirements - Markdown',
			'AE AddDel Test Art'
		);
		await page.close();
	});

	test.afterAll(async ({ request }) => {
		await purgeBranchViaApi(request, branchId);
	});

	test('should support full add/delete attribute workflow', async ({
		page,
	}) => {
		await openBranch(page, BRANCH);
		await searchAndOpenArtifact(page, 'AE AddDel Test Art');
		await switchEditorSection(page, 'Attributes');

		await test.step('Toolbar buttons are visible on Attributes section', async () => {
			await expect(
				page.getByRole('button', { name: 'Add Attribute' })
			).toBeVisible({ timeout: 10000 });
			await expect(
				page.getByRole('button', { name: 'Toggle Delete Mode' })
			).toBeVisible();
		});

		await test.step('Toolbar buttons hidden on other sections', async () => {
			await switchEditorSection(page, 'Relations');
			await expect(
				page.getByRole('button', { name: 'Add Attribute' })
			).not.toBeVisible();
			await switchEditorSection(page, 'Attributes');
			await expect(
				page.getByRole('button', { name: 'Add Attribute' })
			).toBeVisible();
		});

		await test.step('Add dialog opens, filters, and excludes Name', async () => {
			await page.getByRole('button', { name: 'Add Attribute' }).click();
			const dialog = page.locator('mat-dialog-container');
			await expect(
				dialog.getByRole('textbox', {
					name: 'Filter attribute types',
				})
			).toBeVisible({ timeout: 5000 });
			await expect(dialog.locator('mat-checkbox').first()).toBeVisible();

			// Name never appears
			await dialog
				.getByRole('textbox', { name: 'Filter attribute types' })
				.fill('Name');
			await expect(
				dialog.locator('mat-checkbox').filter({ hasText: /^Name$/ })
			).toHaveCount(0);

			// Filter narrows results
			await dialog
				.getByRole('textbox', { name: 'Filter attribute types' })
				.fill('');
			const countAll = await dialog.locator('mat-checkbox').count();
			await dialog
				.getByRole('textbox', { name: 'Filter attribute types' })
				.fill('CUI');
			const countFiltered = await dialog.locator('mat-checkbox').count();
			expect(countFiltered).toBeLessThanOrEqual(countAll);

			await dialog.getByRole('button', { name: 'Cancel' }).click();
			await expect(dialog).not.toBeVisible();
		});

		await test.step('Add single attribute with quantity input', async () => {
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
			const checkbox = dialog.locator('mat-checkbox').first();
			await expect(checkbox).toBeVisible();
			await checkbox.click();

			// Quantity input appears for unlimited types
			const countInput = dialog.locator('input[type="number"]');
			await expect(countInput).toBeVisible();

			await Promise.all([
				page.waitForResponse(
					(res) =>
						res.url().includes('orcs/txs') && res.status() === 200
				),
				dialog.getByRole('button', { name: 'Add' }).click(),
			]);
			await expect(dialog).not.toBeVisible();
		});

		await test.step('Add multiple to create grouped display', async () => {
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
			const checkbox = dialog.locator('mat-checkbox').first();
			await checkbox.click();
			const countInput = dialog.locator('input[type="number"]');
			await countInput.fill('6');

			await Promise.all([
				page.waitForResponse(
					(res) =>
						res.url().includes('orcs/txs') && res.status() === 200
				),
				dialog.getByRole('button', { name: 'Add' }).click(),
			]);
			await expect(dialog).not.toBeVisible();
		});

		await test.step('Grouped attributes show with count and collapse', async () => {
			// Group header shows count
			const groupHeader = page
				.locator('[data-testid="attribute-group-header"]')
				.first();
			await expect(groupHeader).toBeVisible({ timeout: 10000 });
			const headerText = await groupHeader.textContent();
			expect(headerText).toContain('(');

			// "Show more" link should be visible since we have >5 instances
			const showMore = page.getByText(/Show \d+ more/).first();
			await expect(showMore).toBeVisible();

			// Click to expand
			await showMore.click();
			await expect(page.getByText('Show less').first()).toBeVisible();

			// Click to collapse
			await page.getByText('Show less').first().click();
			await expect(page.getByText(/Show \d+ more/).first()).toBeVisible();
		});

		await test.step('Multiplicity: max-1 type disappears after adding', async () => {
			await page.getByRole('button', { name: 'Add Attribute' }).click();
			const dialog = page.locator('mat-dialog-container');
			await expect(
				dialog.getByRole('textbox', {
					name: 'Filter attribute types',
				})
			).toBeVisible({ timeout: 5000 });

			const max1Type = dialog
				.locator('mat-checkbox')
				.filter({ hasText: /Max 1|Exactly One/ })
				.first();
			if ((await max1Type.count()) > 0) {
				const typeName = await max1Type.textContent();
				await max1Type.click();
				await Promise.all([
					page.waitForResponse(
						(res) =>
							res.url().includes('orcs/txs') &&
							res.status() === 200
					),
					dialog.getByRole('button', { name: 'Add' }).click(),
				]);
				await expect(dialog).not.toBeVisible();

				// Re-open — type should be gone
				await page
					.getByRole('button', { name: 'Add Attribute' })
					.click();
				const dialog2 = page.locator('mat-dialog-container');
				await expect(
					dialog2.getByRole('textbox', {
						name: 'Filter attribute types',
					})
				).toBeVisible({ timeout: 5000 });

				if (typeName) {
					const trimmed = typeName.trim().split('(')[0].trim();
					await dialog2
						.getByRole('textbox', {
							name: 'Filter attribute types',
						})
						.fill(trimmed);
					await expect(
						dialog2
							.locator('mat-checkbox')
							.filter({ hasText: trimmed })
					).toHaveCount(0);
				}
				await dialog2.getByRole('button', { name: 'Cancel' }).click();
			} else {
				await dialog.getByRole('button', { name: 'Cancel' }).click();
			}
		});

		await test.step('Delete mode: toggle on shows disabled Name icon', async () => {
			await page
				.getByRole('button', { name: 'Toggle Delete Mode' })
				.click();
			const nameBtn = page.getByRole('button', {
				name: 'Cannot delete Name',
			});
			await expect(nameBtn).toBeVisible({ timeout: 5000 });
			await expect(nameBtn).toBeDisabled();
		});

		await test.step('Delete mode: delete an attribute instance', async () => {
			const deleteBtn = page
				.getByRole('button', {
					name: 'Delete attribute instance',
				})
				.first();
			await expect(deleteBtn).toBeVisible({ timeout: 5000 });

			await Promise.all([
				page.waitForResponse(
					(res) =>
						res.url().includes('orcs/txs') && res.status() === 200
				),
				deleteBtn.click(),
			]);

			await expect(
				page.locator('osee-persisted-artifact-attribute-editor').first()
			).toBeVisible({ timeout: 10000 });
		});

		await test.step('Delete mode: toggle off hides icons', async () => {
			await page
				.getByRole('button', { name: 'Toggle Delete Mode' })
				.click();
			await expect(
				page.getByRole('button', { name: 'Cannot delete Name' })
			).not.toBeVisible();
		});
	});
});
