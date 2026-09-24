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
import { expect, Page } from '@ngx-playwright/test';
import { APIRequestContext } from '@playwright/test';
import { API_BASE, AUTH_HEADER } from '../../../shared/test-config';

// Re-export the shared two-user demo-auth helpers so actra specs use one source.
export { DEMO_USERS, newUserPage } from '../../artifact-explorer/utils/helpers';

/**
 * Creates a real ATS team workflow through the Create Action UI flow (the same
 * flow the MIM/Zenith/PLConfig specs use, proven to work against the demo DB),
 * then resolves the new workflow's artifact id by searching for its unique title.
 *
 * We drive the UI rather than raw REST because the demo server's create-action
 * REST response does not reliably return the created workflow id, whereas the UI
 * flow completes deterministically (it waits on the branch-create response) and
 * the workflow is then findable via the standard team-workflow search.
 *
 * @returns the created workflow's artifact id (for `/actra/workflow?id=<id>`)
 */
export const createWorkflowViaUi = async (
	page: Page,
	request: APIRequestContext,
	title: string,
	options?: { actionableItem?: string; workType?: string }
): Promise<string> => {
	// "CIS Code" is a stable demo AI whose workflow never offers branch creation
	// (no committable state); tests that need branch support pass a work type +
	// actionable item whose work definition has a committable state (e.g. Work Type
	// "Systems" + "SAW Systems", whose Implement state offers Create Branch).
	const actionableItem = options?.actionableItem ?? 'CIS Code';

	// Drive the dedicated Create Action page directly (the same form + createAction
	// service the MIM/Zenith/PLConfig specs use). This avoids the button's
	// branch-context gating in other views.
	await page.goto('/actra/action/create');
	await expect(page.getByLabel('Title')).toBeVisible({ timeout: 20000 });
	await page.getByLabel('Title').fill(title);

	// Some actionable items are only listed once a Work Type is chosen (e.g.
	// "Systems" for "SAW PL ARB"); select it first so the AI appears in the list.
	if (options?.workType) {
		await page.getByLabel('Work Type').fill(options.workType);
		await page
			.getByRole('option', { name: options.workType, exact: true })
			.first()
			.click();
	}

	// Pick the actionable item; selecting it loads its change types, then choose a
	// change type to satisfy the form.
	await page.getByText('Actionable Item').click();
	await page
		.getByRole('combobox', { name: 'Actionable Item' })
		.fill(actionableItem);
	await Promise.all([
		page.waitForResponse(
			(res) => res.url().includes('changeTypes') && res.status() === 200
		),
		page.getByRole('option', { name: actionableItem }).first().click(),
	]);

	await page.getByLabel('Description').fill('E2E SSE workflow');

	// Select the required mat-select fields one at a time, each via its listbox
	// overlay, waiting for the overlay to fully close before opening the next so
	// the selections don't race (a common cause of an ngModel not committing).
	const selectOption = async (testId: string, option: string) => {
		await page.getByTestId(testId).click();
		const listbox = page.getByRole('listbox');
		await expect(listbox).toBeVisible();
		await listbox.getByRole('option', { name: option }).first().click();
		await expect(listbox).toHaveCount(0);
	};
	// Pick the first available option for the required selects; the exact set of
	// change types varies by actionable item, so don't hardcode a specific one.
	const selectFirstOption = async (testId: string) => {
		await page.getByTestId(testId).click();
		const listbox = page.getByRole('listbox');
		await expect(listbox).toBeVisible();
		await listbox.getByRole('option').first().click();
		await expect(listbox).toHaveCount(0);
	};
	await selectOption('select-priority', 'Lowest Priority');
	await selectFirstOption('select-change-type');

	// If the actionable item's work type creates a branch by default, Targeted
	// Version becomes required. Select the first available version when present so
	// the form validates regardless of the chosen AI's work type.
	const targetedVersion = page.getByTestId('select-targeted-version');
	if (await targetedVersion.isVisible().catch(() => false)) {
		await targetedVersion.click();
		const listbox = page.getByRole('listbox');
		await expect(listbox).toBeVisible();
		const firstOption = listbox.getByRole('option').first();
		if (await firstOption.isVisible().catch(() => false)) {
			await firstOption.click();
			await expect(listbox).toHaveCount(0);
		} else {
			// No versions to choose; close the overlay.
			await page.keyboard.press('Escape');
			await expect(listbox).toHaveCount(0);
		}
	}

	// Wait for the form to become valid before submitting (rather than racing the
	// disabled->enabled transition). The submit button is the one inside the form.
	const submit = page.getByRole('button', { name: 'Create Action' });
	await expect(submit).toBeEnabled({ timeout: 15000 });

	// Submit. The action + team workflow is created server-side.
	await Promise.all([
		page.waitForResponse(
			(res) => res.url().includes('/ats/action') && res.status() === 200
		),
		submit.click(),
	]);

	// Resolve the workflow's artifact id via the standard team-workflow search
	// (poll briefly: the workflow may take a moment to be indexed after create).
	const deadline = Date.now() + 20000;
	while (Date.now() < deadline) {
		const res = await request.get(`${API_BASE}/ats/teamwf/search`, {
			params: { search: title },
			headers: { ...AUTH_HEADER, Accept: 'application/json' },
		});
		if (res.status() === 200) {
			const wfs = (await res.json()) as { id: string }[];
			if (wfs.length > 0 && wfs[0].id) {
				return wfs[0].id;
			}
		}
		await page.waitForTimeout(1000);
	}
	throw new Error(`Created workflow "${title}" was not found via search`);
};

/** Navigates to a workflow editor and waits for it to finish loading. */
export const openWorkflow = async (page: Page, workflowId: string) => {
	await page.goto(`/actra/workflow?id=${workflowId}`);
	await expect(page.locator('osee-actra-page-title')).toBeVisible({
		timeout: 20000,
	});
	await expect(page.getByText('Loading workflow...')).toHaveCount(0, {
		timeout: 20000,
	});
};
