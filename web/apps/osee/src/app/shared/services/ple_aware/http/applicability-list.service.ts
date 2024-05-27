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
import { HttpClient } from '@angular/common/http';
import { Injectable, inject } from '@angular/core';
import { apiURL } from '@osee/environments';
import { HttpParamsType, nameAttribute } from '@osee/shared/types';
import { applic } from '@osee/applicability/types';

@Injectable({
	providedIn: 'root',
})
export class ApplicabilityListService {
	private http = inject(HttpClient);

	getApplicabilities(
		branchId: string | number,
		orderByName?: boolean,
		pageNum?: string | number,
		count?: string | number,
		filter?: string
	) {
		let params: HttpParamsType = {};
		if (pageNum) {
			params = { ...params, pageNum: pageNum };
		}
		if (count) {
			params = { ...params, count: count };
		}
		if (orderByName) {
			params = { ...params, orderByName: orderByName };
		}
		if (filter && filter !== '') {
			params = { ...params, filter: filter };
		}
		return this.http.get<applic[]>(
			apiURL + '/orcs/branch/' + branchId + '/applic',
			{
				params: params,
			}
		);
	}

	getApplicabilityCount(branchId: string | number, filter?: string) {
		let params: HttpParamsType = {};
		if (filter && filter !== '') {
			params = { ...params, filter: filter };
		}
		return this.http.get<number>(
			apiURL + '/orcs/branch/' + branchId + '/applic/count',
			{
				params: params,
			}
		);
	}

	getViews(branchId: string | number, orderByAttributeTypeId?: string) {
		let params: HttpParamsType = {};
		if (orderByAttributeTypeId && orderByAttributeTypeId !== '') {
			params = {
				...params,
				orderByAttributeType: orderByAttributeTypeId,
			};
		} else {
			params = { ...params, orderByAttributeType: nameAttribute.typeId };
		}
		return this.http.get<applic[]>(
			apiURL + '/orcs/branch/' + branchId + '/applic/views',
			{
				params: params,
			}
		);
	}
}
