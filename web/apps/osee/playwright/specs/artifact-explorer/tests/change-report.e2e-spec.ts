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
} from '../utils/helpers';

const BRANCH = 'AE Change Report Tests';

/**
 * E2E coverage for the Change Report page (`/ple/change-report`). The page is
 * driven by the `branchId`/`branchType` query params (BranchRoutedUIService maps
 * them onto the branch state), so tests navigate directly to the route with
 * those params rather than clicking through branch management (which opens the
 * report in a new tab).
 */
test.describe('Change Report', () => {
	test.describe.configure({ mode: 'serial' });

	let branchId: string;

	test.beforeAll(async ({ browser, request }) => {
		branchId = await createBranchViaApi(request, BRANCH);
		// Create a couple of markdown artifacts so the report has real rows.
		const page = await browser.newPage();
		await openBranch(page, BRANCH);
		await createArtifact(
			page,
			'System Requirements - Markdown',
			'CR Report Artifact One'
		);
		await createArtifact(
			page,
			'System Requirements - Markdown',
			'CR Report Artifact Two'
		);
		await page.close();
	});

	test.afterAll(async ({ request }) => {
		await purgeBranchViaApi(request, branchId);
	});

	test('should show the empty state when no branch is selected', async ({
		page,
	}) => {
		await page.goto('/ple/change-report');
		await expect(
			page.getByText(
				'Click the branch icon in the top-right to select a branch.'
			)
		).toBeVisible({ timeout: 15000 });
		// The branch picker button (merge_type icon, tooltip "Select Branch")
		// is available. Its accessible name comes from a tooltip, so locate it
		// by the icon rather than by role name.
		await expect(
			page.locator('button:has(mat-icon:text-is("merge_type"))')
		).toBeVisible();
	});

	test('should show the change report table with results for a branch', async ({
		page,
	}) => {
		await page.goto(
			`/ple/change-report?branchId=${branchId}&branchType=working`
		);

		// The comparison header names the branch and its parent.
		await expect(page.getByText(BRANCH)).toBeVisible({ timeout: 15000 });

		// The table renders with its column headers.
		const table = page.locator('osee-change-report-table table');
		await expect(table).toBeVisible();
		await expect(
			table.getByRole('columnheader', { name: 'Change Type' })
		).toBeVisible();

		// The result count is non-zero and at least one row is shown. (Material
		// renders rows as <tr mat-row>, not inside a <tbody>.)
		await expect(page.getByText(/[1-9]\d* result\(s\)/)).toBeVisible();
		const rowCount = await page
			.locator('osee-change-report-table table tr[mat-row]')
			.count();
		expect(rowCount).toBeGreaterThan(0);

		// The created artifact names appear in the report.
		await expect(
			page.getByText('CR Report Artifact One').first()
		).toBeVisible();
	});

	test('should filter the change report by text', async ({ page }) => {
		await page.goto(
			`/ple/change-report?branchId=${branchId}&branchType=working`
		);
		await expect(page.getByText(BRANCH)).toBeVisible({ timeout: 15000 });

		const filter = page.getByPlaceholder(
			'Filter by name, ID, or change type...'
		);
		await filter.fill('CR Report Artifact One');

		// Only the matching artifact remains; the other is filtered out.
		await expect(
			page.getByText('CR Report Artifact One').first()
		).toBeVisible();
		await expect(page.getByText('CR Report Artifact Two')).toHaveCount(0);

		// Clearing the filter restores the other artifact. (Clear button's name
		// comes from a tooltip; locate it by its close icon within the table.)
		await page
			.locator(
				'osee-change-report-table button:has(mat-icon:text-is("close"))'
			)
			.click();
		await expect(filter).toHaveValue('');
		await expect(
			page.getByText('CR Report Artifact Two').first()
		).toBeVisible();
	});

	test('should render a paginator with the default page size', async ({
		page,
	}) => {
		await page.goto(
			`/ple/change-report?branchId=${branchId}&branchType=working`
		);
		await expect(page.getByText(BRANCH)).toBeVisible({ timeout: 15000 });
		// Wait for the async results to load before touching the paginator.
		await expect(page.getByText(/[1-9]\d* result\(s\)/)).toBeVisible();

		const paginator = page.locator('mat-paginator');
		await expect(paginator).toBeVisible();

		// The paginator renders with the default page size (10) and a range
		// label. (The page-size dropdown mechanics are Angular Material's own
		// behavior — its touch-target overlay makes the option click flaky, so
		// we assert the configured state rather than driving the overlay.)
		await expect(
			paginator.getByRole('combobox', { name: 'Items per page:' })
		).toContainText('10');
		await expect(paginator).toContainText(/of \d+/);
	});
});
