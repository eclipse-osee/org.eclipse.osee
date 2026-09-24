/*********************************************************************
 * Copyright (c) 2026 Boeing
 *
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 *
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     Boeing - initial API and implementation
 **********************************************************************/
import { test, expect, Page } from '@ngx-playwright/test';
import {
	DEMO_USERS,
	newUserPage,
	createWorkflowViaUi,
	openWorkflow,
} from '../utils/helpers';
import { waitForNetworkIdleIgnoringSse } from '../../../shared/sse-helpers';

/**
 * Real-time (SSE) behavior for the ACTRA workflow editor with two distinct demo
 * users against the live server. Covers presence on the workflow context and the
 * cross-user propagation paths the editor wires up: an attribute edit, a boolean
 * toggle (the emit-only-changed save regression), and a state transition. The
 * workflow editor refetches `/ats/teamwf/details/<id>` when it receives an
 * `attribute_modified` SSE event for the workflow artifact (ATS work items live on
 * the Common branch) — edits and transitions both flow through that path — so these
 * exercise the editor's own refetch wiring end to end.
 *
 * The reversible behaviors (presence, attribute edit, boolean toggle, state transition)
 * run as labeled steps in one test over a single shared workflow (restoring what they
 * change). Working-branch creation lives in a separate describe with its own workflow,
 * because it needs a work definition that offers branch creation and the created branch
 * is not cleanly reversible.
 */

let workflowId: string;

/** First editable text attribute in the workflow's state panels. */
function firstAttributeTextarea(page: Page) {
	return page.locator('osee-attributes-editor textarea').first();
}

/**
 * The boolean attribute control in a state panel, located by its accessible name.
 * A Material `mat-select` exposes `role="combobox"` and takes its accessible name
 * from the field label (the `ats.` prefix is stripped by the attribute-name-trim
 * pipe), so `getByRole('combobox', { name })` matches the OSEE pattern for selects.
 */
function booleanSelect(page: Page, label: string) {
	return page.getByRole('combobox', { name: label });
}

/** Reads the displayed value ('true' | 'false' | '') of a boolean select. */
async function booleanValue(page: Page, label: string): Promise<string> {
	return (await booleanSelect(page, label).textContent())?.trim() ?? '';
}

/** Opens a boolean select and picks the given option, waiting for the overlay to close. */
async function setBoolean(page: Page, label: string, value: 'true' | 'false') {
	await booleanSelect(page, label).click();
	const listbox = page.getByRole('listbox');
	await expect(listbox).toBeVisible();
	await listbox.getByRole('option', { name: value, exact: true }).click();
	await expect(listbox).toHaveCount(0);
}

async function saveWorkflow(page: Page) {
	await Promise.all([
		page.waitForResponse(
			(res) => res.url().includes('orcs/txs') && res.status() === 200
		),
		page.getByRole('button', { name: 'Save Changes' }).click(),
	]);
}

/**
 * Clicks Save and returns the parsed `orcs/txs` request body so a test can assert
 * exactly which attributes were sent (the emit-only-changed regression is about
 * the payload containing only the changed attribute, not every non-empty one).
 */
async function saveWorkflowAndCaptureTx(page: Page): Promise<unknown> {
	const [response] = await Promise.all([
		page.waitForResponse(
			(res) => res.url().includes('orcs/txs') && res.status() === 200
		),
		page.getByRole('button', { name: 'Save Changes' }).click(),
	]);
	return response.request().postDataJSON();
}

/**
 * The action dropdown's state button. Its visible text is the current state name,
 * which is also its accessible name. Scoped to the `osee-action-dropdown` region
 * because the current state also names a state expansion panel (whose header has
 * role button) — scoping the accessible locator to the region disambiguates the
 * two per the OSEE guidance for shared names across page regions.
 */
function stateButton(page: Page, currentState: string) {
	return page
		.locator('osee-action-dropdown')
		.getByRole('button', { name: currentState });
}

/**
 * Transitions the workflow from `fromState` to `toState` via the action dropdown
 * menu and waits for the transition to commit. Opens the menu from the current
 * state button; the menu item reads "Transition to <state>".
 */
async function transitionTo(page: Page, fromState: string, toState: string) {
	await stateButton(page, fromState).click();
	await Promise.all([
		page.waitForResponse(
			(res) =>
				res.url().includes('/ats/action/transition') &&
				!res.url().includes('transitionValidate') &&
				res.status() === 200
		),
		page
			.getByRole('menuitem', { name: `Transition to ${toState}` })
			.click(),
	]);
}

test.describe('Actra workflow editor real-time (SSE, two users)', () => {
	// One workflow, two editors (Joe + Jason), opened once. The real-time behaviors
	// (presence, attribute edit, boolean toggle, state transition) all share that
	// state and each pays a real navigation cost, so they run as labeled steps in a
	// single test rather than separate tests (per the perf guidance: consolidate
	// shared-state steps to avoid re-paying context + navigation). Each mutating step
	// restores what it changed so the steps stay order-independent in intent.
	test('real-time behaviors propagate between two editors of one workflow', async ({
		browser,
	}) => {
		test.setTimeout(120000);
		const title = `E2E SSE Workflow ${Date.now()}`;
		const setupPage = await newUserPage(browser, DEMO_USERS.joe);
		workflowId = await createWorkflowViaUi(
			setupPage,
			setupPage.request,
			title
		);
		await setupPage.context().close();

		const joe = await newUserPage(browser, DEMO_USERS.joe);
		const jason = await newUserPage(browser, DEMO_USERS.jason);
		try {
			await openWorkflow(joe, workflowId);
			await openWorkflow(jason, workflowId);

			await test.step('presence shows each user the other viewer', async () => {
				// Presence context is `workflow/<id>` (globally unique, not branch-scoped),
				// so both users share it. The current user is filtered out, so an avatar
				// appears only because the other viewer is a DIFFERENT user. Avatars are
				// generic id-derived icons (not name initials), so assert the avatar circle
				// with its icon is shown rather than matching specific text.
				await expect(
					joe.locator('osee-presence-avatars mat-icon').first()
				).toBeVisible({ timeout: 20000 });
				await expect(
					jason.locator('osee-presence-avatars mat-icon').first()
				).toBeVisible({ timeout: 20000 });
			});

			await test.step('an attribute edit propagates to the other editor', async () => {
				const joeField = firstAttributeTextarea(joe);
				await expect(joeField).toBeVisible({ timeout: 20000 });
				const original = (await joeField.inputValue()) ?? '';
				const edited = `${original} [sse ${Date.now()}]`;
				try {
					// Joe edits the attribute and saves; the workflow commits on Common.
					await joeField.click();
					await joeField.fill(edited);
					await saveWorkflow(joe);

					// Jason (not dirty) receives attribute_modified over SSE -> the editor
					// refetches details and the field reflects Joe's value.
					await expect(firstAttributeTextarea(jason)).toHaveValue(
						edited,
						{ timeout: 20000 }
					);
				} finally {
					// Restore the original value so the workflow is left unchanged.
					const restore = firstAttributeTextarea(joe);
					await restore.click();
					await restore.fill(original);
					await saveWorkflow(joe);
				}
			});

			await test.step('toggling a boolean saves only that attribute and it does not revert', async () => {
				// Regression for the emit-only-changed save bug: previously the editor emitted
				// every non-empty attribute on any field change, so a lone boolean toggle sent a
				// transaction full of unchanged attributes and the boolean itself (a never-saved
				// type template with id "-1") was dropped by the set-mapping and reverted. The fix
				// emits only genuinely-changed attributes and routes new instances to `add`.
				//
				// "Validation Required" (ats.Validation Required) is the only boolean in this
				// workflow's state layout; it renders in the Endorse state panel.
				const boolLabel = 'Validation Required';
				await expect(booleanSelect(joe, boolLabel)).toBeVisible({
					timeout: 20000,
				});
				const original = await booleanValue(joe, boolLabel);
				const toggled = original === 'true' ? 'false' : 'true';
				try {
					await setBoolean(joe, boolLabel, toggled);

					// The transaction must carry the boolean's typeId and must NOT sweep in
					// the unchanged Description attribute (the original bug's tell).
					const body = await saveWorkflowAndCaptureTx(joe);
					const payload = JSON.stringify(body);
					expect(payload).toContain('1152921504606847146'); // ats.Validation Required
					expect(payload).not.toContain('1152921504606847196'); // ats.Description (unchanged)

					// After the save + refetch the toggled value sticks (does not revert).
					await expect(booleanSelect(joe, boolLabel)).toHaveText(
						toggled,
						{ timeout: 20000 }
					);

					// And it survives a full reload (the value was really persisted).
					await openWorkflow(joe, workflowId);
					await expect(booleanSelect(joe, boolLabel)).toHaveText(
						toggled,
						{ timeout: 20000 }
					);
				} finally {
					// Restore the original boolean value. Only meaningful for a concrete true/false.
					if (
						(original === 'true' || original === 'false') &&
						(await booleanValue(joe, boolLabel)) !== original
					) {
						await setBoolean(joe, boolLabel, original);
						await saveWorkflow(joe);
					}
				}
			});

			await test.step('a state transition propagates to the other editor', async () => {
				// A transition writes the workflow artifact's state attributes on Common, which
				// emits attribute_modified over SSE. The acting editor refetches off its own
				// (local) emit; the other editor refetches off the remote SSE event. Both reflect
				// the new state. The workflow starts in Endorse and can transition to Analyze.
				await expect(stateButton(joe, 'Endorse')).toBeVisible({
					timeout: 20000,
				});
				await expect(stateButton(jason, 'Endorse')).toBeVisible({
					timeout: 20000,
				});
				let transitioned = false;
				try {
					await transitionTo(joe, 'Endorse', 'Analyze');
					transitioned = true;

					// Acting editor reflects the new state (its own refetch).
					await expect(stateButton(joe, 'Analyze')).toBeVisible({
						timeout: 20000,
					});
					// Other editor reflects it via the remote attribute_modified SSE.
					await expect(stateButton(jason, 'Analyze')).toBeVisible({
						timeout: 20000,
					});
				} finally {
					// Transition back so the workflow is left in its original state.
					if (transitioned) {
						await transitionTo(joe, 'Analyze', 'Endorse');
						await expect(stateButton(joe, 'Endorse')).toBeVisible({
							timeout: 20000,
						});
					}
				}
			});
		} finally {
			await joe.context().close();
			await jason.context().close();
		}
	});
});

/**
 * Working-branch creation propagation, in its own describe because it needs a workflow
 * whose work definition offers branch creation. Branch creation is only available in a
 * state with a Commit Manager widget (the server reports it as `committable`); the
 * default demo workflow (CIS Code) has no such state, whereas a "SAW Systems" workflow
 * does (its Implement state) and can transition there directly from its start state. The
 * created branch is not cleanly reversible, so this workflow is dedicated to this test.
 */
test.describe('Actra workflow editor branch creation (SSE, two users)', () => {
	let branchWorkflowId: string;

	test.beforeAll(async ({ browser }) => {
		test.setTimeout(90000);
		const title = `E2E SSE ARB Workflow ${Date.now()}`;
		const page = await newUserPage(browser, DEMO_USERS.joe);
		branchWorkflowId = await createWorkflowViaUi(
			page,
			page.request,
			title,
			{ workType: 'Systems', actionableItem: 'SAW Systems' }
		);
		await page.context().close();
	});

	test('a working-branch creation by one user propagates to another editor', async ({
		browser,
	}) => {
		// Branch creation writes only the branch row (its associated_art_id points at this
		// workflow); it does not touch the workflow artifact on Common. The editor keys the
		// refetch on a branch `created` event whose associatedArtifactId is this workflow,
		// gated on the workflow currently having no branch. Both editors are open on the SAME
		// workflow, so Joe creating the branch drops the "Create Branch" button in his own
		// editor (its local branch-created emit) and in Jason's (the branch-created SSE).
		test.setTimeout(90000);
		const joe = await newUserPage(browser, DEMO_USERS.joe);
		const jason = await newUserPage(browser, DEMO_USERS.jason);

		try {
			await openWorkflow(joe, branchWorkflowId);

			// The Create Branch button only appears in the committable Implement state; a
			// SAW Systems workflow can transition there directly from its start state. Drive
			// Joe there, then open Jason (loads already in Implement, branchless).
			await expect(stateButton(joe, 'Endorse')).toBeVisible({
				timeout: 20000,
			});
			await transitionTo(joe, 'Endorse', 'Implement');
			await expect(stateButton(joe, 'Implement')).toBeVisible({
				timeout: 20000,
			});
			await openWorkflow(jason, branchWorkflowId);

			// Scope the Create Branch button to the workflow editor's state-actions region.
			// The artifact-explorer tab that Create Branch opens ALSO has a "Create Branch"
			// button (Branch Management), so an unscoped getByRole could match that instead —
			// scoping to osee-actra-workflow-editor keeps us on the workflow editor's button.
			const joeCreate = joe
				.locator('osee-actra-workflow-editor')
				.getByRole('button', { name: 'Create Branch' });
			const jasonCreate = jason
				.locator('osee-actra-workflow-editor')
				.getByRole('button', { name: 'Create Branch' });

			// Precondition: in Implement, neither editor has a working branch yet.
			await expect(joeCreate).toBeEnabled({ timeout: 20000 });
			await expect(jasonCreate).toBeVisible({ timeout: 20000 });

			// Both editors must finish their in-flight loads before Joe clicks: the transition
			// triggers a details refetch on Joe, and Jason just opened — clicking while either
			// is still fetching means that editor isn't fully wired into the SSE mesh yet and
			// misses the branch-created event this test asserts propagates. Plain
			// `networkidle` can't be used on an SSE page (the stream never lets it settle), so
			// use the shared SSE-safe idle wait that ignores the real-time stream.
			await Promise.all([
				waitForNetworkIdleIgnoringSse(joe),
				waitForNetworkIdleIgnoringSse(jason),
			]);

			// Click Create Branch; require the create POST + the artifact-explorer popup it
			// opens (window.open) to prove the click drove the real handler. Close the popup in
			// the background so it doesn't perturb the SSE leader on the assertion path.
			const [, popup] = await Promise.all([
				joe.waitForResponse(
					(res) =>
						res.url().includes('/ats/config/branch') &&
						res.request().method() === 'POST' &&
						res.status() === 200
				),
				joe.context().waitForEvent('page'),
				joeCreate.click(),
			]);
			await expect(popup).toHaveURL(/ple\/artifact\/explorer/, {
				timeout: 20000,
			});
			void popup.close().catch(() => undefined);

			// Cross-user propagation (the behavior under test): once the branch is created,
			// the branch `created` event propagates over SSE (matched by the branch's
			// associated artifact) and both editors replace "Create Branch" with the branch-
			// management actions (Open Commit Manager) — the acting editor via its local emit,
			// the other user's via the SSE relay — without the other user acting.
			await expect(
				jason.getByRole('button', { name: 'Open Commit Manager' })
			).toBeVisible({ timeout: 30000 });
			await expect(jasonCreate).toHaveCount(0);
			await expect(
				joe.getByRole('button', { name: 'Open Commit Manager' })
			).toBeVisible({ timeout: 30000 });
		} finally {
			await joe.context().close();
			await jason.context().close();
		}
	});
});
