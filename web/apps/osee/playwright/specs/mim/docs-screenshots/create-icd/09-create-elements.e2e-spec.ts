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
import { Page, test, expect } from '@ngx-playwright/test';
import { screenshotOnFailure } from '../../../../utils/screenshot-on-failure';

test.afterEach(screenshotOnFailure);

test('create elements', async ({ page }) => {
	await page.goto('http://localhost:4200/ple');
	await page.getByRole('link', { name: 'MIM' }).click();
	await page.getByRole('link', { name: 'Connections' }).click();
	await page.getByLabel('Working').check();
	await page.getByPlaceholder('Branches').click();
	await page.getByText('TW2 - New MIM ICD').click();
	await page.getByText('Connection A-B', { exact: true }).click();
	await page
		.locator('button')
		.filter({ hasText: /^expand_more$/ })
		.click();
	await page
		.getByRole('row', {
			name: 'Submessage 1 1 Go To Message Details Base',
			exact: true,
		})
		.getByRole('link')
		.click();
	await page
		.getByRole('row', { name: 'Structure 1 1 1 0 Misc' })
		.getByRole('button')
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
	await page
		.locator('div')
		.filter({ hasText: /^Name$/ })
		.nth(2)
		.click();
	await page.getByLabel('Name').fill('Demo Fault');
	await page
		.locator('osee-platform-type-dropdown')
		.getByRole('button')
		.click();
	await page.getByLabel('', { exact: true }).locator('span').click();
	await page.getByText('Enumeration').click();
	await page
		.getByLabel('1Select a logical type')
		.getByRole('button', { name: 'Next' })
		.click();
	await page
		.getByLabel('2Fill out type information')
		.getByLabel('Name')
		.click();
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
	await page.waitForTimeout(500);
	await page.getByTestId('type-form-next').click();
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
	await page.goto('http://localhost:4200/ple/messaging/connections/working');
	await page.getByText('Select a Branch').click();
	await page.getByText('TW2 - New MIM ICD').click();
	await page.getByText('Connection A-B', { exact: true }).click();
	await page
		.locator('button')
		.filter({ hasText: /^expand_more$/ })
		.click();
	await page
		.getByRole('row', {
			name: 'Submessage 1 1 Go To Message Details Base',
			exact: true,
		})
		.getByRole('link')
		.click();
	await page
		.getByRole('row', { name: 'Structure 1 1 1 0 Misc' })
		.getByRole('button')
		.click();
	await page.getByRole('button', { name: 'Add Element to:' }).click();
	await page.getByRole('menuitem', { name: 'Structure' }).click();
	await page.getByRole('button', { name: 'Create new Element' }).click();
	await page.getByLabel('Name', { exact: true }).click();
	await page.getByLabel('Name', { exact: true }).fill('Test Char');

	await page.screenshot({
		animations: 'disabled',
		path: 'screenshots/create-element/create-element-dialog.png',
	});

	await page.getByLabel('Array Header').click();
	await page.getByLabel('Use Array Header Name in').click();
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
	await page.getByLabel('Name').fill(name);
	await page
		.locator('osee-platform-type-dropdown')
		.getByRole('button')
		.click();
	await page.getByLabel('', { exact: true }).locator('span').click();
	await page.getByRole('option', { name: logicalType, exact: true }).click();
	await page
		.getByLabel('1Select a logical type')
		.getByRole('button', { name: 'Next' })
		.click();
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
		await page.getByRole('option', { name: units }).click();
	}
	await page
		.getByLabel('2Fill out type information')
		.getByRole('button', { name: 'Next' })
		.click();
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
