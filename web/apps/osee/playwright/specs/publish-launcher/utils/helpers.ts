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
import { Page, expect } from '@ngx-playwright/test';

/** Base URL for the application under test. */
export const BASE_URL = 'http://localhost:4200';

/** Navigate to the Publish Launcher page via the PLE menu. */
export const navigateToPublishLauncher = async (page: Page) => {
	await page.goto(`${BASE_URL}/ple`);
	await page.getByRole('link', { name: 'Publishing' }).click();
};

/** Select a branch by type and name using the branch picker. */
export const selectBranch = async (
	page: Page,
	branchType: 'Working' | 'Product Line' | 'Baseline',
	branchName: string
) => {
	await page.getByLabel(branchType).check();
	await page.getByText('Select a Branch').click();
	await page.getByText(branchName).click();
};

/** Wait for the publish launcher configuration to load. */
export const waitForConfigLoad = async (page: Page) => {
	// Wait for the loading indicator to disappear
	await expect(
		page.getByText('Loading Publishing Configuration...')
	).toBeHidden({ timeout: 15000 });
};

/** Click the publish button within the current tab. */
export const clickPublishButton = async (page: Page, buttonLabel?: string) => {
	const label = buttonLabel || 'Launch Publish';
	await page.getByRole('button', { name: label }).click();
};
