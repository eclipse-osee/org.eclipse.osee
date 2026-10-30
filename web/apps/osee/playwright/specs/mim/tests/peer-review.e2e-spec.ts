/*********************************************************************
 * Copyright (c) 2024 Boeing
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
import { createWorkingBranchFromPL, enableEditMode } from '../utils/helpers';
import { ATTRIBUTETYPEIDENUM } from '@osee/attributes/constants';
import { APP_BASE } from '../../../shared/test-config';
import { selectBranch } from '../../../shared/branch-helpers';
import { openSelectAndChooseOption } from '../../../shared/select-helpers';
import {
	getTransitionFailureReasons,
	transitionResponse,
} from '@osee/shared/types';

test.describe.configure({ mode: 'serial' });

test('create working branches', async ({ page }) => {
	// This test creates three working branches and edits each before adding an element, so the
	// default 45s test timeout is too tight under CI load (the flow reached the final dropdown with
	// no budget left). Raise the ceiling and bound each action so a slow step fails at its own line.
	test.setTimeout(120000);
	page.setDefaultTimeout(20000);
	await page.goto('/ple');

	// Commit MIM Demo branch to create baseline
	await page.getByRole('link', { name: 'MIM' }).click();
	await page.getByRole('link', { name: 'Connections' }).click();

	// Create first working branch
	await createWorkingBranchFromPL(page, 'Edit Message Description');
	await enableEditMode(page);
	await page.getByText('Connection A-B', { exact: true }).click();
	const MessageDescriptionTextbox = page
		.getByTestId('message-table-row-' + 'Message 1')
		.getByTestId('msg-field-description')
		//TODO: review if we still need this inner styling?
		.getByTestId('inner-styling')
		.getByTestId('form-' + ATTRIBUTETYPEIDENUM.DESCRIPTION)
		.getByTestId(ATTRIBUTETYPEIDENUM.DESCRIPTION)
		.getByRole('textbox');
	await MessageDescriptionTextbox.click();
	await MessageDescriptionTextbox.fill('This is the first message');

	await Promise.all([
		page.waitForResponse(
			(res) =>
				res.url() === `${APP_BASE}/orcs/txs` && res.status() === 200
		),
		// Blur via evaluate() rather than keyboard Tab: in headless mode Tab does not reliably move
		// focus off the field, so the blur-save may never fire and the txs POST never comes (the
		// source of this test's flakiness). blur() commits the focus-lost save deterministically.
		MessageDescriptionTextbox.evaluate((el: HTMLElement) => el.blur()),
	]);

	await page.getByRole('link', { name: 'working' }).click();

	// Create second working branch
	await createWorkingBranchFromPL(page, 'Edit Submessage Description');
	await enableEditMode(page);
	await page.getByText('Connection A-B', { exact: true }).click();
	await page.getByTestId('expand-message-btn-' + 'Message 1').click();
	const SubmsgDescriptionTextbox = page
		.getByTestId('sub-message-table-row-' + 'Submessage 1')
		.getByTestId('sub-msg-field-description')
		.getByTestId('form-' + ATTRIBUTETYPEIDENUM.DESCRIPTION)
		.getByTestId(ATTRIBUTETYPEIDENUM.DESCRIPTION)
		.getByRole('textbox');
	await SubmsgDescriptionTextbox.click();
	await SubmsgDescriptionTextbox.fill('This is a new description');

	await Promise.all([
		page.waitForResponse(
			(res) =>
				res.url() === `${APP_BASE}/orcs/txs` && res.status() === 200
		),
		// Blur via evaluate() rather than keyboard Tab (see above): deterministic focus-lost save.
		SubmsgDescriptionTextbox.evaluate((el: HTMLElement) => el.blur()),
	]);

	await page.getByRole('link', { name: 'working' }).click();

	// Create third working branch
	await createWorkingBranchFromPL(page, 'Add an Element');
	await enableEditMode(page);
	await page.locator('rect').nth(1).click();
	await page.getByText('Connection A-B', { exact: true }).click();
	await page.getByTestId('expand-message-btn-' + 'Message 1').click();
	await page.getByTestId('submessage-details-btn' + 'Submessage 1').click();
	await page.getByTestId('structure-table-expand-button').click();
	await page.getByRole('button', { name: 'Add Element to:' }).click();
	await page.getByRole('menuitem', { name: 'Structure 1' }).click();
	await page.getByRole('button', { name: 'Create new Element' }).click();
	const elementName = page.getByLabel('Name');
	await expect(elementName).toBeVisible();
	await elementName.fill('New Element');

	// Open the Platform Type autocomplete and load its options. A bare click focuses the input but
	// does not reliably fire the type-ahead query (the options fetch is driven by the input value
	// stream), so the panel can stay empty. Typing a filter value drives that stream, opens the
	// panel, and narrows to the option we want. Then assert the option before clicking it.
	const platformTypeCombobox = page
		.getByLabel('2Define element')
		.getByRole('combobox', { name: 'Platform Type' });
	await expect(platformTypeCombobox).toBeVisible();
	// Type per-character (not fill): the options come from a debounced search on the input's
	// valueChanges; fill emits a single coalesced event the debounce can mis-time so the query never
	// fires and the listbox stays empty. pressSequentially drives valueChanges as the control expects.
	await platformTypeCombobox.click({ force: true });
	await platformTypeCombobox.fill('');
	await platformTypeCombobox.pressSequentially('Float');
	const floatOption = page
		.locator('mat-option')
		.filter({ hasText: 'Float' })
		.first();
	await expect(floatOption).toBeVisible({ timeout: 60000 });
	await floatOption.click();
	// Next is gated on the form being valid (Platform Type required); assert enabled before click.
	const defineElementNext = page.getByRole('button', { name: 'Next' });
	await expect(defineElementNext).toBeEnabled();
	await defineElementNext.click();

	const submitBtn = page.getByTestId('submit-btn');
	await expect(submitBtn).toBeEnabled();
	await Promise.all([
		page.waitForResponse(
			(res) => res.url().includes('structures') && res.status() === 200
		),
		submitBtn.click(),
	]);
});

test('peer review branch', async ({ page }) => {
	await page.setViewportSize({ width: 1300, height: 800 });
	await page.goto('/ple');
	await page.getByRole('link', { name: 'MIM' }).click();
	await page.getByRole('link', { name: 'Connections' }).click();
	await selectBranch(page, 'Baseline', 'SAW Product Line');

	await page.screenshot({
		animations: 'disabled',
		path: 'screenshots/peer-review/peer-review-button.png',
		clip: { x: 0, y: 0, width: 1200, height: 375 },
	});

	await page.getByRole('button', { name: 'Peer Review' }).click();
	await page.getByRole('button', { name: 'Create Peer Review' }).click();
	await page.getByLabel('Title').fill('MIM Peer Review');
	await page.getByLabel('Actionable Item').click();
	await page.getByRole('combobox', { name: 'Actionable Item' }).fill('mim');
	// Assert the option rendered before clicking (dropdown options arrive async after the filter).
	const aiOption = page.getByRole('option', { name: 'SAW PL MIM' });
	await expect(aiOption).toBeVisible();
	await aiOption.click();
	await page.getByLabel('Description').fill('Peer review');
	const changeTypeOption = page.getByRole('option', { name: 'Improvement' });
	await openSelectAndChooseOption(
		page.getByRole('combobox', { name: 'Change Type', exact: true }),
		changeTypeOption,
		{ optionTimeout: 5000 }
	);

	let requestPromise = page.waitForResponse((response) =>
		response.url().startsWith(`${APP_BASE}/ats/ple/branches/pr`)
	);
	await page.getByRole('button', { name: 'Create Action' }).click();
	let response = await requestPromise;
	expect(response.status()).toBe(200);

	await page
		.getByRole('option', { name: 'Edit Message Description' })
		.locator('div')
		.first()
		.click();
	await page
		.getByRole('option', { name: 'Edit Submessage' })
		.locator('div')
		.first()
		.click();

	await page.screenshot({
		animations: 'disabled',
		path: 'screenshots/peer-review/peer-review-added-selections.png',
	});

	requestPromise = page.waitForResponse((response) =>
		response.url().startsWith(`${APP_BASE}/ats/ple/branches/pr`)
	);
	await page.getByRole('button', { name: 'Apply Selected' }).click();
	response = await requestPromise;
	expect(response.status()).toBe(200);

	await page.screenshot({
		animations: 'disabled',
		path: 'screenshots/peer-review/peer-review-applied.png',
	});

	await page
		.getByRole('option', { name: 'Edit Submessage' })
		.locator('span')
		.first()
		.click();
	await page
		.getByRole('option', { name: 'Add an Element' })
		.locator('span')
		.first()
		.click();

	await page.screenshot({
		animations: 'disabled',
		path: 'screenshots/peer-review/peer-review-add-remove.png',
	});

	await page.getByRole('button', { name: 'Apply Selected' }).click();
	await page.getByTestId('pr-dialog-close').click();
});

test('commit branches', async ({ page }) => {
	await page.setViewportSize({ width: 1300, height: 800 });
	await page.goto('/ple');
	await page.getByRole('link', { name: 'MIM' }).click();
	await page.getByRole('link', { name: 'Connections' }).click();
	await selectBranch(page, 'Baseline', 'SAW Product Line');

	await page.getByRole('button', { name: 'Peer Review' }).click();

	await page.getByText('Select a Peer Review Branch').click();
	const peerReviewOption = page.getByRole('option', {
		name: 'MIM Peer Review',
	});
	await expect(peerReviewOption).toBeVisible();
	await peerReviewOption.click();
	const closePeerReview = page.getByRole('button', {
		name: 'Close Peer Review',
	});
	await expect(closePeerReview).toBeDisabled();

	for (const title of ['Edit Message Description', 'Add an Element']) {
		await test.step(`Commit ${title}`, async () => {
			const branchRow = page
				.getByRole('listbox')
				.locator('div')
				.filter({ hasText: title });
			const actionButton = branchRow.getByRole('button');
			await expect(actionButton).toBeEnabled();
			await actionButton.click();
			const inWork = branchRow.getByRole('button', {
				name: 'In Work',
				exact: true,
			});
			await expect(inWork).toBeVisible();
			await inWork.click();
			const transition = page.getByRole('menuitem', {
				name: 'Transition to Review',
			});
			await expect(transition).toBeEnabled();
			await transition.click();
			const review = branchRow.getByRole('button', {
				name: 'Review',
				exact: true,
			});
			await expect(review).toBeVisible();
			await review.click();
			const commit = page.getByRole('menuitem', {
				name: 'Commit Branch',
			});
			await expect(commit).toBeEnabled();
			const completedResponse = page.waitForResponse(
				(response) =>
					response.url() === `${APP_BASE}/ats/action/transition` &&
					response.request().method() === 'POST' &&
					response.request().postDataJSON().toStateName ===
						'Completed'
			);
			await commit.click();
			const response = await completedResponse;
			expect(response.status()).toBe(200);
			const completion: transitionResponse = await response.json();
			expect(getTransitionFailureReasons(completion)).toEqual([]);
			expect(completion.workItemIds).toEqual([
				expect.objectContaining({ name: title }),
			]);

			const committed = branchRow.getByRole('button', {
				name: 'Committed',
				exact: true,
			});
			await expect(committed).toBeVisible();
			await expect(committed).toBeDisabled();
		});

		if (title === 'Edit Message Description') {
			await expect(closePeerReview).toBeDisabled();
			await page.screenshot({
				animations: 'disabled',
				path: 'screenshots/peer-review/peer-review-committed.png',
			});
		}
	}

	await expect(closePeerReview).toBeEnabled();
	await closePeerReview.click();
	await page.getByRole('button', { name: 'Ok' }).click();
});
