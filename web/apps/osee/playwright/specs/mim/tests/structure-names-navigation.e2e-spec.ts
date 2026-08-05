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

test.describe('Structure Names Navigation', () => {
	test('should navigate from structure names to structure detail without 404', async ({
		page,
	}) => {
		await page.setViewportSize({ width: 1200, height: 900 });
		await page.goto('/ple/messaging/structureNames');

		// Select branch
		await page.getByRole('radio', { name: 'Working' }).click();
		await page.getByRole('combobox', { name: 'Select a Branch' }).click();
		await page
			.getByRole('option', { name: 'TW2 - MIM Demo PL' })
			.click();

		// Expand the "Structure" panel
		await page.getByRole('button', { name: 'Structure' }).click();

		// Click the submessage link
		await page
			.getByRole('link', { name: 'Message 1 > Submessage' })
			.click();

		// Verify we did NOT navigate to a 404 page
		await page.waitForTimeout(1000);
		expect(page.url()).not.toContain('/404');

		// Verify we navigated to a valid connections/elements route
		expect(page.url()).toContain('/ple/messaging/connections');
		expect(page.url()).toContain('branchType=working');
		expect(page.url()).toContain('branchId=');
	});
});
