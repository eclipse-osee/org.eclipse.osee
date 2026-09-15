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
import { HttpClient, HttpParams } from '@angular/common/http';
import { Injectable, inject } from '@angular/core';
import { changeReportRow } from '@osee/shared/types/change-report';
import { apiURL } from '@osee/environments';

@Injectable({
	providedIn: 'root',
})
export class ChangeReportHttpService {
	private http = inject(HttpClient);

	/**
	 * @deprecated Use getFilteredPaginatedChangeReport for paginated, filtered results.
	 */
	getBranchChangeReport(branch1Id: string, branch2Id: string) {
		return this.http.get<changeReportRow[]>(
			`${apiURL}/orcs/branches/${branch1Id}/changes/${branch2Id}`
		);
	}

	getFilteredPaginatedChangeReport(
		branch1Id: string,
		branch2Id: string,
		filter = '',
		pageNum = 0,
		pageSize = 10,
		attributeType = ''
	) {
		let params = new HttpParams()
			.set('pageNum', pageNum.toString())
			.set('count', pageSize.toString());
		if (filter) {
			params = params.set('filter', filter);
		}
		if (attributeType) {
			params = params.set('attributeType', attributeType);
		}
		return this.http.get<changeReportRow[]>(
			`${apiURL}/orcs/branches/${branch1Id}/changes/${branch2Id}/filtered`,
			{ params }
		);
	}

	getFilteredPaginatedChangeReportCount(
		branch1Id: string,
		branch2Id: string,
		filter = '',
		attributeType = ''
	) {
		let params = new HttpParams();
		if (filter) {
			params = params.set('filter', filter);
		}
		if (attributeType) {
			params = params.set('attributeType', attributeType);
		}
		return this.http.get<number>(
			`${apiURL}/orcs/branches/${branch1Id}/changes/${branch2Id}/filtered/count`,
			{ params }
		);
	}

	getTxChangeReport(branchId: string, tx1: string, tx2: string) {
		return this.http.get<changeReportRow[]>(
			`${apiURL}/orcs/branches/${branchId}/changes/${tx1}/${tx2}`
		);
	}
}
