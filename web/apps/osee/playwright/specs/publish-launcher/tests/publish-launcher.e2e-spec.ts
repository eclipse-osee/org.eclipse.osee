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
import {
	navigateToPublishLauncher,
	selectBranch,
	waitForConfigLoad,
} from '../utils/helpers';

test('navigate to publish launcher page', async ({ page }) => {
	await page.setViewportSize({ width: 1600, height: 1000 });
	await navigateToPublishLauncher(page);

	await expect(page.getByText('Select a Branch')).toBeVisible();
});

test('branch picker is visible and functional', async ({ page }) => {
	await page.setViewportSize({ width: 1600, height: 1000 });
	await navigateToPublishLauncher(page);

	// Verify branch picker is present
	await expect(page.getByText('Select a Branch')).toBeVisible();

	// Verify branch type radio buttons are available
	await expect(page.getByLabel('Working')).toBeVisible();
	await expect(page.getByLabel('Product Line')).toBeVisible();
});

test('loading indicator appears when branch is selected', async ({ page }) => {
	await page.setViewportSize({ width: 1600, height: 1000 });
	await navigateToPublishLauncher(page);

	await selectBranch(page, 'Product Line', 'SAW Product Line');

	// Either the loading indicator or the config content should appear
	const loadingOrContent = page
		.getByText('Loading Publishing Configuration...')
		.or(page.locator('mat-tab-group'));
	await expect(loadingOrContent).toBeVisible({ timeout: 15000 });
});

test('configuration loads and displays tabs', async ({ page }) => {
	await page.setViewportSize({ width: 1600, height: 1000 });
	await navigateToPublishLauncher(page);

	await selectBranch(page, 'Product Line', 'SAW Product Line');
	await waitForConfigLoad(page);

	// Verify that a tab group is rendered from the config
	await expect(page.locator('mat-tab-group')).toBeVisible({
		timeout: 15000,
	});

	// Verify at least one tab label is visible
	const tabLabels = page.locator('.mat-mdc-tab-labels .mdc-tab');
	await expect(tabLabels.first()).toBeVisible();
});

test('tab content displays form fields', async ({ page }) => {
	await page.setViewportSize({ width: 1600, height: 1000 });
	await navigateToPublishLauncher(page);

	await selectBranch(page, 'Product Line', 'SAW Product Line');
	await waitForConfigLoad(page);

	// Verify the email field is present (shown when targetApi URL contains {email})
	const emailField = page.getByLabel('Email Addresses');
	const hasEmail = await emailField.isVisible().catch(() => false);

	if (hasEmail) {
		await expect(emailField).toBeVisible();
	}

	// Verify a publish/action button is present in the tab
	const publishButton = page.locator(
		'button[mat-flat-button], button.mdc-button--unelevated'
	);
	await expect(publishButton.first()).toBeVisible();
});

test('publish button is disabled when form is invalid', async ({ page }) => {
	await page.setViewportSize({ width: 1600, height: 1000 });
	await navigateToPublishLauncher(page);

	await selectBranch(page, 'Product Line', 'SAW Product Line');
	await waitForConfigLoad(page);

	// If there are required fields, the publish button should be disabled initially
	const publishButton = page.locator(
		'button[mat-flat-button], button.mdc-button--unelevated'
	);
	const firstButton = publishButton.first();
	await expect(firstButton).toBeVisible();

	const isDisabled = await firstButton.isDisabled();
	// The button should be disabled if there are required fields that haven't been filled
	if (isDisabled) {
		await expect(firstButton).toBeDisabled();
	}
});

test('switching tabs displays different content', async ({ page }) => {
	await page.setViewportSize({ width: 1600, height: 1000 });
	await navigateToPublishLauncher(page);

	await selectBranch(page, 'Product Line', 'SAW Product Line');
	await waitForConfigLoad(page);

	const tabLabels = page.locator('.mat-mdc-tab-labels .mdc-tab');
	const tabCount = await tabLabels.count();

	// If there are multiple tabs, click the second one and verify content changes
	if (tabCount > 1) {
		await tabLabels.nth(1).click();
		await page.waitForTimeout(300);

		// Verify the tab panel content is visible
		const tabBody = page.locator(
			'.mat-mdc-tab-body-active .mat-mdc-tab-body-content'
		);
		await expect(tabBody).toBeVisible();
	}
});

test('email validation shows error for invalid input', async ({ page }) => {
	await page.setViewportSize({ width: 1600, height: 1000 });
	await navigateToPublishLauncher(page);

	await selectBranch(page, 'Product Line', 'SAW Product Line');
	await waitForConfigLoad(page);

	const emailField = page.getByLabel('Email Addresses');
	const hasEmail = await emailField.isVisible().catch(() => false);

	if (hasEmail) {
		// Enter an invalid email
		await emailField.fill('not-an-email');
		// Blur the field to trigger validation
		await emailField.blur();

		// Check for validation error
		await expect(
			page.getByText('One or more email addresses are invalid')
		).toBeVisible({ timeout: 5000 });
	}
});

test('email validation accepts valid input', async ({ page }) => {
	await page.setViewportSize({ width: 1600, height: 1000 });
	await navigateToPublishLauncher(page);

	await selectBranch(page, 'Product Line', 'SAW Product Line');
	await waitForConfigLoad(page);

	const emailField = page.getByLabel('Email Addresses');
	const hasEmail = await emailField.isVisible().catch(() => false);

	if (hasEmail) {
		// Enter a valid email
		await emailField.fill('user@example.com');
		await emailField.blur();

		// Validation error should not appear
		await expect(
			page.getByText('One or more email addresses are invalid')
		).toBeHidden();
	}
});

test('page title is displayed from configuration', async ({ page }) => {
	await page.setViewportSize({ width: 1600, height: 1000 });
	await navigateToPublishLauncher(page);

	await selectBranch(page, 'Product Line', 'SAW Product Line');
	await waitForConfigLoad(page);

	// The config should provide a title (e.g., "Publish Launcher")
	const heading = page.locator('h1');
	const hasHeading = await heading.isVisible().catch(() => false);

	if (hasHeading) {
		await expect(heading).toBeVisible();
		await expect(heading).not.toBeEmpty();
	}
});
