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

test('test', async ({ page }) => {
	test.setTimeout(600000);
	await page.goto('http://localhost:4200/ple');
	await page.getByRole('link', { name: 'MIM' }).click();
	await page.getByRole('link', { name: 'Connections' }).click();
	await page.getByLabel('Product Line').check();
	await page.getByText('Select a Branch').click();
	await page.getByText('SAW Product Line').click();

	await expect(page.getByText('Node B')).toBeVisible();
	await page.keyboard.press('Escape'); // Get rid of a tooltip
	await page.screenshot({
		animations: 'disabled',
		path: 'screenshots/example-icd/overview-connection.png',
		clip: { x: 0, y: 0, width: 650, height: 325 },
	});

	await page
		.getByTestId('link-Connection A-B')
		.getByText('Connection A-B')
		.click();
	await expect(
		page
			.getByTestId('message-table-row-Message 1')
			.getByTestId('msg-field-name')
			.getByTestId('inner-styling')
			.getByTestId('message-table-name-Message 1-Message 1')
	).toBeVisible({ timeout: 30000 });
	await page.screenshot({
		path: 'screenshots/example-icd/message-table.png',
		clip: { x: 0, y: 0, width: 650, height: 325 },
	});

	await page
		.getByRole('row', { name: 'Message 1 1 Periodic 5 true' })
		.getByRole('button')
		.click();
	await expect(page.getByText('Submessage 1')).toBeVisible();
	await page.waitForTimeout(500);

	await page.screenshot({
		path: 'screenshots/example-icd/submessage-table.png',
	});

	await page
		.getByRole('row', {
			name: 'Submessage 1 1 Go To Message Details',
			exact: true,
		})
		.getByRole('link')
		.click();
	await page
		.getByRole('row', { name: 'Structure 1 1 1 0' })
		.getByRole('button')
		.click();
	await page.waitForTimeout(500);
	await page.getByText('Demo Fault').click();

	await page.screenshot({
		path: 'screenshots/example-icd/structure-table.png',
	});
});
