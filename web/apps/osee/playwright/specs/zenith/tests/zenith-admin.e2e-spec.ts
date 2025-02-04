/*********************************************************************
 * Copyright (c) 2025 Boeing
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

test('test baseline branch', async ({ page }) => {
	await page.goto('http://localhost:4200/ci/admin');
	await page.getByLabel('Product Line').check();
	await page.getByText('Select a Branch').click();
	await page.getByText('SAW Product Line').click();

	await expect(page.getByLabel('Number of results to keep')).toBeDisabled();

	await page.getByRole('button', { name: 'CI Sets' }).click();
	await expect(page.getByTestId('add-ci-set-button')).toBeDisabled();

	await page.getByRole('button', { name: 'Subsystems' }).click();
	await expect(
		page.getByRole('button', { name: 'Add New Subsystems' })
	).toBeDisabled();

	await page.getByRole('button', { name: 'Teams' }).click();
	await expect(page.getByTestId('add-team-button')).toBeDisabled();
});

test('test working branch', async ({ page }) => {
	await page.goto('http://localhost:4200/ci/admin');
	await page.getByLabel('Product Line').check();
	await page.getByText('Select a Branch').click();
	await page.getByText('SAW Product Line').click();

	await page.getByRole('button', { name: 'Create Action' }).click();
	await page.getByLabel('Title').fill('CI Admin Test');
	await page.getByText('Actionable Item').click();
	await page.getByRole('combobox', { name: 'Actionable Item' }).fill('mim');

	await Promise.all([
		page.waitForResponse(
			(res) => res.url().includes('changeTypes') && res.status() === 200
		),
		page.getByText('SAW PL MIM').click(),
	]);

	await page.getByLabel('Description').fill('Test');
	await page.getByLabel('Change Type').locator('span').click();
	await page.getByRole('option', { name: 'Improvement' }).click();

	await Promise.all([
		page.waitForResponse(
			(res) => res.url().includes('orcs/branches') && res.status() === 200
		),
		page.getByRole('button', { name: 'Create Action' }).click(),
	]);

	// Create a configuration artifact for this branch
	await expect(page.getByTestId('create-config-button')).toBeVisible();
	await expect(page.getByLabel('Number of results to keep')).toBeDisabled();
	await page.getByTestId('create-config-button').click();
	await expect(page.getByLabel('Number of results to keep')).toBeEnabled();
	await expect(page.getByTestId('create-config-button')).toBeHidden();

	await page.getByRole('button', { name: 'CI Sets' }).click();
	await expect(page.getByTestId('add-ci-set-button')).toBeEnabled();
	await page.getByTestId('add-ci-set-button').click();
	await page.getByLabel('Name').fill('Demo');
	await page.getByLabel('Not Active').click();
	await page.getByRole('button', { name: 'Ok' }).click();
	await expect(
		page.getByRole('cell', { name: 'Demo' }).locator('mat-form-field')
	).toBeVisible();

	await page.getByRole('button', { name: 'Subsystems' }).click();
	await expect(
		page.getByRole('button', { name: 'Add New Subsystems' })
	).toBeEnabled();
	await page.getByRole('button', { name: 'Add New Subsystems' }).click();
	await page.getByLabel('Name', { exact: true }).fill('Test Subsystem');
	await page.getByRole('button', { name: 'Save Changes' }).click();
	await expect(
		page.getByRole('button', { name: 'Test Subsystem' })
	).toBeVisible();

	await page.getByRole('button', { name: 'Teams' }).click();
	await expect(page.getByTestId('add-team-button')).toBeEnabled();
	await page.getByTestId('add-team-button').click();
	await page.getByLabel('Name', { exact: true }).fill('Test Team');
	await page.getByRole('button', { name: 'Ok' }).click();
	await expect(page.getByRole('cell', { name: 'Test Team' })).toBeVisible();
});
