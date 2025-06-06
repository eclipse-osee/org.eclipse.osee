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
import { createWorkingBranchFromPL } from '../utils/helpers';

test.describe.configure({ mode: 'serial' });

test('set up relations', async ({ page }) => {
	await page.setViewportSize({ width: 1200, height: 1000 });
	await page.goto('http://localhost:4200/ple/messaging/connections');
	await createWorkingBranchFromPL(page, 'Traceability');
	await page.goto('http://localhost:4200/ple');
	await page.getByRole('button').click();
	await page
		.locator('osee-top-level-navigation')
		.getByText('Product Line Engineering')
		.click();

	await page.screenshot({
		path: 'screenshots/traceability/artifact-explorer-navigation.png',
		animations: 'disabled',
	});

	await page.getByText('travel_explore Artifact').click();
	await page.getByLabel('Working').check();
	await page.getByText('Select a Branch').click();
	await page
		.getByRole('option', { name: 'Traceability' })
		.locator('span')
		.click();

	// Open Demo Fault tab
	await page.getByRole('button', { name: 'Artifact Search' }).click();
	await page.locator('button').filter({ hasText: 'more_horiz' }).click();
	await page
		.locator('mat-form-field')
		.filter({ hasText: 'Artifact Types' })
		.locator('mat-chip-set')
		.click();
	await page
		.getByRole('combobox', { name: 'Artifact Types' })
		.fill('element');
	await page.getByText('Interface DataElement', { exact: true }).click();
	await page.getByRole('button', { name: 'Ok' }).click();
	await page.locator('button').filter({ hasText: 'search' }).click();
	await page.getByRole('button', { name: 'Demo Fault' }).click();

	// Create software requirement
	await page
		.getByRole('button', { name: 'Software Requirements', exact: true })
		.click({
			button: 'right',
		});
	await page.getByRole('menuitem', { name: 'Create Child Artifact' }).click();
	await page.getByLabel('Enter a Name:').fill('Fault Handling');
	await page.getByPlaceholder('Unspecified').click();
	await page.getByPlaceholder('Unspecified').fill('software requirement');
	await page.getByText('Software Requirement', { exact: true }).click();
	await page.locator('input[name="\\30 "]').click();
	await page.getByText('C', { exact: true }).click();
	await page.locator('[id="\\31 "]').click();
	await page.getByText('Design Constraint').click();
	await page.locator('[id="\\32 "]').click();
	await page.getByText('Navigation').click();
	await page.locator('[id="\\33 "]').click();
	await page.getByText('Aircraft Systems').click();
	await page.locator('[id="\\33 "]').fill('');
	await page.getByText('Navigation').click();
	await page.getByRole('button', { name: 'Ok' }).click();

	// Relate requirement to element
	await page.getByRole('button', { name: 'Relations Editor' }).click();
	await page.waitForTimeout(500);
	await page.keyboard.press('End');
	await page
		.locator('div:nth-child(19) > div > button')
		.click({ force: true });
	await page.waitForTimeout(500);
	await page.keyboard.press('End');
	await page
		.locator('#relationList > div > button')
		.first()
		.click({ force: true });
	await page.waitForTimeout(500);
	await page.keyboard.press('End');

	// Drag and drop requirement to relation tree
	await page.getByRole('button', { name: 'Fault Handling' }).hover();
	await page.mouse.down();
	await page.getByText('Requirement Artifact').hover();
	await page.getByText('Requirement Artifact').hover();
	await page.mouse.up();
	await page.waitForTimeout(500);

	await page.screenshot({
		path: 'screenshots/traceability/artifact-explorer-relations.png',
		animations: 'disabled',
	});
});

test('trace report', async ({ page }) => {
	await page.setViewportSize({ width: 1200, height: 900 });
	await page.goto('http://localhost:4200/ple');
	await page.getByRole('link', { name: 'MIM' }).click();
	await page.getByRole('link', { name: 'Reports' }).click();
	await page.getByLabel('Working').check();
	await page.getByPlaceholder('Branches').click();
	await page.getByText('Traceability').click();
	await page.getByRole('link', { name: 'Traceability Report' }).click();
	await page.getByRole('radio', { name: 'Requirements' }).click();
	await expect(page.getByText('Demo Fault')).toBeVisible();

	await page.screenshot({
		path: 'screenshots/traceability/trace-report-requirements.png',
		animations: 'disabled',
		clip: { x: 0, y: 0, height: 500, width: 1200 },
	});

	await page.getByRole('radio', { name: 'MIM Artifacts' }).click();
	await expect(page.getByText('Demo Fault')).toBeVisible();

	await page.screenshot({
		path: 'screenshots/traceability/trace-report-mim-artifacts.png',
		animations: 'disabled',
		clip: { x: 0, y: 0, height: 500, width: 1200 },
	});
});
