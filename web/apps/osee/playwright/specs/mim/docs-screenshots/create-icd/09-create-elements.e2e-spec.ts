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
import { test } from '@ngx-playwright/test';

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
	await page.getByRole('button', { name: 'Add Element to:' }).click();
	await page.getByRole('menuitem', { name: 'Structure' }).click();
	await page.getByRole('button', { name: 'Create new Element' }).click();
	await page
		.locator('div')
		.filter({ hasText: /^Name$/ })
		.nth(2)
		.click();
	await page.getByLabel('Name').fill('Integer Element');
	await page
		.locator('osee-platform-type-dropdown')
		.getByRole('button')
		.click();
	await page.getByLabel('', { exact: true }).locator('span').click();
	await page.getByRole('option', { name: 'Integer', exact: true }).click();
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
		.fill('Integer 0-100 Meters');
	await page.getByLabel('Bit Size').click();
	await page.getByLabel('Bit Size').fill('32');
	await page.getByLabel('Minval').click();
	await page.getByLabel('Minval').fill('1');
	await page.getByLabel('Maxval').click();
	await page.getByLabel('Maxval').fill('1');
	await page.getByPlaceholder('Units').click();
	await page.getByRole('option', { name: 'meters' }).click();
	await page
		.getByLabel('2Fill out type information')
		.getByRole('button', { name: 'Next' })
		.click();
	await page.getByRole('button', { name: 'Ok' }).click();
	await page.getByRole('button', { name: 'Next' }).click();
	await page.getByRole('button', { name: 'Ok' }).click();

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
	await page
		.getByLabel('Add Element to Structure')
		.locator('button')
		.filter({ hasText: 'add' })
		.click();
	await page
		.locator('div')
		.filter({ hasText: /^Enumeration Set Name$/ })
		.click();
	await page.getByLabel('Enumeration Set Name').fill('Demo Fault');
	await page.locator('osee-enum-form').getByRole('button').click();
	await page.getByText('Enter a name').click();
	await page.getByLabel('Enter a name').fill('Warning');
	await page
		.getByLabel('Add Element to Structure')
		.locator('button')
		.filter({ hasText: 'add' })
		.click();
	await page
		.getByRole('cell', { name: 'Enter a name', exact: true })
		.locator('mat-label')
		.click();
	await page
		.getByRole('row', { name: 'Enter a name Enter an ordinal' })
		.getByLabel('Enter a name')
		.fill('Error');
	await page
		.getByLabel('Add Element to Structure')
		.locator('button')
		.filter({ hasText: 'add' })
		.click();
	await page
		.getByRole('cell', { name: 'Enter a name', exact: true })
		.locator('mat-label')
		.click();
	await page
		.getByRole('row', { name: 'Enter a name Enter an ordinal' })
		.getByLabel('Enter a name')
		.fill('Info');
	await page
		.getByLabel('2Fill out type information')
		.getByRole('button', { name: 'Next' })
		.click();
	await page.getByRole('button', { name: 'Ok' }).click();
	await page.getByRole('button', { name: 'Next' }).click();
	await page.getByRole('button', { name: 'Ok' }).click();

	// Create ETA element
	await page.getByRole('button', { name: 'Add Element to:' }).click();
	await page.getByRole('menuitem', { name: 'Structure' }).click();
	await page.getByRole('button', { name: 'Create new Element' }).click();
	await page.getByLabel('Name').click();
	await page.getByLabel('Name').fill('ETA');
	await page
		.locator('osee-platform-type-dropdown')
		.getByRole('button')
		.click();
	await page.getByLabel('', { exact: true }).locator('span').click();
	await page.getByText('Long', { exact: true }).click();
	await page
		.getByLabel('1Select a logical type')
		.getByRole('button', { name: 'Next' })
		.click();
	await page
		.getByLabel('2Fill out type information')
		.locator('div')
		.filter({ hasText: /^Name$/ })
		.nth(2)
		.click();
	await page
		.getByLabel('2Fill out type information')
		.getByLabel('Name')
		.fill('ETA Seconds');
	await page
		.locator('div')
		.filter({ hasText: /^Bit Size$/ })
		.nth(2)
		.click();
	await page.getByLabel('Bit Size').fill('64');
	await page.getByLabel('Minval').click();
	await page.getByLabel('Minval').fill('0');
	await page
		.locator('div')
		.filter({ hasText: /^Maxval$/ })
		.nth(2)
		.click();
	await page.getByLabel('Maxval').click();
	await page.getByLabel('Maxval').fill('2^64');
	await page.getByRole('combobox', { name: 'Units' }).click();
	await page.getByRole('option', { name: 'seconds' }).click();
	await page
		.getByLabel('2Fill out type information')
		.getByRole('button', { name: 'Next' })
		.click();
	await page.getByRole('button', { name: 'Ok' }).click();
	await page.getByRole('button', { name: 'Next' }).click();
	await page.getByRole('button', { name: 'Ok' }).click();

	// Create Yes or No element
	await page.getByRole('button', { name: 'Add Element to:' }).click();
	await page.getByRole('menuitem', { name: 'Structure' }).click();
	await page.getByRole('button', { name: 'Create new Element' }).click();
	await page.getByLabel('Name').click();
	await page.getByLabel('Name').fill('Yes or No');
	await page
		.locator('osee-platform-type-dropdown')
		.getByRole('button')
		.click();
	await page
		.locator('div')
		.filter({ hasText: /^Logical Type$/ })
		.nth(3)
		.click();
	await page.getByRole('option', { name: 'Boolean' }).click();
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
		.fill('Boolean');
	await page
		.locator('div')
		.filter({ hasText: /^Bit Size$/ })
		.nth(2)
		.click();
	await page.getByLabel('Bit Size').fill('8');
	await page
		.getByLabel('2Fill out type information')
		.getByRole('button', { name: 'Next' })
		.click();
	await page.getByRole('button', { name: 'Ok' }).click();
	await page.getByRole('button', { name: 'Next' }).click();
	await page.getByRole('button', { name: 'Ok' }).click();

	// Change start and end index in table
	await page.getByText('account_circle').click();
	await page.getByRole('menuitem', { name: 'Settings' }).click();
	await page
		.getByRole('option', { name: 'Start Index' })
		.locator('div')
		.first()
		.click();
	await page
		.getByRole('option', { name: 'End Index' })
		.locator('div')
		.first()
		.click();
	await page.getByRole('button', { name: 'Ok' }).click();
	await page.locator('#mat-input-213').click();
	await page.locator('#mat-input-213').fill('1');
	await page.locator('#mat-input-214').click();
	await page.locator('#mat-input-247').fill('8');
	await page
		.getByText('Items per page: 25 1 – 1 of 1 Add Element to: 1add')
		.click();

	await page.waitForTimeout(500);
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
		.getByRole('row', { name: 'Structure 1 1 1 0 Misc 3 16' })
		.getByRole('button')
		.click();
	await page.getByRole('button', { name: 'Add Element to:' }).click();
	await page.getByRole('menuitem', { name: 'Structure' }).click();
	await page.getByRole('button', { name: 'Create new Element' }).click();
	await page.getByLabel('Name', { exact: true }).click();
	await page.getByLabel('Name', { exact: true }).fill('Test Char');

	await page.screenshot({
		animations: 'disabled',
		path: 'screenshots/create-element-dialog.png',
	});

	await page.getByLabel('Array Header').click();
	await page.getByLabel('Use Array Header Name in').click();
	await page.getByLabel('Array Index Delimiter 1').click();
	await page.getByLabel('Array Index Delimiter 1').fill('_');
	await page.getByLabel('Array Index Delimiter 2').click();
	await page.getByLabel('Array Index Delimiter 2').fill('-');

	await page.screenshot({
		animations: 'disabled',
		path: 'screenshots/create-array-header-delimiters.png',
	});
});
