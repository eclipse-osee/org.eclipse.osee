/*********************************************************************
 * Copyright (c) 2025 Boeing
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
import { readFileSync } from 'fs';
import { dirname, join } from 'path';
import { fileURLToPath } from 'url';
import { API_BASE, AUTH_HEADER } from '../../../shared/test-config';

const __dirname = dirname(fileURLToPath(import.meta.url));

test('publish-launcher setup', async ({ request }) => {
	const dataDir = join(__dirname, '..', 'data');
	const publishingArt = JSON.parse(
		readFileSync(join(dataDir, 'PublishingArt.json'), 'utf-8')
	);
	const publishingJson = readFileSync(
		join(dataDir, 'PublishingJson.json'),
		'utf-8'
	);

	// Create the publishing artifact
	let response = await request.post(`${API_BASE}/orcs/txs`, {
		data: publishingArt,
		headers: {
			'Content-Type': 'application/json',
			...AUTH_HEADER,
		},
	});
	await expect(response.status()).toBe(200);

	// Set the publishing configuration on the artifact attribute
	response = await request.put(
		`${API_BASE}/orcs/branch/570/artifact/10716029/attribute/type/1152921504606847380`,
		{
			data: publishingJson,
			headers: {
				'Content-Type': 'text/plain',
				...AUTH_HEADER,
			},
		}
	);
	await expect(response.status()).toBe(200);
});
