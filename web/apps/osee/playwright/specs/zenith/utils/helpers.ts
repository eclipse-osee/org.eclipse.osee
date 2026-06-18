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

export const navigateToScriptTable = async (
	page: Page,
	branchName?: string,
	setName?: string
) => {
	await page.goto('http://localhost:4200/ci/allScripts');
	await page.getByLabel('Product Line').check();
	await page.getByText('Select a Branch').click();
	await page.getByText(branchName || 'SAW Product Line').click();

	await page.getByRole('combobox', { name: 'Select a set' }).click();
	await page.getByText(setName || 'Demo').click();
};
