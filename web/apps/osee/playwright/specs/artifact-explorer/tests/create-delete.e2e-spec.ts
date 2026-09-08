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
	switchEditorSection,
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
			.getByRole('textbox', { name: 'Artifact Name' })
			.fill('AE CD Created');
		await page.getByRole('combobox', { name: 'Artifact Type' }).click();
		await page
			.getByRole('combobox', { name: 'Artifact Type' })
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
			.getByRole('textbox', { name: 'Artifact Name' })
			.fill('Invalid Test');
		const typeInput = page.getByRole('combobox', {
			name: 'Artifact Type',
		});
		await typeInput.click();
		await typeInput.fill('NonExistentType123');
		// Click away to blur without selecting from dropdown
		await page.getByRole('textbox', { name: 'Artifact Name' }).click();

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
		const typeInput = page.getByRole('combobox', { name: 'Artifact Type' });
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

	test('should show the required-fields notice and disable create actions until filled', async ({
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

		// Without touching anything, the required-fields warning is shown and
		// both create actions are disabled.
		await expect(
			page.getByText('Required fields (*) are not all filled out')
		).toBeVisible();
		await expect(
			page.getByRole('button', { name: 'Create', exact: true })
		).toBeDisabled();
		await expect(
			page.getByRole('button', { name: 'Create & add another' })
		).toBeDisabled();

		// Fill both required fields; the legend flips to the informational
		// message and the create actions enable.
		await page
			.getByRole('textbox', { name: 'Artifact Name' })
			.fill('AE CD Required Check');
		const typeInput = page.getByRole('combobox', { name: 'Artifact Type' });
		await typeInput.click();
		await typeInput.fill('Software Requirement - Markdown');
		await page
			.getByRole('option', { name: 'Software Requirement - Markdown' })
			.first()
			.click();

		await expect(
			page.getByText('* indicates a required field')
		).toBeVisible();
		await expect(
			page.getByRole('button', { name: 'Create', exact: true })
		).toBeEnabled();

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

		const typeInput = page.getByRole('combobox', { name: 'Artifact Type' });
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
			.getByRole('textbox', { name: 'Artifact Name' })
			.fill('AE CD Multi One');
		await Promise.all([
			page.waitForResponse(
				(res) => res.url().includes('orcs/txs') && res.status() === 200
			),
			addAnother.click(),
		]);

		// The dialog is still open with the name cleared but type preserved.
		await expect(
			page.getByRole('textbox', { name: 'Artifact Name' })
		).toHaveValue('');
		await expect(typeInput).toHaveValue('Software Requirement - Markdown');

		// Second artifact, this time closing with "Create".
		await page
			.getByRole('textbox', { name: 'Artifact Name' })
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

		const typeInput = page.getByRole('combobox', { name: 'Artifact Type' });
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

		// The Add Attribute affordance (header icon button) is present.
		await expect(
			dialog.getByRole('button', { name: 'Add Attribute' })
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

		const typeInput = page.getByRole('combobox', { name: 'Artifact Type' });
		await typeInput.click();
		await typeInput.fill('Software Requirement - Markdown');
		await page
			.getByRole('option', { name: 'Software Requirement - Markdown' })
			.first()
			.click();

		const dialog = page.getByRole('dialog');

		// Open the Add Attribute dialog via the header add icon button.
		await dialog.getByRole('button', { name: 'Add Attribute' }).click();
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

		// Delete controls only appear in delete mode (toggle in the header).
		await dialog
			.getByRole('button', { name: 'Toggle Delete Mode' })
			.click();
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
			.getByRole('textbox', { name: 'Artifact Name' })
			.fill('AE CD Multi Attr');
		const typeInput = page.getByRole('combobox', { name: 'Artifact Type' });
		await typeInput.click();
		await typeInput.fill('Software Requirement - Markdown');
		await page
			.getByRole('option', { name: 'Software Requirement - Markdown' })
			.first()
			.click();

		const dialog = page.getByRole('dialog');

		// Add two instances of the repeatable "Required Indicators" attribute
		// via the header add icon button.
		for (let i = 0; i < 2; i++) {
			await dialog.getByRole('button', { name: 'Add Attribute' }).click();
			const addDialog = page.getByRole('dialog').last();
			await addDialog
				.getByRole('checkbox', { name: /^Required Indicators\s*\(/ })
				.check();
			await addDialog
				.getByRole('button', { name: 'Add', exact: true })
				.click();
		}

		// Two "Required Indicators" editors are shown, grouped under a count
		// header ("Required Indicators (2)") like the artifact editor. Inside a
		// group the per-field labels are hidden (the header names the type), so
		// scope to the group and select its comboboxes rather than by label.
		const indicatorGroup = dialog
			.locator('osee-attribute-field-group')
			.filter({ hasText: 'Required Indicators' });
		await expect(
			indicatorGroup.getByTestId('attribute-group-header')
		).toContainText('(2)');
		const indicators = indicatorGroup.getByRole('combobox');
		await expect(indicators).toHaveCount(2);

		// Give the two instances distinct values. Select from the open listbox
		// overlay and confirm each value took in the DOM.
		const selectIndicator = async (index: number, option: string) => {
			const combobox = indicators.nth(index);
			await combobox.click();
			const listbox = page.getByRole('listbox');
			await expect(listbox).toBeVisible();
			await listbox.getByRole('option', { name: option }).click();
			await expect(combobox).toHaveValue(option);
			// Overlay closes on selection; wait for it so the next click opens
			// a fresh overlay rather than hitting the closing one.
			await expect(listbox).toHaveCount(0);
		};
		await selectIndicator(0, 'Restricted Rights');
		await selectIndicator(1, 'Unlimited Rights');
		// The enum dropdown writes each selected value back to the model on an
		// auditTime(500) delay. Blur the fields and wait past that window so
		// both debounced writes flush before we submit.
		await dialog.getByRole('heading').first().click();
		await page.waitForTimeout(1500);

		await Promise.all([
			page.waitForResponse(
				(res) => res.url().includes('orcs/txs') && res.status() === 200
			),
			page.getByRole('button', { name: 'Create', exact: true }).click(),
		]);
		// Wait for the dialog to fully close before navigating.
		await expect(dialog).toHaveCount(0);

		// Verify end-to-end via the artifact editor (the source of truth the
		// user sees, which loads every attribute instance). Open the newly
		// created child from the hierarchy (it appears under the expanded
		// parent) and confirm both distinct instances render, grouped under a
		// "Required Indicators (2)" header, with both selected values present.
		//
		// Note: the `related/direct` API JSON does not reliably return multiple
		// instances of the same attribute type, so the editor is the accurate
		// end-to-end check here.
		await page
			.getByText('AE CD Multi Attr', { exact: true })
			.click({ timeout: 10000 });
		await switchEditorSection(page, 'Attributes');
		const editorGroup = page
			.locator('osee-attribute-group')
			.filter({ hasText: 'Required Indicators' });
		await expect(
			editorGroup.getByTestId('attribute-group-header')
		).toContainText('(2)', { timeout: 10000 });
		// The artifact editor renders enum instances as mat-selects (their
		// selected value shows as text, not an input value), so assert both
		// selected values are present within the group.
		const editorIndicators = editorGroup.getByRole('combobox');
		await expect(editorIndicators).toHaveCount(2);
		await expect(editorGroup).toContainText('Restricted Rights');
		await expect(editorGroup).toContainText('Unlimited Rights');
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
			.getByRole('textbox', { name: 'Artifact Name' })
			.fill('AE CD Default Persist');
		const typeInput = page.getByRole('combobox', { name: 'Artifact Type' });
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
			.getByRole('textbox', { name: 'Artifact Name' })
			.fill('AE CD Added Untouched');
		const typeInput = page.getByRole('combobox', { name: 'Artifact Type' });
		await typeInput.click();
		await typeInput.fill('Software Requirement - Markdown');
		await page
			.getByRole('option', { name: 'Software Requirement - Markdown' })
			.first()
			.click();

		const dialog = page.getByRole('dialog');

		// Add an optional attribute and leave it completely untouched.
		await dialog.getByRole('button', { name: 'Add Attribute' }).click();
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

		const typeInput = page.getByRole('combobox', { name: 'Artifact Type' });
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
			.getByRole('textbox', { name: 'Artifact Name' })
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
			.getByRole('textbox', { name: 'Artifact Name' })
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

	test('should persist multiple instances of a repeatable attribute all left at the SAME (default) value and show them in the artifact editor', async ({
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
			.getByRole('textbox', { name: 'Artifact Name' })
			.fill('AE CD Same Value');
		const typeInput = page.getByRole('combobox', { name: 'Artifact Type' });
		await typeInput.click();
		await typeInput.fill('Software Requirement - Markdown');
		await page
			.getByRole('option', { name: 'Software Requirement - Markdown' })
			.first()
			.click();

		const dialog = page.getByRole('dialog');

		// Qualification Method is a required, repeatable (AT_LEAST_ONE) type, so
		// one instance is seeded by default at "Unspecified". Add two more and
		// leave all three at the default "Unspecified" — the identical-value
		// case that a naive de-duping create would collapse to one. No enum
		// selection is needed, which also keeps the test deterministic.
		await expect(
			dialog.getByRole('combobox', { name: 'Qualification Method' })
		).toBeVisible({ timeout: 10000 });
		for (let i = 0; i < 2; i++) {
			await dialog.getByRole('button', { name: 'Add Attribute' }).click();
			const addDialog = page.getByRole('dialog').last();
			await addDialog
				.getByRole('checkbox', { name: /^Qualification Method\s*\(/ })
				.check();
			await addDialog
				.getByRole('button', { name: 'Add', exact: true })
				.click();
		}

		// The three instances group under a "Qualification Method (3)" header.
		const qualGroup = dialog
			.locator('osee-attribute-field-group')
			.filter({ hasText: 'Qualification Method' });
		await expect(
			qualGroup.getByTestId('attribute-group-header')
		).toContainText('(3)');
		await expect(qualGroup.getByRole('combobox')).toHaveCount(3);

		await Promise.all([
			page.waitForResponse(
				(res) => res.url().includes('orcs/txs') && res.status() === 200
			),
			page.getByRole('button', { name: 'Create', exact: true }).click(),
		]);
		// Wait for the dialog to fully close before navigating.
		await expect(dialog).toHaveCount(0);

		// Verify via the artifact editor (the source of truth the user sees):
		// open the new child from the hierarchy and confirm all three
		// identical-value instances render, grouped under a
		// "Qualification Method (3)" header.
		//
		// Note: the `related/direct` API JSON collapses attributes with
		// identical values, so it under-reports the count for same-value
		// instances — the editor (which loads every instance) is the reliable
		// end-to-end check here.
		await page
			.getByText('AE CD Same Value', { exact: true })
			.click({ timeout: 10000 });
		await switchEditorSection(page, 'Attributes');
		const editorGroup = page
			.locator('osee-attribute-group')
			.filter({ hasText: 'Qualification Method' });
		await expect(
			editorGroup.getByTestId('attribute-group-header')
		).toContainText('(3)', { timeout: 10000 });
		await expect(editorGroup.getByRole('combobox')).toHaveCount(3);
	});

	test('should NOT persist a removed attribute instance (no lingering after delete)', async ({
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
			.getByRole('textbox', { name: 'Artifact Name' })
			.fill('AE CD Removed Not Lingering');
		const typeInput = page.getByRole('combobox', { name: 'Artifact Type' });
		await typeInput.click();
		await typeInput.fill('Software Requirement - Markdown');
		await page
			.getByRole('option', { name: 'Software Requirement - Markdown' })
			.first()
			.click();

		const dialog = page.getByRole('dialog');

		// Add two instances of "Required Indicators".
		for (let i = 0; i < 2; i++) {
			await dialog.getByRole('button', { name: 'Add Attribute' }).click();
			const addDialog = page.getByRole('dialog').last();
			await addDialog
				.getByRole('checkbox', { name: /^Required Indicators\s*\(/ })
				.check();
			await addDialog
				.getByRole('button', { name: 'Add', exact: true })
				.click();
		}

		const indicatorGroup = dialog
			.locator('osee-attribute-field-group')
			.filter({ hasText: 'Required Indicators' });
		const groupedIndicators = indicatorGroup.getByRole('combobox');
		await expect(groupedIndicators).toHaveCount(2);

		// Give the first a distinct value so we can identify what remains.
		await groupedIndicators.nth(0).click();
		const listbox = page.getByRole('listbox');
		await expect(listbox).toBeVisible();
		await listbox.getByRole('option', { name: 'Unlimited Rights' }).click();
		await expect(groupedIndicators.nth(0)).toHaveValue('Unlimited Rights');
		await page.waitForTimeout(1000);

		// Enter delete mode and remove the SECOND instance.
		await dialog
			.getByRole('button', { name: 'Toggle Delete Mode' })
			.click();
		await indicatorGroup
			.getByRole('button', { name: /Remove Required Indicators/ })
			.nth(1)
			.click();

		// Only one instance remains, so it collapses out of the group and back
		// into a single labeled field.
		await expect(indicatorGroup).toHaveCount(0);
		await expect(
			dialog.getByRole('combobox', { name: 'Required Indicators' })
		).toHaveCount(1);

		const [createResponse] = await Promise.all([
			page.waitForResponse(
				(res) => res.url().includes('orcs/txs') && res.status() === 200
			),
			page.getByRole('button', { name: 'Create', exact: true }).click(),
		]);
		const newArtifactId: string = (await createResponse.json()).results
			.ids[0];
		expect(newArtifactId).toBeTruthy();

		// API: exactly ONE Required Indicators instance persisted — the removed
		// one must not linger in the created artifact.
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
		expect(indicatorValues.length).toBe(1);

		// Wait for the dialog to fully close before navigating.
		await expect(dialog).toHaveCount(0);

		// Artifact editor: opening the new child from the hierarchy shows a
		// single instance, so there is no leftover from the removed one.
		await page
			.getByText('AE CD Removed Not Lingering', { exact: true })
			.click({ timeout: 10000 });
		await switchEditorSection(page, 'Attributes');
		await expect(page.getByLabel('Name')).toBeVisible({ timeout: 10000 });
		// A single instance renders as one combobox (no "(2)" group header).
		await expect(
			page
				.locator('osee-attribute-group')
				.filter({ hasText: 'Required Indicators' })
				.getByTestId('attribute-group-header')
		).toHaveCount(0);
	});
});
