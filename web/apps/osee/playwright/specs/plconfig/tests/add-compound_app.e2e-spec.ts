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

test('test', async ({ page }) => {
	await page.goto('/ple');
	await page
		.getByRole('link', { name: 'Product Line Configuration' })
		.click();
	await page.goto('/ple/plconfig?branchType=baseline');
	await selectBranch(page, 'Baseline', 'SAW PL Hardening Branch');
	await page.getByRole('button', { name: 'Create Action' }).click();
	await page.getByTestId('action-title').click();
	await page.getByTestId('action-title').fill('Test');
	await page
		.locator('div')
		.filter({ hasText: /^Actionable Item$/ })
		.nth(2)
		.click();
	await page.getByTestId('option-SAW PL ARB').click();
	await page
		.locator('div')
		.filter({ hasText: /^Description$/ })
		.nth(2)
		.click();
	await page.getByTestId('action-description').fill('Desc');
	await page
		.locator('div')
		.filter({ hasText: /^Change Type$/ })
		.nth(2)
		.click();
	await page.getByTestId('option-Improvement').click();
	await page.getByRole('button', { name: 'Create Action' }).click();
	await page.getByRole('button', { name: 'Edit Definitions' }).click();
	await page
		.getByRole('menuitem', { name: 'Change Compound Applicabities' })
		.click();
	await page
		.getByRole('menuitem', { name: 'Add Compound Applicability' })
		.click();
	await page
		.locator('div')
		.filter({ hasText: /^Select a Feature$/ })
		.nth(2)
		.click();
	await page
		.getByRole('option', { name: 'JHU_CONTROLLER', exact: true })
		.first()
		.click();
	await page
		.locator('div')
		.filter({ hasText: /^Select a Value$/ })
		.nth(2)
		.click();
	await page
		.getByRole('option', { name: 'Included', exact: true })
		.first()
		.click();
	await page
		.locator('div')
		.filter({ hasText: /^Select a Relationship$/ })
		.nth(2)
		.click();
	await page
		.getByRole('option', { name: 'AND', exact: true })
		.first()
		.click();
	await page
		.locator('div')
		.filter({ hasText: /^Select a Feature$/ })
		.nth(2)
		.click();
	await page
		.getByRole('option', { name: 'ROBOT_ARM_LIGHT', exact: true })
		.first()
		.click();
	await page
		.locator('div')
		.filter({ hasText: /^Select a Value$/ })
		.nth(2)
		.click();
	await page
		.getByRole('option', { name: 'Included', exact: true })
		.first()
		.click();
	await page.getByRole('button', { name: 'Confirm' }).click();

	// Drive the branch In Work -> Review -> (approve) -> Commit, waiting for each state control to
	// render before clicking the next. Each transition is an async server round-trip that
	// re-renders the action control, so a back-to-back click can fire before the re-render and be
	// silently dropped, stalling the flow. Gate each click on its own target (the per-element form
	// of "wait for state, not time").
	await page.getByRole('button', { name: 'In Work' }).click();
	await page.getByRole('menuitem', { name: 'Transition to Review' }).click();

	const reviewAfterTransition = page.getByRole('button', { name: 'Review' });
	await expect(reviewAfterTransition).toBeVisible({ timeout: 60000 });
	await reviewAfterTransition.click();
	const approveTransition = page.getByRole('menuitem', {
		name: 'Approve Transition to',
	});
	await expect(approveTransition).toBeEnabled({ timeout: 60000 });
	await approveTransition.click();

	const reviewAfterApprove = page.getByRole('button', { name: 'Review' });
	await expect(reviewAfterApprove).toBeVisible({ timeout: 60000 });
	await reviewAfterApprove.click();
	const commitBranch = page.getByRole('menuitem', { name: 'Commit Branch' });
	// Gate on enabled, not just visible: during the Review re-render the Commit Branch item can
	// render briefly disabled, and a click on a disabled menuitem is silently dropped.
	await expect(commitBranch).toBeEnabled({ timeout: 60000 });
	await commitBranch.click();

	// Committing is an async server round-trip; wait on the resulting config cell with a budget
	// that covers the commit load rather than the 5s default.
	await expect(
		page.getByRole('cell', { name: 'JHU_CONTROLLER = Included &' })
	).toBeVisible({ timeout: 60000 });
});
