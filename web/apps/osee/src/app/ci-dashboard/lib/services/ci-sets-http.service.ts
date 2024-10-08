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
import { Injectable, inject } from '@angular/core';
import { CISet } from '../types';
import { apiURL } from '@osee/environments';
import { HttpParamsType } from '@osee/shared/types';

@Injectable({
	providedIn: 'root',
})
export class CiSetsHttpService {
	private http = inject(HttpClient);

	getCiSets(
		branchId: string,
		activeOnly: boolean,
		orderByAttributeType?: `${number}`
	) {
		let params: HttpParamsType = { activeOnly };
		if (orderByAttributeType) {
			params = { ...params, orderByAttributeType };
		}
		return this.http.get<CISet[]>(`${apiURL}/script/tmo/${branchId}/set`, {
			params,
		});
	}
}
