/*********************************************************************
 * Copyright (c) 2026 Boeing
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
import { mkdtempSync, writeFileSync } from 'fs';
import { tmpdir } from 'os';
import { join } from 'path';
import {
	navigateToDispatchPage,
	selectArtifactType,
	selectBranch,
	uploadFile,
} from '../utils/helpers';

test.describe('Dispatch', () => {
	test.describe.configure({ mode: 'parallel' });

	test.beforeEach(async ({ page }) => {
		await page.setViewportSize({ width: 1600, height: 1000 });
		await navigateToDispatchPage(page, 'Attribute Types Explorer');
	});

	test('displays page title from config', async ({ page }) => {
		await expect(page.locator('h1')).toHaveText('Attribute Types Explorer');
	});

	test('renders tab from config', async ({ page }) => {
		const tabLabels = page.locator('.mat-mdc-tab-labels .mdc-tab');
		await expect(tabLabels).toHaveCount(1);
		await expect(tabLabels.nth(0)).toContainText('Get Attribute Types');
	});

	test('displays description in the tab', async ({ page }) => {
		await expect(
			page.getByText(
				'Fetch attribute types for a selected artifact type.'
			)
		).toBeVisible();
	});

	test('displays instructions in the tab', async ({ page }) => {
		await expect(page.getByText('Description:')).toBeVisible();
		await expect(page.getByText('Instructions:')).toBeVisible();
	});

	test('renders tab content when navigating directly to the page URL', async ({
		page,
	}) => {
		await page.goto('/ple/dispatch/publishing');
		await expect(page.getByText('Loading Configuration...')).toBeHidden({
			timeout: 15000,
		});

		// The page's single tab and its branch picker should render.
		const tabLabels = page.locator('.mat-mdc-tab-labels .mdc-tab');
		await expect(tabLabels).toHaveCount(1);
		await expect(tabLabels.nth(0)).toContainText('Get Attribute Types');
		await expect(
			page.locator('mat-button-toggle[data-cy="baseline"]')
		).toBeVisible();
	});

	test('branch picker renders', async ({ page }) => {
		await expect(
			page.locator('mat-button-toggle[data-cy="baseline"]')
		).toBeVisible();
		await expect(
			page.locator('mat-button-toggle[data-cy="working"]')
		).toBeVisible();
	});

	test('artifact type dropdown is hidden before branch selection', async ({
		page,
	}) => {
		await expect(
			page.getByText('Artifact Type', { exact: true })
		).not.toBeVisible();
	});

	test('artifact type dropdown appears after branch selection', async ({
		page,
	}) => {
		await selectBranch(page, 'Baseline', 'SAW Product Line');

		await expect(
			page.getByText('Artifact Type', { exact: true })
		).toBeVisible({ timeout: 10000 });
	});

	test('dependsOn: artifact type dropdown loads options after branch selection', async ({
		page,
	}) => {
		await selectBranch(page, 'Baseline', 'SAW Product Line');

		await expect(
			page.getByText('Artifact Type', { exact: true })
		).toBeVisible({ timeout: 10000 });

		const artTypeInput = page.getByRole('combobox', {
			name: 'Artifact Type',
		});
		await artTypeInput.click();

		await expect(page.locator('mat-option').first()).toBeVisible({
			timeout: 15000,
		});
	});

	test('publish button is disabled without required artifact type', async ({
		page,
	}) => {
		await selectBranch(page, 'Baseline', 'SAW Product Line');

		const publishButton = page.getByRole('button', {
			name: 'Get Attribute Types',
		});
		await expect(publishButton).toBeDisabled();
	});

	test('publish button shows tooltip when disabled', async ({ page }) => {
		await selectBranch(page, 'Baseline', 'SAW Product Line');

		const publishButton = page.getByRole('button', {
			name: 'Get Attribute Types',
		});
		await expect(publishButton).toBeDisabled();

		await publishButton.locator('..').hover();
		await expect(page.locator('.mat-mdc-tooltip').first()).toBeVisible({
			timeout: 5000,
		});
	});

	test('publish button enables after selecting artifact type (file optional)', async ({
		page,
	}) => {
		await selectBranch(page, 'Baseline', 'SAW Product Line');
		await selectArtifactType(page);

		const publishButton = page.getByRole('button', {
			name: 'Get Attribute Types',
		});
		await expect(publishButton).toBeEnabled();
	});

	test('file input renders with correct accept type', async ({ page }) => {
		const fileInput = page.locator(
			'osee-drag-and-drop-upload input[type="file"]'
		);
		await expect(fileInput).toHaveAttribute('accept', '.json,.txt');
	});

	test('file input displays its label', async ({ page }) => {
		await expect(
			page.getByRole('heading', { name: /Attachment \(optional\)/ })
		).toBeVisible();
	});

	test('file selector displays selected file and replaces on re-upload', async ({
		page,
	}) => {
		const tempDir = mkdtempSync(join(tmpdir(), 'dispatch-file-test-'));
		const firstPath = join(tempDir, 'first.json');
		writeFileSync(firstPath, '{}');

		await uploadFile(page, firstPath);
		await expect(page.getByText('first.json')).toBeVisible();

		const secondPath = join(tempDir, 'second.json');
		writeFileSync(secondPath, '{}');
		await uploadFile(page, secondPath);

		await expect(page.getByText('second.json')).toBeVisible();
		await expect(page.getByText('first.json')).not.toBeVisible();
	});

	test('file selector only accepts configured file types', async ({
		page,
	}) => {
		const fileInput = page.locator(
			'osee-drag-and-drop-upload input[type="file"]'
		);
		await expect(fileInput).toHaveAttribute('accept', '.json,.txt');
	});

	test('publish triggers request and shows inline result dialog', async ({
		page,
	}) => {
		await selectBranch(page, 'Baseline', 'SAW Product Line');
		await selectArtifactType(page);

		await page.getByRole('button', { name: 'Get Attribute Types' }).click();

		await expect(page.locator('mat-dialog-container')).toBeVisible({
			timeout: 30000,
		});
		await expect(page.getByRole('button', { name: 'Close' })).toBeVisible();
		await expect(
			page.getByRole('button', { name: 'Download', exact: true })
		).not.toBeVisible();

		await expect(page.locator('mat-dialog-container pre')).toBeVisible();
	});

	test('close button dismisses result dialog', async ({ page }) => {
		await selectBranch(page, 'Baseline', 'SAW Product Line');
		await selectArtifactType(page);

		await page.getByRole('button', { name: 'Get Attribute Types' }).click();

		await expect(page.getByRole('button', { name: 'Close' })).toBeVisible({
			timeout: 30000,
		});

		await page.getByRole('button', { name: 'Close' }).click();

		await expect(page.locator('mat-dialog-container')).not.toBeVisible();
	});
});

test.describe('Dispatch Multi-Page', () => {
	test('index page lists all dispatch pages', async ({ page }) => {
		await page.setViewportSize({ width: 1600, height: 1000 });
		await page.goto('/ple/dispatch');

		await expect(page.getByText('Loading Dispatch Pages...')).toBeHidden({
			timeout: 15000,
		});

		await expect(
			page.getByRole('link', {
				name: 'Attribute Types Explorer',
				exact: true,
			})
		).toBeVisible();
		await expect(
			page.getByRole('link', {
				name: 'Attribute Types Explorer - Download',
			})
		).toBeVisible();
		await expect(
			page.getByRole('link', {
				name: 'Attribute Types Explorer - Dispatch Features',
			})
		).toBeVisible();

		const pageLinks = page.locator('a[mat-stroked-button]');
		await expect(pageLinks).toHaveCount(3);
	});

	test('index page does not show out-of-version configs', async ({
		page,
	}) => {
		await page.setViewportSize({ width: 1600, height: 1000 });
		await page.goto('/ple/dispatch');

		await expect(page.getByText('Loading Dispatch Pages...')).toBeHidden({
			timeout: 15000,
		});

		await expect(
			page.getByRole('link', { name: 'Old Deprecated Page' })
		).not.toBeVisible();
		await expect(page.locator('a[mat-stroked-button]')).toHaveCount(3);
	});

	test('clicking a page navigates to it', async ({ page }) => {
		await page.setViewportSize({ width: 1600, height: 1000 });
		await page.goto('/ple/dispatch');

		await expect(page.getByText('Loading Dispatch Pages...')).toBeHidden({
			timeout: 15000,
		});

		await page
			.getByRole('link', {
				name: 'Attribute Types Explorer - Download',
			})
			.click({ force: true });

		await expect(page.locator('h1')).toHaveText(
			'Attribute Types Explorer - Download'
		);
		expect(page.url()).toContain('/dispatch/reports');
	});

	test('second page renders its own tab, dropdowns, and publish button', async ({
		page,
	}) => {
		await page.setViewportSize({ width: 1600, height: 1000 });
		await navigateToDispatchPage(
			page,
			'Attribute Types Explorer - Download'
		);

		await expect(page.locator('h1')).toHaveText(
			'Attribute Types Explorer - Download'
		);

		const tabLabels = page.locator('.mat-mdc-tab-labels .mdc-tab');
		await expect(tabLabels).toHaveCount(1);
		await expect(tabLabels.nth(0)).toContainText(
			'Download Attribute Types'
		);

		await expect(
			page.locator('mat-button-toggle[data-cy="baseline"]')
		).toBeVisible();
		await expect(
			page.getByRole('button', { name: 'Download Attribute Types' })
		).toBeVisible();
	});

	test('V0 config content from multi-valued attribute does not appear', async ({
		page,
	}) => {
		await page.setViewportSize({ width: 1600, height: 1000 });
		await navigateToDispatchPage(page, 'Attribute Types Explorer');

		await expect(page.getByText('Old Deprecated Page')).not.toBeVisible();
		await expect(page.getByText('Should Not Appear')).not.toBeVisible();
		await expect(page.locator('h1')).toContainText(
			'Attribute Types Explorer'
		);
	});

	test('navigating to a V0 page slug shows not found', async ({ page }) => {
		await page.setViewportSize({ width: 1600, height: 1000 });
		await page.goto('/ple/dispatch/old-deprecated-page');

		await expect(page.getByText('Dispatch page not found.')).toBeVisible({
			timeout: 10000,
		});
	});

	test('navigating to a nonexistent page slug shows not found', async ({
		page,
	}) => {
		await page.setViewportSize({ width: 1600, height: 1000 });
		await page.goto('/ple/dispatch/nonexistent-page');

		await expect(page.getByText('Dispatch page not found.')).toBeVisible({
			timeout: 10000,
		});
	});

	test('page toolbar has home and menu navigation icons', async ({
		page,
	}) => {
		await page.setViewportSize({ width: 1600, height: 1000 });
		await navigateToDispatchPage(page, 'Attribute Types Explorer');

		await expect(
			page.getByRole('link', { name: 'Dispatch Home' })
		).toBeVisible();
		await expect(
			page.getByRole('button', { name: 'Navigate to Page' })
		).toBeVisible();
	});

	test('navigating to Dispatch Home preserves the branch query params', async ({
		page,
	}) => {
		await page.setViewportSize({ width: 1600, height: 1000 });
		await navigateToDispatchPage(page, 'Attribute Types Explorer');

		// Selecting a branch writes ?branchType=... into the URL.
		await selectBranch(page, 'Baseline', 'SAW Product Line');
		await page.waitForURL(/[?&]branchType=baseline/, { timeout: 10000 });

		await page.getByRole('link', { name: 'Dispatch Home' }).click();

		// Landed on the index (or an auto-redirected page) with the branch
		// query param still present.
		await page.waitForURL(/[?&]branchType=baseline/, { timeout: 10000 });
		expect(page.url()).toContain('branchType=baseline');
	});

	test('menu lists all available pages for cross-navigation', async ({
		page,
	}) => {
		await page.setViewportSize({ width: 1600, height: 1000 });
		await navigateToDispatchPage(page, 'Attribute Types Explorer');

		await page.getByRole('button', { name: 'Navigate to Page' }).click();

		await expect(
			page.getByRole('menuitem', {
				name: 'Attribute Types Explorer',
				exact: true,
			})
		).toBeVisible();
		await expect(
			page.getByRole('menuitem', {
				name: 'Attribute Types Explorer - Download',
			})
		).toBeVisible();
		await expect(
			page.getByRole('menuitem', {
				name: 'Attribute Types Explorer - Dispatch Features',
			})
		).toBeVisible();
	});
});

test.describe('Dispatch Result Dialog - Download', () => {
	test('download page shows Download and Close buttons', async ({ page }) => {
		await page.setViewportSize({ width: 1600, height: 1000 });
		await navigateToDispatchPage(
			page,
			'Attribute Types Explorer - Download'
		);

		await selectBranch(page, 'Baseline', 'SAW Product Line');
		await selectArtifactType(page);

		await page
			.getByRole('button', { name: 'Download Attribute Types' })
			.click();

		await expect(page.locator('mat-dialog-container')).toBeVisible({
			timeout: 30000,
		});
		await expect(page.getByRole('button', { name: 'Close' })).toBeVisible();
		await expect(
			page.getByRole('button', { name: 'Download', exact: true })
		).toBeVisible();
	});

	test('download triggers file save with configured filename', async ({
		page,
	}) => {
		await page.setViewportSize({ width: 1600, height: 1000 });
		await navigateToDispatchPage(
			page,
			'Attribute Types Explorer - Download'
		);

		await selectBranch(page, 'Baseline', 'SAW Product Line');
		await selectArtifactType(page);

		await page
			.getByRole('button', { name: 'Download Attribute Types' })
			.click();

		await expect(
			page.getByRole('button', { name: 'Download', exact: true })
		).toBeVisible({ timeout: 30000 });

		const downloadPromise = page.waitForEvent('download');
		await page
			.getByRole('button', { name: 'Download', exact: true })
			.click();
		const download = await downloadPromise;

		expect(download.suggestedFilename()).toBe('attribute-types.json');
	});

	test('close button dismisses the download dialog', async ({ page }) => {
		await page.setViewportSize({ width: 1600, height: 1000 });
		await navigateToDispatchPage(
			page,
			'Attribute Types Explorer - Download'
		);

		await selectBranch(page, 'Baseline', 'SAW Product Line');
		await selectArtifactType(page);

		await page
			.getByRole('button', { name: 'Download Attribute Types' })
			.click();

		await expect(page.getByRole('button', { name: 'Close' })).toBeVisible({
			timeout: 30000,
		});

		await page.getByRole('button', { name: 'Close' }).click();

		await expect(page.locator('mat-dialog-container')).not.toBeVisible();
	});
});

test.describe('Dispatch Feature Coverage', () => {
	test.describe.configure({ mode: 'parallel' });

	test.beforeEach(async ({ page }) => {
		await page.setViewportSize({ width: 1600, height: 1000 });
		await navigateToDispatchPage(
			page,
			'Attribute Types Explorer - Dispatch Features'
		);
	});

	test('renders the feature coverage tab', async ({ page }) => {
		await expect(page.locator('h1')).toHaveText(
			'Attribute Types Explorer - Dispatch Features'
		);
		const tabLabels = page.locator('.mat-mdc-tab-labels .mdc-tab');
		await expect(tabLabels).toHaveCount(1);
		await expect(tabLabels.nth(0)).toContainText('Feature Coverage');
	});

	test('renders checkboxes with their configured default state', async ({
		page,
	}) => {
		const includeInherited = page.getByRole('checkbox', {
			name: 'Include Inherited',
		});
		const includeDeprecated = page.getByRole('checkbox', {
			name: 'Include Deprecated',
		});

		await expect(includeInherited).toBeVisible();
		await expect(includeDeprecated).toBeVisible();

		// includeInherited defaults to true, includeDeprecated to false.
		await expect(includeInherited).toBeChecked();
		await expect(includeDeprecated).not.toBeChecked();
	});

	test('checkbox can be toggled', async ({ page }) => {
		const includeDeprecated = page.getByRole('checkbox', {
			name: 'Include Deprecated',
		});
		await expect(includeDeprecated).not.toBeChecked();

		await includeDeprecated.click();
		await expect(includeDeprecated).toBeChecked();
	});

	test('static-options dropdown shows configured options and a (none) option', async ({
		page,
	}) => {
		const formatInput = page.getByRole('combobox', { name: 'Format' });
		await expect(formatInput).toBeVisible();
		await formatInput.click();

		// Not required, so a "(none)" option is present alongside the static options.
		await expect(
			page.getByRole('option', { name: '(none)' })
		).toBeVisible();
		await expect(page.getByRole('option', { name: 'JSON' })).toBeVisible();
		await expect(page.getByRole('option', { name: 'XML' })).toBeVisible();
		await expect(page.getByRole('option', { name: 'CSV' })).toBeVisible();
	});

	test('static-options dropdown filters options on type', async ({
		page,
	}) => {
		const formatInput = page.getByRole('combobox', { name: 'Format' });
		await formatInput.click();
		await formatInput.fill('XM');

		await expect(page.getByRole('option', { name: 'XML' })).toBeVisible();
		await expect(
			page.getByRole('option', { name: 'JSON' })
		).not.toBeVisible();
		await expect(
			page.getByRole('option', { name: 'CSV' })
		).not.toBeVisible();
	});

	test('static-options dropdown selection displays the chosen label', async ({
		page,
	}) => {
		const formatInput = page.getByRole('combobox', { name: 'Format' });
		await formatInput.click();
		await page.getByRole('option', { name: 'JSON' }).click();

		await expect(formatInput).toHaveValue('JSON');
	});

	test('view selector is hidden before branch selection', async ({
		page,
	}) => {
		await expect(
			page.locator('osee-current-view-selector')
		).not.toBeVisible();
	});

	test('view selector appears after branch selection', async ({ page }) => {
		await selectBranch(page, 'Baseline', 'SAW Product Line');

		await expect(page.locator('osee-current-view-selector')).toBeVisible({
			timeout: 10000,
		});
	});

	test('email selector renders and lists autocomplete options', async ({
		page,
	}) => {
		const emailInput = page.locator(
			'osee-dispatch-email-selector input[matInput]'
		);
		await expect(emailInput).toBeVisible();
		await emailInput.click();

		await expect(page.locator('mat-option').first()).toBeVisible({
			timeout: 10000,
		});
	});

	test('email selector adds and removes a chip', async ({ page }) => {
		const emailInput = page.locator(
			'osee-dispatch-email-selector input[matInput]'
		);
		await emailInput.click();

		const firstOption = page.locator('mat-option').first();
		await expect(firstOption).toBeVisible({ timeout: 10000 });
		const selectedEmail = (await firstOption.textContent())?.trim() ?? '';
		await firstOption.click();

		const chip = page
			.locator('mat-chip-row')
			.filter({ hasText: selectedEmail });
		await expect(chip).toBeVisible();

		await chip.getByRole('button', { name: 'Remove email' }).click();
		await expect(chip).not.toBeVisible();
	});

	test('required file input blocks publish until a file is provided', async ({
		page,
	}) => {
		await selectBranch(page, 'Baseline', 'SAW Product Line');
		await selectArtifactType(page);

		// Config File is required, so publish stays disabled without it.
		const publishButton = page.getByRole('button', {
			name: 'Get Attribute Types',
		});
		await expect(publishButton).toBeDisabled();

		const tempDir = mkdtempSync(join(tmpdir(), 'dispatch-features-'));
		const configPath = join(tempDir, 'config.json');
		writeFileSync(configPath, '{}');
		await uploadFile(page, configPath, 'Config File');

		await expect(page.getByText('config.json')).toBeVisible();
		await expect(publishButton).toBeEnabled();
	});

	test('multiple file input accepts several files', async ({ page }) => {
		const tempDir = mkdtempSync(join(tmpdir(), 'dispatch-features-multi-'));
		const fileA = join(tempDir, 'a.json');
		const fileB = join(tempDir, 'b.xml');
		const fileC = join(tempDir, 'c.csv');
		writeFileSync(fileA, '{}');
		writeFileSync(fileB, '<x/>');
		writeFileSync(fileC, 'x');

		await uploadFile(page, [fileA, fileB, fileC], 'Supplementary Files');

		await expect(page.getByText('a.json')).toBeVisible();
		await expect(page.getByText('b.xml')).toBeVisible();
		await expect(page.getByText('c.csv')).toBeVisible();
	});

	test('publish with checkboxes and files triggers request and shows result dialog', async ({
		page,
	}) => {
		await selectBranch(page, 'Baseline', 'SAW Product Line');
		await selectArtifactType(page);

		const tempDir = mkdtempSync(join(tmpdir(), 'dispatch-features-pub-'));
		const configPath = join(tempDir, 'config.json');
		writeFileSync(configPath, '{}');
		await uploadFile(page, configPath, 'Config File');

		await page.getByRole('button', { name: 'Get Attribute Types' }).click();

		await expect(page.locator('mat-dialog-container')).toBeVisible({
			timeout: 30000,
		});
		await expect(page.getByRole('button', { name: 'Close' })).toBeVisible();
	});
});
