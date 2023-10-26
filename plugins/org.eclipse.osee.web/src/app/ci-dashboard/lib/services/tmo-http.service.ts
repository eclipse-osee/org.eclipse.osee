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
import { DefReference, ScriptBatch } from '../types/tmo';
import { ResultReference } from '../types/tmo';
import { TestCaseReference } from '../types/tmo';
import { TestPoint } from '../types/tmo';
import { apiURL } from '@osee/environments';
import { ATTRIBUTETYPEIDENUM } from '@osee/shared/types/constants';

@Injectable({
	providedIn: 'root',
})
export class TmoHttpService {
	constructor(private http: HttpClient) {}

	getScriptDefList(branchId: string | number, setId: string | number) {
		return this.http.get<DefReference[]>(
			`${apiURL}/script/tmo/${branchId}/def/set/${setId}`
		);
	}

	getScriptResultList(branchId: string | number) {
		return this.http.get<ResultReference[]>(
			`${apiURL}/script/tmo/${branchId}/result`
		);
	}

	getTestCaseList(branchId: string | number) {
		return this.http.get<TestCaseReference[]>(
			`${apiURL}/script/tmo/${branchId}/case`
		);
	}

	getTestPointList(branchId: string | number) {
		return this.http.get<TestPoint[]>(
			`${apiURL}/script/tmo/${branchId}/point`
		);
	}

	getBatch(branchId: string, batchId: string) {
		return this.http.get<ScriptBatch>(
			`${apiURL}/script/tmo/${branchId}/batch/${batchId}`
		);
	}

	getBatches(
		branchId: string,
		setId: string,
		filter: string,
		pageNum: string | number,
		pageSize: number
	) {
		return this.http.get<ScriptBatch[]>(
			`${apiURL}/script/tmo/${branchId}/batch/set/${setId}`,
			{
				params: {
					count: pageSize,
					pageNum: pageNum,
					filter: filter,
				},
			}
		);
	}

	getBatchesCount(branchId: string, setId: string, filter: string) {
		return this.http.get<number>(
			`${apiURL}/script/tmo/${branchId}/batch/set/${setId}/count`,
			{
				params: {
					filter: filter,
				},
			}
		);
	}

	getBatchResults(
		branchId: string,
		batchId: string,
		pageNum: string | number,
		pageSize: number
	) {
		return this.http.get<ResultReference[]>(
			`${apiURL}/script/tmo/${branchId}/result/batch/${batchId}`,
			{
				params: {
					count: pageSize,
					pageNum: pageNum,
					orderByAttributeType: ATTRIBUTETYPEIDENUM.NAME,
				},
			}
		);
	}

	getBatchResultsCount(branchId: string, batchId: string) {
		return this.http.get<number>(
			`${apiURL}/script/tmo/${branchId}/result/batch/${batchId}/count`
		);
	}
}
