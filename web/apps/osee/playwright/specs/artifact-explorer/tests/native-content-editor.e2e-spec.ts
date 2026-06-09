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
import path from 'path';
import { fileURLToPath } from 'url';
import {
	navigateToArtifactExplorer,
	searchForArtifact,
	selectBranch,
} from '../utils/helpers';

const __filename = fileURLToPath(import.meta.url);
const __dirname = path.dirname(__filename);

/**
 * Tests for the Native Content Editor in the Artifact Explorer.
 * Uses the "SAW Markdown Requirements Updates" working branch which has
 * Image artifacts with Native Content (e.g., SAWTSR.png).
 */
test.describe('Native Content Editor', () => {
	const branchName = 'SAW Markdown Requirements Updates';
	const artifactName = 'SAWTSR';

	test.beforeEach(async ({ page }) => {
		await navigateToArtifactExplorer(page);
		await selectBranch(page, 'Working', branchName);
		await searchForArtifact(page, artifactName);
	});

	test('should display native content editor for image artifact', async ({
		page,
	}) => {
		// Verify the native content editor is visible with correct file info
		await expect(page.getByText(artifactName).first()).toBeVisible();
		await expect(page.getByText('Ext:')).toBeVisible();
		await expect(page.getByText('png')).toBeVisible();

		// Verify download and update buttons are present
		await expect(
			page.getByRole('button', { name: 'Download' })
		).toBeVisible();
		await expect(
			page.getByRole('button', { name: 'Update' })
		).toBeVisible();
	});

	test('should download native content file', async ({ page }) => {
		// Wait for the download button to be enabled
		const downloadButton = page.getByRole('button', { name: 'Download' });
		await expect(downloadButton).toBeEnabled();

		// Click download and verify a download starts
		const downloadPromise = page.waitForEvent('download');
		await downloadButton.click();
		const download = await downloadPromise;

		// Verify the downloaded file has the expected name
		expect(download.suggestedFilename()).toBe('SAWTSR.png');
	});

	test('should upload a new file and show unsaved state', async ({
		page,
	}) => {
		// Click the Update button to open the dialog
		await page.getByRole('button', { name: 'Update' }).click();

		// Verify the dialog opened
		await expect(page.getByText('Update File')).toBeVisible();
		await expect(page.getByText('Current file:')).toBeVisible();

		// Upload a test file via the file input
		const fileInput = page.locator('input[type="file"]');
		await fileInput.setInputFiles(
			path.resolve(__dirname, '../fixtures/test-upload.txt')
		);

		// Verify the file appears in the dialog
		await expect(
			page.getByLabel('Update File').getByText('test-upload.txt')
		).toBeVisible();

		// Click Update to confirm
		await page.getByRole('button', { name: 'Update' }).last().click();

		// Verify the editor shows unsaved state
		await expect(page.getByText('(unsaved)')).toBeVisible();

		// Verify download is disabled while unsaved
		const downloadButton = page.getByRole('button', { name: 'Download' });
		await expect(downloadButton).toBeDisabled();
	});

	test('should save uploaded file and re-enable download', async ({
		page,
	}) => {
		// Upload a new file
		await page.getByRole('button', { name: 'Update' }).click();
		const fileInput = page.locator('input[type="file"]');
		await fileInput.setInputFiles(
			path.resolve(__dirname, '../fixtures/test-upload.txt')
		);
		await page.getByRole('button', { name: 'Update' }).last().click();

		// Verify unsaved state
		await expect(page.getByText('(unsaved)')).toBeVisible();

		// Click the save button
		await page.getByRole('button').filter({ hasText: 'save' }).click();

		// Verify unsaved indicator disappears
		await expect(page.getByText('(unsaved)')).not.toBeVisible();

		// Verify download is re-enabled
		const downloadButton = page.getByRole('button', { name: 'Download' });
		await expect(downloadButton).toBeEnabled();
	});
});
