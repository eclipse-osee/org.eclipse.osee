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
import { HttpClient, HttpHeaders } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { user } from 'src/app/userdata/types/user-data-user';
import { apiURL } from 'src/environments/environment';
import { element } from '../../types/element';
import { structure } from '../../types/structure';
import { message } from '../../../message-interface/types/messages';
import { subMessage } from '../../../message-interface/types/sub-messages';
import { MimPreferences } from '../../types/mim.preferences';

@Injectable({
	providedIn: 'root',
})
export class MimPreferencesService {
	constructor(private http: HttpClient) {}

	getUserPrefs(branchId: string, user: user) {
		return this.http.get<
			MimPreferences<structure & message & subMessage & element>
		>(apiURL + '/mim/user/' + branchId);
	}

	getBranchPrefs(user: user) {
		return this.http.get<string[]>(apiURL + '/mim/user/branches');
	}
}
