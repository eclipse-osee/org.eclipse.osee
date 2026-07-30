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
import { Page, expect } from '@ngx-playwright/test';
import { selectBranch } from '../../../shared/branch-helpers';

export const createWorkingBranchFromPL = async (
	page: Page,
	branchName: string,
	plBranchName?: string,
	branchDescription?: string,
	screenshots?: boolean
) => {
	await selectBranch(page, 'Baseline', plBranchName || 'SAW Product Line');

	await page.getByRole('button', { name: 'Create Action' }).click();
	await page.getByLabel('Title').fill(branchName);
	await page.getByText('Actionable Item').click();
	await page.getByRole('combobox', { name: 'Actionable Item' }).fill('mim');

	await Promise.all([
		page.waitForResponse(
			(res) => res.url().includes('changeTypes') && res.status() === 200
		),
		page.getByText('SAW PL MIM').click(),
	]);

	await page.getByLabel('Description').fill(branchDescription || 'Test');
	await page.getByLabel('Change Type').locator('span').click();
	await page.getByRole('option', { name: 'Improvement' }).click();

	if (screenshots) {
		await page.screenshot({
			animations: 'disabled',
			path: 'screenshots/create-icd/create-action-dialog.png',
		});
	}

	await Promise.all([
		page.waitForResponse(
			(res) => res.url().includes('orcs/branches') && res.status() === 200
		),
		page.getByRole('button', { name: 'Create Action' }).click(),
	]);
};

export const enableEditMode = async (page: Page) => {
	await page.getByText('account_circle').click();
	await page.getByRole('menuitem', { name: 'Settings' }).click();

	const editCheckbox = page.getByLabel('Edit Mode');
	await editCheckbox.waitFor({ state: 'visible' });
	// Wait for the checkbox to become interactive
	await expect(editCheckbox).toBeEnabled({ timeout: 10000 });
	const isChecked = await editCheckbox.isChecked();
	if (!isChecked) {
		await editCheckbox.check();
	}

	await page.getByRole('button', { name: 'Ok' }).click();
};
