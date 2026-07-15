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
 * Tests for the Markdown Editor component.
 * Configured for parallel execution — each test gets its own page and
 * navigates independently. Tests are consolidated to minimize navigation
 * overhead while still covering all behaviors.
 */

const branchName = 'SAW Markdown Requirements Updates';
const artifactName = 'Events';

function getEditor(page: Page): Locator {
	return page.locator('osee-markdown-editor');
}

function getTextarea(page: Page): Locator {
	return getEditor(page).getByRole('textbox', {
		name: 'Markdown content editor',
	});
}

function getToolbarButton(page: Page, iconName: string): Locator {
	const escaped = iconName.replace(/[.*+?^${}()|[\]\\]/g, '\\$&');
	return getEditor(page)
		.getByRole('button')
		.filter({
			has: page.locator(`mat-icon`, {
				hasText: new RegExp(`^\\s*${escaped}\\s*$`),
			}),
		});
}

function getPreviewPanel(page: Page): Locator {
	return getEditor(page).locator(
		'[role="region"][aria-label="Markdown preview"]'
	);
}

function getDivider(page: Page): Locator {
	return getEditor(page).locator('[role="separator"]');
}

/** Locates a cell textarea by row and column (1-indexed). */
function getCellTextarea(page: Page, row: number, col: number): Locator {
	return page.locator(`textarea[aria-label="Row ${row}, Column ${col}"]`);
}

/**
 * Locates the split button by its main icon name.
 * Returns the root span containing both the main button and the arrow.
 * Uses a mat-icon scoped regex to match the exact icon text content.
 */
function getSplitButton(page: Page, iconName: string): Locator {
	const escaped = iconName.replace(/[.*+?^${}()|[\]\\]/g, '\\$&');
	return getEditor(page)
		.locator('osee-split-button')
		.filter({
			has: page.locator('mat-icon', {
				hasText: new RegExp(`^\\s*${escaped}\\s*$`),
			}),
		});
}

/**
 * Clicks the dropdown arrow of a split button (opens the overlay).
 */
async function openSplitButtonDropdown(
	page: Page,
	iconName: string
): Promise<void> {
	const splitBtn = getSplitButton(page, iconName);
	await splitBtn.locator('button').nth(1).click();
}

test.describe('Markdown Editor', () => {
	test.describe.configure({ mode: 'parallel' });

	test.beforeEach(async ({ page }) => {
		// Use a wide viewport so all toolbar sections remain expanded
		await page.setViewportSize({ width: 1600, height: 900 });
		// Retry navigation once if ERR_ABORTED (parallel worker contention)
		try {
			await navigateToArtifactExplorer(page);
		} catch {
			await navigateToArtifactExplorer(page);
		}
		await selectBranch(page, 'Working', branchName);
		await searchForArtifact(page, artifactName);
		await expect(getEditor(page)).toBeVisible({ timeout: 10000 });
	});

	test('should handle text editing, preview, undo, and redo', async ({
		page,
	}) => {
		const textarea = getTextarea(page);
		const preview = getPreviewPanel(page);

		await test.step('Text input appears in textarea and preview', async () => {
			await textarea.click();
			await textarea.fill('# Hello World');
			await expect(textarea).toHaveValue('# Hello World');
			await expect(preview.locator('h1')).toBeVisible({ timeout: 5000 });
			await expect(preview.locator('h1')).toContainText('Hello World');
		});

		await test.step('Undo reverts and redo reapplies', async () => {
			await textarea.fill('First');
			await page.waitForTimeout(100);
			await textarea.fill('Second');
			await page.waitForTimeout(100);
			await getToolbarButton(page, 'undo').click();
			await expect(textarea).toHaveValue('First');
			await getToolbarButton(page, 'redo').click();
			await expect(textarea).toHaveValue('Second');
		});
	});

	test('should toggle preview panel and fullscreen', async ({ page }) => {
		await test.step('Collapse and expand preview panel', async () => {
			const collapseButton = getToolbarButton(page, 'view_sidebar');
			await collapseButton.click();
			await expect(getPreviewPanel(page)).not.toBeVisible();
			const expandButton = getToolbarButton(page, 'vertical_split');
			await expandButton.click();
			await expect(getPreviewPanel(page)).toBeVisible();
		});

		await test.step('Enter and exit fullscreen', async () => {
			const fullscreenButton = getEditor(page)
				.getByRole('button')
				.filter({ hasText: /^\s*fullscreen\s*$/ });
			await fullscreenButton.click();
			expect(
				await page.evaluate(() => !!document.fullscreenElement)
			).toBe(true);
			await getToolbarButton(page, 'fullscreen_exit').click();
			expect(
				await page.evaluate(() => !!document.fullscreenElement)
			).toBe(false);
		});
	});

	test('should support draggable divider with accessibility', async ({
		page,
	}) => {
		const divider = getDivider(page);

		await test.step('Verify accessibility attributes', async () => {
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

		await test.step('Mouse drag resizes editor pane', async () => {
			const dividerBox = await divider.boundingBox();
			expect(dividerBox).not.toBeNull();
			const startX = dividerBox!.x + dividerBox!.width / 2;
			const startY = dividerBox!.y + dividerBox!.height / 2;
			await page.mouse.move(startX, startY);
			await page.mouse.down();
			await page.mouse.move(startX + 100, startY, { steps: 5 });
			await page.mouse.up();
			const editorPane = getEditor(page).locator(
				'[data-testid="editor-pane"]'
			);
			expect(await editorPane.getAttribute('style')).toContain('width');
		});

		await test.step('Keyboard resize updates aria-valuenow', async () => {
			await divider.focus();
			await divider.press('ArrowLeft');
			await divider.press('ArrowLeft');
			const valueNow = await divider.getAttribute('aria-valuenow');
			expect(parseInt(valueNow!)).toBeGreaterThan(20);
		});
	});

	test('should display toolbar buttons for all sections', async ({
		page,
	}) => {
		await test.step('History section: Undo and Redo buttons', async () => {
			await expect(getToolbarButton(page, 'undo')).toBeVisible();
			await expect(getToolbarButton(page, 'redo')).toBeVisible();
		});

		await test.step('Format section: Heading split button and inline formatting', async () => {
			// Heading split button (main icon is "title")
			await expect(getSplitButton(page, 'title')).toBeVisible();
			// Inline formatting buttons
			await expect(getToolbarButton(page, 'format_bold')).toBeVisible();
			await expect(getToolbarButton(page, 'format_italic')).toBeVisible();
			await expect(
				getToolbarButton(page, 'strikethrough_s')
			).toBeVisible();
		});

		await test.step('Insert section: List split button and block formatting', async () => {
			// List split button (main icon is "format_list_bulleted")
			await expect(
				getSplitButton(page, 'format_list_bulleted')
			).toBeVisible();
			// Block formatting buttons
			await expect(getToolbarButton(page, 'code')).toBeVisible();
			await expect(getToolbarButton(page, 'link')).toBeVisible();
			await expect(getToolbarButton(page, 'format_quote')).toBeVisible();
			await expect(getToolbarButton(page, 'data_object')).toBeVisible();
			await expect(
				getToolbarButton(page, 'horizontal_rule')
			).toBeVisible();
		});

		await test.step('Media section: Image, Table, Select Table, and Caption buttons', async () => {
			await expect(getToolbarButton(page, 'image')).toBeVisible();
			await expect(getToolbarButton(page, 'table_chart')).toBeVisible();
			await expect(getToolbarButton(page, 'select_all')).toBeVisible();
			await expect(getToolbarButton(page, 'video_label')).toBeVisible();
			await expect(getToolbarButton(page, 'legend_toggle')).toBeVisible();
		});

		await test.step('View section: Preview, Panel toggle, Fullscreen, Help', async () => {
			await expect(getToolbarButton(page, 'visibility')).toBeVisible();
			await expect(getToolbarButton(page, 'view_sidebar')).toBeVisible();
			await expect(
				getEditor(page)
					.getByRole('button')
					.filter({ hasText: /^\s*fullscreen\s*$/ })
			).toBeVisible();
			// Help button (osee-help-button)
			await expect(
				getEditor(page).locator('osee-help-button')
			).toBeVisible();
		});

		await test.step('Image preview mode disables editing and table buttons', async () => {
			const textarea = getTextarea(page);
			await getToolbarButton(page, 'visibility').click();
			await expect(textarea).toBeDisabled();
			await expect(getToolbarButton(page, 'table_chart')).toBeDisabled();
			await expect(getToolbarButton(page, 'select_all')).toBeDisabled();
			// Format buttons disabled in image preview
			await expect(getToolbarButton(page, 'format_bold')).toBeDisabled();
			await getToolbarButton(page, 'edit').click();
			await expect(textarea).toBeEnabled();
		});
	});

	test('should insert formatting at cursor via toolbar buttons', async ({
		page,
	}) => {
		const textarea = getTextarea(page);

		await test.step('Bold wraps selected text in **', async () => {
			await textarea.click();
			await textarea.fill('hello world');
			// Select "world"
			await page.evaluate(() => {
				const ta = document.querySelector(
					'textarea[aria-label="Markdown content editor"]'
				) as HTMLTextAreaElement;
				ta.selectionStart = 6;
				ta.selectionEnd = 11;
				ta.dispatchEvent(new Event('blur'));
			});
			await getToolbarButton(page, 'format_bold').click();
			await expect(textarea).toHaveValue('hello **world**');
		});

		await test.step('Italic wraps selected text in *', async () => {
			await textarea.click();
			await textarea.fill('some text');
			await page.evaluate(() => {
				const ta = document.querySelector(
					'textarea[aria-label="Markdown content editor"]'
				) as HTMLTextAreaElement;
				ta.selectionStart = 5;
				ta.selectionEnd = 9;
				ta.dispatchEvent(new Event('blur'));
			});
			await getToolbarButton(page, 'format_italic').click();
			await expect(textarea).toHaveValue('some *text*');
		});

		await test.step('Strikethrough wraps selected text in ~~', async () => {
			await textarea.click();
			await textarea.fill('remove this');
			await page.evaluate(() => {
				const ta = document.querySelector(
					'textarea[aria-label="Markdown content editor"]'
				) as HTMLTextAreaElement;
				ta.selectionStart = 7;
				ta.selectionEnd = 11;
				ta.dispatchEvent(new Event('blur'));
			});
			await getToolbarButton(page, 'strikethrough_s').click();
			await expect(textarea).toHaveValue('remove ~~this~~');
		});

		await test.step('Bold with no selection inserts placeholder', async () => {
			await textarea.click();
			await textarea.fill('');
			await page.evaluate(() => {
				const ta = document.querySelector(
					'textarea[aria-label="Markdown content editor"]'
				) as HTMLTextAreaElement;
				ta.selectionStart = 0;
				ta.selectionEnd = 0;
				ta.dispatchEvent(new Event('blur'));
			});
			await getToolbarButton(page, 'format_bold').click();
			await expect(textarea).toHaveValue('**bold text**');
		});

		await test.step('Link wraps selected text with [text](url)', async () => {
			await textarea.click();
			await textarea.fill('click here');
			await page.evaluate(() => {
				const ta = document.querySelector(
					'textarea[aria-label="Markdown content editor"]'
				) as HTMLTextAreaElement;
				ta.selectionStart = 6;
				ta.selectionEnd = 10;
				ta.dispatchEvent(new Event('blur'));
			});
			await getToolbarButton(page, 'link').click();
			await expect(textarea).toHaveValue('click [here](url)');
		});

		await test.step('Blockquote prefixes selected text with > ', async () => {
			await textarea.click();
			await textarea.fill('a quote');
			await page.evaluate(() => {
				const ta = document.querySelector(
					'textarea[aria-label="Markdown content editor"]'
				) as HTMLTextAreaElement;
				ta.selectionStart = 2;
				ta.selectionEnd = 7;
				ta.dispatchEvent(new Event('blur'));
			});
			await getToolbarButton(page, 'format_quote').click();
			await expect(textarea).toHaveValue('a > quote');
		});
	});

	test('should support split button default action and dropdown', async ({
		page,
	}) => {
		const textarea = getTextarea(page);

		await test.step('Heading split button default action inserts H2', async () => {
			await textarea.click();
			await textarea.fill('');
			await page.evaluate(() => {
				const ta = document.querySelector(
					'textarea[aria-label="Markdown content editor"]'
				) as HTMLTextAreaElement;
				ta.selectionStart = 0;
				ta.selectionEnd = 0;
				ta.dispatchEvent(new Event('blur'));
			});
			// Click the main icon button (first button in split button)
			const headingSplit = getSplitButton(page, 'title');
			await headingSplit.locator('button').first().click();
			const value = await textarea.inputValue();
			expect(value).toContain('## ');
		});

		await test.step('Heading split button dropdown shows H1-H6 options', async () => {
			await openSplitButtonDropdown(page, 'title');
			// CDK overlay should show heading options
			const overlay = page.locator('.cdk-overlay-container');
			await expect(overlay.getByText('Heading 1')).toBeVisible();
			await expect(overlay.getByText('Heading 3')).toBeVisible();
			await expect(overlay.getByText('Heading 6')).toBeVisible();
			// Select H3
			await textarea.fill('');
			await page.evaluate(() => {
				const ta = document.querySelector(
					'textarea[aria-label="Markdown content editor"]'
				) as HTMLTextAreaElement;
				ta.selectionStart = 0;
				ta.selectionEnd = 0;
				ta.dispatchEvent(new Event('blur'));
			});
			await overlay.getByText('Heading 3').click();
			const value = await textarea.inputValue();
			expect(value).toContain('### ');
		});

		await test.step('List split button default action inserts bulleted list', async () => {
			await textarea.click();
			await textarea.fill('');
			await page.evaluate(() => {
				const ta = document.querySelector(
					'textarea[aria-label="Markdown content editor"]'
				) as HTMLTextAreaElement;
				ta.selectionStart = 0;
				ta.selectionEnd = 0;
				ta.dispatchEvent(new Event('blur'));
			});
			const listSplit = getSplitButton(page, 'format_list_bulleted');
			await listSplit.locator('button').first().click();
			const value = await textarea.inputValue();
			expect(value).toContain('- ');
		});

		await test.step('List split button dropdown shows list type options', async () => {
			await openSplitButtonDropdown(page, 'format_list_bulleted');
			const overlay = page.locator('.cdk-overlay-container');
			await expect(overlay.getByText('Bulleted List')).toBeVisible();
			await expect(overlay.getByText('Numbered List')).toBeVisible();
			await expect(overlay.getByText('Task List')).toBeVisible();
			// Select task list
			await textarea.fill('');
			await page.evaluate(() => {
				const ta = document.querySelector(
					'textarea[aria-label="Markdown content editor"]'
				) as HTMLTextAreaElement;
				ta.selectionStart = 0;
				ta.selectionEnd = 0;
				ta.dispatchEvent(new Event('blur'));
			});
			await overlay.getByText('Task List').click();
			const value = await textarea.inputValue();
			expect(value).toContain('- [ ]');
		});
	});

	test('should group undo by word boundaries and treat toolbar actions as discrete steps', async ({
		page,
	}) => {
		const textarea = getTextarea(page);

		await test.step('Typing groups by word boundaries — undo reverts a word', async () => {
			await textarea.click();
			await textarea.fill('');
			// Type two words with a space boundary
			await textarea.pressSequentially('hello ', { delay: 50 });
			await textarea.pressSequentially('world', { delay: 50 });
			// Wait for debounce to commit the group
			await page.waitForTimeout(1200);
			// Undo should revert at least the second word
			await getToolbarButton(page, 'undo').click();
			const valueAfterUndo = await textarea.inputValue();
			// After undo we should not have the full "hello world"
			expect(valueAfterUndo.length).toBeLessThan('hello world'.length);
		});

		await test.step('Toolbar formatting is a discrete undo step', async () => {
			await textarea.click();
			await textarea.fill('before');
			await page.waitForTimeout(100);
			// Apply bold formatting (toolbar action)
			await page.evaluate(() => {
				const ta = document.querySelector(
					'textarea[aria-label="Markdown content editor"]'
				) as HTMLTextAreaElement;
				ta.selectionStart = 6;
				ta.selectionEnd = 6;
				ta.dispatchEvent(new Event('blur'));
			});
			await getToolbarButton(page, 'format_bold').click();
			// Content now has formatting appended
			const afterFormat = await textarea.inputValue();
			expect(afterFormat).toContain('**');
			// Undo should revert exactly the toolbar action
			await getToolbarButton(page, 'undo').click();
			await expect(textarea).toHaveValue('before');
		});
	});

	test('should collapse toolbar sections into dropdowns when narrow', async ({
		page,
	}) => {
		await test.step('Resize viewport to trigger toolbar collapse', async () => {
			// Shrink viewport to force sections to collapse
			await page.setViewportSize({ width: 600, height: 800 });
			// Wait for resize observer to fire
			await page.waitForTimeout(300);
			// At least one collapsed section dropdown should appear
			const collapsedDropdowns = getEditor(page).locator(
				'osee-toolbar-section-dropdown'
			);
			await expect(collapsedDropdowns.first()).toBeVisible({
				timeout: 3000,
			});
		});

		await test.step('Collapsed dropdown opens and shows actions', async () => {
			const collapsedDropdowns = getEditor(page).locator(
				'osee-toolbar-section-dropdown'
			);
			const dropdownBtn = collapsedDropdowns.first().locator('button');
			await expect(dropdownBtn).toBeVisible();
			// Use dispatchEvent('click') instead of click() because Playwright's
			// click() dispatches pointerdown → pointerup → click. CDK's outside-click
			// detector listens on pointerdown; when the overlay opens synchronously
			// during the click event, CDK sees the prior pointerdown as "outside"
			// and immediately closes it. dispatchEvent fires only the click event.
			await dropdownBtn.dispatchEvent('click');
			const overlayItem = page.getByRole('button', {
				name: 'Upload Image',
			});
			await expect(overlayItem).toBeVisible({ timeout: 5000 });
			await page.mouse.click(10, 10);
		});

		await test.step('Restore viewport shows expanded sections', async () => {
			await page.setViewportSize({ width: 1920, height: 900 });
			// Wait for format_bold to reappear (proves Format section expanded)
			await expect(getToolbarButton(page, 'format_bold')).toBeVisible({
				timeout: 5000,
			});
			// At this width, all collapsible sections should be expanded.
			const collapsedDropdowns = getEditor(page).locator(
				'osee-toolbar-section-dropdown'
			);
			await expect(collapsedDropdowns).toHaveCount(0, { timeout: 5000 });
		});

		await test.step('Help anchor highlights collapsed dropdown for child anchors', async () => {
			// Shrink viewport back so Media section collapses
			await page.setViewportSize({ width: 600, height: 800 });
			const collapsedDropdowns = getEditor(page).locator(
				'osee-toolbar-section-dropdown'
			);
			await expect(collapsedDropdowns.first()).toBeVisible({
				timeout: 3000,
			});
			// Simulate a help highlight for the image button anchor
			// (which is inside the collapsed Media section)
			await page.evaluate(() => {
				window.postMessage(
					{
						type: 'osee-help-highlight',
						anchorId: 'md-editor-image-btn',
					},
					window.location.origin
				);
			});
			// The collapsed Media dropdown should receive the highlight class
			await expect(
				getEditor(page).locator(
					'osee-toolbar-section-dropdown.osee-help-highlight'
				)
			).toBeVisible({ timeout: 3000 });
		});
	});

	test('should create, edit, and select tables via the table dialog', async ({
		page,
	}) => {
		const textarea = getTextarea(page);

		await test.step('Open dialog in create mode and insert table', async () => {
			await getToolbarButton(page, 'table_chart').click();
			await expect(
				page.getByRole('heading', { name: /Insert Table/i })
			).toBeVisible({ timeout: 5000 });
			await expect(
				page.getByRole('spinbutton', { name: 'Column count' })
			).toBeVisible();
			await expect(
				page.getByRole('spinbutton', { name: 'Row count' })
			).toBeVisible();
			await page.getByRole('button', { name: 'Insert Table' }).click();
			const value = await textarea.inputValue();
			expect(value).toContain('|');
			expect(value).toContain(':--');
		});

		await test.step('Cancel does not modify content', async () => {
			await textarea.click();
			await textarea.fill('Original content');
			await getToolbarButton(page, 'table_chart').click();
			await expect(
				page.getByRole('heading', { name: /Insert Table/i })
			).toBeVisible({ timeout: 5000 });
			await page.getByRole('button', { name: 'Cancel' }).click();
			await expect(textarea).toHaveValue('Original content');
		});

		await test.step('Edit mode parses existing table', async () => {
			await textarea.click();
			await textarea.fill(
				'| Name | Age |\n| :-- | :-: |\n| Alice | 30 |\n| Bob | 25 |'
			);
			await textarea.click();
			await textarea.press('Home');
			await getToolbarButton(page, 'table_chart').click();
			await expect(
				page.getByRole('heading', { name: /Edit Table/i })
			).toBeVisible({ timeout: 5000 });
			await expect(
				page.getByRole('textbox', { name: 'Header 1' })
			).toHaveValue('Name');
			await expect(
				page.getByRole('textbox', { name: 'Header 2' })
			).toHaveValue('Age');
			await expect(getCellTextarea(page, 1, 1)).toHaveValue('Alice');
			await expect(getCellTextarea(page, 2, 2)).toHaveValue('25');
			await page.getByRole('button', { name: 'Cancel' }).click();
			await expect(
				page.getByRole('heading', { name: /Edit Table/i })
			).not.toBeVisible();
		});

		await test.step('Select table highlights correct range', async () => {
			await textarea.click();
			await textarea.fill(
				'Before\n| X | Y |\n| :-- | :-- |\n| 1 | 2 |\nAfter'
			);
			await page.evaluate(() => {
				const ta = document.querySelector(
					'textarea[aria-label="Markdown content editor"]'
				) as HTMLTextAreaElement;
				ta.selectionStart = 10;
				ta.selectionEnd = 10;
				ta.dispatchEvent(new Event('blur'));
			});
			await getToolbarButton(page, 'select_all').click();
			const selection = await page.evaluate(() => {
				const ta = document.querySelector(
					'textarea[aria-label="Markdown content editor"]'
				) as HTMLTextAreaElement;
				return ta.value.substring(ta.selectionStart, ta.selectionEnd);
			});
			expect(selection).toContain('| X | Y |');
			expect(selection).not.toContain('Before');
			expect(selection).not.toContain('After');
		});

		await test.step('No table shows snackbar', async () => {
			await textarea.click();
			await textarea.fill('No table here');
			await getToolbarButton(page, 'select_all').click();
			await expect(
				page.getByText('No table found at the cursor position.')
			).toBeVisible({ timeout: 3000 });
		});
	});

	test('should add, remove rows and columns, and cycle alignment', async ({
		page,
	}) => {
		await getToolbarButton(page, 'table_chart').click();
		await expect(
			page.getByRole('heading', { name: /Insert Table/i })
		).toBeVisible({ timeout: 5000 });

		const rowInput = page.getByRole('spinbutton', { name: 'Row count' });
		const colInput = page.getByRole('spinbutton', { name: 'Column count' });

		await test.step('Add and remove rows and columns', async () => {
			await page
				.locator('button')
				.filter({ hasText: 'keyboard_arrow_down' })
				.first()
				.click();
			expect(parseInt(await rowInput.inputValue())).toBe(4);
			await page
				.locator('button')
				.filter({ hasText: 'keyboard_arrow_right' })
				.first()
				.click();
			expect(parseInt(await colInput.inputValue())).toBe(4);
			await page
				.locator('td button')
				.filter({ hasText: 'close' })
				.first()
				.click();
			expect(parseInt(await rowInput.inputValue())).toBe(3);
			await page
				.locator('thead button')
				.filter({ hasText: 'close' })
				.first()
				.click();
			expect(parseInt(await colInput.inputValue())).toBe(3);
		});

		await test.step('Cycle alignment and verify markdown output', async () => {
			const alignButton = page
				.locator('thead button')
				.filter({ hasText: 'format_align_left' })
				.first();
			await alignButton.click();
			await expect(
				page
					.locator('thead button')
					.filter({ hasText: 'format_align_center' })
					.first()
			).toBeVisible();
			await page
				.locator('thead button')
				.filter({ hasText: 'format_align_center' })
				.first()
				.click();
			await expect(
				page
					.locator('thead button')
					.filter({ hasText: 'format_align_right' })
					.first()
			).toBeVisible();
			const textarea = getTextarea(page);
			await page.getByRole('button', { name: 'Insert Table' }).click();
			expect(await textarea.inputValue()).toContain('--:');
		});
	});

	test('should support undo/redo and header spans in the table dialog', async ({
		page,
	}) => {
		await getToolbarButton(page, 'table_chart').click();
		await expect(
			page.getByRole('heading', { name: /Insert Table/i })
		).toBeVisible({ timeout: 5000 });

		await test.step('Undo/redo structural changes', async () => {
			const rowInput = page.getByRole('spinbutton', {
				name: 'Row count',
			});
			await page
				.locator('button')
				.filter({ hasText: 'keyboard_arrow_down' })
				.first()
				.click();
			expect(parseInt(await rowInput.inputValue())).toBe(4);
			await page.keyboard.press('Control+z');
			expect(parseInt(await rowInput.inputValue())).toBe(3);
			await page.keyboard.press('Control+y');
			expect(parseInt(await rowInput.inputValue())).toBe(4);
			await page.keyboard.press('Control+z');
		});

		await test.step('Undo text input captured on focus', async () => {
			const cell = getCellTextarea(page, 1, 1);
			await cell.click();
			await cell.fill('Hello');
			const cell2 = getCellTextarea(page, 1, 2);
			await cell2.click();
			await cell2.fill('World');
			await page.keyboard.press('Control+z');
			await expect(cell2).toHaveValue('');
			await page.keyboard.press('Control+z');
			await expect(cell).toHaveValue('');
		});

		await test.step('Merge, unmerge, and verify colspan output', async () => {
			await page
				.locator('button')
				.filter({ hasText: 'merge_type' })
				.nth(1)
				.click();
			await expect(
				page.locator('mat-label').filter({ hasText: '2 cols' })
			).toBeVisible();
			await page
				.locator('button')
				.filter({ hasText: 'call_split' })
				.first()
				.click();
			await expect(
				page.locator('mat-label').filter({ hasText: '2 cols' })
			).not.toBeVisible();
			const headerInput = page.getByRole('textbox', { name: /Header 1/ });
			await headerInput.click();
			await headerInput.fill('Merged');
			await page
				.locator('button')
				.filter({ hasText: 'merge_type' })
				.nth(1)
				.click();
			const textarea = getTextarea(page);
			await page.getByRole('button', { name: 'Insert Table' }).click();
			expect(await textarea.inputValue()).toMatch(/Merged\s*\|\|/);
		});
	});

	test('should parse colspan syntax and handle edge cases', async ({
		page,
	}) => {
		const textarea = getTextarea(page);

		await test.step('Parse existing colspan in edit mode', async () => {
			await textarea.click();
			await textarea.fill(
				'| Span1 ||| Span2 |\n| :-- | :-- | :-- | :-- |\n| a | b | c | d |'
			);
			await textarea.click();
			await textarea.press('Home');
			await getToolbarButton(page, 'table_chart').click();
			await expect(
				page.getByRole('heading', { name: /Edit Table/i })
			).toBeVisible({ timeout: 5000 });
			await expect(
				page.locator('mat-label').filter({ hasText: '3 cols' })
			).toBeVisible();
			await expect(
				page.getByRole('textbox', { name: /Header 1/ })
			).toHaveValue('Span1');
			await page.getByRole('button', { name: 'Cancel' }).click();
			await expect(
				page.getByRole('heading', { name: /Edit Table/i })
			).not.toBeVisible();
		});

		await test.step('Dialog does not close on backdrop click', async () => {
			await textarea.click();
			await textarea.fill('');
			await getToolbarButton(page, 'table_chart').click();
			await expect(
				page.getByRole('heading', { name: /Insert Table/i })
			).toBeVisible({ timeout: 5000 });
			await page.mouse.click(10, 10);
			await expect(
				page.getByRole('heading', { name: /Insert Table/i })
			).toBeVisible();
		});

		await test.step('Encode newlines and escape pipes in cells', async () => {
			const cell = getCellTextarea(page, 1, 1);
			await cell.click();
			await cell.fill('Line1\nLine2');
			const cell2 = getCellTextarea(page, 1, 2);
			await cell2.click();
			await cell2.fill('a | b');
			await page.getByRole('button', { name: 'Insert Table' }).click();
			const value = await textarea.inputValue();
			expect(value).toContain('Line1<br>Line2');
			expect(value).toContain('a \\| b');
		});

		await test.step('Unescape pipes when loading table for edit', async () => {
			await textarea.click();
			await textarea.fill('| H1 | H2 |\n| :-- | :-- |\n| a \\| b | c |');
			await textarea.press('Home');
			await getToolbarButton(page, 'table_chart').click();
			await expect(
				page.getByRole('heading', { name: /Edit Table/i })
			).toBeVisible({ timeout: 5000 });
			await expect(getCellTextarea(page, 1, 1)).toHaveValue('a | b');
			await page.getByRole('button', { name: 'Cancel' }).click();
		});
	});

	test('should handle caption insertion, detection from caption line, and blank lines', async ({
		page,
	}) => {
		const textarea = getTextarea(page);

		await test.step('Insert table with caption', async () => {
			await textarea.click();
			await textarea.fill('');
			await getToolbarButton(page, 'table_chart').click();
			await expect(
				page.getByRole('heading', { name: /Insert Table/i })
			).toBeVisible({ timeout: 5000 });
			const captionInput = page.getByRole('textbox', {
				name: 'Table caption',
			});
			await captionInput.click();
			await captionInput.fill('My Caption');
			await page.getByRole('button', { name: 'Insert Table' }).click();
			expect(await textarea.inputValue()).toContain(
				'<table-caption>My Caption</table-caption>'
			);
		});

		await test.step('Insert table without caption', async () => {
			await textarea.click();
			await textarea.fill('');
			await getToolbarButton(page, 'table_chart').click();
			await expect(
				page.getByRole('heading', { name: /Insert Table/i })
			).toBeVisible({ timeout: 5000 });
			await page.getByRole('button', { name: 'Insert Table' }).click();
			expect(await textarea.inputValue()).not.toContain(
				'<table-caption>'
			);
		});

		await test.step('Detect table when cursor is on caption line', async () => {
			await textarea.click();
			await textarea.fill(
				'| H1 | H2 |\n| :-- | :-- |\n| a | b |\n<table-caption>Caption Here</table-caption>'
			);
			await page.evaluate(() => {
				const ta = document.querySelector(
					'textarea[aria-label="Markdown content editor"]'
				) as HTMLTextAreaElement;
				const pos = ta.value.indexOf('<table-caption>') + 5;
				ta.selectionStart = pos;
				ta.selectionEnd = pos;
				ta.dispatchEvent(new Event('blur'));
			});
			await getToolbarButton(page, 'table_chart').click();
			await expect(
				page.getByRole('heading', { name: /Edit Table/i })
			).toBeVisible({ timeout: 5000 });
			await expect(
				page.getByRole('textbox', { name: 'Table caption' })
			).toHaveValue('Caption Here');
			await page.getByRole('button', { name: 'Cancel' }).click();
			await expect(
				page.getByRole('heading', { name: /Edit Table/i })
			).not.toBeVisible();
		});

		await test.step('Select table includes caption from caption line', async () => {
			await textarea.click();
			await textarea.fill(
				'Before\n| H1 | H2 |\n| :-- | :-- |\n| a | b |\n<table-caption>Cap</table-caption>\nAfter'
			);
			await page.evaluate(() => {
				const ta = document.querySelector(
					'textarea[aria-label="Markdown content editor"]'
				) as HTMLTextAreaElement;
				const pos = ta.value.indexOf('<table-caption>') + 3;
				ta.selectionStart = pos;
				ta.selectionEnd = pos;
				ta.dispatchEvent(new Event('blur'));
			});
			await getToolbarButton(page, 'select_all').click();
			const selection = await page.evaluate(() => {
				const ta = document.querySelector(
					'textarea[aria-label="Markdown content editor"]'
				) as HTMLTextAreaElement;
				return ta.value.substring(ta.selectionStart, ta.selectionEnd);
			});
			expect(selection).toContain('| H1 | H2 |');
			expect(selection).toContain('<table-caption>Cap</table-caption>');
			expect(selection).not.toContain('Before');
			expect(selection).not.toContain('After');
		});

		await test.step('Detect table with blank lines between table and caption', async () => {
			await textarea.click();
			await textarea.fill(
				'| H1 | H2 |\n| :-- | :-- |\n| a | b |\n\n<table-caption>Spaced</table-caption>'
			);
			await page.evaluate(() => {
				const ta = document.querySelector(
					'textarea[aria-label="Markdown content editor"]'
				) as HTMLTextAreaElement;
				const pos = ta.value.indexOf('<table-caption>') + 5;
				ta.selectionStart = pos;
				ta.selectionEnd = pos;
				ta.dispatchEvent(new Event('blur'));
			});
			await getToolbarButton(page, 'table_chart').click();
			await expect(
				page.getByRole('heading', { name: /Edit Table/i })
			).toBeVisible({ timeout: 5000 });
			await expect(
				page.getByRole('textbox', { name: 'Table caption' })
			).toHaveValue('Spaced');
			await page.getByRole('button', { name: 'Cancel' }).click();
		});

		await test.step('Insert table with caption position above', async () => {
			await textarea.click();
			await textarea.fill('');
			await getToolbarButton(page, 'table_chart').click();
			await expect(
				page.getByRole('heading', { name: /Insert Table/i })
			).toBeVisible({ timeout: 5000 });
			const captionInput = page.getByRole('textbox', {
				name: 'Table caption',
			});
			await captionInput.click();
			await captionInput.fill('Above Caption');
			// Toggle position to above
			await page
				.locator('button')
				.filter({ hasText: 'vertical_align_bottom' })
				.click();
			await page.getByRole('button', { name: 'Insert Table' }).click();
			const value = await textarea.inputValue();
			expect(value).toContain(
				'<table-caption position="above">Above Caption</table-caption>'
			);
			const captionIdx = value.indexOf('<table-caption');
			const tableIdx = value.indexOf('|');
			expect(captionIdx).toBeLessThan(tableIdx);
		});

		await test.step('Parse caption with position above in edit mode', async () => {
			await textarea.click();
			await textarea.fill(
				'<table-caption position="above">Above Cap</table-caption>\n\n| H1 | H2 |\n| :-- | :-- |\n| a | b |'
			);
			await page.evaluate(() => {
				const ta = document.querySelector(
					'textarea[aria-label="Markdown content editor"]'
				) as HTMLTextAreaElement;
				const pos = ta.value.indexOf('| H1');
				ta.selectionStart = pos;
				ta.selectionEnd = pos;
				ta.dispatchEvent(new Event('blur'));
			});
			await getToolbarButton(page, 'table_chart').click();
			await expect(
				page.getByRole('heading', { name: /Edit Table/i })
			).toBeVisible({ timeout: 5000 });
			await expect(
				page.getByRole('textbox', { name: 'Table caption' })
			).toHaveValue('Above Cap');
			await expect(
				page.locator('button').filter({ hasText: 'vertical_align_top' })
			).toBeVisible();
			await page.getByRole('button', { name: 'Cancel' }).click();
		});

		await test.step('Show warning when table has both above and below captions', async () => {
			await textarea.click();
			await textarea.fill(
				'<table-caption position="above">Above</table-caption>\n\n| H1 | H2 |\n| :-- | :-- |\n| a | b |\n\n<table-caption>Below</table-caption>'
			);
			await page.evaluate(() => {
				const ta = document.querySelector(
					'textarea[aria-label="Markdown content editor"]'
				) as HTMLTextAreaElement;
				const pos = ta.value.indexOf('| H1');
				ta.selectionStart = pos;
				ta.selectionEnd = pos;
				ta.dispatchEvent(new Event('blur'));
			});
			await getToolbarButton(page, 'table_chart').click();
			await expect(
				page.getByText('This table has captions both above and below')
			).toBeVisible({ timeout: 3000 });
			await expect(
				page.getByRole('heading', { name: /Edit Table/i })
			).toBeVisible({ timeout: 5000 });
			await expect(
				page.getByRole('textbox', { name: 'Table caption' })
			).toHaveValue('Above');
			await page.getByRole('button', { name: 'Cancel' }).click();
		});

		await test.step('Escape < in caption and unescape on edit', async () => {
			await textarea.click();
			await textarea.fill('');
			await getToolbarButton(page, 'table_chart').click();
			await expect(
				page.getByRole('heading', { name: /Insert Table/i })
			).toBeVisible({ timeout: 5000 });
			const captionInput = page.getByRole('textbox', {
				name: 'Table caption',
			});
			await captionInput.click();
			await captionInput.fill('Values < 100');
			await page.getByRole('button', { name: 'Insert Table' }).click();
			const raw = await textarea.inputValue();
			expect(raw).toContain(
				'<table-caption>Values &lt; 100</table-caption>'
			);
			await page.evaluate(() => {
				const ta = document.querySelector(
					'textarea[aria-label="Markdown content editor"]'
				) as HTMLTextAreaElement;
				const pos = ta.value.indexOf('|');
				ta.selectionStart = pos;
				ta.selectionEnd = pos;
				ta.dispatchEvent(new Event('blur'));
			});
			await getToolbarButton(page, 'table_chart').click();
			await expect(
				page.getByRole('heading', { name: /Edit Table/i })
			).toBeVisible({ timeout: 5000 });
			await expect(
				page.getByRole('textbox', { name: 'Table caption' })
			).toHaveValue('Values < 100');
			await page.getByRole('button', { name: 'Cancel' }).click();
		});
	});

	test('should include caption in undo/redo within the table dialog', async ({
		page,
	}) => {
		await getToolbarButton(page, 'table_chart').click();
		await expect(
			page.getByRole('heading', { name: /Insert Table/i })
		).toBeVisible({ timeout: 5000 });
		const captionInput = page.getByRole('textbox', {
			name: 'Table caption',
		});
		await captionInput.click();
		await captionInput.fill('First Caption');
		const cell = getCellTextarea(page, 1, 1);
		await cell.click();
		await cell.fill('data');
		await captionInput.click();
		await captionInput.fill('Second Caption');
		await page.keyboard.press('Control+z');
		await expect(captionInput).toHaveValue('First Caption');
		await page.keyboard.press('Control+y');
		await expect(captionInput).toHaveValue('Second Caption');
		await page.getByRole('button', { name: 'Cancel' }).click();
	});

	test('should display row numbers, update on add, and show correct tooltips', async ({
		page,
	}) => {
		await getToolbarButton(page, 'table_chart').click();
		await expect(
			page.getByRole('heading', { name: /Insert Table/i })
		).toBeVisible({ timeout: 5000 });

		const rowHeaders = page.getByRole('rowheader');
		await expect(rowHeaders.nth(0)).toHaveAttribute('aria-label', 'Row 1');
		await expect(rowHeaders.nth(1)).toHaveAttribute('aria-label', 'Row 2');
		await expect(rowHeaders.nth(2)).toHaveAttribute('aria-label', 'Row 3');

		await page
			.locator('button')
			.filter({ hasText: 'keyboard_arrow_down' })
			.first()
			.click();
		await expect(rowHeaders.nth(3)).toHaveAttribute('aria-label', 'Row 4');

		const upButton = page
			.locator('tbody button')
			.filter({ hasText: 'keyboard_arrow_up' })
			.first();
		await upButton.hover();
		await expect(
			page
				.locator('mat-tooltip-component')
				.filter({ hasText: 'Insert Row Above' })
				.first()
		).toBeVisible({ timeout: 3000 });

		await page.getByRole('button', { name: 'Cancel' }).click();
	});

	test('should insert figure and table captions via toolbar buttons', async ({
		page,
	}) => {
		const textarea = getTextarea(page);

		await test.step('Figure caption button inserts figure-caption tag', async () => {
			await textarea.click();
			await textarea.fill('');
			await page.evaluate(() => {
				const ta = document.querySelector(
					'textarea[aria-label="Markdown content editor"]'
				) as HTMLTextAreaElement;
				ta.selectionStart = 0;
				ta.selectionEnd = 0;
				ta.dispatchEvent(new Event('blur'));
			});
			await getToolbarButton(page, 'video_label').click();
			expect(await textarea.inputValue()).toContain('<figure-caption>');
		});

		await test.step('Table caption button inserts table-caption tag', async () => {
			await textarea.click();
			await textarea.fill('');
			await page.evaluate(() => {
				const ta = document.querySelector(
					'textarea[aria-label="Markdown content editor"]'
				) as HTMLTextAreaElement;
				ta.selectionStart = 0;
				ta.selectionEnd = 0;
				ta.dispatchEvent(new Event('blur'));
			});
			await getToolbarButton(page, 'legend_toggle').click();
			expect(await textarea.inputValue()).toContain('<table-caption>');
		});

		await test.step('Figure caption wraps selected text', async () => {
			await textarea.click();
			await textarea.fill('My Figure Title');
			await page.evaluate(() => {
				const ta = document.querySelector(
					'textarea[aria-label="Markdown content editor"]'
				) as HTMLTextAreaElement;
				ta.selectionStart = 0;
				ta.selectionEnd = 15;
				ta.dispatchEvent(new Event('blur'));
			});
			await getToolbarButton(page, 'video_label').click();
			await expect(textarea).toHaveValue(
				'<figure-caption>My Figure Title</figure-caption>'
			);
		});
	});

	test('should not close image upload dialog on backdrop click', async ({
		page,
	}) => {
		const uploadButton = getToolbarButton(page, 'image');
		await expect(uploadButton).toBeEnabled();
		await uploadButton.click();
		await expect(
			page.getByRole('heading', { name: /Upload Image/i })
		).toBeVisible({ timeout: 5000 });
		await page.mouse.click(10, 10);
		await expect(
			page.getByRole('heading', { name: /Upload Image/i })
		).toBeVisible();
		await page.getByRole('button', { name: 'Cancel' }).click();
		await expect(
			page.getByRole('heading', { name: /Upload Image/i })
		).not.toBeVisible();
	});

	test('should display size selector in image upload dialog', async ({
		page,
	}) => {
		const uploadButton = getToolbarButton(page, 'image');
		await uploadButton.click();
		await expect(
			page.getByRole('heading', { name: /Upload Image/i })
		).toBeVisible({ timeout: 5000 });

		await test.step('Size selector is not visible before file selection', async () => {
			await expect(
				page.getByText('Drag & drop an image here')
			).toBeVisible();
			await expect(page.getByText('Publish Size')).not.toBeVisible();
		});

		await test.step('Size selector visible after file selection with Auto default', async () => {
			const fileInput = page.locator('input[type="file"]');
			await fileInput.setInputFiles({
				name: 'test-image.png',
				mimeType: 'image/png',
				buffer: Buffer.from(
					'iVBORw0KGgoAAAANSUhEUgAAAAEAAAABCAYAAAAfFcSJAAAADUlEQVR42mNk+M9QDwADhgGAWjR9awAAAABJRU5ErkJggg==',
					'base64'
				),
			});
			await expect(page.getByText('Publish Size')).toBeVisible();
			const autoToggle = page.locator('mat-button-toggle[value=""]');
			await expect(autoToggle).toHaveClass(/mat-button-toggle-checked/);
		});

		await test.step('Size toggles are selectable and show tooltips', async () => {
			const xsToggle = page.locator('mat-button-toggle[value="xs"]');
			await xsToggle.click();
			await expect(xsToggle).toHaveClass(/mat-button-toggle-checked/);

			const mToggle = page.locator('mat-button-toggle[value="m"]');
			await mToggle.click();
			await expect(mToggle).toHaveClass(/mat-button-toggle-checked/);
			await expect(xsToggle).not.toHaveClass(/mat-button-toggle-checked/);

			await mToggle.hover();
			await expect(
				page
					.locator('mat-tooltip-component')
					.filter({ hasText: '75% of max width' })
					.first()
			).toBeVisible({ timeout: 3000 });
		});

		await page.getByRole('button', { name: 'Cancel' }).click();
	});
});
