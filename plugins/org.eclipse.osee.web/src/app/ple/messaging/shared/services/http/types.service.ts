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
import { Injectable } from '@angular/core';
import { Observable, of } from 'rxjs';
import { apiURL } from '@osee/environments';
import type {
	logicalType,
	logicalTypeFormDetail,
} from '../../types/logicaltype';
import type { PlatformType } from '../../types/platformType';
import {
	ATTRIBUTETYPEIDENUM,
	ARTIFACTTYPEIDENUM,
} from '@osee/shared/types/constants';
import {
	TransactionBuilderService,
	TransactionService,
} from '@osee/shared/transactions';
import type { HttpParamsType, relation, transaction } from '@osee/shared/types';

@Injectable({
	providedIn: 'root',
})
export class TypesService {
	constructor(
		private http: HttpClient,
		private builder: TransactionBuilderService,
		private transactionService: TransactionService
	) {}

	/**
	 * Gets a list of Platform Types based on a filter condition using the platform types filter GET API
	 * @param filter @type {string} filter conditions for finding the correct platform types
	 * @param branchId @type {string} branch to fetch from
	 * @returns @type {Observable<PlatformType[]>} Observable of array of platform types matching filter conditions (see @type {PlatformType} and @type {Observable})
	 */
	getFilteredTypes(
		filter: string,
		branchId: string,
		pageNum: number,
		pageSize: number
	): Observable<PlatformType[]> {
		return this.http.get<PlatformType[]>(
			apiURL + '/mim/branch/' + branchId + '/types/filter/' + filter,
			{
				params: {
					count: pageSize,
					pageNum: pageNum,
				},
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
		pageNum: string
	): Observable<PlatformType[]> {
		return this.http.get<PlatformType[]>(
			apiURL + '/mim/branch/' + branchId + '/types/filter/' + filter,
			{
				params: {
					count: 3,
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

	createPlatformType(
		branchId: string,
		type: PlatformType | Partial<PlatformType>,
		relations: relation[],
		transaction?: transaction,
		key?: string
	) {
		return of<transaction>(
			this.builder.createArtifact(
				type,
				ARTIFACTTYPEIDENUM.PLATFORMTYPE,
				relations,
				transaction,
				branchId,
				'Create Platform Type',
				key
			)
		);
	}

	changePlatformType(branchId: string, type: Partial<PlatformType>) {
		return of<transaction>(
			this.builder.modifyArtifact(
				type,
				undefined,
				branchId,
				'Change platform type attributes'
			)
		);
	}
	performMutation(body: transaction) {
		return this.transactionService.performMutation(body);
	}

	get logicalTypes() {
		return this.http.get<logicalType[]>(apiURL + '/mim/logicalType');
	}
	getLogicalTypeFormDetail(id: string) {
		return this.http.get<logicalTypeFormDetail>(
			apiURL + '/mim/logicalType/' + id
		);
	}
}
