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
import { ATTRIBUTETYPEIDENUM } from '@osee/attributes/constants';
import { apiURL } from '@osee/environments';
import { HttpParamsType } from '@osee/shared/types';
import { ARTIFACTTYPEIDENUM } from '@osee/shared/types/constants';
import { createArtifact } from '@osee/transactions/functions';
import { TransactionService } from '@osee/transactions/services';
import type { legacyTransaction, transaction } from '@osee/transactions/types';
import { Observable } from 'rxjs';
import type {
	logicalType,
	logicalTypeFormDetail,
} from '../../types/logicaltype';
import type { PlatformType, PlatformTypeAttr } from '../../types/platformType';

@Injectable({
	providedIn: 'root',
})
export class TypesService {
	private http = inject(HttpClient);

	private transactionService = inject(TransactionService);

	/**
	 * Gets a list of Platform Types based on a filter condition using the platform types filter GET API
	 * @param filter @type {string} filter conditions for finding the correct platform types
	 * @param branchId @type {string} branch to fetch from
	 * @returns @type {Observable<PlatformType[]>} Observable of array of platform types matching filter conditions (see @type {PlatformType} and @type {Observable})
	 */
	getFilteredFullTypes(
		filter: string,
		branchId: string,
		pageNum: number,
		pageSize: number
	): Observable<PlatformType[]> {
		let params: HttpParamsType = {};
		//leaving a note here: it is ok to use this rest call with the regular types count as we are not allowing searches based on the enum set here, this is just so that platform type dialog and enum set dialogs have a full platform type to diff with
		if (filter !== '') {
			params = { ...params, filter: filter };
		}
		if (pageSize !== 0 && pageNum !== 0) {
			params = { ...params, count: pageSize, pageNum: pageNum };
		}
		params = { ...params, orderByAttributeType: ATTRIBUTETYPEIDENUM.NAME };
		return this.http.get<PlatformType[]>(
			apiURL + '/mim/branch/' + branchId + '/types',
			{
				params: params,
			}
		);
	}

	getFilteredTypesCount(
		filter: string,
		branchId: string
	): Observable<number> {
		let params: HttpParamsType = {};
		if (filter && filter !== '') {
			params = { ...params, filter: filter };
		}
		return this.http.get<number>(
			apiURL + '/mim/branch/' + branchId + '/types/filter/count',
			{ params: params }
		);
	}

	getPaginatedFilteredTypes(
		filter: string,
		branchId: string,
		count: number,
		pageNum: string | number
	): Observable<PlatformType[]> {
		const formattedFilter = filter === '' ? '' : filter.replace('/', '%2F');
		return this.http.get<PlatformType[]>(
			apiURL +
				'/mim/branch/' +
				branchId +
				'/types/filter/' +
				formattedFilter,
			{
				params: {
					count: count,
					pageNum: pageNum,
					orderByAttributeType: ATTRIBUTETYPEIDENUM.NAME,
				},
			}
		);
	}
	getTypes(branchId: string): Observable<PlatformType[]> {
		return this.http.get<PlatformType[]>(
			apiURL + '/mim/branch/' + branchId + '/types'
		);
	}

	getPaginatedTypes(branchId: string, pageNum: string) {
		return this.http.get<PlatformType[]>(
			apiURL + '/mim/branch/' + branchId + '/types',
			{
				params: {
					count: 3,
					pageNum: pageNum,
					orderByAttributeType: ATTRIBUTETYPEIDENUM.NAME,
				},
			}
		);
	}

	getType(branchId: string, platformTypeId: string) {
		return this.http.get<PlatformType>(
			apiURL + '/mim/branch/' + branchId + '/types/' + platformTypeId
		);
	}

	addNewPlatformTypeToTransaction(
		type: PlatformType,
		tx: Required<transaction>,
		key?: string
	) {
		const {
			id,
			gammaId,
			added,
			deleted,
			changes,
			applicability,
			enumSet,
			...remainingAttributes
		} = type;
		const attributeKeys = Object.keys(
			remainingAttributes
		) as (keyof typeof remainingAttributes)[];
		const attributes = attributeKeys.map((k) => remainingAttributes[k]);
		const results = createArtifact(
			tx,
			ARTIFACTTYPEIDENUM.PLATFORMTYPE,
			applicability,
			[],
			key,
			...attributes
		);
		return results.tx;
	}
	performMutation(body: legacyTransaction) {
		return this.transactionService.performMutation(body);
	}

	get logicalTypes() {
		return this.http.get<logicalType[]>(apiURL + '/mim/logicalType');
	}
	getLogicalTypeFormDetail(id: string) {
		return this.http.get<logicalTypeFormDetail<keyof PlatformTypeAttr>>(
			apiURL + '/mim/logicalType/' + id
		);
	}
}
