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
import { test, expect, Page } from '@ngx-playwright/test';
import { selectBranch } from '../../../shared/branch-helpers';

/**
 * Regression coverage for the Zenith top-toolbar navigation.
 *
 * The nav buttons used to append a legacy branch path segment
 * (`/ci/allScripts/working/<id>`) to each routerLink. The CI dashboard routes
 * have no branch path params, so every button 404'd (redirect to `/404`,
 * which renders osee-page-not-found). The branch is now carried as query
 * params (`branchType`, `branchId`, plus the CI set as `set`). These tests
 * click each toolbar button and assert the target page actually opens with the
 * query-param scheme and is NOT redirected to the 404 page.
 */

// Every top-toolbar entry that uses the branch. Timeline/Admin/Import are
// covered separately (Import/Admin gate on CI_ADMIN); these are the links the
// bug report called out as 404'ing.
const navTargets: { label: string; path: string }[] = [
	{ label: 'Timeline', path: '/ci/timeline' },
	{ label: 'All Scripts', path: '/ci/allScripts' },
	{ label: 'Results', path: '/ci/results' },
	{ label: 'Dashboard', path: '/ci/dashboard' },
	{ label: 'Subsystems', path: '/ci/subsystems' },
	{ label: 'Batches', path: '/ci/batches' },
	{ label: 'Set Diffs', path: '/ci/diffs' },
];

/** Assert the page is on the expected CI route with the new branch query-param scheme and is not the 404 page. */
const expectPageOpened = async (page: Page, path: string) => {
	const url = new URL(page.url());

	// Landed on the intended route, not redirected to /404.
	expect(url.pathname, `expected to be on ${path}`).toContain(path);
	expect(url.pathname).not.toContain('/404');

	// Branch is carried as query params (the new scheme), not a legacy
	// `/working/<id>` path segment appended to the route.
	expect(url.searchParams.get('branchType')).toBe('baseline');
	expect(url.searchParams.get('branchId')).not.toBeNull();
	expect(url.pathname).not.toMatch(/\/(working|baseline)\/\d+/);

	// The page-not-found component is never rendered.
	await expect(
		page.getByText('The page you are looking for could not be found.')
	).toBeHidden();
};

test.describe('Zenith toolbar navigation', () => {
	test('every toolbar button opens its page with the branch query-param scheme', async ({
		page,
	}) => {
		// Start on a known CI page and select a branch. Selecting the branch is
		// what populates ?branchType=...&branchId=... via BranchRoutedUIService.
		await page.goto('/ci/allScripts');
		await selectBranch(page, 'Baseline', 'SAW Product Line');

		// The selection must have written branchType/branchId to the URL before
		// we start clicking — the nav links read it to build their query params.
		await expect
			.poll(() => new URL(page.url()).searchParams.get('branchType'))
			.toBe('baseline');
		await expect
			.poll(() => new URL(page.url()).searchParams.get('branchId'))
			.not.toBeNull();

		// Scope to the Zenith toolbar nav — the layout also renders a top-level
		// sidenav landmark, so an unscoped navigation role would be ambiguous.
		const nav = page.getByRole('navigation', { name: 'Zenith' });

		for (const { label, path } of navTargets) {
			await test.step(`navigate to ${label}`, async () => {
				const link = nav.getByRole('link', {
					name: label,
					exact: true,
				});
				await expect(link).toBeVisible();
				await link.click();
				await page.waitForURL(`**${path}**`);
				await expectPageOpened(page, path);
			});
		}
	});

	test('toolbar links target routes directly, with the branch as a query param (no legacy path segment)', async ({
		page,
	}) => {
		await page.goto('/ci/allScripts');
		await selectBranch(page, 'Baseline', 'SAW Product Line');
		await expect
			.poll(() => new URL(page.url()).searchParams.get('branchType'))
			.toBe('baseline');

		const nav = page.getByRole('navigation', { name: 'Zenith' });

		// The href itself must be the bare route with query params — not the old
		// `/ci/allScripts/working/<id>` form that caused the 404. Asserting the
		// rendered href guards the regression even without a click.
		for (const { label, path } of navTargets) {
			const href = await nav
				.getByRole('link', { name: label, exact: true })
				.getAttribute('href');
			expect(href, `${label} href`).not.toBeNull();
			expect(href!, `${label} href path`).toContain(path);
			expect(
				href!,
				`${label} should not use a legacy branch path segment`
			).not.toMatch(/\/(working|baseline)\/\d+/);
			expect(
				href!,
				`${label} should carry branchType query param`
			).toContain('branchType=baseline');
			expect(
				href!,
				`${label} should carry branchId query param`
			).toContain('branchId=');
		}
	});
});
