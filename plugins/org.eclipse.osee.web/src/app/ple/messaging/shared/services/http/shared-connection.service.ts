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
import { apiURL } from '../../../../../../environments/environment';
import type { connection } from '../../types/connection';

@Injectable({
	providedIn: 'root',
})
export class SharedConnectionService {
	constructor(private http: HttpClient) {}

	getConnection(branchId: string, connectionId: string) {
		return this.http.get<connection>(
			apiURL + '/mim/branch/' + branchId + '/connections/' + connectionId
		);
	}
}
