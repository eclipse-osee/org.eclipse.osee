/*********************************************************************
 * Copyright (c) 2023 Boeing
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
import { CrossReference } from '@osee/messaging/shared';
import { apiURL } from 'src/environments/environment';

@Injectable({
	providedIn: 'root',
})
export class CrossReferenceHttpService {
	constructor(private http: HttpClient) {}

	getAll(branchId: string, connectionId: string, filter: string) {
		return this.http.get<CrossReference[]>(
			apiURL +
				'/mim/branch/' +
				branchId +
				'/crossReference/connection/' +
				connectionId +
				'/' +
				filter
		);
	}

	get(branchId: string, artId: string) {
		return this.http.get<CrossReference>(
			apiURL + '/mim/branch/' + branchId + '/crossReference/' + artId
		);
	}
}
