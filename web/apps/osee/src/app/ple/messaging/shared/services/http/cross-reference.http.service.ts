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
import type { CrossReference } from '@osee/messaging/shared/types';
import { apiURL } from '@osee/environments';
import { HttpParamsType } from '@osee/shared/types';
import { ATTRIBUTETYPEIDENUM } from '@osee/shared/types/constants';

@Injectable({
	providedIn: 'root',
})
export class CrossReferenceHttpService {
	constructor(private http: HttpClient) {}

	getAll(
		branchId: string,
		connectionId: string,
		filter: string,
		viewId: string,
		pageNum?: string | number,
		pageSize?: number,
		orderByName?: boolean
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
		if (orderByName) {
			params = {
				...params,
				orderByAttributeType: ATTRIBUTETYPEIDENUM.NAME,
			};
		}
		if (
			connectionId &&
			connectionId !== '' &&
			connectionId !== '0' &&
			connectionId !== '-1'
		) {
			params = { ...params, connectionId: connectionId };
		}
		return this.http.get<CrossReference[]>(
			apiURL + '/mim/branch/' + branchId + '/crossReference',
			{
				params: params,
			}
		);
	}

	getCount(
		branchId: string,
		connectionId: string,
		filter: string,
		viewId: string
	) {
		let params: HttpParamsType = {};
		if (viewId && viewId !== '') {
			params = { ...params, viewId: viewId };
		}
		if (filter && filter !== '') {
			params = { ...params, filter: filter };
		}
		return this.http.get<number>(
			apiURL + '/mim/branch/' + branchId + '/crossReference/count',
			{
				params: params,
			}
		);
	}

	get(branchId: string, artId: string) {
		return this.http.get<CrossReference>(
			apiURL + '/mim/branch/' + branchId + '/crossReference/' + artId
		);
	}
}
