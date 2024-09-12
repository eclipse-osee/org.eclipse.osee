/*********************************************************************
 * Copyright (c) 2024 Boeing
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
import { inject, Injectable } from '@angular/core';
import { apiURL } from '@osee/environments';
import { HttpParamsType } from '@osee/shared/types';
import { workType } from '@osee/shared/types/configuration-management';
import {
	applyResult,
	branchSelected,
	peerReviewApplyData,
} from '../types/peer-review';

@Injectable({
	providedIn: 'root',
})
export class PeerReviewService {
	private http = inject(HttpClient);

	getWorkingBranches(
		prBranchId: `${number}`,
		branchType: string,
		category: string,
		workType: workType,
		filter?: string,
		pageSize?: number,
		pageNum?: string | number
	) {
		let params: HttpParamsType = {};
		params = { ...params, prBranch: prBranchId };
		params = { ...params, workType };
		params = { ...params, type: branchType };
		if (category.length > 0) {
			params = { ...params, category };
		}
		if (filter) {
			params = { ...params, filter };
		}
		if (pageSize) {
			params = { ...params, count: pageSize };
		}
		if (pageNum) {
			params = { ...params, pageNum };
		}

		return this.http.get<branchSelected[]>(
			apiURL + '/ats/ple/branches/pr',
			{ params }
		);
	}

	applyWorkingBranches(prBranchId: `${number}`, data: peerReviewApplyData) {
		return this.http.post<applyResult>(
			apiURL + `/mim/pr/${prBranchId}/apply`,
			data
		);
	}
}
