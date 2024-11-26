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
		screenshot: 'only-on-failure',
	},

	testDir: join(__dirname, 'playwright/specs'),
	testMatch: '**/*.e2e-spec.ts',
	reportSlowTests: {
		max: 5, // 5 is the default
		threshold: 60000,
	},

	reporter: [
		[process.env['GITHUB_ACTION'] ? 'github' : 'list'],
		['junit', { outputFile: join(__dirname, 'results/junit.xml') }],
	],
	projects: [
		{
			name: 'MIM Demo Init',
			testMatch: 'playwright/specs/mim/setup/mim-demo-init.e2e-spec.ts',
		},
		{
			name: 'MIM Commit Demo Branch',
			use: { ...devices['Desktop Chrome'] },
			testMatch:
				'playwright/specs/mim/setup/commit-demo-branch.e2e-spec.ts',
			dependencies: ['MIM Demo Init'],
		},
		{
			name: 'MIM Tests',
			use: { ...devices['Desktop Chrome'] },
			testDir: 'playwright/specs/mim/tests',
			dependencies: ['MIM Commit Demo Branch'],
		},
	],
};

export default config;
