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
import { DefReference, ScriptBatch, SetDiff } from '../types/tmo';
import { ResultReference } from '../types/tmo';
import { apiURL } from '@osee/environments';
import { ATTRIBUTETYPEIDENUM } from '@osee/attributes/constants';
import { Observable } from 'rxjs';
import { HttpParamsType } from '@osee/shared/types';

@Injectable({
	providedIn: 'root',
})
export class TmoHttpService {
	private http = inject(HttpClient);

	getScriptDefList(branchId: string | number, setId: string | number) {
		return this.http.get<DefReference[]>(
			`${apiURL}/script/tmo/${branchId}/def/set/${setId}`
		);
	}

	getScriptDefListPagination(
		branchId: string | number,
		setId: string | number,
		filter?: string,
		pageNum?: number,
		pageSize?: number
	): Observable<DefReference[]> {
		let params: HttpParamsType = {};
		if (filter && filter !== '') {
			params = { ...params, filter: filter };
		}
		if (pageNum) {
			params = { ...params, pageNum: pageNum };
		}
		if (pageSize) {
			params = { ...params, count: pageSize };
		}
		return this.http.get<DefReference[]>(
			`${apiURL}/script/tmo/${branchId}/def/set/${setId}`,
			{
				params: params,
			}
		);
	}

	getFilteredScriptDefCount(branchId: string | number, filter?: string) {
		let params: HttpParamsType = {};
		if (filter && filter !== '') {
			params = { ...params, filter: filter };
		}

		return this.http.get<number>(
			`${apiURL}/script/tmo/${branchId}/def/count`,
			{ params }
		);
	}

	getScriptDef(branchId: string | number, defId: string | number) {
		return this.http.get<DefReference>(
			`${apiURL}/script/tmo/${branchId}/def/${defId}`
		);
	}

	getAllScriptResults(branchId: string | number) {
		return this.http.get<ResultReference[]>(
			`${apiURL}/script/tmo/${branchId}/result`
		);
	}

	getScriptResults(branchId: string | number, defId: string | number) {
		return this.http.get<ResultReference[]>(
			`${apiURL}/script/tmo/${branchId}/result/def/${defId}`
		);
	}

	getScriptResult(branchId: string | number, resId: string | number) {
		return this.http.get<ResultReference>(
			`${apiURL}/script/tmo/${branchId}/result/${resId}/details`
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

	downloadTmo(branchId: string, resultId: string) {
		return this.http.get(
			`${apiURL}/script/tmo/${branchId}/download/${resultId}`,
			{
				responseType: 'blob',
			}
		);
	}

	downloadBatch(branchId: string, batchId: string) {
		return this.http.get(
			`${apiURL}/script/tmo/${branchId}/download/batch/${batchId}`,
			{
				responseType: 'blob',
			}
		);
	}

	getSetDiffs(branchId: string, setIds: string[]) {
		return this.http.post<SetDiff[]>(
			`${apiURL}/script/tmo/${branchId}/diff`,
			setIds
		);
	}
}
