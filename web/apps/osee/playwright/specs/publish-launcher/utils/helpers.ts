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
export { selectBranch, selectBranchType } from '../../../shared/branch-helpers';

/** Navigate to the Publish Launcher page via the PLE menu. */
export const navigateToPublishLauncher = async (page: Page) => {
	await page.goto('/ple');
	await page.getByRole('link', { name: 'Publishing' }).click();
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
