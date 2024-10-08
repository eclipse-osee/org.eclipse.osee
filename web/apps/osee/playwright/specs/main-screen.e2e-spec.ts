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
import { expect, createTest } from '@ngx-playwright/test';

import { MainScreen } from '../screens/main-screen.js';

const test = createTest(MainScreen);

test.describe('the main screen of the application', () => {
	test('it should have a title', ({ $: { title } }) => {
		expect(title).toBeTruthy();
	});
});
