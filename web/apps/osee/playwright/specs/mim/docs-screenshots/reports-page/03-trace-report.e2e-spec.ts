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
import { test, expect } from '@ngx-playwright/test';

test('test', async ({ page }) => {
	await page.setViewportSize({ width: 1200, height: 900 });
	await page.goto('http://localhost:4200/ple');
	await page.getByRole('link', { name: 'MIM' }).click();
	await page.getByRole('link', { name: 'Reports' }).click();
	await page.getByLabel('Working').check();
	await page.getByPlaceholder('Branches').click();
	await page.getByText('TW2 - Make Changes').click();
	await page.getByRole('link', { name: 'Traceability Report' }).click();
	await page.getByRole('radio', { name: 'Requirements' }).click();
	await expect(page.getByText('Demo Fault')).toBeVisible();

	await page.screenshot({
		path: 'screenshots/trace-report-requirements.png',
		animations: 'disabled',
		clip: { x: 0, y: 0, height: 500, width: 1200 },
	});

	await page.getByRole('radio', { name: 'MIM Artifacts' }).click();
	await expect(page.getByText('Demo Fault')).toBeVisible();

	await page.screenshot({
		path: 'screenshots/trace-report-mim-artifacts.png',
		animations: 'disabled',
		clip: { x: 0, y: 0, height: 500, width: 1200 },
	});
});
