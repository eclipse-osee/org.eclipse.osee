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
import { apiURL } from '@osee/environments';
import { ATTRIBUTETYPEIDENUM } from '@osee/shared/types/constants';
import type { transportType } from '@osee/messaging/shared/types';

@Injectable({
	providedIn: 'root',
})
export class TransportTypeService {
	constructor(private http: HttpClient) {}

	getAll(branchId: string) {
		return this.http.get<Required<transportType>[]>(
			apiURL + '/mim/branch/' + branchId + '/transportTypes'
		);
	}

	getPaginated(
		branchId: string,
		pageNum: string | number,
		pageSize: string | number
	) {
		return this.http.get<Required<transportType>[]>(
			apiURL + '/mim/branch/' + branchId + '/transportTypes',
			{
				params: {
					pageNum: pageNum,
					count: pageSize,
					orderByAttributeType: ATTRIBUTETYPEIDENUM.NAME,
				},
			}
		);
	}

	get(branchId: string, artId: string) {
		return this.http.get<Required<transportType>>(
			apiURL + '/mim/branch/' + branchId + '/transportTypes/' + artId
		);
	}
}
