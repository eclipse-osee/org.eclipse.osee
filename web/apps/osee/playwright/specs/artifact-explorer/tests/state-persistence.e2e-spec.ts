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
	navigateToArtifactExplorer,
	selectBranch,
	expandArtifact,
	switchToSearch,
	switchToHierarchy,
	searchAndOpenArtifact,
} from '../utils/helpers';

const BRANCH = 'AE State Tests';
let branchId: string;

test.describe('State Persistence', () => {
	test.describe.configure({ mode: 'serial' });

	test.beforeAll(async ({ browser, request }) => {
		branchId = await createBranchViaApi(request, BRANCH);
		const page = await browser.newPage();
		await openBranch(page, BRANCH);
		await createArtifact(
			page,
			'System Requirements - Markdown',
			'AE State Parent',
			'Folder'
		);
		await page.close();
	});

	test.afterAll(async ({ request }) => {
		await purgeBranchViaApi(request, branchId);
	});

	test('should preserve expanded folders when switching panel sections', async ({
		page,
	}) => {
		await openBranch(page, BRANCH);
		await expandArtifact(page, 'System Requirements - Markdown');
		await expect(page.getByText('AE State Parent')).toBeVisible({
			timeout: 10000,
		});

		await switchToSearch(page);
		await switchToHierarchy(page);

		await expect(page.getByText('AE State Parent')).toBeVisible();
	});

	test('should clear expanded state when navigating away and re-selecting branch', async ({
		page,
	}) => {
		await openBranch(page, BRANCH);
		await expandArtifact(page, 'System Requirements - Markdown');
		await expect(page.getByText('AE State Parent')).toBeVisible({
			timeout: 10000,
		});

		await page.goto('/ple/messaging/connections');
		await page.waitForLoadState('networkidle');

		await navigateToArtifactExplorer(page);
		await selectBranch(page, 'Working', BRANCH);
		await expect(
			page.getByText('System Requirements - Markdown', { exact: true })
		).toBeVisible({ timeout: 15000 });

		await expect(page.getByText('AE State Parent')).not.toBeVisible();
	});

	// Each activity-bar button is a single-click toggle: clicking the active
	// section closes the panel in ONE click, and clicking it again reopens it.
	// (Regression guard: collapsing used to require two clicks / reset to the
	// hierarchy because it navigated back to the base route and re-created the
	// component.)
	const sectionCases = [
		{ button: 'Artifact Hierarchy', heading: 'Hierarchy' },
		{ button: 'Artifact Search', heading: 'Search' },
		{ button: 'Branch Management', heading: 'Branch Management' },
	] as const;

	for (const { button, heading } of sectionCases) {
		test(`should open and close the panel in one click each via the ${button} button`, async ({
			page,
		}) => {
			await openBranch(page, BRANCH);
			const panel = page.locator('osee-artifact-explorer-sidebar');
			const activityButton = page.getByRole('button', { name: button });
			const sectionHeading = panel.getByRole('heading', {
				name: heading,
				exact: true,
			});

			// Ensure this section is open to start (single click if not already).
			if (!(await sectionHeading.isVisible())) {
				await activityButton.click();
			}
			await expect(panel).toBeVisible();
			await expect(sectionHeading).toBeVisible();

			// One click closes the panel entirely.
			await activityButton.click();
			await expect(panel).not.toBeVisible();

			// One click reopens the same section.
			await activityButton.click();
			await expect(panel).toBeVisible();
			await expect(sectionHeading).toBeVisible();
		});
	}

	test('should switch sections and still close in one click on the active section', async ({
		page,
	}) => {
		await openBranch(page, BRANCH);
		const panel = page.locator('osee-artifact-explorer-sidebar');
		await expect(panel).toBeVisible();

		// Switch hierarchy -> search -> branch, one click each.
		await page.getByRole('button', { name: 'Artifact Search' }).click();
		await expect(
			panel.getByRole('heading', { name: 'Search', exact: true })
		).toBeVisible();

		await page.getByRole('button', { name: 'Branch Management' }).click();
		await expect(
			panel.getByRole('heading', {
				name: 'Branch Management',
				exact: true,
			})
		).toBeVisible();

		// Clicking the active (branch) section closes the panel in one click —
		// it must NOT fall back to showing the hierarchy.
		await page.getByRole('button', { name: 'Branch Management' }).click();
		await expect(panel).not.toBeVisible();
	});

	test('should resize the hierarchy panel via the drag handle', async ({
		page,
	}) => {
		await openBranch(page, BRANCH);
		const handle = page.locator('[role="separator"]');
		await expect(handle).toBeVisible();

		const panel = page
			.locator('osee-artifact-explorer-sidebar')
			.locator('..');
		const initialBox = await panel.boundingBox();
		expect(initialBox).not.toBeNull();

		const handleBox = await handle.boundingBox();
		expect(handleBox).not.toBeNull();
		const startX = handleBox!.x + handleBox!.width / 2;
		const startY = handleBox!.y + handleBox!.height / 2;

		await page.mouse.move(startX, startY);
		await page.mouse.down();
		await page.mouse.move(startX + 80, startY, { steps: 5 });
		await page.mouse.up();

		const newBox = await panel.boundingBox();
		expect(newBox).not.toBeNull();
		expect(newBox!.width).toBeGreaterThan(initialBox!.width);
	});

	test('should have matching vertical and horizontal scrollbar sizes', async ({
		page,
	}) => {
		await openBranch(page, BRANCH);
		const scrollArea = page
			.getByRole('tree', {
				name: 'Artifact hierarchy',
			})
			.first();
		await expect(scrollArea).toBeVisible();

		const scrollbarStyles = await scrollArea.evaluate((el) => {
			const computedStyle = window.getComputedStyle(el);
			return { scrollbarWidth: computedStyle.scrollbarWidth };
		});

		expect(scrollbarStyles).not.toBeNull();
		expect(scrollbarStyles.scrollbarWidth).toBe('auto');
	});

	test('should keep the artifact editor toolbar pinned when the editor scrolls', async ({
		page,
	}) => {
		await openBranch(page, BRANCH);

		// Open an artifact into an editor tab.
		await searchAndOpenArtifact(page, 'AE State Parent');
		const toolbar = page.locator('osee-artifact-editor [role="toolbar"]');
		await expect(toolbar).toBeVisible({ timeout: 10000 });

		// The section content scrolls in the tab-group's overflow container.
		const scrollContainer = page
			.locator('osee-artifact-tab-group .tw-overflow-auto')
			.first();
		await expect(scrollContainer).toBeVisible();

		// Record the toolbar's top before scrolling.
		const toolbarTopBefore = (await toolbar.boundingBox())!.y;

		// Scroll the content down.
		await scrollContainer.evaluate((el) => (el.scrollTop = 400));
		await page.waitForTimeout(200);

		// The toolbar must not move — it is sticky at the top of the scroll
		// container. If the content were still nested inside the toolbar div,
		// sticky would silently fail because the toolbar would be the full
		// height of its scroll context.
		const toolbarTopAfter = (await toolbar.boundingBox())!.y;
		expect(Math.abs(toolbarTopAfter - toolbarTopBefore)).toBeLessThan(4);
		await expect(toolbar).toBeVisible();
	});
});
