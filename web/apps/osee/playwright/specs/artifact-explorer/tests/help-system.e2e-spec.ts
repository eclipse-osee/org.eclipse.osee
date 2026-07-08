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
	navigateToArtifactExplorer,
	searchForArtifact,
	selectBranch,
} from '../utils/helpers';

const branchName = 'SAW Markdown Requirements Updates';
const artifactName = 'Events';

function getEditor(page: Page): Locator {
	return page.locator('osee-markdown-editor');
}

function getHelpButton(page: Page): Locator {
	return getEditor(page).getByRole('button').filter({ hasText: 'help_outline' });
}

test.describe('Help System', () => {
	test.describe.configure({ mode: 'parallel' });

	test.beforeEach(async ({ page }) => {
		await navigateToArtifactExplorer(page);
		await selectBranch(page, 'Working', branchName);
		await searchForArtifact(page, artifactName);
		await expect(getEditor(page)).toBeVisible({ timeout: 10000 });
	});

	test('should open help popup and display content', async ({
		page,
		context,
	}) => {
		const [popup] = await Promise.all([
			context.waitForEvent('page'),
			getHelpButton(page).click(),
		]);

		await popup.waitForLoadState('domcontentloaded');

		await test.step('Popup displays correct title', async () => {
			await expect(
				popup.getByRole('heading', { name: /Markdown Editor/i }).first()
			).toBeVisible();
		});

		await test.step('Popup renders markdown section headings', async () => {
			await expect(
				popup.getByRole('heading', { name: 'Toolbar' })
			).toBeVisible();
			await expect(
				popup.getByRole('heading', { name: 'Formatting' })
			).toBeVisible();
			await expect(
				popup.getByRole('heading', { name: 'Images' })
			).toBeVisible();
		});

		await test.step('Popup has section navigation chips', async () => {
			await expect(
				popup.getByRole('navigation', { name: 'Help sections' })
			).toBeVisible();
			await expect(
				popup
					.getByRole('navigation', { name: 'Help sections' })
					.getByRole('button', { name: /Toolbar/ })
			).toBeVisible();
		});

		await test.step('Material icons render at icon size', async () => {
			// Icons in the toolbar table should render as glyphs, not text.
			// "table_chart" as text would be ~90px+; as an icon it's ~24px
			// within a cell that has padding (~75px total).
			const iconCell = popup.getByRole('cell').first();
			await expect(iconCell).toBeVisible();
			const box = await iconCell.boundingBox();
			expect(box).not.toBeNull();
			expect(box!.width).toBeLessThan(100);
		});

		await popup.close();
	});

	test('should highlight UI elements via Show Me and auto-clear', async ({
		page,
		context,
	}) => {
		const [popup] = await Promise.all([
			context.waitForEvent('page'),
			getHelpButton(page).click(),
		]);

		await popup.waitForLoadState('domcontentloaded');

		// Wait for sections to load via postMessage handshake
		await expect(
			popup
				.getByRole('navigation', { name: 'Help sections' })
				.getByRole('button', { name: /Toolbar/ })
		).toBeVisible({ timeout: 5000 });

		await test.step('Show Me highlights the corresponding element in the parent', async () => {
			// Click the Toolbar section chip which triggers highlight
			await popup
				.getByRole('navigation', { name: 'Help sections' })
				.getByRole('button', { name: /Toolbar/ })
				.click();

			// Parent page should have the highlight class on the toolbar
			const highlighted = getEditor(page).locator(
				'[role="toolbar"].osee-help-highlight'
			);
			await expect(highlighted).toBeVisible({ timeout: 3000 });
		});

		await test.step('Highlight auto-clears after animation completes', async () => {
			// Wait for highlight to disappear (animation is 3s + 0.2s buffer)
			await expect(
				getEditor(page).locator('.osee-help-highlight')
			).toHaveCount(0, { timeout: 5000 });
		});

		await popup.close();
	});

	test('should navigate sections and update active chip', async ({
		page,
		context,
	}) => {
		const [popup] = await Promise.all([
			context.waitForEvent('page'),
			getHelpButton(page).click(),
		]);

		await popup.waitForLoadState('domcontentloaded');

		// Wait for section chips to appear
		const nav = popup.getByRole('navigation', { name: 'Help sections' });
		await expect(nav.getByRole('button', { name: /Tables/ })).toBeVisible({
			timeout: 5000,
		});

		await test.step('Clicking a section chip scrolls to that section', async () => {
			await nav.getByRole('button', { name: /Tables/ }).click();

			// Verify the Tables heading is visible in the content area
			await expect(
				popup.getByRole('heading', { name: 'Tables' })
			).toBeInViewport();
		});

		await test.step('Clicked section chip becomes active', async () => {
			await expect(
				nav.getByRole('button', { name: /Tables/ })
			).toHaveClass(/active/);
		});

		await popup.close();
	});

	test('should open help from table dialog with correct topic', async ({
		page,
		context,
	}) => {
		// Open the table dialog
		await getEditor(page)
			.getByRole('button')
			.filter({ hasText: 'table_chart' })
			.click();

		// Wait for dialog
		await expect(
			page.getByRole('heading', { name: /Table/i })
		).toBeVisible({ timeout: 5000 });

		// Click help button in the dialog
		const dialogHelpBtn = page
			.getByRole('dialog')
			.getByRole('button')
			.filter({ hasText: 'help_outline' });

		const [popup] = await Promise.all([
			context.waitForEvent('page'),
			dialogHelpBtn.click(),
		]);

		await popup.waitForLoadState('domcontentloaded');

		await test.step('Table dialog help shows table-specific content', async () => {
			await expect(
				popup.getByRole('heading', { name: 'Table Size' })
			).toBeVisible();
			await expect(
				popup.getByRole('heading', { name: 'Headers & Spans' })
			).toBeVisible();
		});

		await popup.close();
	});

	test('should reuse popup window when switching help topics', async ({
		page,
		context,
	}) => {
		// Open markdown editor help
		const [popup] = await Promise.all([
			context.waitForEvent('page'),
			getHelpButton(page).click(),
		]);
		await popup.waitForLoadState('domcontentloaded');
		await expect(
			popup.getByRole('heading', { name: 'Toolbar' })
		).toBeVisible();

		// Now open table dialog and switch to its help
		await getEditor(page)
			.getByRole('button')
			.filter({ hasText: 'table_chart' })
			.click();

		await expect(
			page.getByRole('heading', { name: /Table/i })
		).toBeVisible({ timeout: 5000 });

		await page
			.getByRole('dialog')
			.getByRole('button')
			.filter({ hasText: 'help_outline' })
			.click();

		// Same popup should navigate to new topic
		await expect(
			popup.getByRole('heading', { name: 'Table Size' })
		).toBeVisible({ timeout: 5000 });

		// Still only 2 pages total (main + popup)
		expect(context.pages().length).toBe(2);

		await popup.close();
	});
});
