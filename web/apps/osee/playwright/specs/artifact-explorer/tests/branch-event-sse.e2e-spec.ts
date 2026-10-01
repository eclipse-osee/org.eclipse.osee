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
	DEMO_USERS,
	newUserPage,
	createBranchViaApi,
	purgeBranchViaApi,
	navigateToArtifactExplorer,
	selectBranchType,
} from '../utils/helpers';

/**
 * A branch metadata change made by one user propagates to another user's branch
 * list via the SSE branch-event path (BranchChangeEventService.listAffectingChanges$,
 * merged into the branch-list reload) -- without a manual refresh. We observe the
 * `created` list-affecting event across two distinct demo users (each with its own
 * SSE connection).
 */

test.describe('Artifact explorer branch events (SSE, two users)', () => {
	test.describe.configure({ mode: 'parallel' });

	let createdBranchId: string | undefined;

	test.afterEach(async ({ request }) => {
		if (createdBranchId) {
			await purgeBranchViaApi(request, createdBranchId);
			createdBranchId = undefined;
		}
	});

	test('a branch created by one user appears in another user branch list without refresh', async ({
		browser,
		request,
	}) => {
		// Unique name so the assertion is unambiguous and re-runs stay clean.
		const branchName = `AE SSE Branch Event ${Date.now()}`;

		const jason = await newUserPage(browser, DEMO_USERS.jason);
		try {
			// Jason opens the working-branch selector filtered to the not-yet-
			// existing branch name -- zero matches so far.
			await navigateToArtifactExplorer(jason);
			await selectBranchType(jason, 'Working');
			const branchCombobox = jason.getByRole('combobox', {
				name: 'Select a Branch',
			});
			await expect(branchCombobox).toBeEnabled({ timeout: 5000 });
			await branchCombobox.click({ force: true });
			await branchCombobox.fill(branchName);
			await expect(
				jason.locator('mat-option').filter({ hasText: branchName })
			).toHaveCount(0);

			// A different user (the API uses Joe's auth) creates the working branch,
			// firing the server `created` branch event -> S2S/SSE -> Jason's list.
			createdBranchId = await createBranchViaApi(request, branchName);

			// Jason's branch list reloads reactively (listAffectingChanges$) and the
			// new branch surfaces as a matching option -- no manual refresh.
			await expect(
				jason
					.locator('mat-option')
					.filter({ hasText: branchName })
					.first()
			).toBeVisible({ timeout: 20000 });
		} finally {
			await jason.context().close();
		}
	});
});
