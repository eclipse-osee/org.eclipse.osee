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

	test('should collapse panel when clicking the active section button', async ({
		page,
	}) => {
		await openBranch(page, BRANCH);
		const panel = page.locator('osee-artifact-hierarchy-panel');
		await expect(panel).toBeVisible();

		await page.getByRole('button', { name: 'Artifact Hierarchy' }).click();
		await expect(panel).not.toBeVisible();

		await page.getByRole('button', { name: 'Artifact Hierarchy' }).click();
		await expect(panel).toBeVisible();
		await expect(
			page.getByText('System Requirements - Markdown', { exact: true })
		).toBeVisible({ timeout: 15000 });
	});

	test('should resize the hierarchy panel via the drag handle', async ({
		page,
	}) => {
		await openBranch(page, BRANCH);
		const handle = page.locator('[role="separator"]');
		await expect(handle).toBeVisible();

		const panel = page
			.locator('osee-artifact-hierarchy-panel')
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
		const scrollArea = page.getByRole('tree', {
			name: 'Artifact hierarchy',
		});
		await expect(scrollArea.first()).toBeVisible();

		const scrollbarStyles = await page.evaluate(() => {
			const el = document.querySelector('.hierarchy-scroll');
			if (!el) return null;
			const computedStyle = window.getComputedStyle(el);
			return { scrollbarWidth: computedStyle.scrollbarWidth };
		});

		expect(scrollbarStyles).not.toBeNull();
		expect(scrollbarStyles!.scrollbarWidth).toBe('auto');
	});
});
