/*********************************************************************
 * Copyright (c) 2021 Boeing
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
import { transactionResultMock } from '../../../../transactions/transaction.mock';
import { user } from '../../../../userdata/types/user-data-user';
import { MimPreferencesService } from '../services/http/mim-preferences.service';
import { MimUserGlobalPreferences } from '../types/mim.preferences';
import { MimPreferencesMock } from './mim-preferences.response.mock';

export const MimPreferencesServiceMock: Partial<MimPreferencesService> = {
	getUserPrefs(branchId: string, user: user) {
		return of(MimPreferencesMock);
	},
	getBranchPrefs(user: user) {
		return of(['10:false', '8:true']);
	},
	createGlobalUserPrefs(
		user: user,
		prefs: Partial<MimUserGlobalPreferences>
	) {
		return of(transactionResultMock);
	},
	updateGlobalUserPrefs(
		current: MimUserGlobalPreferences,
		updated: MimUserGlobalPreferences
	) {
		return of(transactionResultMock);
	},
};

export const MimPreferencesServiceNoGlobalPrefsMock: Partial<MimPreferencesService> =
	{
		...MimPreferencesServiceMock,
		getUserPrefs(branchId: string, user: user) {
			return of({
				...MimPreferencesMock,
				globalPrefs: {
					id: '-1',
					name: 'Global Prefs',
					wordWrap: true,
				},
			});
		},
	};
