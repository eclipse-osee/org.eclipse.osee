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
import { test, expect, Page } from '@ngx-playwright/test';
import { createWorkingBranchFromPL } from '../utils/helpers';
import { selectBranch } from '../../../shared/branch-helpers';

const branchName = 'New MIM ICD';

test('create action', async ({ page }) => {
	await page.setViewportSize({ width: 1600, height: 1000 });
	await page.goto('/ple');
	await page.getByRole('link', { name: 'MIM' }).click();
	await page.getByRole('link', { name: 'Connections' }).click();
	await selectBranch(page, 'Baseline', 'SAW PL Hardening Branch');
	await expect(page.getByText('SAW PL Hardening Branch')).toBeVisible();

	await page.screenshot({
		animations: 'disabled',
		path: 'screenshots/create-icd/create-action-button.png',
	});

	await createWorkingBranchFromPL(
		page,
		branchName,
		'SAW PL Hardening Branch',
		'Creating a new MIM ICD',
		true
	);
	await expect(page.getByText(branchName)).toBeVisible();
});

test('create nodes', async ({ page }) => {
	await page.setViewportSize({ width: 1600, height: 1000 });
	await page.goto('/ple');
	await page.getByRole('link', { name: 'MIM' }).click();
	await page.getByRole('link', { name: 'Connections' }).click();
	await selectBranch(page, 'Working', branchName);

	await page.getByText('account_circle').click();

	await page.screenshot({
		animations: 'disabled',
		path: 'screenshots/create-icd/user-menu.png',
	});

	await page.getByRole('menuitem', { name: 'Settings' }).click();
	await page.getByLabel('Edit Mode').check();

	await page.screenshot({
		animations: 'disabled',
		path: 'screenshots/create-icd/user-settings.png',
	});

	await page.getByRole('button', { name: 'Ok' }).click();
	await page.locator('rect').nth(1).click({
		button: 'right',
	});

	await page.screenshot({
		animations: 'disabled',
		path: 'screenshots/create-icd/create-node-connection-menu.png',
	});

	await page.getByRole('menuitem', { name: 'Create New Node' }).click();
	// Wait for the dialog before filling.
	await expect(page.getByLabel('Add name', { exact: true })).toBeVisible();
	await page.getByLabel('Add name', { exact: true }).fill('Node A');
	await page.getByText('Add node number').click();
	await page.getByLabel('Add node number').fill('A');

	await page.screenshot({
		animations: 'disabled',
		path: 'screenshots/create-icd/create-node-dialog.png',
	});

	await page.getByText('Cancel Ok').click();
	await page.getByRole('button', { name: 'Ok' }).click();
	await page.locator('rect').nth(1).click({
		button: 'right',
	});
	await page.getByRole('menuitem', { name: 'Create New Node' }).click();
	// Wait for the dialog before filling.
	await expect(page.getByLabel('Add name', { exact: true })).toBeVisible();
	await page.getByLabel('Add name', { exact: true }).fill('Node B');
	await page.getByLabel('Add node number').fill('B');
	await page.getByRole('button', { name: 'Ok' }).click();

	await expect(page.getByText('Node A')).toBeVisible();
	await expect(page.getByText('Node B')).toBeVisible();
});

test('create enum list artifacts', async ({ page }) => {
	await page.setViewportSize({ width: 1600, height: 1000 });
	await page.goto('/ple');
	await page.getByRole('link', { name: 'MIM' }).click();
	await page.getByRole('link', { name: 'List Configuration' }).click();
	await selectBranch(page, 'Working', branchName);

	await page.screenshot({
		animations: 'disabled',
		path: 'screenshots/create-icd/enum-list-config.png',
	});

	await page.getByRole('button', { name: 'Units' }).click();
	await page.locator('button').filter({ hasText: /^add$/ }).click();
	await page.getByLabel('Name').fill('seconds');
	await page
		.locator('div')
		.filter({ hasText: /^Measurement$/ })
		.nth(2)
		.click();
	await page.getByLabel('Measurement').fill('time');
	await page.getByRole('button', { name: 'Ok' }).click();
	// Wait for the dialog to close before adding the next unit.
	await expect(page.locator('mat-dialog-container')).toBeHidden();
	await page.locator('button').filter({ hasText: /^add$/ }).click();
	await page.getByLabel('Name').fill('meters');
	await page
		.locator('div')
		.filter({ hasText: /^Measurement$/ })
		.nth(2)
		.click();
	await page.getByLabel('Measurement').fill('distance');
	await page.getByRole('button', { name: 'Ok' }).click();
	await expect(page.locator('mat-dialog-container')).toBeHidden();
	await page.locator('button').filter({ hasText: /^add$/ }).click();
	await page.getByLabel('Name').fill('hertz');
	await page.getByLabel('Measurement').click();
	await page.getByLabel('Measurement').fill('frequency');
	await page.getByRole('button', { name: 'Ok' }).click();
	await expect(page.locator('mat-dialog-container')).toBeHidden();
	await page.getByRole('button', { name: 'Units' }).click();
	await page.getByRole('button', { name: 'Rates' }).click();
	await page.getByRole('button', { name: 'Add New Rates' }).click();
	await page.getByLabel('Name', { exact: true }).click();
	await page.getByLabel('Name', { exact: true }).fill('1');
	await page.getByRole('button', { name: 'Save Changes' }).click();
	await page.getByRole('button', { name: 'Add New Rates' }).click();
	await page.getByLabel('Name', { exact: true }).fill('5');
	await page.getByRole('button', { name: 'Save Changes' }).click();
	await page.getByRole('button', { name: 'Add New Rates' }).click();
	await page.getByLabel('Name', { exact: true }).fill('10');
	await page.getByRole('button', { name: 'Save Changes' }).click();
	await page.getByRole('button', { name: 'Add New Rates' }).click();
	await page.getByLabel('Name', { exact: true }).fill('25');
	await page.getByRole('button', { name: 'Save Changes' }).click();
	await page.getByRole('button', { name: 'Add New Rates' }).click();
	await page.getByLabel('Name', { exact: true }).fill('Aperiodic');
	await page.getByRole('button', { name: 'Save Changes' }).click();
	await page.getByRole('button', { name: 'Rates', exact: true }).click();
	await page.getByRole('button', { name: 'Message Types' }).click();
	await page.getByRole('button', { name: 'Add New Message Types' }).click();
	await page.getByLabel('Name', { exact: true }).click();
	await page.getByLabel('Name', { exact: true }).fill('Operational');
	await page.getByRole('button', { name: 'Save Changes' }).click();
	await page.getByRole('button', { name: 'Add New Message Types' }).click();
	await page.getByLabel('Name', { exact: true }).fill('Connection');
	await page.getByRole('button', { name: 'Save Changes' }).click();
	await page
		.getByRole('button', { name: 'Message Types', exact: true })
		.click();
	await page.getByRole('button', { name: 'Structure Categories' }).click();
	await page.getByPlaceholder('Filter Structure Categories').click();
	await page.getByPlaceholder('Filter Structure Categories').fill('');
	await page
		.getByRole('button', { name: 'Add New Structure Categories' })
		.click();
	await page.getByLabel('Name', { exact: true }).fill('Misc');
	await page.getByRole('button', { name: 'Save Changes' }).click();
	await page
		.getByRole('button', { name: 'Add New Structure Categories' })
		.click();
	await page.getByLabel('Name', { exact: true }).fill('Test');
	await page.getByRole('button', { name: 'Save Changes' }).click();
	await page
		.getByRole('button', { name: 'Structure Categories', exact: true })
		.click();
	await page.getByRole('button', { name: 'Message Periodicities' }).click();
	await page
		.getByRole('button', { name: 'Add New Message Periodicities' })
		.click();
	await page.getByLabel('Name', { exact: true }).fill('Periodic');
	await page.getByRole('button', { name: 'Save Changes' }).click();
	await page
		.getByRole('button', { name: 'Add New Message Periodicities' })
		.click();
	await page.getByLabel('Name', { exact: true }).fill('Aperiodic');
	await page.getByRole('button', { name: 'Save Changes' }).click();
	await page
		.getByRole('button', { name: 'Message Periodicities', exact: true })
		.click();
});

test('create transport type', async ({ page }) => {
	await page.setViewportSize({ width: 1600, height: 1000 });
	await page.goto('/ple');
	await page.getByRole('link', { name: 'MIM' }).click();
	await page.getByRole('link', { name: 'Transport Types' }).click();
	await selectBranch(page, 'Working', branchName);
	await page.locator('button').filter({ hasText: 'add' }).click();
	// Wait for the dialog before filling.
	await expect(page.getByLabel('Name')).toBeVisible();
	await page.getByLabel('Name').fill('Ethernet');
	await page.getByLabel('Byte Align Validation', { exact: true }).click();
	await page.getByLabel('Byte Align Validation Size').click();
	await page.getByLabel('Byte Align Validation Size').fill('8');
	await page.getByLabel('Message Generation', { exact: true }).click();
	await page.getByPlaceholder('Message Types').click();
	// Options load async; wait for the option before clicking.
	const transportMessageType = page.getByText('Operational');
	await expect(transportMessageType).toBeVisible({ timeout: 60000 });
	await transportMessageType.click();
	await page
		.getByLabel('Create New Transport Type')
		.getByText('Message Generation Position')
		.click();
	await page.getByLabel('Message Generation Position').fill('0');
	await page.getByLabel('Minimum Publisher Multiplicity').click();
	await page.getByLabel('Minimum Publisher Multiplicity').fill('1');
	await page.getByLabel('Maximum Publisher Multiplicity').click();
	await page.getByLabel('Maximum Publisher Multiplicity').fill('1');
	await page.getByLabel('Minimum Subscriber').click();
	await page.getByLabel('Minimum Subscriber').fill('1');
	await page.getByLabel('Maximum Subscriber').click();
	await page.getByLabel('Maximum Subscriber').fill('1');
	// Each "Select Available ..." multi-select loads its options async; wait for the option to
	// render before clicking its checkbox rather than racing the fetch.
	await page.getByLabel('Select Available Message').locator('span').click();
	const availableMessageOption = page.getByRole('option', { name: 'Name' });
	await expect(availableMessageOption).toBeVisible({ timeout: 60000 });
	await availableMessageOption.locator('mat-pseudo-checkbox').click();
	await page.keyboard.press('Escape');
	await page
		.getByLabel('Select Available Submessage')
		.locator('span')
		.click();
	const availableSubmessageOption = page.getByRole('option', {
		name: 'SubMessage Name',
	});
	await expect(availableSubmessageOption).toBeVisible({ timeout: 60000 });
	await availableSubmessageOption.locator('mat-pseudo-checkbox').click();
	await page.keyboard.press('Escape');

	await page.getByLabel('Select Available Structure').locator('span').click();
	const availableStructureOption = page.getByRole('option', {
		name: 'Name',
		exact: true,
	});
	await expect(availableStructureOption).toBeVisible({ timeout: 60000 });
	await availableStructureOption.locator('mat-pseudo-checkbox').click();
	await page.getByRole('combobox', { name: 'Select Available Structure' });
	await page.keyboard.press('Escape');

	await page.getByLabel('Select Available Element').locator('span').click();
	const availableElementOption = page.getByRole('option', {
		name: 'Start Index',
	});
	await expect(availableElementOption).toBeVisible({ timeout: 60000 });
	await availableElementOption.locator('mat-pseudo-checkbox').click();
	await page.getByRole('combobox', { name: 'Select Available Element' });
	await page.keyboard.press('Escape');
	await page.getByText('message, submessage,').click();
	await page.keyboard.press('Escape');
	await page.getByRole('button', { name: 'Ok' }).click();

	await expect(page.getByText('Ethernet')).toBeVisible();

	await expect(page.getByText('Select a Branch')).toBeVisible();
});

test('create connection', async ({ page }) => {
	await page.setViewportSize({ width: 1600, height: 1000 });
	await page.goto('/ple');
	await page.getByRole('link', { name: 'MIM' }).click();
	await page.getByRole('link', { name: 'Connections' }).click();
	await selectBranch(page, 'Working', branchName);
	await page.locator('rect').nth(1).click({
		button: 'right',
	});
	await page.getByRole('menuitem', { name: 'Create New Connection' }).click();
	// Wait for the dialog before filling.
	await expect(page.getByLabel('Add a Name')).toBeVisible();
	await page.getByLabel('Add a Name').fill('Connection A-B');
	// The transport-type mat-select loads its options asynchronously (osee-mat-option-loading
	// fetches "Transport Types" from the server when the panel opens), so the options are not in
	// the DOM at open time. Open the select, then wait for the Ethernet option to actually render
	// before clicking it, rather than racing the fetch with a bare getByText click (the empty-panel
	// timeout this fixes).
	await page
		.getByRole('combobox', { name: 'Select a Transport Type' })
		.click();
	const ethernetOption = page.getByRole('option', { name: 'Ethernet' });
	await expect(ethernetOption).toBeVisible({ timeout: 60000 });
	await ethernetOption.click();
	// The node list loads async; wait for each node option before clicking.
	await page.getByText('Select Nodes').click();
	const nodeAOption = page.getByLabel('Select Nodes').getByText('Node A');
	await expect(nodeAOption).toBeVisible({ timeout: 60000 });
	await nodeAOption.click();
	await page.getByPlaceholder('Add a node').click();
	const nodeBOption = page.getByLabel('Select Nodes').getByText('Node B');
	await expect(nodeBOption).toBeVisible({ timeout: 60000 });
	await nodeBOption.click();
	// Ok is disabled until the required fields (name + transport type) are set; gate on enabled so
	// a click can't land on the still-disabled button if the option selection hasn't applied yet.
	const okButton = page.getByRole('button', { name: 'Ok' });
	await expect(okButton).toBeEnabled({ timeout: 60000 });
	await okButton.click();

	// Creating the connection is a server round-trip; wait on the resulting graph node with a
	// budget that covers the create rather than the 5s default.
	await expect(page.getByText('Connection A-B', { exact: true })).toBeVisible(
		{ timeout: 60000 }
	);
});

test('create message and submessage', async ({ page }) => {
	await page.setViewportSize({ width: 1600, height: 1000 });
	await page.goto('/ple');
	await page.getByRole('link', { name: 'MIM' }).click();
	await page.getByRole('link', { name: 'Connections' }).click();
	await selectBranch(page, 'Working', branchName);
	// Selecting the branch triggers an async graph load; wait for the connection node to render
	// before clicking it instead of racing the fetch with a default-timeout click.
	const connectionNode = page.getByText('Connection A-B', { exact: true });
	await expect(connectionNode).toBeVisible({ timeout: 60000 });
	await connectionNode.click();

	await page.screenshot({
		animations: 'disabled',
		path: 'screenshots/create-icd/message-table-add.png',
	});

	await page.locator('button').filter({ hasText: 'add' }).click();
	// Wait for the dialog before filling.
	await expect(page.getByLabel('Name')).toBeVisible();
	await page.getByLabel('Name').fill('Message 1');
	// These dropdowns load their options asynchronously; wait for each option to render before
	// clicking rather than racing the fetch with a bare click (the empty-panel timeout pattern).
	await page.getByPlaceholder('Rates').click();
	const rateOption = page.getByRole('option', { name: '5', exact: true });
	await expect(rateOption).toBeVisible({ timeout: 60000 });
	await rateOption.click();
	await page.getByPlaceholder('Message Periodicity').click();
	const periodicityOption = page.getByText('Periodic', { exact: true });
	await expect(periodicityOption).toBeVisible({ timeout: 60000 });
	await periodicityOption.click();
	await page.getByPlaceholder('Message Types').click();
	const messageTypeOption = page.getByText('Operational');
	await expect(messageTypeOption).toBeVisible({ timeout: 60000 });
	await messageTypeOption.click();
	await page
		.getByLabel('Create New Message')
		.getByText('Message Number')
		.click();
	await page.getByLabel('Message Number').fill('1');
	await page.getByText('Select Publisher Node(s)').click();
	const publisherNode = page.getByRole('option', { name: 'Node A' });
	await expect(publisherNode).toBeVisible({ timeout: 60000 });
	await publisherNode.click();
	await page.getByText('Select Subscriber Node(s)').click();
	const subscriberNode = page.getByText('Node B');
	await expect(subscriberNode).toBeVisible({ timeout: 60000 });
	await subscriberNode.click();

	await page.screenshot({
		animations: 'disabled',
		path: 'screenshots/create-icd/create-message.png',
	});

	await page.getByRole('button', { name: 'Ok' }).click();
	await page
		.locator('button')
		.filter({ hasText: /^expand_more$/ })
		.click();

	await page.screenshot({
		animations: 'disabled',
		path: 'screenshots/create-icd/submessage-table-add.png',
	});

	await page.getByRole('button', { name: 'Add Submessage to:' }).click();
	await page.getByRole('menuitem', { name: 'Message' }).click();

	await page.screenshot({
		animations: 'disabled',
		path: 'screenshots/create-icd/create-submessage-dialog.png',
	});

	await page.getByRole('button', { name: 'Create new Submessage' }).click();
	// Wait for the dialog before filling.
	await expect(page.getByLabel('Name')).toBeVisible();
	await page.getByLabel('Name').fill('Submessage 1');
	await page.getByText('Sub Message Number', { exact: true }).click();
	await page.getByLabel('Sub Message Number').fill('1');

	await page.screenshot({
		animations: 'disabled',
		path: 'screenshots/create-icd/create-submessage-dialog-new.png',
	});

	await page.getByRole('button', { name: 'Next' }).click();
	await page.getByRole('button', { name: 'Ok' }).click();

	await expect(
		page.getByTestId('sub-message-table-row-Submessage 1')
	).toBeVisible();
});

test('create structure', async ({ page }) => {
	await page.setViewportSize({ width: 1600, height: 1000 });
	await page.goto('/ple');
	await page.getByRole('link', { name: 'MIM' }).click();
	await page.getByRole('link', { name: 'Connections' }).click();
	await selectBranch(page, 'Working', branchName);
	// Selecting the branch triggers an async graph load; wait for the connection node to render
	// before clicking it instead of racing the fetch with a default-timeout click.
	const connectionNode = page.getByText('Connection A-B', { exact: true });
	await expect(connectionNode).toBeVisible({ timeout: 60000 });
	await connectionNode.click();
	await page
		.locator('button')
		.filter({ hasText: /^expand_more$/ })
		.click();
	await page.getByTestId('submessage-details-btnSubmessage 1').click();
	await page.getByTestId('add-structure').waitFor({ state: 'visible' });
	await page.getByTestId('add-structure').click();
	await page.getByRole('button', { name: 'Create new Structure' }).click();
	// Wait for the dialog before filling.
	await expect(page.getByLabel('Name', { exact: true })).toBeVisible();
	await page.getByLabel('Name', { exact: true }).fill('Structure 1');
	await page.getByText('Max Simultaneity', { exact: true }).click();
	await page.getByLabel('Max Simultaneity').fill('1');
	await page.getByText('Min Simultaneity', { exact: true }).click();
	await page.getByLabel('Min Simultaneity').fill('1');
	await page.getByPlaceholder('Structure Categories').click();
	// Options load async; wait for the option before clicking.
	const structureCategoryOption = page.getByRole('option', { name: 'Misc' });
	await expect(structureCategoryOption).toBeVisible({ timeout: 60000 });
	await structureCategoryOption.click();
	await page.getByRole('button', { name: 'Next' }).click();
	await page.getByRole('button', { name: 'Ok' }).click();

	await expect(
		page
			.getByRole('row', { name: 'Structure 1 1 1 0 Misc' })
			.getByTestId('structure-table-expand-button')
	).toBeVisible();
});

test('create elements', async ({ page }) => {
	// Loose ceiling for the ~3.2m sequential runtime; per-action timeouts below catch stuck steps.
	test.setTimeout(300000);
	// Bound every action so a stuck step fails at its own line, not after the whole test budget.
	page.setDefaultTimeout(20000);
	await page.goto('/ple');
	await page.getByRole('link', { name: 'MIM' }).click();
	await page.getByRole('link', { name: 'Connections' }).click();
	await selectBranch(page, 'Working', branchName);
	// Selecting the branch triggers an async graph load; wait for the connection node to render
	// before clicking it instead of racing the fetch with a default-timeout click.
	const connectionNode = page.getByText('Connection A-B', { exact: true });
	await expect(connectionNode).toBeVisible({ timeout: 60000 });
	await connectionNode.click();
	await page
		.locator('button')
		.filter({ hasText: /^expand_more$/ })
		.click();
	await page.getByTestId('submessage-details-btnSubmessage 1').click();
	await page
		.getByRole('row', { name: 'Structure 1 1 1 0 Misc' })
		.getByTestId('structure-table-expand-button')
		.waitFor({ state: 'visible' });
	await page
		.getByRole('row', { name: 'Structure 1 1 1 0 Misc' })
		.getByTestId('structure-table-expand-button')
		.click();

	// Create Integer Element
	await createElement(
		page,
		'Integer Element',
		'Integer',
		'Integer 0-100 Meters',
		'32',
		'1',
		'1',
		'meters'
	);

	// Create Demo Fault element
	await page.getByRole('button', { name: 'Add Element to:' }).click();
	await page.getByRole('menuitem', { name: 'Structure' }).click();
	await page.getByRole('button', { name: 'Create new Element' }).click();
	await expect(page.getByLabel('Name')).toBeVisible();
	await page.getByLabel('Name').fill('Demo Fault');
	await page
		.locator('osee-platform-type-dropdown')
		.getByRole('button')
		.click();
	await page
		.locator('osee-logical-type-dropdown')
		.getByTestId('logical-type-selector')
		.locator('span')
		.click();
	// Logical-type options load async; wait for the option before clicking.
	const enumerationOption = page.getByText('Enumeration');
	await expect(enumerationOption).toBeVisible({ timeout: 60000 });
	await enumerationOption.click();
	// Step 2 renders in two phases: a minimal form, then the real fields once
	// GET /mim/logicalType/<id> resolves. Filling before that fetch lands writes into inputs the
	// second render replaces, dropping the value. Wait for the fetch before filling.
	await Promise.all([
		page.waitForResponse((res) => res.url().includes('/mim/logicalType/')),
		page
			.getByLabel('1Select a logical type')
			.getByRole('button', { name: 'Next' })
			.click(),
	]);
	await page
		.getByLabel('2Fill out type information')
		.getByLabel('Name')
		.fill('Demo Fault');
	await page.getByLabel('Bit Size').click();
	await page.getByLabel('Bit Size').fill('32');
	await page.getByTestId('create-enum-set').click();
	await page.getByTestId('enum-set-name-field').click();
	await page.getByTestId('enum-set-name-field').fill('Demo Fault');
	await page.getByTestId('add-enum-button').click();
	await page.getByText('Enter a name').click();
	await page.getByLabel('Enter a name').fill('Warning');
	await page.getByTestId('add-enum-button').click();
	await page
		.getByRole('cell', { name: 'Enter a name', exact: true })
		.locator('mat-label')
		.click();
	await page
		.getByRole('row', { name: 'Enter a name Enter an ordinal' })
		.getByLabel('Enter a name')
		.fill('Error');
	await page.getByTestId('add-enum-button').click();
	await page
		.getByRole('cell', { name: 'Enter a name', exact: true })
		.locator('mat-label')
		.click();
	await page
		.getByRole('row', { name: 'Enter a name Enter an ordinal' })
		.getByLabel('Enter a name')
		.fill('Info');
	// Commit the last enum literal by blurring the focused input. Blur the currently-focused
	// element rather than re-resolving the row by name: filling the row changes its accessible name
	// (it no longer contains the "Enter a name" placeholder), so a name-based re-lookup finds nothing.
	await page.locator(':focus').blur();
	// Gate on Next enabling (form valid + async unique settled).
	const enumTypeNext = page.getByTestId('type-form-next');
	await expect(enumTypeNext).toBeEnabled();
	await enumTypeNext.click();
	await page.getByRole('button', { name: 'Ok' }).click();
	await page.getByRole('button', { name: 'Next' }).click();
	await page.getByRole('button', { name: 'Ok' }).click();

	// Create ETA element
	await createElement(
		page,
		'ETA',
		'Long',
		'ETA Seconds',
		'64',
		'0',
		'2^64',
		'seconds'
	);

	// Create Yes or No element
	await createElement(
		page,
		'Yes or No',
		'Boolean',
		'Boolean',
		'8',
		'',
		'',
		''
	);

	await page.getByTestId('structure-table').screenshot({
		animations: 'disabled',
		path: 'screenshots/create-icd/element-table-with-spare.png',
	});

	await page
		.getByTestId('element-table-row-Yes or No')
		.getByTestId('element-table-cell-interfaceElementIndexStart')
		.locator('.mat-mdc-input-element')
		.click();
	await page
		.getByTestId('element-table-row-Yes or No')
		.getByTestId('element-table-cell-interfaceElementIndexStart')
		.locator('.mat-mdc-input-element')
		.fill('1', { force: true, timeout: 60000 });
	await page
		.getByTestId('element-table-row-Yes or No')
		.getByTestId('element-table-cell-interfaceElementIndexEnd')
		.locator('.mat-mdc-input-element')
		.click();
	await page
		.getByTestId('element-table-row-Yes or No')
		.getByTestId('element-table-cell-interfaceElementIndexEnd')
		.locator('.mat-mdc-input-element')
		.fill('8', { force: true, timeout: 60000 });

	await Promise.all([
		page.waitForResponse(
			(res) => res.url().includes('structures') && res.status() === 200
		),
		page
			.getByText('Items per page: 25 1 – 1 of 1 Add Element to: 1add')
			.click(),
	]);

	await expect(page.getByTestId('structure-table')).toBeVisible({
		timeout: 60000,
	});
	await page.getByTestId('structure-table').screenshot({
		animations: 'disabled',
		path: 'screenshots/create-icd/element-table-complete.png',
		timeout: 60000,
	});
});

test('add array element', async ({ page }) => {
	await page.setViewportSize({ width: 1200, height: 900 });
	await page.goto('/ple/messaging/connections?branchType=working');
	await selectBranch(page, 'Working', branchName);
	// Selecting the branch triggers an async graph load; wait for the connection node to render
	// before clicking it instead of racing the fetch with a default-timeout click.
	const connectionNode = page.getByText('Connection A-B', { exact: true });
	await expect(connectionNode).toBeVisible({ timeout: 60000 });
	await connectionNode.click();
	await page
		.locator('button')
		.filter({ hasText: /^expand_more$/ })
		.click();
	await page.getByTestId('submessage-details-btnSubmessage 1').click();
	await page
		.getByRole('row', { name: 'Structure 1 1 1 0 Misc' })
		.getByTestId('structure-table-expand-button')
		.waitFor({ state: 'visible' });
	await page
		.getByRole('row', { name: 'Structure 1 1 1 0 Misc' })
		.getByTestId('structure-table-expand-button')
		.click();
	await page.getByRole('button', { name: 'Add Element to:' }).click();
	await page.getByRole('menuitem', { name: 'Structure' }).click();
	await page.getByRole('button', { name: 'Create new Element' }).click();
	// Wait for the dialog before filling.
	await expect(page.getByLabel('Name', { exact: true })).toBeVisible();
	await page.getByLabel('Name', { exact: true }).fill('Test Char');

	await page.screenshot({
		animations: 'disabled',
		path: 'screenshots/create-element/create-element-dialog.png',
	});

	await page.getByLabel('Array Header').click();
	await page.getByLabel('Array Index Delimiter 1').click();
	await page.getByLabel('Array Index Delimiter 1').fill('_');
	await page.getByLabel('Array Index Delimiter 2').click();
	await page.getByLabel('Array Index Delimiter 2').fill('-');

	await page.screenshot({
		animations: 'disabled',
		path: 'screenshots/create-element/create-array-header-delimiters.png',
	});
});

async function createElement(
	page: Page,
	name: string,
	logicalType: string,
	platformTypeName: string,
	bitSize: string,
	min: string,
	max: string,
	units: string
) {
	await page.getByRole('button', { name: 'Add Element to:' }).click();
	await page.getByRole('menuitem', { name: 'Structure' }).click();
	await page.getByRole('button', { name: 'Create new Element' }).click();
	// Wait for the dialog to render before filling.
	const elementName = page.getByLabel('Name');
	await expect(elementName).toBeVisible();
	await elementName.fill(name);
	await page
		.locator('osee-platform-type-dropdown')
		.getByRole('button')
		.click();
	await page
		.locator('osee-logical-type-dropdown')
		.getByTestId('logical-type-selector')
		.locator('span')
		.click();
	// Logical-type options load async; wait for the option before clicking.
	const logicalTypeOption = page.getByRole('option', {
		name: logicalType,
		exact: true,
	});
	await expect(logicalTypeOption).toBeVisible({ timeout: 60000 });
	await logicalTypeOption.click();
	// Step 2 renders in two phases; wait for GET /mim/logicalType/<id> before filling so the value
	// is not dropped by the second render.
	await Promise.all([
		page.waitForResponse((res) => res.url().includes('/mim/logicalType/')),
		page
			.getByLabel('1Select a logical type')
			.getByRole('button', { name: 'Next' })
			.click(),
	]);
	await page
		.getByLabel('2Fill out type information')
		.getByLabel('Name')
		.fill(platformTypeName, { timeout: 60000 });
	await page.getByLabel('Bit Size').fill(bitSize);

	if (min !== '') {
		await page.getByLabel('Minval').fill(min);
	}

	if (max !== '') {
		await page.getByLabel('Maxval').fill(max);
	}

	if (units !== '') {
		await page.getByRole('combobox', { name: 'Units' }).click();
		// Options load async; wait for the option before clicking.
		const unitsOption = page.getByRole('option', { name: units });
		await expect(unitsOption).toBeVisible({ timeout: 60000 });
		await unitsOption.click();
	}
	// Gate on Next enabling (form valid + async unique settled) before clicking.
	const page2Next = page
		.getByLabel('2Fill out type information')
		.getByRole('button', { name: 'Next' });
	await expect(page2Next).toBeEnabled();
	await page2Next.click();
	await page.getByRole('button', { name: 'Ok' }).click({ force: true });
	await page.getByRole('button', { name: 'Next' }).click({ force: true });
	await expect(page.getByTestId('submit-btn')).toBeVisible({
		timeout: 60000,
	});
	await expect(page.getByTestId('submit-btn')).toBeEnabled({
		timeout: 60000,
	});
	await page.getByTestId('submit-btn').click({ force: true, timeout: 40000 });

	const response = await page.waitForResponse(
		new RegExp('^.+mim.+connections.+messages.+submessages.+structures.+$'),
		{ timeout: 60000 }
	);
	await expect(response.status()).toBe(200);
}
