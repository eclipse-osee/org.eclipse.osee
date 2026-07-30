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
import { selectBranch } from '../../../shared/branch-helpers';

test.describe('Branch Query Params', () => {
	test.describe.configure({ mode: 'parallel' });

	test('selecting a branch adds branchType and branchId to URL', async ({
		page,
	}) => {
		await page.goto('/ple/messaging/connections');
		await selectBranch(page, 'Working', 'SAW PL');

		// Verify query params appear in URL
		const url = new URL(page.url());
		expect(url.searchParams.get('branchType')).toBe('working');
		expect(url.searchParams.get('branchId')).toBeTruthy();
		// branchType and branchId should NOT be in the path
		expect(url.pathname).not.toContain('working');
	});

	test('navigating with query params pre-populates branch picker', async ({
		page,
	}) => {
		// First select a branch to get a valid branchId
		await page.goto('/ple/messaging/connections');
		await selectBranch(page, 'Working', 'SAW PL');

		// Capture the branchId from the URL
		const firstUrl = new URL(page.url());
		const branchId = firstUrl.searchParams.get('branchId');
		expect(branchId).toBeTruthy();

		// Navigate to artifact explorer with the same query params
		await page.goto(
			`/ple/artifact/explorer?branchType=working&branchId=${branchId}`
		);

		// Verify the branch type toggle shows "working" as selected
		const workingToggle = page.locator(
			'mat-button-toggle[data-cy="working"]'
		);
		await expect(workingToggle).toHaveClass(/mat-button-toggle-checked/, {
			timeout: 10000,
		});

		// Verify the branch name input has a value (not empty)
		const branchInput = page.getByRole('combobox', {
			name: 'Select a Branch',
		});
		await expect(branchInput).not.toHaveValue('', { timeout: 10000 });
	});

	test('branch context persists when navigating between pages via sidebar', async ({
		page,
	}) => {
		// Start on connections page and select a branch
		await page.goto('/ple/messaging/connections');
		await selectBranch(page, 'Working', 'SAW PL');

		// Capture the branchId
		const firstUrl = new URL(page.url());
		const branchId = firstUrl.searchParams.get('branchId');
		expect(branchId).toBeTruthy();

		// Navigate to a different page via the sidebar menu
		await page.locator('button').filter({ hasText: 'menu' }).click();
		await page.getByText('Product Line Engineering').click();
		await page.getByRole('link', { name: 'Artifact Explorer' }).click();

		// Wait for navigation to complete
		await page.waitForURL(/artifact/, { timeout: 10000 });

		// Verify query params are preserved
		const newUrl = new URL(page.url());
		expect(newUrl.searchParams.get('branchType')).toBe('working');
		expect(newUrl.searchParams.get('branchId')).toBe(branchId);

		// Verify branch picker is still populated
		const workingToggle = page.locator(
			'mat-button-toggle[data-cy="working"]'
		);
		await expect(workingToggle).toHaveClass(/mat-button-toggle-checked/, {
			timeout: 10000,
		});
	});

	test('clearing branch removes query params from URL', async ({ page }) => {
		await page.goto('/ple/messaging/connections');
		await selectBranch(page, 'Working', 'SAW PL');

		// Verify branchId is in URL
		let url = new URL(page.url());
		expect(url.searchParams.get('branchId')).toBeTruthy();

		// Click the clear button on the branch input
		const branchInput = page.getByRole('combobox', {
			name: 'Select a Branch',
		});
		await branchInput.click();
		await page
			.locator('button[aria-label="Clear branch selection"]')
			.click();

		// Wait for URL to update
		await page.waitForTimeout(500);

		// Verify branchId is removed from URL
		url = new URL(page.url());
		expect(url.searchParams.has('branchId')).toBe(false);
	});
});
