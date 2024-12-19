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
import { expect, test } from '@ngx-playwright/test';
import { enableEditMode } from '../utils/helpers';

test('test', async ({ page }) => {
	await page.setViewportSize({ width: 1200, height: 800 });
	await page.goto('http://localhost:4200/ple');
	await page.getByRole('link', { name: 'MIM' }).click();
	await page.getByRole('link', { name: 'Cross-References' }).click();
	await page.getByLabel('Working').check();
	await page.getByText('Select a Branch').click();
	await page.getByText('TW2 - MIM Demo').click();
	await page.getByLabel('Select a Connection').locator('span').click();
	await page.getByText('Connection A-B').click();

	await enableEditMode(page);

	// Create cross-reference
	await page.getByTestId('add-cross-ref').filter({ hasText: 'add' }).click();
	const nameField = page.getByTestId('field-name');
	await expect(nameField).toHaveValue('');
	await nameField.fill('C1');
	await expect(nameField).toHaveValue('C1');
	const valueField = page.getByTestId('field-value');
	await expect(valueField).toHaveValue('');
	await valueField.fill('Private Array');
	await expect(valueField).toHaveValue('Private Array');
	await page
		.getByTestId('add-array-value')
		.and(page.getByRole('button'))
		.click();
	await page.getByTestId('field-item-key-' + 0).fill('1');
	await page.getByTestId('field-item-value-' + 0).fill('FIRST_VAL');
	await page.getByTestId('add-array-value').click();
	await page.getByTestId('field-item-key-' + 1).fill('2');
	await page.getByTestId('field-item-value-' + 1).fill('SECOND_VAL');

	await expect(nameField).toHaveValue('C1');
	await expect(valueField).toHaveValue('Private Array');

	await page.screenshot({
		animations: 'disabled',
		path: 'screenshots/cross-references/create-cross-reference.png',
	});

	await page.getByRole('button', { name: 'Ok' }).click();
});
