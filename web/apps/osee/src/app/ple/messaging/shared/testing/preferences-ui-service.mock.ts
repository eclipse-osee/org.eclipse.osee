/*********************************************************************
 * Copyright (c) 2022 Boeing
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
import { of } from 'rxjs';
import {
	MimPreferencesMock,
	testGlobalUserPrefs,
} from './mim-preferences.response.mock';
import { PreferencesUIService } from '@osee/messaging/shared/services';
import { transactionResultMock } from '@osee/transactions/testing';

export const preferencesUiServiceMock: Partial<PreferencesUIService> = {
	createOrUpdateGlobalUserPrefs() {
		return of(transactionResultMock);
	},
	updatePreferences(preferences) {
		return of(transactionResultMock);
	},
	get preferences() {
		return of(MimPreferencesMock);
	},
	get inEditMode() {
		return of(false);
	},
	get globalPrefs() {
		return of(testGlobalUserPrefs);
	},
};
