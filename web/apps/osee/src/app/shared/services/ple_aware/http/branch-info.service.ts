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
import {
	ConflictUpdateData,
	CreateBranchDetails,
	CreateBranchResponse,
	UpdateBranchData,
	mergeConflict,
	mergeData,
	validateCommitResult,
} from '@osee/commit/types';
import { apiURL } from '@osee/environments';
import {
	HttpParamsType,
	branch,
	commitResponse,
	XResultData,
	viewedId,
	permissionEnum,
} from '@osee/shared/types';
import { workType } from '@osee/shared/types/configuration-management';

@Injectable({
	providedIn: 'root',
})
export class BranchInfoService {
	private http = inject(HttpClient);

	getBranch(id: string) {
		return this.http.get<branch>(apiURL + '/orcs/branches/' + id);
	}

	public getBranches(
		type: string,
		category: `${number}`,
		excludeCategory: `${number}`,
		workType: workType,
		filter?: string,
		pageSize?: number,
		pageNum?: string | number
	) {
		let params: HttpParamsType = {};
		if (type === '0') {
			params = { ...params, workType: workType };
		}
		params = { ...params, type: type };
		if (category !== '-1') {
			params = { ...params, category };
		}
		if (excludeCategory !== '-1') {
			params = { ...params, excludeCategory };
		}
		if (filter) {
			params = { ...params, filter: filter };
		}
		if (pageSize) {
			params = { ...params, count: pageSize };
		}
		if (pageNum) {
			params = { ...params, pageNum: pageNum };
		}
		return this.http.get<branch[]>(apiURL + '/ats/ple/branches', {
			params: params,
		});
	}

	public getBranchAccess(id: string) {
		return this.http.get<permissionEnum>(
			apiURL + '/orcs/branches/' + id + '/access'
		);
	}

	public getBranchCount(
		type: string,
		category: `${number}`,
		excludeCategory: `${number}`,
		workType: workType,
		filter?: string
	) {
		let params: HttpParamsType = {};
		params = { ...params, workType: workType };
		params = { ...params, type: type };
		if (category !== '-1') {
			params = { ...params, category };
		}
		if (excludeCategory !== '-1') {
			params = { ...params, excludeCategory };
		}
		if (filter) {
			params = { ...params, filter: filter };
		}
		return this.http.get<number>(apiURL + '/ats/ple/branches/count', {
			params: params,
		});
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

	public updateFromParent(branchId: string) {
		return this.http.post<UpdateBranchData>(
			apiURL + '/orcs/branches/' + branchId + '/updatefromparent',
			null
		);
	}

	public createBranch(body: CreateBranchDetails) {
		return this.http.post<CreateBranchResponse>(
			apiURL + `/orcs/branches`,
			body
		);
	}

	public setBranchCategory(
		branchId: string | number | undefined,
		category: string
	) {
		return this.http.post<XResultData>(
			`${apiURL}/orcs/branches/${branchId}/category/${category}`,
			null
		);
	}

	public validateCommit(branchId: string, parentBranchId: string) {
		return this.http.get<validateCommitResult>(
			apiURL +
				`/orcs/branches/${branchId}/commit/${parentBranchId}/validate`
		);
	}

	public loadMergeConflicts(branchId: string, parentBranchId: string) {
		return this.http.post<mergeConflict[]>(
			apiURL + `/orcs/branches/${branchId}/conflicts/${parentBranchId}`,
			null,
			{
				params: {
					load: true,
				},
			}
		);
	}

	public getMergeData(branchId: string) {
		return this.http.get<mergeData[]>(
			apiURL + `/orcs/branches/${branchId}/mergedata`
		);
	}

	public getMergeBranchId(branchId: string, parentBranchId: string) {
		return this.http.get<viewedId>(
			apiURL + `/orcs/branches/${branchId}/mergebranch/${parentBranchId}`
		);
	}

	public updateMergeConflicts(
		branchId: string,
		parentBranchId: string,
		data: ConflictUpdateData[]
	) {
		return this.http.put<number>(
			apiURL +
				`/orcs/branches/${branchId}/updateconflicts/${parentBranchId}`,
			data
		);
	}

	public archiveBranch(branchId: string) {
		return this.http.post(
			`${apiURL}/orcs/branches/${branchId}/archive`,
			null,
			{ observe: 'response' }
		);
	}
}
