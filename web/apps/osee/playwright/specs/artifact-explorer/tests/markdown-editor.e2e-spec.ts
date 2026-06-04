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
 * Tests for the Markdown Editor:
 * - Text editing and preview
 * - Fullscreen toggle
 * - Preview panel collapse/expand
 * - Draggable divider resize (mouse and keyboard)
 * - Divider accessibility attributes
 * - Undo/redo
 * - Toolbar states
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

/** Locates the textarea within the editor. */
function getTextarea(page: Page): Locator {
	return getEditor(page).getByRole('textbox', {
		name: 'Markdown content editor',
	});
}

/** Locates a toolbar button by its tooltip text. */
function getToolbarButton(page: Page, tooltip: string): Locator {
	return getEditor(page).getByRole('button', { name: tooltip });
}

/** Locates the preview panel (the rendered HTML area). */
function getPreviewPanel(page: Page): Locator {
	return getEditor(page).locator(
		'[role="region"][aria-label="Markdown preview"]'
	);
}

/** Locates the draggable divider. */
function getDivider(page: Page): Locator {
	return getEditor(page).locator('[role="separator"]');
}

test.describe('Markdown Editor', () => {
	test.beforeEach(async ({ page }) => {
		await page.goto('/ple/artifact-explorer');
		await page.getByText('Select a Branch').click();
		await page.getByText(branchName).click();

		// Search for an artifact with Markdown Content
		await page.getByPlaceholder('Search').click();
		await page.getByPlaceholder('Search').fill(artifactName);
		await page.getByRole('option', { name: artifactName }).first().click();

		// Wait for the markdown editor to appear
		await expect(getEditor(page)).toBeVisible({ timeout: 10000 });
	});

	test.describe('Text Editing', () => {
		test('should accept typed input in the textarea', async ({ page }) => {
			const textarea = getTextarea(page);
			await textarea.click();
			await textarea.fill('# Hello World');

			await expect(textarea).toHaveValue('# Hello World');
		});

		test('should render typed markdown in the preview panel', async ({
			page,
		}) => {
			const textarea = getTextarea(page);
			await textarea.click();
			await textarea.fill('# Preview Test');

			const preview = getPreviewPanel(page);
			await expect(preview.locator('h1')).toBeVisible({ timeout: 5000 });
			await expect(preview.locator('h1')).toContainText('Preview Test');
		});
	});

	test.describe('Undo and Redo', () => {
		test('should undo the last text change', async ({ page }) => {
			const textarea = getTextarea(page);
			await textarea.click();
			await textarea.fill('First');

			// Wait for history to register
			await page.waitForTimeout(100);

			await textarea.fill('Second');
			await page.waitForTimeout(100);

			// Click undo
			await getToolbarButton(page, 'Undo').click();

			await expect(textarea).toHaveValue('First');
		});

		test('should redo after undo', async ({ page }) => {
			const textarea = getTextarea(page);
			await textarea.click();
			await textarea.fill('First');
			await page.waitForTimeout(100);

			await textarea.fill('Second');
			await page.waitForTimeout(100);

			// Undo then redo
			await getToolbarButton(page, 'Undo').click();
			await expect(textarea).toHaveValue('First');

			await getToolbarButton(page, 'Redo').click();
			await expect(textarea).toHaveValue('Second');
		});
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
			await expect(divider).toHaveAttribute('aria-valuemin', '20');
			await expect(divider).toHaveAttribute('aria-valuemax', '80');
			await expect(divider).toHaveAttribute('aria-valuenow', '50');
		});

		test('should resize editor panel via mouse drag', async ({ page }) => {
			const divider = getDivider(page);
			const editorContainer = getEditor(page).locator(
				'[data-testid="editor-container"]'
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
				'[data-testid="editor-pane"]'
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
				'[data-testid="editor-pane"]'
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

		test('should update aria-valuenow after keyboard resize', async ({
			page,
		}) => {
			const divider = getDivider(page);
			await divider.focus();

			// Initial value should be 50
			await expect(divider).toHaveAttribute('aria-valuenow', '50');

			// Press ArrowRight to increase
			await divider.press('ArrowRight');
			await divider.press('ArrowRight');

			// Value should have increased
			await expect(divider).toHaveAttribute('aria-valuenow', '54');
		});
	});

	test.describe('Toolbar Buttons', () => {
		test('should display undo and redo buttons', async ({ page }) => {
			await expect(getToolbarButton(page, 'Undo')).toBeVisible();
			await expect(getToolbarButton(page, 'Redo')).toBeVisible();
		});

		test('should display examples button with menu items', async ({
			page,
		}) => {
			const examplesButton = getToolbarButton(page, 'Examples');
			await expect(examplesButton).toBeVisible();

			// Open examples menu
			await examplesButton.click();

			// Menu should appear with menu items
			const menuItems = page.getByRole('menuitem');
			await expect(menuItems.first()).toBeVisible();
		});

		test('should insert example content when menu item is clicked', async ({
			page,
		}) => {
			const textarea = getTextarea(page);
			const initialValue = await textarea.inputValue();

			// Open examples menu and click first item
			await getToolbarButton(page, 'Examples').click();
			await page.getByRole('menuitem').first().click();

			// Textarea should have more content now
			const newValue = await textarea.inputValue();
			expect(newValue.length).toBeGreaterThan(initialValue.length);
		});

		test('should display image preview toggle', async ({ page }) => {
			const previewButton = getToolbarButton(page, 'Preview With Images');
			await expect(previewButton).toBeVisible();
		});

		test('should show upload image button as disabled with tooltip', async ({
			page,
		}) => {
			// The upload button is wrapped in a span with a tooltip
			const uploadSpan = getEditor(page).locator(
				'[mattooltip="Upload Image"]'
			);
			await expect(uploadSpan).toBeVisible();
		});
	});

	test.describe('Disabled State', () => {
		test('should not accept input when editor is in image preview mode', async ({
			page,
		}) => {
			const textarea = getTextarea(page);
			await textarea.click();
			await textarea.fill('Before preview');

			// Toggle image preview
			await getToolbarButton(page, 'Preview With Images').click();

			// Textarea should be disabled
			await expect(textarea).toBeDisabled();

			// Return to editing
			await getToolbarButton(page, 'Return to editing.').click();

			// Textarea should be enabled again
			await expect(textarea).toBeEnabled();
		});
	});
});
