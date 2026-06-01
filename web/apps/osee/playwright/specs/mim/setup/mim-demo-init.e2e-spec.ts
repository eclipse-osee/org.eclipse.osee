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
import { API_BASE, AUTH_HEADER } from '../../../shared/test-config';

test('setup', async ({ request }) => {
	let response = await request.post(`${API_BASE}/mim/init/demo`, {
		headers: AUTH_HEADER,
	});
	await expect(response.status()).toBe(200);

	response = await request.get(`${API_BASE}/ats/config/clearcache`, {
		headers: AUTH_HEADER,
	});
	await expect(response.status()).toBe(200);
});
