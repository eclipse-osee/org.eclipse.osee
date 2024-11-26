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

test.describe.configure({ mode: 'serial' });

test('create working branches', async ({ page }) => {
	await page.goto('http://localhost:4200/ple');

	// Commit MIM Demo branch to create baseline
	await page.getByRole('link', { name: 'MIM' }).click();
	await page.getByRole('link', { name: 'Connections' }).click();

	// Create first working branch
	await createWorkingBranchFromPL(page, 'Edit Message Description');
	await enableEditMode(page);
	await page.getByText('Connection A-B', { exact: true }).click();
	await page.locator('#mat-input-12').click();
	await page.locator('#mat-input-12').fill('This is the first message');

	await Promise.all([
		page.waitForResponse(
			(res) =>
				res.url() === 'http://localhost:4200/orcs/txs' &&
				res.status() === 200
		),
		page.locator('#mat-input-12').press('Tab'),
	]);

	await page.getByRole('link', { name: 'working' }).click();

	// Create second working branch
	await createWorkingBranchFromPL(page, 'Edit Submessage Description');
	await enableEditMode(page);
	await page.getByText('Connection A-B', { exact: true }).click();
	await page
		.locator('button')
		.filter({ hasText: /^expand_more$/ })
		.click();
	await page.locator('#mat-input-39').click();
	await page.locator('#mat-input-39').fill('This is a new description');

	await Promise.all([
		page.waitForResponse(
			(res) =>
				res.url() === 'http://localhost:4200/orcs/txs' &&
				res.status() === 200
		),
		page.locator('#mat-input-39').press('Tab'),
	]);

	await page.getByRole('link', { name: 'working' }).click();

	// Create third working branch
	await createWorkingBranchFromPL(page, 'Add an Element');
	await enableEditMode(page);
	await page.locator('rect').nth(1).click();
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
		.click({ timeout: 60000 });
	await page.getByRole('button', { name: 'Add Element to:' }).click();
	await page.getByRole('menuitem', { name: 'Structure 1' }).click();
	await page.getByRole('button', { name: 'Create new Element' }).click();
	await page.getByLabel('Name').fill('New Element', { force: true });

	await Promise.all([
		page.waitForResponse((res) => res.url().includes('types/filter'), {
			timeout: 60000,
		}),
		page
			.getByLabel('2Define element')
			.getByText('Platform Type')
			.click({ force: true }),
	]);
	await page
		.getByText('Float', { exact: true })
		.click({ timeout: 60000, force: true });
	await page.getByRole('button', { name: 'Next' }).click();

	await Promise.all([
		page.waitForResponse(
			(res) => res.url().includes('structures') && res.status() === 200
		),
		page.getByTestId('submit-btn').click({ force: true, timeout: 40000 }),
	]);
});

test('peer review branch', async ({ page }) => {
	await page.setViewportSize({ width: 1200, height: 800 });
	await page.goto('http://localhost:4200/ple');
	await page.getByRole('link', { name: 'MIM' }).click();
	await page.getByRole('link', { name: 'Connections' }).click();
	await page.getByLabel('Product Line').check();
	await page.getByText('Select a Branch').click();
	await page.getByText('SAW Product Line').click();

	await page.screenshot({
		animations: 'disabled',
		path: 'screenshots/peer-review/peer-review-button.png',
		clip: { x: 0, y: 0, width: 1200, height: 375 },
	});

	await page.getByRole('button', { name: 'Peer Review' }).click();
	await page.getByRole('button', { name: 'Create Peer Review' }).click();
	await page.getByLabel('Title').fill('MIM Peer Review');
	await page.getByLabel('Actionable Item').click();
	await page.getByRole('combobox', { name: 'Actionable Item' }).fill('mim');
	await page.getByText('SAW PL MIM').click();
	await page.getByLabel('Description').fill('Peer review');
	await page.getByLabel('Change Type').locator('span').click();
	await page.getByText('Improvement').click();

	let requestPromise = page.waitForResponse((response) =>
		response.url().startsWith('http://localhost:4200/ats/ple/branches/pr')
	);
	await page.getByRole('button', { name: 'Create Action' }).click();
	let response = await requestPromise;
	expect(response.status()).toBe(200);

	await page
		.getByRole('option', { name: 'Edit Message Description' })
		.locator('div')
		.first()
		.click();
	await page
		.getByRole('option', { name: 'Edit Submessage' })
		.locator('div')
		.first()
		.click();

	await page.screenshot({
		animations: 'disabled',
		path: 'screenshots/peer-review/peer-review-added-selections.png',
	});

	requestPromise = page.waitForResponse((response) =>
		response.url().startsWith('http://localhost:4200/ats/ple/branches/pr')
	);
	await page.getByRole('button', { name: 'Apply Selected' }).click();
	response = await requestPromise;
	expect(response.status()).toBe(200);

	await page.screenshot({
		animations: 'disabled',
		path: 'screenshots/peer-review/peer-review-applied.png',
	});

	await page
		.getByRole('option', { name: 'Edit Submessage' })
		.locator('span')
		.first()
		.click();
	await page
		.getByRole('option', { name: 'Add an Element' })
		.locator('span')
		.first()
		.click();

	await page.screenshot({
		animations: 'disabled',
		path: 'screenshots/peer-review/peer-review-add-remove.png',
	});

	await page.getByRole('button', { name: 'Apply Selected' }).click();
	await page.getByRole('button', { name: 'Close' }).click();
});
