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

/** Attribute type ID for Dispatch Config Json. */
const DISPATCH_CONFIG_JSON_ATTR = '6371428937946281743';

test('dispatch setup', async ({ request }) => {
	const dataDir = join(__dirname, '..', 'data');
	const explorerJson = readFileSync(
		join(dataDir, 'AttributeTypesExplorer.json'),
		'utf-8'
	);
	const explorerDownloadJson = readFileSync(
		join(dataDir, 'AttributeTypesExplorerDownload.json'),
		'utf-8'
	);
	const deprecatedJson = readFileSync(
		join(dataDir, 'DeprecatedJson.json'),
		'utf-8'
	);
	const featuresJson = readFileSync(
		join(dataDir, 'AttributeTypesExplorerDispatchFeatures.json'),
		'utf-8'
	);

	// Create the DispatchConfig artifacts with their JSON attribute values.
	// Publishing gets two JSON values (V1 + V0) to test version filtering.
	// Reports gets one JSON value (V1).
	// Features gets one JSON value (V1) exercising checkboxes, static
	// dropdowns, file inputs, view selector, and email selector.
	const createTx = {
		branch: '570',
		txComment: 'Create Dispatch Config artifacts for testing',
		createArtifacts: [
			{
				id: '10716029',
				applicabilityId: '1',
				typeId: '7226028762153318337',
				key: '10716029',
				attributes: [
					{
						value: ['Publishing'],
						typeId: '1152921504606847088',
					},
					{
						value: [explorerJson, deprecatedJson],
						typeId: DISPATCH_CONFIG_JSON_ATTR,
					},
				],
			},
			{
				id: '10716030',
				applicabilityId: '1',
				typeId: '7226028762153318337',
				key: '10716030',
				attributes: [
					{
						value: ['Reports'],
						typeId: '1152921504606847088',
					},
					{
						value: [explorerDownloadJson],
						typeId: DISPATCH_CONFIG_JSON_ATTR,
					},
				],
			},
			{
				id: '10716031',
				applicabilityId: '1',
				typeId: '7226028762153318337',
				key: '10716031',
				attributes: [
					{
						value: ['Features'],
						typeId: '1152921504606847088',
					},
					{
						value: [featuresJson],
						typeId: DISPATCH_CONFIG_JSON_ATTR,
					},
				],
			},
		],
		addRelations: [
			{
				aArtId: '89216872',
				bArtId: '10716029',
				typeId: '2305843009213694292',
			},
			{
				aArtId: '89216872',
				bArtId: '10716030',
				typeId: '2305843009213694292',
			},
			{
				aArtId: '89216872',
				bArtId: '10716031',
				typeId: '2305843009213694292',
			},
		],
	};

	const response = await request.post(`${API_BASE}/orcs/txs`, {
		data: createTx,
		headers: {
			'Content-Type': 'application/json',
			...AUTH_HEADER,
		},
	});
	expect(response.status()).toBe(200);
});
