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
import { apiURL } from '@osee/environments';
import { HttpParamsType, NamedId } from '@osee/shared/types';

@Injectable({
	providedIn: 'root',
})
export class RatesService {
	constructor(private http: HttpClient) {}

	getFiltered(
		branchId: string,
		filter?: string,
		viewId?: string,
		pageNum?: string | number,
		pageSize?: number
	) {
		let params: HttpParamsType = {};
		if (pageNum) {
			params = { ...params, pageNum: pageNum };
		}
		if (pageSize) {
			params = { ...params, count: pageSize };
		}
		if (viewId && viewId !== '') {
			params = { ...params, viewId: viewId };
		}
		if (filter && filter !== '') {
			params = { ...params, filter: filter };
		}
		return this.http.get<NamedId[]>(
			apiURL + '/mim/branch/' + branchId + '/rates',
			{
				params: params,
			}
		);
	}
	getOne(branchId: string, rateId: string) {
		return this.http.get<NamedId>(
			apiURL + '/mim/branch/' + branchId + '/rates/' + rateId
		);
	}

	getCount(branchId: string, filter?: string, viewId?: string) {
		let params: HttpParamsType = {};
		if (viewId && viewId !== '') {
			params = { ...params, viewId: viewId };
		}
		if (filter && filter !== '') {
			params = { ...params, filter: filter };
		}
		return this.http.get<number>(
			apiURL + '/mim/branch/' + branchId + '/rates/count',
			{
				params: params,
			}
		);
	}
}
