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
import { type PlaywrightTestConfig } from '@ngx-playwright/test';
import ngPlaywrightConfig from './playwright.config.ng';

const config: PlaywrightTestConfig = {
	...ngPlaywrightConfig,
	webServer: {
		command: 'ng serve',
		url: 'http://localhost:4200',
		reuseExistingServer: !process.env.CI,
	},
};

export default config;
