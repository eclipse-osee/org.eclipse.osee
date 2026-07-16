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
import { selectBranch } from '../../../shared/branch-helpers';

test.describe.configure({ mode: 'serial' });

test('set up relations', async ({ page }) => {
	await page.setViewportSize({ width: 1200, height: 1000 });
	await page.goto('/ple/messaging/connections');
	await createWorkingBranchFromPL(page, 'Traceability');
	await page.goto('/ple');
	await page.locator('osee-toolbar').getByRole('button').first().click();
	await page
		.locator('osee-top-level-navigation')
		.getByText('Product Line Engineering')
		.click();

	await page.screenshot({
		path: 'screenshots/traceability/artifact-explorer-navigation.png',
		animations: 'disabled',
	});

	await page.getByText('travel_explore Artifact').click();
	await selectBranch(page, 'Working', 'Traceability');

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
	await page
		.locator('osee-artifact-search button')
		.filter({ hasText: 'search' })
		.click();
	await page.getByRole('button', { name: 'Demo Fault' }).click();

	// Switch to hierarchy view after selecting search result
	await page.getByRole('button', { name: 'Artifact Hierarchy' }).click();

	// Create software requirement
	await page
		.getByRole('button', { name: 'Software Requirements', exact: true })
		.click({
			button: 'right',
		});
	await page.getByRole('menuitem', { name: 'Create Child Artifact' }).click();
	await page.getByLabel('Enter a Name:').fill('Fault Handling');
	await page.getByPlaceholder('Unspecified').first().click();
	await page
		.getByPlaceholder('Unspecified')
		.first()
		.fill('software requirement');
	await page.getByText('Software Requirement', { exact: true }).click();
	await page.getByRole('button', { name: 'Ok' }).click();

	// Relate requirement to element
	await page.getByRole('button', { name: 'Relations' }).click();
	await page.waitForTimeout(500);

	// Expand the "Requirements to Interface" relation type
	const reqToInterfaceRelation = page
		.locator('osee-relations-editor-panel')
		.getByText('Requirements to Interface');
	await reqToInterfaceRelation.scrollIntoViewIfNeeded();
	await reqToInterfaceRelation
		.locator('..')
		.locator('..')
		.locator('button')
		.first()
		.click();
	await page.waitForTimeout(300);

	// Expand the "Requirement Artifact" relation side
	const requirementArtifactSide = page
		.locator('osee-relations-editor-panel')
		.getByText('Requirement Artifact');
	await requirementArtifactSide.scrollIntoViewIfNeeded();
	await requirementArtifactSide
		.locator('..')
		.locator('..')
		.locator('button')
		.first()
		.click();
	await page.waitForTimeout(300);

	// Drag and drop "Fault Handling" from hierarchy to the relation side
	await page.getByRole('button', { name: 'Fault Handling' }).hover();
	await page.mouse.down();
	await requirementArtifactSide.hover();
	await requirementArtifactSide.hover();
	await page.mouse.up();
	await page.waitForTimeout(500);

	await page.screenshot({
		path: 'screenshots/traceability/artifact-explorer-relations.png',
		animations: 'disabled',
	});
});

test('trace report', async ({ page }) => {
	await page.setViewportSize({ width: 1200, height: 900 });
	await page.goto('/ple');
	await page.getByRole('link', { name: 'MIM' }).click();
	await page.getByRole('link', { name: 'Reports' }).click();
	await selectBranch(page, 'Working', 'Traceability');
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
