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
import { test, expect, Browser, Page } from '@ngx-playwright/test';
import { APIRequestContext } from '@playwright/test';
import {
	DEMO_USERS,
	newUserPage,
	createBranchViaApi,
	purgeBranchViaApi,
	createArtifact,
	openBranch,
	searchAndOpenArtifact,
	switchEditorSection,
} from '../utils/helpers';

/**
 * Real-time (SSE) behavior for the artifact editor, exercised with two distinct
 * demo users against the live server: cross-user attribute propagation
 * (GET-on-notify), presence, and the conflict banner + resolution dialog. These
 * hit the real SSE socket and server broadcast path that the Vitest unit tests
 * stub out.
 *
 * Each test creates its OWN uniquely-named branch + artifact inside the test and
 * purges it after. There is no shared beforeAll fixture -- that is what makes
 * these safe to run in parallel: Playwright runs beforeAll once per worker, so a
 * shared-branch fixture would be duplicated across workers and make artifact
 * search ambiguous. Self-contained per-test fixtures avoid that entirely.
 */

function nameField(page: Page) {
	return page.locator('osee-focus-lost-input').first().getByRole('textbox');
}

/** Creates a unique branch with one artifact; returns branchId + artifact name. */
async function setupBranchWithArtifact(
	browser: Browser,
	request: APIRequestContext,
	label: string
): Promise<{ branchId: string; branchName: string; artifact: string }> {
	const suffix = `${Date.now()}-${Math.floor(Math.random() * 1e6)}`;
	const branchName = `AE SSE ${label} ${suffix}`;
	const artifact = `AE SSE ${label} Art ${suffix}`;
	const branchId = await createBranchViaApi(request, branchName);

	const page = await newUserPage(browser, DEMO_USERS.joe);
	await openBranch(page, branchName);
	await createArtifact(page, 'System Requirements - Markdown', artifact);
	await page.context().close();

	return { branchId, branchName, artifact };
}

async function openArtifact(page: Page, branchName: string, artifact: string) {
	await openBranch(page, branchName);
	await searchAndOpenArtifact(page, artifact);
	await switchEditorSection(page, 'Attributes');
	await expect(page.getByLabel('Name')).toBeVisible({ timeout: 15000 });
}

/** Fills the Name field and waits for the auto-save transaction to commit. */
async function editNameAndSave(page: Page, value: string) {
	const field = nameField(page);
	await field.click();
	await field.fill(value);
	await Promise.all([
		page.waitForResponse(
			(res) => res.url().includes('orcs/txs') && res.status() === 200
		),
		field.evaluate((el) => el.blur()),
	]);
}

test.describe('Artifact editor real-time (SSE, two users)', () => {
	// Self-contained tests (own branch+artifact each) -> safe to run in parallel.
	test.describe.configure({ mode: 'parallel' });

	test('an attribute edit by one user propagates to another without a manual refresh', async ({
		browser,
		request,
	}) => {
		const { branchId, branchName, artifact } =
			await setupBranchWithArtifact(browser, request, 'Propagation');
		const joe = await newUserPage(browser, DEMO_USERS.joe);
		const jason = await newUserPage(browser, DEMO_USERS.jason);
		try {
			await openArtifact(joe, branchName, artifact);
			await openArtifact(jason, branchName, artifact);

			const renamed = artifact + ' Renamed';
			await editNameAndSave(joe, renamed);

			// Jason receives the change over SSE (GET-on-notify), no reload.
			await expect(
				jason
					.locator('osee-artifact-tab-group')
					.getByText(renamed)
					.first()
			).toBeVisible({ timeout: 20000 });
		} finally {
			await joe.context().close();
			await jason.context().close();
			await purgeBranchViaApi(request, branchId);
		}
	});

	test('presence shows each user the other viewer of the same artifact', async ({
		browser,
		request,
	}) => {
		// Presence is heartbeat-driven (eventually consistent) and each side is
		// asserted, so give the whole test a larger budget than the 30s default.
		test.setTimeout(60000);
		const { branchId, branchName, artifact } =
			await setupBranchWithArtifact(browser, request, 'Presence');
		const joe = await newUserPage(browser, DEMO_USERS.joe);
		const jason = await newUserPage(browser, DEMO_USERS.jason);
		try {
			await openArtifact(joe, branchName, artifact);
			await openArtifact(jason, branchName, artifact);

			// The current user is filtered out of presence, so an avatar appears
			// only because the other viewer is a DIFFERENT user. Avatars are generic
			// id-derived icons (not name initials), so a visible avatar circle is the
			// presence signal. A single retrying visibility assertion handles the
			// heartbeat latency. Assert both sides concurrently so one side's wait
			// doesn't serialize onto the other.
			await Promise.all([
				expect(
					joe.locator('osee-presence-avatars mat-icon').first()
				).toBeVisible({ timeout: 25000 }),
				expect(
					jason.locator('osee-presence-avatars mat-icon').first()
				).toBeVisible({ timeout: 25000 }),
			]);
		} finally {
			await joe.context().close();
			await jason.context().close();
			await purgeBranchViaApi(request, branchId);
		}
	});

	test('a concurrent edit raises the conflict banner and can be resolved via the dialog', async ({
		browser,
		request,
	}) => {
		const { branchId, branchName, artifact } =
			await setupBranchWithArtifact(browser, request, 'Conflict');
		const joe = await newUserPage(browser, DEMO_USERS.joe);
		const jason = await newUserPage(browser, DEMO_USERS.jason);
		try {
			await openArtifact(joe, branchName, artifact);
			await openArtifact(jason, branchName, artifact);

			const joeValue = artifact + ' - Joe saved';

			await test.step('remote change while dirty raises the banner', async () => {
				// Jason edits (dirty) and KEEPS focus so no auto-save-on-blur fires.
				const jasonName = nameField(jason);
				await jasonName.click();
				await jasonName.fill(artifact + ' - Jason unsaved');

				// Joe changes the SAME attribute to a DIFFERENT value and saves.
				await editNameAndSave(joe, joeValue);

				// Jason -- still dirty -- gets the remote change and the banner shows.
				await expect(
					jason.locator('osee-conflict-resolution-banner')
				).toBeVisible({ timeout: 20000 });
			});

			await test.step('resolving via the dialog converges to the server value', async () => {
				await jason
					.getByRole('button', { name: 'Resolve conflicts' })
					.click();

				const dialog = jason.locator(
					'osee-attribute-conflict-resolution-dialog'
				);
				await expect(dialog).toBeVisible({ timeout: 15000 });

				// Default resolution is "Take Server's" (take-theirs); apply it.
				await jason
					.getByRole('button', { name: /^Apply Resolution/ })
					.click();

				await expect(
					jason.locator('osee-conflict-resolution-banner')
				).toBeHidden({ timeout: 20000 });
				await expect(nameField(jason)).toHaveValue(joeValue, {
					timeout: 20000,
				});
			});
		} finally {
			await joe.context().close();
			await jason.context().close();
			await purgeBranchViaApi(request, branchId);
		}
	});
});
