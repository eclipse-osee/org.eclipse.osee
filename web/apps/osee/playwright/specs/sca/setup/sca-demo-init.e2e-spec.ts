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

/**
 * TODO: SCA (Software Composition Analysis) is built on a legacy AngularJS
 * application and has known intermittent failures. These tests are marked with
 * test.fixme() until SCA is migrated to the current Angular application.
 * Once the migration is complete, these tests will be rewritten using modern
 * Angular testing patterns.
 */
test('setup', async ({ request }) => {
	test.fixme(
		true,
		'SCA is a legacy AngularJS page with known failures. Tests will be rewritten after migration to current Angular application.'
	);
	let response = await request.post(
		`${API_BASE}/dispo/program/init/demo`,
		{
			headers: AUTH_HEADER,
		}
	);
	await expect(response.status()).toBe(200);
});
