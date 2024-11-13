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
import { Page, TestInfo } from '@ngx-playwright/test';

export async function screenshotOnFailure(
	{ page }: { page: Page },
	testInfo: TestInfo
) {
	if (testInfo.status !== testInfo.expectedStatus) {
		const screenshotPath = testInfo.outputPath(`failure.png`);
		testInfo.attachments.push({
			name: 'screenshot',
			path: screenshotPath,
			contentType: 'image/png',
		});
		await page.screenshot({ path: screenshotPath, timeout: 5000 });
	}
}
