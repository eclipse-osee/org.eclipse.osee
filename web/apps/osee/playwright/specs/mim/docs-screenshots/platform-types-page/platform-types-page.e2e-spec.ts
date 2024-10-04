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

test('test', async ({ page }) => {
	await page.setViewportSize({ width: 1200, height: 900 });
	await page.goto('http://localhost:4200/ple');
	await page.getByRole('link', { name: 'MIM' }).click();
	await page.getByRole('link', { name: 'Platform Types' }).click();
	await page.getByLabel('Working').check();
	await page.getByText('Select a Branch').click();
	await page.getByText('MIM Demo').click();
	await page.getByText('account_circle').click();
	await page.getByRole('menuitem', { name: 'Settings' }).click();
	await page.getByLabel('Edit Mode').check();
	await page.getByRole('button', { name: 'Ok' }).click();
	await page.locator('button').filter({ hasText: 'add' }).click();
	await page.getByLabel('', { exact: true }).locator('span').click();
	await page
		.getByRole('option', { name: 'Integer', exact: true })
		.locator('span')
		.click();

	await page.screenshot({
		path: 'screenshots/select-logical-type.png',
		animations: 'disabled',
	});

	await page.getByRole('button', { name: 'Next' }).click();
	await page.getByLabel('Name').click();
	await page.getByLabel('Name').fill('Distance');
	await page.getByLabel('Bit Size').click();
	await page.getByLabel('Bit Size').fill('32');
	await page.getByLabel('Description').click();
	await page.getByLabel('Description').fill('Distance in meters');
	await page.getByLabel('Minval').click();
	await page.getByLabel('Minval').fill('0');
	await page.getByLabel('Maxval').click();
	await page.getByLabel('Maxval').fill('2000');
	await page.getByPlaceholder('Units').click();
	await page.getByRole('option', { name: 'Meters', exact: true }).click();
	await page.getByLabel('Default Value').click();
	await page.getByLabel('Default Value').fill('0');

	await page.screenshot({
		path: 'screenshots/create-platform-type.png',
		animations: 'disabled',
	});

	await page.getByRole('button', { name: 'Next' }).click();
	await page.getByRole('button', { name: 'Ok' }).click();
	await page.locator('mat-row:nth-child(3) > mat-cell:nth-child(2)').click();
	await page.locator('button').filter({ hasText: /^add$/ }).click();
	await page.getByLabel('', { exact: true }).locator('span').click();
	await page.getByText('Enumeration', { exact: true }).click();
	await page.getByRole('button', { name: 'Next' }).click();
	await page.getByLabel('Name').click();
	await page.getByLabel('Name').fill('Decision');
	await page.getByLabel('Bit Size').click();
	await page.getByLabel('Bit Size').fill('32');

	await page.screenshot({
		path: 'screenshots/select-enumeration-set.png',
		animations: 'disabled',
	});

	await page
		.getByLabel('2Fill out type information')
		.locator('button')
		.filter({ hasText: 'add' })
		.click();
	await page.getByLabel('Enumeration Set Name').click();
	await page.getByLabel('Enumeration Set Name').fill('Decision');
	await page.getByRole('columnheader', { name: 'Name' }).click();
	await page.locator('osee-enum-form').getByRole('button').click();
	await page
		.getByLabel('2Fill out type information')
		.locator('button')
		.filter({ hasText: 'add' })
		.scrollIntoViewIfNeeded();

	await page
		.locator('div')
		.filter({ hasText: /^Enter a name$/ })
		.nth(0)
		.click();
	await page.getByLabel('Enter a name').nth(0).fill('Yes');
	await page
		.getByLabel('2Fill out type information')
		.locator('button')
		.filter({ hasText: 'add' })
		.click();
	await page
		.getByLabel('2Fill out type information')
		.locator('button')
		.filter({ hasText: 'add' })
		.scrollIntoViewIfNeeded();
	await page
		.locator('div')
		.filter({ hasText: /^Enter a name$/ })
		.nth(1)
		.click();
	await page.getByLabel('Enter a name').nth(1).fill('No');
	await page
		.getByLabel('2Fill out type information')
		.locator('button')
		.filter({ hasText: 'add' })
		.click();
	await page
		.getByLabel('2Fill out type information')
		.locator('button')
		.filter({ hasText: 'add' })
		.scrollIntoViewIfNeeded();
	await page
		.locator('div')
		.filter({ hasText: /^Enter a name$/ })
		.nth(2)
		.click();
	await page.getByLabel('Enter a name').nth(2).fill('Maybe');
	await page.getByText('Back Next').click();

	await page.screenshot({
		path: 'screenshots/added-enums.png',
		animations: 'disabled',
	});

	await page.getByRole('button', { name: 'Next' }).click();
	await page.getByRole('button', { name: 'Ok' }).click();
});
