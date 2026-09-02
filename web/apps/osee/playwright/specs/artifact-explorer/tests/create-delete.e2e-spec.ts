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
	expandArtifact,
} from '../utils/helpers';

const BRANCH = 'AE Create Delete Tests';
let branchId: string;

test.describe('Artifact Create & Delete', () => {
	test.describe.configure({ mode: 'serial' });

	test.beforeAll(async ({ browser, request }) => {
		branchId = await createBranchViaApi(request, BRANCH);
		const page = await browser.newPage();
		await openBranch(page, BRANCH);
		await createArtifact(
			page,
			'System Requirements - Markdown',
			'AE CD Parent',
			'Folder'
		);
		await page.close();
	});

	test.afterAll(async ({ request }) => {
		await purgeBranchViaApi(request, branchId);
	});

	test('should create a child artifact via context menu', async ({
		page,
	}) => {
		await openBranch(page, BRANCH);
		await expandArtifact(page, 'System Requirements - Markdown');
		await expect(page.getByText('AE CD Parent')).toBeVisible({
			timeout: 10000,
		});

		await page.getByText('AE CD Parent').click({ button: 'right' });
		await page
			.getByRole('menuitem', { name: 'Create Child Artifact' })
			.click();

		await page
			.getByRole('textbox', { name: 'Enter a Name' })
			.fill('AE CD Created');
		await page.getByRole('combobox', { name: 'Select a Type' }).click();
		await page
			.getByRole('combobox', { name: 'Select a Type' })
			.fill('Software Requirement - Markdown');
		await page
			.getByRole('option', {
				name: 'Software Requirement - Markdown',
			})
			.first()
			.click();

		await Promise.all([
			page.waitForResponse(
				(res) => res.url().includes('orcs/txs') && res.status() === 200
			),
			page.getByRole('button', { name: 'Create', exact: true }).click(),
		]);

		await expect(page.getByText('AE CD Created')).toBeVisible({
			timeout: 10000,
		});
	});

	test('should disable Create when artifact type is not selected from dropdown', async ({
		page,
	}) => {
		await openBranch(page, BRANCH);
		await expandArtifact(page, 'System Requirements - Markdown');
		await expect(page.getByText('AE CD Parent')).toBeVisible({
			timeout: 10000,
		});

		await page.getByText('AE CD Parent').click({ button: 'right' });
		await page
			.getByRole('menuitem', { name: 'Create Child Artifact' })
			.click();

		await page
			.getByRole('textbox', { name: 'Enter a Name' })
			.fill('Invalid Test');
		const typeInput = page.getByRole('combobox', {
			name: 'Select a Type',
		});
		await typeInput.click();
		await typeInput.fill('NonExistentType123');
		// Click away to blur without selecting from dropdown
		await page.getByRole('textbox', { name: 'Enter a Name' }).click();

		await expect(
			page.getByRole('button', { name: 'Create', exact: true })
		).toBeDisabled();

		await page.getByRole('button', { name: 'Cancel' }).click();
	});

	test('should delete an artifact via context menu', async ({ page }) => {
		await openBranch(page, BRANCH);
		await expandArtifact(page, 'System Requirements - Markdown');
		await expect(page.getByText('AE CD Parent')).toBeVisible({
			timeout: 10000,
		});
		await expandArtifact(page, 'AE CD Parent');
		await expect(page.getByText('AE CD Created')).toBeVisible({
			timeout: 10000,
		});

		await page.getByText('AE CD Created').click({ button: 'right' });
		await page.getByRole('menuitem', { name: 'Delete Artifact' }).click();

		await Promise.all([
			page.waitForResponse(
				(res) => res.url().includes('orcs/txs') && res.status() === 200
			),
			page.getByRole('button', { name: 'Delete' }).click(),
		]);

		await expect(page.getByText('AE CD Created')).not.toBeVisible({
			timeout: 10000,
		});
	});

	test('should prepopulate attribute editors with the type default value', async ({
		page,
	}) => {
		await openBranch(page, BRANCH);
		await expandArtifact(page, 'System Requirements - Markdown');
		await expect(page.getByText('AE CD Parent')).toBeVisible({
			timeout: 10000,
		});

		await page.getByText('AE CD Parent').click({ button: 'right' });
		await page
			.getByRole('menuitem', { name: 'Create Child Artifact' })
			.click();

		// Selecting Software Requirement - Markdown loads its attribute editors.
		const typeInput = page.getByRole('combobox', { name: 'Select a Type' });
		await typeInput.click();
		await typeInput.fill('Software Requirement - Markdown');
		await page
			.getByRole('option', { name: 'Software Requirement - Markdown' })
			.first()
			.click();

		// The Extension attribute defaults to "md" on the Markdown type, so its
		// editor should be prepopulated without any user input.
		const extensionField = page.getByRole('textbox', { name: 'Extension' });
		await expect(extensionField).toHaveValue('md', { timeout: 10000 });

		await page.getByRole('button', { name: 'Cancel' }).click();
	});

	test('should show required fields as invalid immediately on open', async ({
		page,
	}) => {
		await openBranch(page, BRANCH);
		await expandArtifact(page, 'System Requirements - Markdown');
		await expect(page.getByText('AE CD Parent')).toBeVisible({
			timeout: 10000,
		});

		await page.getByText('AE CD Parent').click({ button: 'right' });
		await page
			.getByRole('menuitem', { name: 'Create Child Artifact' })
			.click();

		// Without touching anything, the required-fields notice is shown and
		// both create actions are disabled.
		await expect(
			page.getByText('Required fields not filled out')
		).toBeVisible();
		await expect(
			page.getByRole('button', { name: 'Create', exact: true })
		).toBeDisabled();
		await expect(
			page.getByRole('button', { name: 'Create & add another' })
		).toBeDisabled();

		await page.getByRole('button', { name: 'Cancel' }).click();
	});

	test('should create multiple artifacts with "Create & add another"', async ({
		page,
	}) => {
		await openBranch(page, BRANCH);
		await expandArtifact(page, 'System Requirements - Markdown');
		await expect(page.getByText('AE CD Parent')).toBeVisible({
			timeout: 10000,
		});

		await page.getByText('AE CD Parent').click({ button: 'right' });
		await page
			.getByRole('menuitem', { name: 'Create Child Artifact' })
			.click();

		const typeInput = page.getByRole('combobox', { name: 'Select a Type' });
		await typeInput.click();
		await typeInput.fill('Software Requirement - Markdown');
		await page
			.getByRole('option', { name: 'Software Requirement - Markdown' })
			.first()
			.click();

		const addAnother = page.getByRole('button', {
			name: 'Create & add another',
		});

		// First artifact via "Create & add another" — dialog stays open.
		await page
			.getByRole('textbox', { name: 'Enter a Name' })
			.fill('AE CD Multi One');
		await Promise.all([
			page.waitForResponse(
				(res) => res.url().includes('orcs/txs') && res.status() === 200
			),
			addAnother.click(),
		]);

		// The dialog is still open with the name cleared but type preserved.
		await expect(
			page.getByRole('textbox', { name: 'Enter a Name' })
		).toHaveValue('');
		await expect(typeInput).toHaveValue('Software Requirement - Markdown');

		// Second artifact, this time closing with "Create".
		await page
			.getByRole('textbox', { name: 'Enter a Name' })
			.fill('AE CD Multi Two');
		await Promise.all([
			page.waitForResponse(
				(res) => res.url().includes('orcs/txs') && res.status() === 200
			),
			page.getByRole('button', { name: 'Create', exact: true }).click(),
		]);

		// Both artifacts exist under the parent (the create flow auto-expands it).
		await expect(page.getByText('AE CD Multi One')).toBeVisible({
			timeout: 10000,
		});
		await expect(page.getByText('AE CD Multi Two')).toBeVisible({
			timeout: 10000,
		});
	});
});
