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
import { Page } from '@ngx-playwright/test';

/** Navigate to the Artifact Explorer page. */
export const navigateToArtifactExplorer = async (page: Page) => {
	await page.goto('/ple/artifact/explorer');
};

/** Select a branch by type and name using the branch picker. */
export const selectBranch = async (
	page: Page,
	branchType: 'Working' | 'Product Line' | 'Baseline',
	branchName: string
) => {
	await page.getByLabel(branchType).check({ timeout: 10000 });
	await page.getByText('Select a Branch').click();
	await page.getByText(branchName).click();
};

/** Search for an artifact by name using the Artifact Search panel. */
export const searchForArtifact = async (page: Page, artifactName: string) => {
	await page.getByRole('button', { name: 'Artifact Search' }).click();
	await page.getByText('Search for Artifact').click();
	await page
		.getByRole('textbox', { name: 'Search for Artifact' })
		.fill(artifactName);
	await page
		.getByRole('textbox', { name: 'Search for Artifact' })
		.press('Enter');
	await page.locator('button').filter({ hasText: 'search' }).click();
	await page.getByRole('button', { name: artifactName }).click();
};
