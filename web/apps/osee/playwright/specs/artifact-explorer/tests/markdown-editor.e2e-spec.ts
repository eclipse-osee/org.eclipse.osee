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

/**
 * Tests for the Markdown Editor layout features:
 * - Fullscreen toggle
 * - Preview panel collapse/expand
 * - Draggable divider resize (mouse and keyboard)
 * - Divider accessibility attributes
 *
 * Uses the "SAW Markdown Requirements Updates" working branch which has
 * artifacts with "Markdown Content" attributes.
 */

const branchName = 'SAW Markdown Requirements Updates';
const artifactName = 'Software Artifact Template Specification Record';

/** Locates the markdown editor component on the page. */
function getEditor(page: Page): Locator {
	return page.locator('osee-markdown-editor');
}

/** Locates a toolbar button by its tooltip text. */
function getToolbarButton(page: Page, tooltip: string): Locator {
	return getEditor(page).getByRole('button', { name: tooltip });
}

/** Locates the preview panel (the rendered HTML area). */
function getPreviewPanel(page: Page): Locator {
	return getEditor(page).locator('[innerhtml]').first();
}

/** Locates the draggable divider. */
function getDivider(page: Page): Locator {
	return getEditor(page).locator('[role="separator"]');
}

test.describe('Markdown Editor Layout Features', () => {
	test.beforeEach(async ({ page }) => {
		await page.goto('/artifact-explorer');
		await page.getByText('Select a Branch').click();
		await page.getByText(branchName).click();

		// Search for an artifact with Markdown Content
		await page.getByPlaceholder('Search').click();
		await page.getByPlaceholder('Search').fill(artifactName);
		await page.getByRole('option', { name: artifactName }).first().click();

		// Wait for the markdown editor to appear
		await expect(getEditor(page)).toBeVisible({ timeout: 10000 });
	});

	test.describe('Preview Panel Collapse', () => {
		test('should toggle preview panel visibility', async ({ page }) => {
			const collapseButton = getToolbarButton(page, 'Hide Preview Panel');
			await expect(collapseButton).toBeVisible();

			// Collapse the preview
			await collapseButton.click();

			// Preview panel should not be visible
			await expect(getPreviewPanel(page)).not.toBeVisible();

			// Button tooltip should change
			const expandButton = getToolbarButton(page, 'Show Preview Panel');
			await expect(expandButton).toBeVisible();

			// Expand the preview
			await expandButton.click();
			await expect(getPreviewPanel(page)).toBeVisible();
		});
	});

	test.describe('Fullscreen Toggle', () => {
		test('should enter and exit fullscreen', async ({ page }) => {
			const fullscreenButton = getToolbarButton(page, 'Fullscreen');
			await expect(fullscreenButton).toBeVisible();

			// Enter fullscreen
			await fullscreenButton.click();

			// Wait for fullscreen to activate
			const isFullscreen = await page.evaluate(
				() => !!document.fullscreenElement
			);
			expect(isFullscreen).toBe(true);

			// Exit button should now be visible
			const exitButton = getToolbarButton(page, 'Exit Fullscreen');
			await expect(exitButton).toBeVisible();

			// Exit fullscreen
			await exitButton.click();

			// Verify we exited
			const isStillFullscreen = await page.evaluate(
				() => !!document.fullscreenElement
			);
			expect(isStillFullscreen).toBe(false);
		});
	});

	test.describe('Draggable Divider', () => {
		test('should have correct accessibility attributes', async ({
			page,
		}) => {
			const divider = getDivider(page);
			await expect(divider).toBeVisible();
			await expect(divider).toHaveAttribute('role', 'separator');
			await expect(divider).toHaveAttribute(
				'aria-orientation',
				'vertical'
			);
			await expect(divider).toHaveAttribute('tabindex', '0');
		});

		test('should resize editor panel via mouse drag', async ({ page }) => {
			const divider = getDivider(page);
			const editorContainer = getEditor(page).locator(
				'[class*="tw-relative"][class*="tw-flex"]'
			);

			// Get initial container bounds
			const containerBox = await editorContainer.boundingBox();
			expect(containerBox).not.toBeNull();

			const dividerBox = await divider.boundingBox();
			expect(dividerBox).not.toBeNull();

			// Drag the divider to the right
			const startX = dividerBox!.x + dividerBox!.width / 2;
			const startY = dividerBox!.y + dividerBox!.height / 2;
			const dragDistance = 100;

			await page.mouse.move(startX, startY);
			await page.mouse.down();
			await page.mouse.move(startX + dragDistance, startY, {
				steps: 5,
			});
			await page.mouse.up();

			// The editor width style should have changed
			const editorPane = getEditor(page).locator(
				'[class*="tw-min-w-0"][class*="tw-flex-col"]'
			);
			const widthStyle = await editorPane.getAttribute('style');
			expect(widthStyle).toContain('width');
		});

		test('should resize editor panel via keyboard', async ({ page }) => {
			const divider = getDivider(page);

			// Focus the divider
			await divider.focus();

			// Get initial editor width
			const editorPane = getEditor(page).locator(
				'[class*="tw-min-w-0"][class*="tw-flex-col"]'
			);
			const initialStyle = await editorPane.getAttribute('style');

			// Press ArrowRight to increase editor width
			await divider.press('ArrowRight');
			await divider.press('ArrowRight');
			await divider.press('ArrowRight');

			const newStyle = await editorPane.getAttribute('style');

			// Width should have changed
			expect(newStyle).not.toEqual(initialStyle);
		});
	});

	test.describe('Toolbar Buttons', () => {
		test('should display undo and redo buttons', async ({ page }) => {
			await expect(getToolbarButton(page, 'Undo')).toBeVisible();
			await expect(getToolbarButton(page, 'Redo')).toBeVisible();
		});

		test('should display examples button with menu', async ({ page }) => {
			const examplesButton = getToolbarButton(page, 'Examples');
			await expect(examplesButton).toBeVisible();

			// Open examples menu
			await examplesButton.click();

			// Menu should appear with example items
			const menu = page.locator('mat-menu');
			await expect(menu).toBeVisible();
		});

		test('should display image preview toggle', async ({ page }) => {
			const previewButton = getToolbarButton(page, 'Preview With Images');
			await expect(previewButton).toBeVisible();
		});
	});
});
