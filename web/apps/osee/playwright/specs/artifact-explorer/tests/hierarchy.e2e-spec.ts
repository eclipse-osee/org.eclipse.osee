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
	expandArtifact,
	switchToSearch,
	switchToHierarchy,
	switchToBranchManagement,
} from '../utils/helpers';

const BRANCH = 'AE Hierarchy Tests';
let branchId: string;

test.describe('Hierarchy Navigation', () => {
	test.describe.configure({ mode: 'serial' });

	test.beforeAll(async ({ browser, request }) => {
		branchId = await createBranchViaApi(request, BRANCH);
		const page = await browser.newPage();
		await openBranch(page, BRANCH);
		await createArtifact(
			page,
			'System Requirements - Markdown',
			'AE Hierarchy Parent',
			'Folder'
		);
		await page.close();
	});

	test.afterAll(async ({ request }) => {
		await purgeBranchViaApi(request, branchId);
	});

	test('should display root artifacts in the hierarchy', async ({ page }) => {
		await openBranch(page, BRANCH);
		await expect(
			page.getByText('System Requirements - Markdown', { exact: true })
		).toBeVisible();
	});

	test('should expand a folder to show children', async ({ page }) => {
		await openBranch(page, BRANCH);
		await expandArtifact(page, 'System Requirements - Markdown');
		await expect(page.getByText('AE Hierarchy Parent')).toBeVisible({
			timeout: 10000,
		});
	});

	test('should collapse a folder to hide children', async ({ page }) => {
		await openBranch(page, BRANCH);
		await expandArtifact(page, 'System Requirements - Markdown');
		await expect(page.getByText('AE Hierarchy Parent')).toBeVisible({
			timeout: 10000,
		});
		// Collapse
		await expandArtifact(page, 'System Requirements - Markdown');
		await expect(page.getByText('AE Hierarchy Parent')).not.toBeVisible();
	});

	test('should switch between hierarchy, search, and branch management', async ({
		page,
	}) => {
		await openBranch(page, BRANCH);
		await switchToSearch(page);
		await expect(page.getByLabel('Search for Artifact')).toBeVisible();

		await switchToBranchManagement(page);
		await switchToHierarchy(page);
		await expect(
			page.getByText('System Requirements - Markdown', { exact: true })
		).toBeVisible({ timeout: 15000 });
	});

	test('should open artifact in tab on click', async ({ page }) => {
		await openBranch(page, BRANCH);
		await expandArtifact(page, 'System Requirements - Markdown');
		await expect(page.getByText('AE Hierarchy Parent')).toBeVisible({
			timeout: 10000,
		});

		await page.getByRole('button', { name: 'AE Hierarchy Parent' }).click();

		await expect(
			page
				.locator('osee-artifact-tab-group')
				.getByText('AE Hierarchy Parent')
				.first()
		).toBeVisible({ timeout: 10000 });
	});

	test('should show blue text for artifacts open in a tab', async ({
		page,
	}) => {
		await openBranch(page, BRANCH);
		await expandArtifact(page, 'System Requirements - Markdown');
		await page.getByRole('button', { name: 'AE Hierarchy Parent' }).click();
		await expect(
			page
				.locator('osee-artifact-tab-group')
				.getByText('AE Hierarchy Parent')
				.first()
		).toBeVisible({ timeout: 10000 });

		const artifactButton = page
			.locator('osee-artifact-hierarchy')
			.getByRole('button', { name: 'AE Hierarchy Parent' })
			.first();

		const color = await artifactButton.evaluate((el) => {
			return window.getComputedStyle(el).color;
		});
		const rgb = color.match(/\d+/g)?.map(Number) ?? [0, 0, 0];
		expect(rgb[2]).toBeGreaterThan(rgb[0] + 50);
	});

	test('should not show content behind sticky editor toolbar on scroll', async ({
		page,
	}) => {
		await openBranch(page, BRANCH);
		await expandArtifact(page, 'System Requirements - Markdown');
		await page.getByRole('button', { name: 'AE Hierarchy Parent' }).click();
		await expect(
			page
				.locator('osee-artifact-tab-group')
				.getByText('AE Hierarchy Parent')
				.first()
		).toBeVisible({ timeout: 10000 });

		const toolbar = page.getByRole('toolbar', {
			name: 'Editor section toolbar',
		});
		await expect(toolbar).toBeVisible();
		const bgColor = await toolbar.evaluate((el) => {
			return window.getComputedStyle(el).backgroundColor;
		});
		expect(bgColor).not.toBe('rgba(0, 0, 0, 0)');
		expect(bgColor).not.toBe('transparent');
	});
});
