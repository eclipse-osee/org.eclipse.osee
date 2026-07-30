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
			.click();

		await Promise.all([
			page.waitForResponse(
				(res) => res.url().includes('orcs/txs') && res.status() === 200
			),
			page.getByRole('button', { name: 'Ok' }).click(),
		]);

		await expect(page.getByText('AE CD Created')).toBeVisible({
			timeout: 10000,
		});
	});

	test('should disable Ok when artifact type is not selected from dropdown', async ({
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

		await expect(page.getByRole('button', { name: 'Ok' })).toBeDisabled();

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
});
