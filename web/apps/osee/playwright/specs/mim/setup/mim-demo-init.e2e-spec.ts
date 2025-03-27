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

test('setup', async ({ request }) => {
	let response = await request.post('http://localhost:8089/mim/init/demo', {
		headers: { Authorization: 'Basic 3333' },
	});
	await expect(response.status()).toBe(200);

	response = await request.get(
		'http://localhost:8089/ats/config/clearcache',
		{
			headers: { Authorization: 'Basic 3333' },
		}
	);
	await expect(response.status()).toBe(200);
});
