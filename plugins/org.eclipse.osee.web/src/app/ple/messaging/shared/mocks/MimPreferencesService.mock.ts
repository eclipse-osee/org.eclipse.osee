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
import { user } from 'src/app/userdata/types/user-data-user';
import { MimPreferencesService } from '../services/http/mim-preferences.service';
import { MimPreferencesMock } from './MimPreferences.mock';

export const MimPreferencesServiceMock: Partial<MimPreferencesService> = {
	getUserPrefs(branchId: string, user: user) {
		return of(MimPreferencesMock);
	},
	getBranchPrefs(user: user) {
		return of(['10:false', '8:true']);
	},
};
