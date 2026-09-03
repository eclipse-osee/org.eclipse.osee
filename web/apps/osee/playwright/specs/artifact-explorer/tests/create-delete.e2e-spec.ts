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
import { API_BASE, AUTH_HEADER } from '../../../shared/test-config';

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

	test('should show only required attributes initially, with an Add attribute button', async ({
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

		const dialog = page.getByRole('dialog');

		// Required attributes are shown (Extension is required with default "md").
		await expect(
			dialog.getByRole('textbox', { name: 'Extension' })
		).toBeVisible({ timeout: 10000 });

		// An optional attribute (Required Indicators, multiplicity ANY) is NOT
		// shown up front — it must be added explicitly.
		await expect(
			dialog.getByRole('combobox', { name: 'Required Indicators' })
		).toHaveCount(0);

		// The Add attribute affordance is present.
		await expect(
			dialog.getByRole('button', { name: 'Add attribute' })
		).toBeVisible();

		await page.getByRole('button', { name: 'Cancel' }).click();
	});

	test('should add an optional attribute (prefilled with its default) and allow removing it', async ({
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

		const dialog = page.getByRole('dialog');

		// Open the Add Attribute dialog (opens on top of the create dialog).
		await dialog.getByRole('button', { name: 'Add attribute' }).click();
		const addDialog = page.getByRole('dialog').last();
		await addDialog
			.getByRole('checkbox', { name: /^Required Indicators\s*\(/ })
			.check();
		await addDialog
			.getByRole('button', { name: 'Add', exact: true })
			.click();

		// The added attribute now renders its editor in the create dialog.
		const indicators = dialog.getByRole('combobox', {
			name: 'Required Indicators',
		});
		await expect(indicators).toBeVisible({ timeout: 10000 });

		// It is optional, so it has a remove (X) button that removes it.
		await dialog
			.getByRole('button', { name: /Remove Required Indicators/ })
			.click();
		await expect(
			dialog.getByRole('combobox', { name: 'Required Indicators' })
		).toHaveCount(0);

		await page.getByRole('button', { name: 'Cancel' }).click();
	});

	test('should persist multiple instances of a repeatable attribute', async ({
		page,
		request,
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
			.fill('AE CD Multi Attr');
		const typeInput = page.getByRole('combobox', { name: 'Select a Type' });
		await typeInput.click();
		await typeInput.fill('Software Requirement - Markdown');
		await page
			.getByRole('option', { name: 'Software Requirement - Markdown' })
			.first()
			.click();

		const dialog = page.getByRole('dialog');

		// Add two instances of the repeatable "Required Indicators" attribute.
		for (let i = 0; i < 2; i++) {
			await dialog.getByRole('button', { name: 'Add attribute' }).click();
			const addDialog = page.getByRole('dialog').last();
			await addDialog
				.getByRole('checkbox', { name: /^Required Indicators\s*\(/ })
				.check();
			await addDialog
				.getByRole('button', { name: 'Add', exact: true })
				.click();
		}

		// Two "Required Indicators" editors are shown, grouped under a count
		// header ("Required Indicators (2)") like the artifact editor.
		const indicators = dialog.getByRole('combobox', {
			name: 'Required Indicators',
		});
		await expect(indicators).toHaveCount(2);
		await expect(
			dialog.getByTestId('attribute-group-header').filter({
				hasText: 'Required Indicators',
			})
		).toContainText('(2)');

		// Both default to the same value, so the duplicate-value warning shows —
		// the backend stores attribute values as a set per type, so identical
		// same-type values would collapse to one on save.
		await expect(
			dialog.getByText(
				'Duplicate value — only one instance of this value will be saved.'
			)
		).toHaveCount(2);

		// Give the two instances distinct values so both persist. Select from the
		// open listbox overlay and confirm each value took before moving on.
		const selectIndicator = async (index: number, option: string) => {
			const combobox = indicators.nth(index);
			await combobox.click();
			const listbox = page.getByRole('listbox');
			await expect(listbox).toBeVisible();
			await listbox.getByRole('option', { name: option }).click();
			await expect(combobox).toHaveValue(option);
		};
		await selectIndicator(0, 'Restricted Rights');
		await selectIndicator(1, 'Unlimited Rights');

		// With distinct values the warning is gone.
		await expect(
			dialog.getByText(
				'Duplicate value — only one instance of this value will be saved.'
			)
		).toHaveCount(0);

		// The enum dropdown writes the selected value back to the model on an
		// auditTime(500) delay, so wait past that window before submitting to
		// ensure both selected values are captured in the payload.
		await page.waitForTimeout(700);

		// Capture the create response so we can read the new artifact's id.
		const [createResponse] = await Promise.all([
			page.waitForResponse(
				(res) => res.url().includes('orcs/txs') && res.status() === 200
			),
			page.getByRole('button', { name: 'Create', exact: true }).click(),
		]);
		const createBody = await createResponse.json();
		const newArtifactId: string = createBody.results.ids[0];
		expect(newArtifactId).toBeTruthy();

		// Verify via the API that BOTH distinct instances persisted (the frontend
		// groups same-type values into one array-valued node so the backend
		// creates one instance per distinct value instead of overwriting).
		const artifactRes = await request.get(
			`${API_BASE}/orcs/branch/${branchId}/artifact/${newArtifactId}/related/direct?viewId=-1&includeRelations=false&includeAttributes=true`,
			{ headers: { ...AUTH_HEADER, Accept: 'application/json' } }
		);
		expect(artifactRes.status()).toBe(200);
		const artifact = await artifactRes.json();
		const indicatorValues: string[] = (artifact.attributes ?? [])
			.filter(
				(a: { typeId: string }) => a.typeId === '1152921504606847317'
			)
			.map((a: { value: string }) => a.value);
		expect(indicatorValues.length).toBe(2);
		expect(indicatorValues).toContain('Restricted Rights');
		expect(indicatorValues).toContain('Unlimited Rights');
	});

	test('should persist the type default value on the created artifact', async ({
		page,
		request,
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
			.fill('AE CD Default Persist');
		const typeInput = page.getByRole('combobox', { name: 'Select a Type' });
		await typeInput.click();
		await typeInput.fill('Software Requirement - Markdown');
		await page
			.getByRole('option', { name: 'Software Requirement - Markdown' })
			.first()
			.click();

		// The Extension default "md" is prefilled; create without touching it.
		await expect(
			page.getByRole('textbox', { name: 'Extension' })
		).toHaveValue('md', { timeout: 10000 });

		const [createResponse] = await Promise.all([
			page.waitForResponse(
				(res) => res.url().includes('orcs/txs') && res.status() === 200
			),
			page.getByRole('button', { name: 'Create', exact: true }).click(),
		]);
		const newArtifactId: string = (await createResponse.json()).results
			.ids[0];
		expect(newArtifactId).toBeTruthy();

		// The default value must actually persist on the created artifact.
		const artifactRes = await request.get(
			`${API_BASE}/orcs/branch/${branchId}/artifact/${newArtifactId}/related/direct?viewId=-1&includeRelations=false&includeAttributes=true`,
			{ headers: { ...AUTH_HEADER, Accept: 'application/json' } }
		);
		expect(artifactRes.status()).toBe(200);
		const artifact = await artifactRes.json();
		const extension = (artifact.attributes ?? []).find(
			(a: { typeId: string }) => a.typeId === '1152921504606847064'
		);
		expect(extension?.value).toBe('md');
	});

	test('should persist an added-but-untouched attribute with its default value', async ({
		page,
		request,
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
			.fill('AE CD Added Untouched');
		const typeInput = page.getByRole('combobox', { name: 'Select a Type' });
		await typeInput.click();
		await typeInput.fill('Software Requirement - Markdown');
		await page
			.getByRole('option', { name: 'Software Requirement - Markdown' })
			.first()
			.click();

		const dialog = page.getByRole('dialog');

		// Add an optional attribute and leave it completely untouched.
		await dialog.getByRole('button', { name: 'Add attribute' }).click();
		const addDialog = page.getByRole('dialog').last();
		await addDialog
			.getByRole('checkbox', { name: /^Required Indicators\s*\(/ })
			.check();
		await addDialog
			.getByRole('button', { name: 'Add', exact: true })
			.click();
		await expect(
			dialog.getByRole('combobox', { name: 'Required Indicators' })
		).toBeVisible({ timeout: 10000 });

		const [createResponse] = await Promise.all([
			page.waitForResponse(
				(res) => res.url().includes('orcs/txs') && res.status() === 200
			),
			page.getByRole('button', { name: 'Create', exact: true }).click(),
		]);
		const newArtifactId: string = (await createResponse.json()).results
			.ids[0];
		expect(newArtifactId).toBeTruthy();

		// The added-but-untouched attribute must still be created (with its
		// default value), not silently dropped for being unedited.
		const artifactRes = await request.get(
			`${API_BASE}/orcs/branch/${branchId}/artifact/${newArtifactId}/related/direct?viewId=-1&includeRelations=false&includeAttributes=true`,
			{ headers: { ...AUTH_HEADER, Accept: 'application/json' } }
		);
		expect(artifactRes.status()).toBe(200);
		const artifact = await artifactRes.json();
		const indicators = (artifact.attributes ?? []).filter(
			(a: { typeId: string }) => a.typeId === '1152921504606847317'
		);
		expect(indicators.length).toBeGreaterThanOrEqual(1);
	});

	test('should carry attribute values across "Create & add another" entries', async ({
		page,
		request,
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

		// Change the Extension default so we can prove it carries over.
		const extension = page.getByRole('textbox', { name: 'Extension' });
		await expect(extension).toHaveValue('md', { timeout: 10000 });
		await extension.fill('rst');

		// First artifact via "Create & add another".
		await page
			.getByRole('textbox', { name: 'Enter a Name' })
			.fill('AE CD Carry One');
		const [firstResponse] = await Promise.all([
			page.waitForResponse(
				(res) => res.url().includes('orcs/txs') && res.status() === 200
			),
			page.getByRole('button', { name: 'Create & add another' }).click(),
		]);
		const firstId: string = (await firstResponse.json()).results.ids[0];

		// The Extension value carries over to the next entry (not reset).
		await expect(extension).toHaveValue('rst');

		// Second artifact created with the carried-over value.
		await page
			.getByRole('textbox', { name: 'Enter a Name' })
			.fill('AE CD Carry Two');
		const [secondResponse] = await Promise.all([
			page.waitForResponse(
				(res) => res.url().includes('orcs/txs') && res.status() === 200
			),
			page.getByRole('button', { name: 'Create', exact: true }).click(),
		]);
		const secondId: string = (await secondResponse.json()).results.ids[0];

		// Both created artifacts have the carried-over Extension value.
		for (const id of [firstId, secondId]) {
			const res = await request.get(
				`${API_BASE}/orcs/branch/${branchId}/artifact/${id}/related/direct?viewId=-1&includeRelations=false&includeAttributes=true`,
				{ headers: { ...AUTH_HEADER, Accept: 'application/json' } }
			);
			expect(res.status()).toBe(200);
			const artifact = await res.json();
			const ext = (artifact.attributes ?? []).find(
				(a: { typeId: string }) => a.typeId === '1152921504606847064'
			);
			expect(ext?.value).toBe('rst');
		}
	});
});
