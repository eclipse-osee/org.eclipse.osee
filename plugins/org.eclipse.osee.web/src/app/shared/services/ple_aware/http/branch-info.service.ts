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
import { iif } from 'rxjs';
import { apiURL } from '@osee/environments';
import {
	HttpParamsType,
	branch,
	commitResponse,
	response,
} from '@osee/shared/types';
import {
	OseeNode,
	nodeData,
	OseeEdge,
	connection,
} from '@osee/messaging/shared/types';

@Injectable({
	providedIn: 'root',
})
export class BranchInfoService {
	constructor(private http: HttpClient) {}

	getBranch(id: string) {
		return this.http.get<branch>(apiURL + '/orcs/branches/' + id);
	}

	public getBranches(
		type: string,
		category?: string,
		searchType?: boolean,
		workType?: string
	) {
		let params: HttpParamsType = {};
		if (workType && workType !== '') {
			params = { ...params, workType: workType };
		}
		return iif(
			() => searchType || false,
			this.http.get<branch[]>(apiURL + '/ats/ple/branches/' + type, {
				params: params,
			}),
			this.http.get<branch[]>(
				apiURL + `/orcs/branches/${type}/category/${category}`
			)
		);
	}

	public commitBranch(
		branchId: string | number | undefined,
		parentBranchId: string | number | undefined,
		body: { committer: string; archive: string }
	) {
		return this.http.post<commitResponse>(
			apiURL + '/orcs/branches/' + branchId + '/commit/' + parentBranchId,
			body
		);
	}
	public setBranchCategory(
		branchId: string | number | undefined,
		category: string
	) {
		return this.http.post<response>(
			`${apiURL}/orcs/branches/${branchId}/category/${category}`,
			null
		);
	}
}
