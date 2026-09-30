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
import { test, expect } from '@ngx-playwright/test';
import { createWorkingBranchFromPL, enableEditMode } from '../utils/helpers';

test('test', async ({ page }) => {
	await page.setViewportSize({ width: 1200, height: 1000 });
	await page.goto('/ple');
	await page.getByRole('link', { name: 'MIM' }).click();
	await page.getByRole('link', { name: 'Platform Types' }).click();
	await createWorkingBranchFromPL(page, 'Platform Types');
	await enableEditMode(page);
	await page.locator('osee-platform-types-fab').locator('button').click();
	await expect(page.locator('osee-new-type-form')).toBeVisible();
	await expect(page.getByTestId('logical-type-selector')).toBeVisible();
	await page
		.locator('osee-logical-type-dropdown')
		.getByTestId('logical-type-selector')
		.locator('.mat-mdc-select-trigger')
		.locator('span')
		.first()
		.click();

	await page
		.getByRole('option', { name: 'Integer', exact: true })
		.locator('span')
		.click();

	await page.screenshot({
		path: 'screenshots/platform-types-page/select-logical-type.png',
		animations: 'disabled',
	});

	// Wait for the step-2 fields to render (two-phase; see below) before filling.
	await Promise.all([
		page.waitForResponse((res) => res.url().includes('/mim/logicalType/')),
		page.getByRole('button', { name: 'Next' }).click(),
	]);
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
	await page.getByLabel('Default Value').blur();

	await page.screenshot({
		path: 'screenshots/platform-types-page/create-platform-type.png',
		animations: 'disabled',
	});

	// Gate on the step-2 Next being enabled (form valid + async uniqueness settled) before clicking.
	const intTypeNext = page.getByTestId('type-form-next');
	await expect(intTypeNext).toBeEnabled({ timeout: 20000 });
	await intTypeNext.click();
	await page.getByRole('button', { name: 'Ok' }).click();
	await page.locator('mat-row:nth-child(3) > mat-cell:nth-child(2)').click();
	await page.locator('osee-platform-types-fab').locator('button').click();
	await expect(page.locator('osee-new-type-form')).toBeVisible();
	await page
		.locator('osee-logical-type-dropdown')
		.getByTestId('logical-type-selector')
		.click();
	await page.getByText('Enumeration', { exact: true }).click();
	// Step 2 renders in two phases: a minimal form first, then the real fields once
	// GET /mim/logicalType/<id> resolves. Filling before that fetch lands writes into inputs that
	// the second render replaces, silently dropping the value. Wait for the fetch before filling.
	await Promise.all([
		page.waitForResponse((res) => res.url().includes('/mim/logicalType/')),
		page.getByRole('button', { name: 'Next' }).click(),
	]);
	await page.getByLabel('Name').fill('Decision');
	await page.getByLabel('Bit Size').click();
	await page.getByLabel('Bit Size').fill('32');

	await page.screenshot({
		path: 'screenshots/platform-types-page/select-enumeration-set.png',
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
	// Blur the input to commit the value (Tab does not reliably move focus in headless).
	await page.getByLabel('Enter a name').nth(2).blur();

	await page.screenshot({
		path: 'screenshots/platform-types-page/added-enums.png',
		animations: 'disabled',
	});

	// Next is gated on the step-2 form group being valid AND not pending: the Name and Enumeration
	// Set Name are required, and async uniqueness validators run on blur. Assert the button is
	// enabled (which waits out the in-flight async validators) before clicking, rather than clicking
	// a still-disabled button and timing out.
	const enumTypeNext = page.getByTestId('type-form-next');
	await expect(enumTypeNext).toBeEnabled({ timeout: 20000 });
	await enumTypeNext.click();
	await page.getByRole('button', { name: 'Ok' }).click();
});
