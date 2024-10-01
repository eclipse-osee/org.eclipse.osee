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
import { devices, type PlaywrightTestConfig } from '@ngx-playwright/test';
import { join, dirname } from 'node:path';
import { fileURLToPath } from 'node:url';

const __dirname = dirname(fileURLToPath(import.meta.url));

const config: PlaywrightTestConfig = {
	use: {
		channel: 'chrome',
		headless: true,
		baseURL: 'http://localhost:4200/ple',
	},

	testDir: join(__dirname, 'playwright/specs'),
	testMatch: '**/*.e2e-spec.ts',
	workers: 1,

	reporter: [
		[process.env['GITHUB_ACTION'] ? 'github' : 'list'],
		['junit', { outputFile: join(__dirname, 'results/junit.xml') }],
	],
	projects: [
		{
			name: 'chromium',
			use: { ...devices['Desktop Chrome'] },
		},
		{
			name: 'MIM Setup',
			testDir: 'playwright/specs/mim/setup',
		},
		{
			name: 'MIM Create ICD Screenshots',
			use: { ...devices['Desktop Chrome'] },
			testDir: 'playwright/specs/mim/docs-screenshots',
			dependencies: ['MIM Setup'],
		},
		{
			name: 'MIM Peer Review Screenshots',
			use: { ...devices['Desktop Chrome'] },
			testDir: 'playwright/specs/mim/docs-screenshots/peer-review',
			dependencies: ['MIM Setup'],
		},
	],
};

export default config;
