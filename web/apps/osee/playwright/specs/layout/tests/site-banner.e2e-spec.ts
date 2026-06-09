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

test.describe('Site Banner', () => {
	test.beforeEach(async ({ page }) => {
		await page.goto('/ple');
		await page.evaluate(() => localStorage.clear());
		await page.reload();
		await page.waitForResponse(
			(resp) =>
				resp.url().includes('/orcs/datastore/banner') &&
				resp.status() === 200
		);
	});

	test('should display the banner when content is configured', async ({
		page,
	}) => {
		const banner = page.locator('osee-site-banner [role="alert"]');
		await expect(banner).toBeVisible();
	});

	test('should display the banner content from the server', async ({
		page,
	}) => {
		const banner = page.locator('osee-site-banner [role="alert"]');
		await expect(banner).toContainText('DATA CONTROLS');
	});

	test('should have a dismiss button', async ({ page }) => {
		const dismissButton = page.getByRole('button', {
			name: 'Dismiss banner',
		});
		await expect(dismissButton).toBeVisible();
	});

	test('should hide the banner when dismissed', async ({ page }) => {
		const banner = page.locator('osee-site-banner [role="alert"]');
		await expect(banner).toBeVisible();

		const dismissButton = page.getByRole('button', {
			name: 'Dismiss banner',
		});
		await dismissButton.click();

		await expect(banner).not.toBeVisible();
	});

	test('should remain dismissed on same-page navigation', async ({
		page,
	}) => {
		const banner = page.locator('osee-site-banner [role="alert"]');
		await expect(banner).toBeVisible();

		await page.getByRole('button', { name: 'Dismiss banner' }).click();
		await expect(banner).not.toBeVisible();

		await page.goto('/ple');
		await expect(banner).not.toBeVisible();
	});

	test('should not display the banner when API returns empty content', async ({
		page,
	}) => {
		await page.route('**/orcs/datastore/banner', (route) => {
			route.fulfill({
				status: 200,
				contentType: 'application/json',
				body: JSON.stringify({ content: '' }),
			});
		});

		await page.goto('/ple');
		const banner = page.locator('osee-site-banner [role="alert"]');
		await expect(banner).not.toBeVisible();
	});
});
