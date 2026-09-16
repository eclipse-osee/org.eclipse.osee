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

const BRANCH = 'AE Markdown Change Report Tests';

/**
 * E2E coverage for the Markdown Change Report page
 * (`/ple/markdown-change-report`). Like the Change Report page it is driven by
 * the `branchId`/`branchType` query params, so tests navigate directly to the
 * route. Creating "Software Requirement - Markdown" artifacts seeds each with a
 * `Markdown Content` attribute, so they surface here as "Markdown Content
 * Added" changes.
 */
test.describe('Markdown Change Report', () => {
	test.describe.configure({ mode: 'serial' });

	let branchId: string;

	test.beforeAll(async ({ browser, request }) => {
		branchId = await createBranchViaApi(request, BRANCH);
		const page = await browser.newPage();
		await openBranch(page, BRANCH);
		await createArtifact(
			page,
			'System Requirements - Markdown',
			'MD Report Artifact One'
		);
		await createArtifact(
			page,
			'System Requirements - Markdown',
			'MD Report Artifact Two'
		);
		await page.close();
	});

	test.afterAll(async ({ request }) => {
		await purgeBranchViaApi(request, branchId);
	});

	test('should show the empty state and a disabled export when no branch is selected', async ({
		page,
	}) => {
		await page.goto('/ple/markdown-change-report');

		await expect(
			page.getByText('Markdown Change Report').first()
		).toBeVisible({ timeout: 15000 });
		await expect(
			page.getByText(
				'Click the branch icon in the top-right to select a branch.'
			)
		).toBeVisible();

		// Export (download icon) is disabled when there are no results. Its
		// accessible name comes from a tooltip, so locate it by the icon.
		await expect(
			page.locator('button:has(mat-icon:text-is("download"))')
		).toBeDisabled();
	});

	test('should list markdown changes with a summary table and diff entries', async ({
		page,
	}) => {
		await page.goto(
			`/ple/markdown-change-report?branchId=${branchId}&branchType=working`
		);

		await expect(page.getByText(BRANCH)).toBeVisible({ timeout: 15000 });

		// Summary table shows the created markdown artifacts.
		const table = page.locator('osee-markdown-diff table');
		await expect(table).toBeVisible();
		await expect(
			page.getByText('MD Report Artifact One').first()
		).toBeVisible();
		await expect(page.getByText(/\d+ result\(s\)/)).toBeVisible();

		// The "Markdown Content Added" change description is shown for new
		// markdown artifacts.
		await expect(
			page.getByText('Markdown Content Added').first()
		).toBeVisible();

		// The Differences section renders a diff entry per shown artifact.
		const entries = page.locator('osee-markdown-diff-entry');
		expect(await entries.count()).toBeGreaterThan(0);
	});

	test('should enable export when results are present', async ({ page }) => {
		await page.goto(
			`/ple/markdown-change-report?branchId=${branchId}&branchType=working`
		);
		await expect(page.getByText(BRANCH)).toBeVisible({ timeout: 15000 });
		await expect(page.getByText(/[1-9]\d* result\(s\)/)).toBeVisible();

		// With results present the export button is enabled and opens a menu
		// offering Markdown and HTML. (Locate by the download icon since its
		// accessible name comes from a tooltip.)
		const exportButton = page.locator(
			'button:has(mat-icon:text-is("download"))'
		);
		await expect(exportButton).toBeEnabled();
		await exportButton.click();
		await expect(
			page.getByRole('menuitem', { name: 'Export as Markdown (.md)' })
		).toBeVisible();
		await expect(
			page.getByRole('menuitem', { name: 'Export as HTML (.html)' })
		).toBeVisible();
		await page.keyboard.press('Escape');
	});

	test('should download a Markdown export file', async ({ page }) => {
		await page.goto(
			`/ple/markdown-change-report?branchId=${branchId}&branchType=working`
		);
		await expect(page.getByText(BRANCH)).toBeVisible({ timeout: 15000 });
		await expect(page.getByText(/[1-9]\d* result\(s\)/)).toBeVisible();

		await page.locator('button:has(mat-icon:text-is("download"))').click();

		// Clicking the Markdown menu item generates a client-side blob and
		// triggers a browser download.
		const [download] = await Promise.all([
			page.waitForEvent('download'),
			page
				.getByRole('menuitem', { name: 'Export as Markdown (.md)' })
				.click(),
		]);

		// The suggested filename follows markdown-change-report-<branch>.md.
		const filename = download.suggestedFilename();
		expect(filename).toMatch(/^markdown-change-report-.*\.md$/);

		// The downloaded report has content, including the report heading and
		// the created artifacts.
		const stream = await download.createReadStream();
		const chunks: Buffer[] = [];
		for await (const chunk of stream) {
			chunks.push(chunk as Buffer);
		}
		const content = Buffer.concat(chunks).toString('utf-8');
		expect(content).toContain('# Markdown Change Report');
		expect(content).toContain('MD Report Artifact One');
	});

	test('should filter the markdown changes by text', async ({ page }) => {
		await page.goto(
			`/ple/markdown-change-report?branchId=${branchId}&branchType=working`
		);
		await expect(page.getByText(BRANCH)).toBeVisible({ timeout: 15000 });

		const filter = page.getByPlaceholder(
			'Filter by name, ID, or change type...'
		);
		await filter.fill('MD Report Artifact One');

		await expect(
			page.getByText('MD Report Artifact One').first()
		).toBeVisible();
		await expect(page.getByText('MD Report Artifact Two')).toHaveCount(0);

		// Clear button's name comes from a tooltip; locate it by its close icon
		// within the markdown-diff component.
		await page
			.locator('osee-markdown-diff button:has(mat-icon:text-is("close"))')
			.click();
		await expect(filter).toHaveValue('');
		await expect(
			page.getByText('MD Report Artifact Two').first()
		).toBeVisible();
	});

	test('should render a paginator with the default page size', async ({
		page,
	}) => {
		await page.goto(
			`/ple/markdown-change-report?branchId=${branchId}&branchType=working`
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
