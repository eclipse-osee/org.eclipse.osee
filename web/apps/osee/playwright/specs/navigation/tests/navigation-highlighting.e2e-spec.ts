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
import { test, expect, type Page } from '@ngx-playwright/test';

/**
 * Opens the top-level navigation side panel by clicking the menu button.
 */
async function openNavigator(page: Page) {
	const menuButton = page.locator('mat-icon', { hasText: 'menu' }).first();
	await menuButton.click();
	await expect(page.locator('osee-top-level-navigation')).toBeVisible();
}

/**
 * Gets a nav item by its visible label text.
 */
function getNavItem(page: Page, label: string) {
	return page
		.locator('osee-top-level-navigation mat-list-item')
		.filter({ hasText: label })
		.first();
}

/**
 * Asserts that a nav item has the data-active attribute set to "true".
 */
async function expectActive(page: Page, label: string) {
	const item = getNavItem(page, label);
	await expect(item).toHaveAttribute('data-active', 'true');
}

/**
 * Asserts that a nav item has the data-active attribute set to "false".
 */
async function expectNotActive(page: Page, label: string) {
	const item = getNavItem(page, label);
	await expect(item).toHaveAttribute('data-active', 'false');
}

/**
 * Selects a branch using the branch picker UI.
 */
async function selectBranch(
	page: Page,
	branchType: 'Working' | 'Product Line' | 'Baseline',
	branchName: string
) {
	await page.getByLabel(branchType).check();
	await page.getByText('Select a Branch').click();
	await page.getByText(branchName).click();
}

test.describe('Navigation highlighting', () => {
	test('highlights PLE Home when on /ple', async ({ page }) => {
		await page.goto('/ple');
		await openNavigator(page);

		// Open the PLE dropdown
		await getNavItem(page, 'Product Line Engineering').click();

		await expectActive(page, 'Product Line Engineering Home');
		await expectNotActive(page, 'Artifact Explorer');
		await expectNotActive(page, 'Product Line Configuration');
	});

	test('highlights only Connections when on a MIM sub-route', async ({
		page,
	}) => {
		await page.goto('/ple/messaging/connections');
		await openNavigator(page);

		// Open PLE dropdown, then MIM dropdown
		await getNavItem(page, 'Product Line Engineering').click();
		await getNavItem(page, 'MIM').click();

		await expectActive(page, 'Connections');
		await expectNotActive(page, 'MIM Home');
		await expectNotActive(page, 'Product Line Engineering Home');
	});

	test('highlights Artifact Explorer after selecting a branch', async ({
		page,
	}) => {
		await page.goto('/ple/artifact/explorer');
		await selectBranch(page, 'Product Line', 'SAW Product Line');

		// URL now includes branch segments — verify nav still highlights correctly
		await openNavigator(page);
		await getNavItem(page, 'Product Line Engineering').click();

		await expectActive(page, 'Artifact Explorer');
		await expectNotActive(page, 'Product Line Engineering Home');
	});

	test('highlights Server Health Dashboard only on exact route', async ({
		page,
	}) => {
		await page.goto('/server/health');
		await openNavigator(page);

		// Open Server Health dropdown
		await getNavItem(page, 'Server Health').click();

		await expectActive(page, 'Server Health Dashboard');
		await expectNotActive(page, 'Status');
	});

	test('does not highlight Server Health Dashboard when on Status', async ({
		page,
	}) => {
		await page.goto('/server/health/status');
		await openNavigator(page);

		// Open Server Health dropdown
		await getNavItem(page, 'Server Health').click();

		await expectActive(page, 'Status');
		await expectNotActive(page, 'Server Health Dashboard');
	});

	test('dropdown highlights when collapsed and child is active', async ({
		page,
	}) => {
		await page.goto('/ple/messaging/connections');
		await openNavigator(page);

		// PLE dropdown is collapsed — should be highlighted
		await expectActive(page, 'Product Line Engineering');
	});

	test('dropdown does not highlight when no child matches', async ({
		page,
	}) => {
		await page.goto('/ple');
		await openNavigator(page);

		// Server Health dropdown should not be highlighted
		await expectNotActive(page, 'Server Health');
	});
});
