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
import { MimUserGlobalPreferences } from '@osee/messaging/shared/types';
import { transactionResultMock } from '@osee/shared/transactions/testing';
import { of } from 'rxjs';
import { user } from '@osee/shared/types/auth';
import { MimPreferencesService } from '../services/http/mim-preferences.service';
import { MimPreferencesMock } from './mim-preferences.response.mock';
import { transaction } from '@osee/shared/types';

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
	performMutation(tx: transaction) {
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
		performMutation(tx: transaction) {
			return of(transactionResultMock);
		},
	};
