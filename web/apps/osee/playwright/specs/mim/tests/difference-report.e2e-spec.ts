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
import { createWorkingBranchFromPL, enableEditMode } from '../utils/helpers';

test('test', async ({ page }) => {
	await page.setViewportSize({ width: 1200, height: 900 });
	await page.goto('http://localhost:4200/ple/messaging/connections');
	await createWorkingBranchFromPL(page, 'Difference Report');
	await enableEditMode(page);

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
		.getByRole('row', { name: 'Structure 1 1 1 0' })
		.getByRole('button')
		.click();
	await page
		.getByRole('cell', { name: 'Float Element', exact: true })
		.click();
	await page.keyboard.press('Control+a');
	await page.keyboard.type('Float');
	await page
		.getByText(
			'Select a Viewundochange_history In Workexpand_more Peer Review'
		)
		.click();
	await page
		.getByRole('cell', { name: 'Integer Element', exact: true })
		.click({
			button: 'right',
		});
	await page
		.getByRole('menuitem', { name: 'Remove element from structure' })
		.click();
	await page.getByRole('button', { name: 'Yes' }).click();

	// Create new element
	await page.getByRole('button', { name: 'Add Element to:' }).click();
	await page.getByRole('menuitem', { name: 'Structure' }).click();
	await page.getByRole('button', { name: 'Create new Element' }).click();
	await page
		.getByLabel('Add Element to Structure')
		.getByText('Name', { exact: true })
		.click();
	await page.getByLabel('Name').fill('New Element');
	await page.getByLabel('2Define element').getByText('Platform Type').click();
	await page.getByText('Integer').click();
	await page.getByRole('button', { name: 'Next' }).click();
	await page.getByRole('button', { name: 'Ok' }).click();

	// Go to difference report
	await page.locator('button').filter({ hasText: 'menu' }).click();
	await page.getByText('Product Line Engineering').click();
	await page.getByText('MIM').click();
	await page.getByRole('link', { name: 'Reports' }).click();
	await page.getByRole('link', { name: 'Difference Report' }).click();

	await expect(
		page.getByRole('heading', { name: 'Structures' })
	).toBeVisible();

	await page.keyboard.press('End');
	await page.waitForTimeout(500);

	await page.screenshot({
		path: 'screenshots/reports/difference-report.png',
		animations: 'disabled',
	});
});
