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

/**
 * Tests for the Markdown Editor:
 * - Text editing and preview
 * - Fullscreen toggle
 * - Preview panel collapse/expand
 * - Draggable divider resize (mouse and keyboard)
 * - Divider accessibility attributes
 * - Undo/redo
 * - Toolbar states
 * - Table dialog (create, edit, caption, row numbers, undo/redo, spans)
 * - Image upload dialog
 * - Caption examples
 *
 * Uses the "SAW Markdown Requirements Updates" working branch which has
 * artifacts with "Markdown Content" attributes.
 */

const branchName = 'SAW Markdown Requirements Updates';
const artifactName = 'Events';

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

/** Locates a toolbar button by its icon name. */
function getToolbarButton(page: Page, iconName: string): Locator {
	return getEditor(page)
		.getByRole('button')
		.filter({ hasText: iconName.toLowerCase() });
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
		await navigateToArtifactExplorer(page);
		await selectBranch(page, 'Working', branchName);
		await searchForArtifact(page, artifactName);
		await expect(getEditor(page)).toBeVisible({ timeout: 10000 });
	});

	test.describe('Text Editing', () => {
		test('should accept typed input in the textarea', async ({ page }) => {
			const textarea = getTextarea(page);
			await textarea.click();
			await textarea.fill('# Hello World');
			await expect(textarea).toHaveValue('# Hello World');
		});

		test('should render typed markdown in the preview panel', async ({ page }) => {
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
			await getToolbarButton(page, 'undo').click();
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
			await getToolbarButton(page, 'undo').click();
			await expect(textarea).toHaveValue('First');
			await getToolbarButton(page, 'redo').click();
			await expect(textarea).toHaveValue('Second');
		});
	});

	test.describe('Preview Panel Collapse', () => {
		test('should toggle preview panel visibility', async ({ page }) => {
			const collapseButton = getToolbarButton(page, 'view_sidebar');
			await expect(collapseButton).toBeVisible();
			// Collapse the preview
			await collapseButton.click();
			// Preview panel should not be visible
			await expect(getPreviewPanel(page)).not.toBeVisible();
			// Button icon should change to vertical_split
			const expandButton = getToolbarButton(page, 'vertical_split');
			await expect(expandButton).toBeVisible();
			// Expand the preview
			await expandButton.click();
			await expect(getPreviewPanel(page)).toBeVisible();
		});
	});

	test.describe('Fullscreen Toggle', () => {
		test('should enter and exit fullscreen', async ({ page }) => {
			const fullscreenButton = getEditor(page)
				.getByRole('button')
				.filter({ hasText: /^\s*fullscreen\s*$/ });
			await expect(fullscreenButton).toBeVisible();
			// Enter fullscreen
			await fullscreenButton.click();
			// Wait for fullscreen to activate
			const isFullscreen = await page.evaluate(() => !!document.fullscreenElement);
			expect(isFullscreen).toBe(true);
			// Exit button should now be visible
			const exitButton = getToolbarButton(page, 'fullscreen_exit');
			await expect(exitButton).toBeVisible();
			// Exit fullscreen
			await exitButton.click();
			// Verify we exited
			const isStillFullscreen = await page.evaluate(() => !!document.fullscreenElement);
			expect(isStillFullscreen).toBe(false);
		});
	});

	test.describe('Draggable Divider', () => {
		test('should have correct accessibility attributes', async ({ page }) => {
			const divider = getDivider(page);
			await expect(divider).toBeVisible();
			await expect(divider).toHaveAttribute('role', 'separator');
			await expect(divider).toHaveAttribute('aria-orientation', 'vertical');
			await expect(divider).toHaveAttribute('tabindex', '0');
			await expect(divider).toHaveAttribute('aria-valuemin', '20');
			await expect(divider).toHaveAttribute('aria-valuemax', '80');
			await expect(divider).toHaveAttribute('aria-valuenow', '50');
		});

		test('should resize editor panel via mouse drag', async ({ page }) => {
			const divider = getDivider(page);
			const editorContainer = getEditor(page).locator('[data-testid="editor-container"]');
			// Get initial container bounds
			const containerBox = await editorContainer.boundingBox();
			expect(containerBox).not.toBeNull();
			const dividerBox = await divider.boundingBox();
			expect(dividerBox).not.toBeNull();
			// Drag the divider to the right
			const startX = dividerBox!.x + dividerBox!.width / 2;
			const startY = dividerBox!.y + dividerBox!.height / 2;
			await page.mouse.move(startX, startY);
			await page.mouse.down();
			await page.mouse.move(startX + 100, startY, { steps: 5 });
			await page.mouse.up();
			// The editor width style should have changed
			const editorPane = getEditor(page).locator('[data-testid="editor-pane"]');
			const widthStyle = await editorPane.getAttribute('style');
			expect(widthStyle).toContain('width');
		});

		test('should resize editor panel via keyboard', async ({ page }) => {
			const divider = getDivider(page);
			// Focus the divider
			await divider.focus();
			// Get initial editor width
			const editorPane = getEditor(page).locator('[data-testid="editor-pane"]');
			const initialStyle = await editorPane.getAttribute('style');
			// Press ArrowRight to increase editor width
			await divider.press('ArrowRight');
			await divider.press('ArrowRight');
			await divider.press('ArrowRight');
			// Width should have changed
			const newStyle = await editorPane.getAttribute('style');
			expect(newStyle).not.toEqual(initialStyle);
		});

		test('should update aria-valuenow after keyboard resize', async ({ page }) => {
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
			await expect(getToolbarButton(page, 'undo')).toBeVisible();
			await expect(getToolbarButton(page, 'redo')).toBeVisible();
		});

		test('should display examples button with menu items', async ({ page }) => {
			const examplesButton = getToolbarButton(page, 'lightbulb');
			await expect(examplesButton).toBeVisible();
			// Open examples menu
			await examplesButton.click();
			// Menu should appear with menu items
			const menuItems = page.getByRole('menuitem');
			await expect(menuItems.first()).toBeVisible();
		});

		test('should insert example content when menu item is clicked', async ({ page }) => {
			const textarea = getTextarea(page);
			const initialValue = await textarea.inputValue();
			// Open examples menu and click first item
			await getToolbarButton(page, 'lightbulb').click();
			await page.getByRole('menuitem').first().click();
			// Textarea should have more content now
			const newValue = await textarea.inputValue();
			expect(newValue.length).toBeGreaterThan(initialValue.length);
		});

		test('should display image preview toggle', async ({ page }) => {
			await expect(getToolbarButton(page, 'visibility')).toBeVisible();
		});

		test('should show upload image button', async ({ page }) => {
			await expect(getToolbarButton(page, 'image')).toBeVisible();
		});
	});

	test.describe('Disabled State', () => {
		test('should not accept input when editor is in image preview mode', async ({ page }) => {
			const textarea = getTextarea(page);
			await textarea.click();
			await textarea.fill('Before preview');
			// Toggle image preview (visibility icon)
			await getToolbarButton(page, 'visibility').click();
			// Textarea should be disabled
			await expect(textarea).toBeDisabled();
			// Return to editing (edit icon)
			await getToolbarButton(page, 'edit').click();
			// Textarea should be enabled again
			await expect(textarea).toBeEnabled();
		});
	});

	test.describe('Table Dialog', () => {
		test('should display the table toolbar button', async ({ page }) => {
			await expect(getToolbarButton(page, 'table_chart')).toBeVisible();
		});

		test('should display the select table toolbar button', async ({ page }) => {
			await expect(getToolbarButton(page, 'select_all')).toBeVisible();
		});

		test('should open table dialog in create mode', async ({ page }) => {
			await getToolbarButton(page, 'table_chart').click();
			// Dialog should appear with Insert Table title
			await expect(page.getByRole('heading', { name: /Insert Table/i })).toBeVisible({ timeout: 5000 });
			// Should have Cols and Rows inputs
			await expect(page.getByRole('spinbutton', { name: 'Column count' })).toBeVisible();
			await expect(page.getByRole('spinbutton', { name: 'Row count' })).toBeVisible();
		});

		test('should insert a table into the editor on submit', async ({ page }) => {
			const textarea = getTextarea(page);
			await textarea.click();
			await textarea.fill('');
			await getToolbarButton(page, 'table_chart').click();
			await expect(page.getByRole('heading', { name: /Insert Table/i })).toBeVisible({ timeout: 5000 });
			// Click Insert Table button
			await page.getByRole('button', { name: 'Insert Table' }).click();
			// Editor should now contain table markdown
			const value = await textarea.inputValue();
			expect(value).toContain('|');
			expect(value).toContain(':--');
		});

		test('should cancel dialog without modifying content', async ({ page }) => {
			const textarea = getTextarea(page);
			await textarea.click();
			await textarea.fill('Original content');
			await getToolbarButton(page, 'table_chart').click();
			await expect(page.getByRole('heading', { name: /Insert Table/i })).toBeVisible({ timeout: 5000 });
			// Cancel
			await page.getByRole('button', { name: 'Cancel' }).click();
			// Content should be unchanged
			await expect(textarea).toHaveValue('Original content');
		});

		test('should open dialog in edit mode when cursor is in a table', async ({ page }) => {
			const textarea = getTextarea(page);
			await textarea.click();
			await textarea.fill('| Name | Age |\n| :-- | :-: |\n| Alice | 30 |\n| Bob | 25 |');
			// Place cursor inside the table
			await textarea.click();
			await textarea.press('Home');
			await getToolbarButton(page, 'table_chart').click();
			// Dialog should show Edit Table
			await expect(page.getByRole('heading', { name: /Edit Table/i })).toBeVisible({ timeout: 5000 });
			// Verify headers were parsed correctly
			await expect(page.getByRole('textbox', { name: 'Header 1' })).toHaveValue('Name');
			await expect(page.getByRole('textbox', { name: 'Header 2' })).toHaveValue('Age');
			// Verify cell data was parsed correctly
			await expect(page.locator('textarea[aria-label="Row 1, Column 1"]')).toHaveValue('Alice');
			await expect(page.locator('textarea[aria-label="Row 1, Column 2"]')).toHaveValue('30');
			await expect(page.locator('textarea[aria-label="Row 2, Column 1"]')).toHaveValue('Bob');
			await expect(page.locator('textarea[aria-label="Row 2, Column 2"]')).toHaveValue('25');
			// Verify size inputs reflect the table dimensions
			await expect(page.getByRole('spinbutton', { name: 'Column count' })).toHaveValue('2');
			await expect(page.getByRole('spinbutton', { name: 'Row count' })).toHaveValue('2');
			await page.getByRole('button', { name: 'Cancel' }).click();
		});

		test('should select the full table text when using select table', async ({ page }) => {
			const tableMarkdown = '| X | Y |\n| :-- | :-- |\n| 1 | 2 |\n| 3 | 4 |';
			const textarea = getTextarea(page);
			await textarea.click();
			await textarea.fill('Before\n' + tableMarkdown + '\nAfter');
			// Place cursor somewhere in the table (after "Before\n")
			await textarea.click();
			await page.evaluate(() => {
				const ta = document.querySelector('textarea[aria-label="Markdown content editor"]') as HTMLTextAreaElement;
				ta.selectionStart = 10;
				ta.selectionEnd = 10;
				ta.dispatchEvent(new Event('blur'));
			});
			// Click select table
			await getToolbarButton(page, 'select_all').click();
			// Verify the selection covers the entire table
			const selection = await page.evaluate(() => {
				const ta = document.querySelector('textarea[aria-label="Markdown content editor"]') as HTMLTextAreaElement;
				return ta.value.substring(ta.selectionStart, ta.selectionEnd);
			});
			// The selected text should contain the full table
			expect(selection).toContain('| X | Y |');
			expect(selection).toContain('| :-- | :-- |');
			expect(selection).toContain('| 3 | 4 |');
			// Should NOT contain the text outside the table
			expect(selection).not.toContain('Before');
			expect(selection).not.toContain('After');
		});

		test('should disable table button in image preview mode', async ({ page }) => {
			// Enter image preview mode
			await getToolbarButton(page, 'visibility').click();
			// Table button should be disabled
			await expect(getToolbarButton(page, 'table_chart')).toBeDisabled();
			// Select table button should also be disabled
			await expect(getToolbarButton(page, 'select_all')).toBeDisabled();
			// Return to editing
			await getToolbarButton(page, 'edit').click();
		});

		test('should show snackbar when select table finds no table', async ({ page }) => {
			const textarea = getTextarea(page);
			await textarea.click();
			await textarea.fill('No table here');
			// Click select table
			await getToolbarButton(page, 'select_all').click();
			// Snackbar should appear
			await expect(page.getByText('No table found at the cursor position.')).toBeVisible({ timeout: 3000 });
		});
	});

	test.describe('Table Dialog Controls', () => {
		test.beforeEach(async ({ page }) => {
			await getToolbarButton(page, 'table_chart').click();
			await expect(page.getByRole('heading', { name: /Insert Table/i })).toBeVisible({ timeout: 5000 });
		});

		test('should add a row via insert row after button', async ({ page }) => {
			const rowInput = page.getByRole('spinbutton', { name: 'Row count' });
			const initialRows = await rowInput.inputValue();
			// The insert row after button contains keyboard_arrow_down icon
			await page.locator('button').filter({ hasText: 'keyboard_arrow_down' }).first().click();
			const newRows = await rowInput.inputValue();
			expect(parseInt(newRows)).toBe(parseInt(initialRows) + 1);
		});

		test('should add a column via insert column after button', async ({ page }) => {
			const colInput = page.getByRole('spinbutton', { name: 'Column count' });
			const initialCols = await colInput.inputValue();
			// The insert column after button contains keyboard_arrow_right icon
			await page.locator('button').filter({ hasText: 'keyboard_arrow_right' }).first().click();
			const newCols = await colInput.inputValue();
			expect(parseInt(newCols)).toBe(parseInt(initialCols) + 1);
		});

		test('should remove a row', async ({ page }) => {
			const rowInput = page.getByRole('spinbutton', { name: 'Row count' });
			const initialRows = await rowInput.inputValue();
			// The remove row button is in the row controls area with "close" icon in tbody
			await page.locator('td button').filter({ hasText: 'close' }).first().click();
			const newRows = await rowInput.inputValue();
			expect(parseInt(newRows)).toBe(parseInt(initialRows) - 1);
		});

		test('should remove a column', async ({ page }) => {
			const colInput = page.getByRole('spinbutton', { name: 'Column count' });
			const initialCols = await colInput.inputValue();
			// The remove column button is in thead with "close" icon
			await page.locator('thead button').filter({ hasText: 'close' }).first().click();
			const newCols = await colInput.inputValue();
			expect(parseInt(newCols)).toBe(parseInt(initialCols) - 1);
		});

		test('should cycle column alignment', async ({ page }) => {
			const textarea = getTextarea(page);
			// First column starts as left aligned (format_align_left)
			const alignButton = page.locator('thead button').filter({ hasText: 'format_align_left' }).first();
			await expect(alignButton).toBeVisible();
			// Click to cycle to center
			await alignButton.click();
			await expect(page.locator('thead button').filter({ hasText: 'format_align_center' }).first()).toBeVisible();
			// Click again to cycle to right
			await page.locator('thead button').filter({ hasText: 'format_align_center' }).first().click();
			await expect(page.locator('thead button').filter({ hasText: 'format_align_right' }).first()).toBeVisible();
			// Insert the table and verify alignment syntax in markdown
			await page.getByRole('button', { name: 'Insert Table' }).click();
			const value = await textarea.inputValue();
			expect(value).toContain('--:');
		});
	});

	test.describe('Table Dialog Undo/Redo', () => {
		test.beforeEach(async ({ page }) => {
			await getToolbarButton(page, 'table_chart').click();
			await expect(page.getByRole('heading', { name: /Insert Table/i })).toBeVisible({ timeout: 5000 });
		});

		test('should undo adding a row', async ({ page }) => {
			const rowInput = page.getByRole('spinbutton', { name: 'Row count' });
			const initialRows = await rowInput.inputValue();
			await page.locator('button').filter({ hasText: 'keyboard_arrow_down' }).first().click();
			expect(parseInt(await rowInput.inputValue())).toBe(parseInt(initialRows) + 1);
			await page.keyboard.press('Control+z');
			expect(parseInt(await rowInput.inputValue())).toBe(parseInt(initialRows));
		});

		test('should redo after undo', async ({ page }) => {
			const rowInput = page.getByRole('spinbutton', { name: 'Row count' });
			const initialRows = await rowInput.inputValue();
			await page.locator('button').filter({ hasText: 'keyboard_arrow_down' }).first().click();
			await page.keyboard.press('Control+z');
			expect(parseInt(await rowInput.inputValue())).toBe(parseInt(initialRows));
			await page.keyboard.press('Control+y');
			expect(parseInt(await rowInput.inputValue())).toBe(parseInt(initialRows) + 1);
		});

		test('should undo text input changes captured on focus', async ({ page }) => {
			const cell = page.locator('textarea[aria-label="Row 1, Column 1"]');
			await cell.click();
			await cell.fill('Hello');
			const cell2 = page.locator('textarea[aria-label="Row 1, Column 2"]');
			await cell2.click();
			await cell2.fill('World');
			await page.keyboard.press('Control+z');
			await expect(cell2).toHaveValue('');
			await page.keyboard.press('Control+z');
			await expect(cell).toHaveValue('');
		});
	});

	test.describe('Table Dialog Header Spans', () => {
		test.beforeEach(async ({ page }) => {
			await getToolbarButton(page, 'table_chart').click();
			await expect(page.getByRole('heading', { name: /Insert Table/i })).toBeVisible({ timeout: 5000 });
		});

		test('should merge a column into the left header', async ({ page }) => {
			await page.locator('button').filter({ hasText: 'merge_type' }).nth(1).click();
			await expect(page.locator('mat-label').filter({ hasText: '2 cols' })).toBeVisible();
		});

		test('should unmerge a spanned column', async ({ page }) => {
			await page.locator('button').filter({ hasText: 'merge_type' }).nth(1).click();
			await page.locator('button').filter({ hasText: 'call_split' }).first().click();
			await expect(page.locator('mat-label').filter({ hasText: '2 cols' })).not.toBeVisible();
		});

		test('should generate correct colspan syntax', async ({ page }) => {
			const textarea = getTextarea(page);
			const headerInput = page.getByRole('textbox', { name: /Header 1/ });
			await headerInput.click();
			await headerInput.fill('Merged');
			await page.locator('button').filter({ hasText: 'merge_type' }).nth(1).click();
			await page.getByRole('button', { name: 'Insert Table' }).click();
			const value = await textarea.inputValue();
			expect(value).toContain('Merged');
			expect(value).toMatch(/Merged\s*\|\|/);
		});

		test('should parse existing colspan syntax in edit mode', async ({ page }) => {
			await page.keyboard.press('Escape');
			const textarea = getTextarea(page);
			await textarea.click();
			await textarea.fill('| Span1 ||| Span2 |\n| :-- | :-- | :-- | :-- |\n| a | b | c | d |');
			await textarea.click();
			await textarea.press('Home');
			await getToolbarButton(page, 'table_chart').click();
			await expect(page.getByRole('heading', { name: /Edit Table/i })).toBeVisible({ timeout: 5000 });
			await expect(page.locator('mat-label').filter({ hasText: '3 cols' })).toBeVisible();
			await expect(page.getByRole('textbox', { name: /Header 1/ })).toHaveValue('Span1');
			await page.keyboard.press('Escape');
		});
	});

	test.describe('Table Dialog Edge Cases', () => {
		test.beforeEach(async ({ page }) => {
			await getToolbarButton(page, 'table_chart').click();
			await expect(page.getByRole('heading', { name: /Insert Table/i })).toBeVisible({ timeout: 5000 });
		});

		test('should not close on backdrop click', async ({ page }) => {
			await page.mouse.click(10, 10);
			await expect(page.getByRole('heading', { name: /Insert Table/i })).toBeVisible();
		});

		test('should close on Escape key', async ({ page }) => {
			await page.keyboard.press('Escape');
			await expect(page.getByRole('heading', { name: /Insert Table/i })).not.toBeVisible();
		});

		test('should encode newlines as br in cell content', async ({ page }) => {
			const textarea = getTextarea(page);
			const cell = page.locator('textarea[aria-label="Row 1, Column 1"]');
			await cell.click();
			await cell.fill('Line1\nLine2');
			await page.getByRole('button', { name: 'Insert Table' }).click();
			const value = await textarea.inputValue();
			expect(value).toContain('Line1<br>Line2');
		});

		test('should escape pipe characters in cell content', async ({ page }) => {
			const textarea = getTextarea(page);
			const cell = page.locator('textarea[aria-label="Row 1, Column 1"]');
			await cell.click();
			await cell.fill('a | b');
			await page.getByRole('button', { name: 'Insert Table' }).click();
			const value = await textarea.inputValue();
			expect(value).toContain('a \\| b');
		});

		test('should unescape pipe characters when loading table for edit', async ({ page }) => {
			await page.keyboard.press('Escape');
			const textarea = getTextarea(page);
			await textarea.click();
			await textarea.fill('| H1 | H2 |\n| :-- | :-- |\n| a \\| b | c |');
			await textarea.press('Home');
			await getToolbarButton(page, 'table_chart').click();
			await expect(page.getByRole('heading', { name: /Edit Table/i })).toBeVisible({ timeout: 5000 });
			const cell = page.locator('textarea[aria-label="Row 1, Column 1"]');
			await expect(cell).toHaveValue('a | b');
			await page.keyboard.press('Escape');
		});
	});

	test.describe('Table Caption', () => {
		test('should insert a table with a caption', async ({ page }) => {
			const textarea = getTextarea(page);
			await textarea.click();
			await textarea.fill('');
			await getToolbarButton(page, 'table_chart').click();
			await expect(page.getByRole('heading', { name: /Insert Table/i })).toBeVisible({ timeout: 5000 });
			// Fill in the caption
			const captionInput = page.getByRole('textbox', { name: 'Table caption' });
			await captionInput.click();
			await captionInput.fill('My Table Caption');
			await page.getByRole('button', { name: 'Insert Table' }).click();
			// Editor should contain the caption tag
			const value = await textarea.inputValue();
			expect(value).toContain('|');
			expect(value).toContain('<table-caption>My Table Caption</table-caption>');
		});

		test('should insert a table without a caption when field is empty', async ({ page }) => {
			const textarea = getTextarea(page);
			await textarea.click();
			await textarea.fill('');
			await getToolbarButton(page, 'table_chart').click();
			await expect(page.getByRole('heading', { name: /Insert Table/i })).toBeVisible({ timeout: 5000 });
			await page.getByRole('button', { name: 'Insert Table' }).click();
			const value = await textarea.inputValue();
			expect(value).toContain('|');
			expect(value).not.toContain('<table-caption>');
		});

		test('should parse caption when editing a table with a caption', async ({ page }) => {
			const textarea = getTextarea(page);
			await textarea.click();
			await textarea.fill('| H1 | H2 |\n| :-- | :-- |\n| a | b |\n<table-caption>Existing Caption</table-caption>');
			await textarea.click();
			await textarea.press('Home');
			await getToolbarButton(page, 'table_chart').click();
			await expect(page.getByRole('heading', { name: /Edit Table/i })).toBeVisible({ timeout: 5000 });
			await expect(page.getByRole('textbox', { name: 'Table caption' })).toHaveValue('Existing Caption');
			await page.keyboard.press('Escape');
		});

		test('should detect table when cursor is on the caption line', async ({ page }) => {
			const textarea = getTextarea(page);
			await textarea.click();
			await textarea.fill('| H1 | H2 |\n| :-- | :-- |\n| a | b |\n<table-caption>Caption Here</table-caption>');
			// Place cursor inside the <table-caption> tag
			await page.evaluate(() => {
				const ta = document.querySelector('textarea[aria-label="Markdown content editor"]') as HTMLTextAreaElement;
				const pos = ta.value.indexOf('<table-caption>') + 5;
				ta.selectionStart = pos;
				ta.selectionEnd = pos;
				ta.dispatchEvent(new Event('blur'));
			});
			await getToolbarButton(page, 'table_chart').click();
			// Should open in Edit mode, not Insert mode
			await expect(page.getByRole('heading', { name: /Edit Table/i })).toBeVisible({ timeout: 5000 });
			// Caption should be populated
			await expect(page.getByRole('textbox', { name: 'Table caption' })).toHaveValue('Caption Here');
			await page.keyboard.press('Escape');
		});

		test('should select table including caption when cursor is on caption line', async ({ page }) => {
			const textarea = getTextarea(page);
			await textarea.click();
			await textarea.fill('Before\n| H1 | H2 |\n| :-- | :-- |\n| a | b |\n<table-caption>Caption</table-caption>\nAfter');
			// Place cursor on the caption line
			await page.evaluate(() => {
				const ta = document.querySelector('textarea[aria-label="Markdown content editor"]') as HTMLTextAreaElement;
				const pos = ta.value.indexOf('<table-caption>') + 3;
				ta.selectionStart = pos;
				ta.selectionEnd = pos;
				ta.dispatchEvent(new Event('blur'));
			});
			await getToolbarButton(page, 'select_all').click();
			// Verify selection covers table + caption but not surrounding text
			const selection = await page.evaluate(() => {
				const ta = document.querySelector('textarea[aria-label="Markdown content editor"]') as HTMLTextAreaElement;
				return ta.value.substring(ta.selectionStart, ta.selectionEnd);
			});
			expect(selection).toContain('| H1 | H2 |');
			expect(selection).toContain('<table-caption>Caption</table-caption>');
			expect(selection).not.toContain('Before');
			expect(selection).not.toContain('After');
		});

		test('should detect table when blank lines exist between table and caption', async ({ page }) => {
			const textarea = getTextarea(page);
			await textarea.click();
			await textarea.fill('| H1 | H2 |\n| :-- | :-- |\n| a | b |\n\n<table-caption>Spaced Caption</table-caption>');
			await page.evaluate(() => {
				const ta = document.querySelector('textarea[aria-label="Markdown content editor"]') as HTMLTextAreaElement;
				const pos = ta.value.indexOf('<table-caption>') + 5;
				ta.selectionStart = pos;
				ta.selectionEnd = pos;
				ta.dispatchEvent(new Event('blur'));
			});
			await getToolbarButton(page, 'table_chart').click();
			await expect(page.getByRole('heading', { name: /Edit Table/i })).toBeVisible({ timeout: 5000 });
			await expect(page.getByRole('textbox', { name: 'Table caption' })).toHaveValue('Spaced Caption');
			await page.keyboard.press('Escape');
		});

		test('should include caption in undo/redo within the table dialog', async ({ page }) => {
			await getToolbarButton(page, 'table_chart').click();
			await expect(page.getByRole('heading', { name: /Insert Table/i })).toBeVisible({ timeout: 5000 });
			// Focus the caption input (triggers saveUndoState)
			const captionInput = page.getByRole('textbox', { name: 'Table caption' });
			await captionInput.click();
			await captionInput.fill('First Caption');
			// Focus a cell to save state, then modify caption again
			const cell = page.locator('textarea[aria-label="Row 1, Column 1"]');
			await cell.click();
			await cell.fill('data');
			// Focus caption again and change it
			await captionInput.click();
			await captionInput.fill('Second Caption');
			// Undo should revert caption
			await page.keyboard.press('Control+z');
			await expect(captionInput).toHaveValue('First Caption');
			// Redo should restore it
			await page.keyboard.press('Control+y');
			await expect(captionInput).toHaveValue('Second Caption');
			await page.keyboard.press('Escape');
		});
	});

	test.describe('Image Upload Dialog', () => {
		test('should not close on backdrop click', async ({ page }) => {
			const uploadButton = getToolbarButton(page, 'image');
			const isDisabled = await uploadButton.isDisabled();
			if (isDisabled) {
				test.skip();
				return;
			}
			await uploadButton.click();
			await expect(page.getByRole('heading', { name: /Upload Image/i })).toBeVisible({ timeout: 5000 });
			await page.mouse.click(10, 10);
			await expect(page.getByRole('heading', { name: /Upload Image/i })).toBeVisible();
			await page.getByRole('button', { name: 'Cancel' }).click();
			await expect(page.getByRole('heading', { name: /Upload Image/i })).not.toBeVisible();
		});
	});

	test.describe('Table Dialog Row Numbers', () => {
		test.beforeEach(async ({ page }) => {
			await getToolbarButton(page, 'table_chart').click();
			await expect(page.getByRole('heading', { name: /Insert Table/i })).toBeVisible({ timeout: 5000 });
		});

		test('should display row numbers in the table editor', async ({ page }) => {
			const dataRows = page.locator('tbody tr:has(td.tw-sticky)');
			await expect(dataRows.nth(0).locator('td.tw-sticky')).toContainText('1');
			await expect(dataRows.nth(1).locator('td.tw-sticky')).toContainText('2');
			await expect(dataRows.nth(2).locator('td.tw-sticky')).toContainText('3');
		});

		test('should update row numbers when a row is added', async ({ page }) => {
			await page.locator('button').filter({ hasText: 'keyboard_arrow_down' }).first().click();
			const dataRows = page.locator('tbody tr:has(td.tw-sticky)');
			await expect(dataRows.nth(3).locator('td.tw-sticky')).toContainText('4');
		});

		test('should show correct tooltips on row insert buttons', async ({ page }) => {
			const upButton = page.locator('tbody button').filter({ hasText: 'keyboard_arrow_up' }).first();
			await upButton.hover();
			await expect(
				page.locator('.mat-mdc-tooltip-surface').filter({ hasText: 'Insert Row Above' })
			).toBeVisible({ timeout: 3000 });
		});
	});

	test.describe('Caption Examples', () => {
		test('should insert a table caption via the examples menu', async ({ page }) => {
			const textarea = getTextarea(page);
			await textarea.click();
			await textarea.fill('');
			// Open examples menu and click "Table Caption"
			await getToolbarButton(page, 'lightbulb').click();
			await page.getByRole('menuitem', { name: 'Table Caption' }).click();
			const value = await textarea.inputValue();
			expect(value).toContain('<table-caption>');
			expect(value).toContain('</table-caption>');
			expect(value).toContain('Table caption text');
		});

		test('should insert a figure caption via the examples menu', async ({ page }) => {
			const textarea = getTextarea(page);
			await textarea.click();
			await textarea.fill('');
			// Open examples menu and click "Figure Caption"
			await getToolbarButton(page, 'lightbulb').click();
			await page.getByRole('menuitem', { name: 'Figure Caption' }).click();
			const value = await textarea.inputValue();
			expect(value).toContain('<figure-caption>');
			expect(value).toContain('</figure-caption>');
			expect(value).toContain('Figure caption text');
		});

		test('should insert table example via the examples menu', async ({ page }) => {
			const textarea = getTextarea(page);
			await textarea.click();
			await textarea.fill('');
			// Open examples menu and click "Table" (exact match to avoid "Table Caption")
			await getToolbarButton(page, 'lightbulb').click();
			await page.getByRole('menuitem', { name: 'Table', exact: true }).click();
			const value = await textarea.inputValue();
			expect(value).toContain('|col 1|col 2|col 3|');
			expect(value).toContain('|:--|:-:|--:|');
		});

		test('should edit a table inserted via examples after adding a caption', async ({ page }) => {
			const textarea = getTextarea(page);
			await textarea.click();
			await textarea.fill('');
			// Insert table example
			await getToolbarButton(page, 'lightbulb').click();
			await page.getByRole('menuitem', { name: 'Table', exact: true }).click();
			// Insert table caption example (appends after)
			await getToolbarButton(page, 'lightbulb').click();
			await page.getByRole('menuitem', { name: 'Table Caption' }).click();
			// Place cursor inside the <table-caption> tag text
			await textarea.click();
			const captionPos = await page.evaluate(() => {
				const ta = document.querySelector('textarea[aria-label="Markdown content editor"]') as HTMLTextAreaElement;
				return ta.value.indexOf('table-caption>') + 15;
			});
			await page.evaluate((pos) => {
				const ta = document.querySelector('textarea[aria-label="Markdown content editor"]') as HTMLTextAreaElement;
				ta.selectionStart = pos;
				ta.selectionEnd = pos;
				ta.focus();
			}, captionPos);
			// Blur to save the selection position
			await textarea.dispatchEvent('blur');
			// Open the table dialog — should be in Edit mode
			await getToolbarButton(page, 'table_chart').click();
			await expect(page.getByRole('heading', { name: /Edit Table/i })).toBeVisible({ timeout: 5000 });
			// Verify the table was parsed (has the example headers)
			await expect(page.getByRole('textbox', { name: 'Header 1' })).toHaveValue('col 1');
			// Verify caption was parsed
			await expect(page.getByRole('textbox', { name: 'Table caption' })).toHaveValue('Table caption text');
			await page.keyboard.press('Escape');
		});
	});
});
