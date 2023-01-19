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
import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { apiURL } from 'src/environments/environment';
import { userHistory } from '../../types/grid-commander-types/userHistory';

@Injectable({
	providedIn: 'root',
})
export class GetUserHistoryService {
	constructor(private http: HttpClient) {}

	getUserHistory(branchId: string) {
		return this.http.get<userHistory>(
			apiURL + `/orcs/branch/${branchId}/gc/user/history`
		);
	}
}
