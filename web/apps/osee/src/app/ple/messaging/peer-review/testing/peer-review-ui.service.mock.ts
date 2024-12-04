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
import { BehaviorSubject, of } from 'rxjs';
import { PeerReviewUiService } from '../services/peer-review-ui.service';
import { workType } from '@osee/shared/types/configuration-management';
import { testBranchListing } from '@osee/shared/testing';
import { branch } from '@osee/shared/types';
import { signal } from '@angular/core';

export const PeerReviewUiServiceMock: Partial<PeerReviewUiService> = {
	prBranchId: new BehaviorSubject<`${number}`>('1234'),
	branchesToAdd: signal<branch[]>([testBranchListing[1]]),
	branchesToRemove: signal<branch[]>([testBranchListing[2]]),
	prBranch: of(testBranchListing[0]),
	workingBranches: of([
		{
			branch: testBranchListing[0],
			selected: true,
			selectable: true,
			committedToBaseline: false,
		},
		{
			branch: testBranchListing[1],
			selected: false,
			selectable: false,
			committedToBaseline: false,
		},
	]),
	getPeerReviewBranches(
		filter: string,
		branchType: string,
		workType: workType,
		pageSize: number,
		pageNum: string | number
	) {
		return of(testBranchListing);
	},
	getPeerReviewBranchesCount(
		filter: string,
		branchType: string,
		workType: workType
	) {
		return of(3);
	},
	applyWorkingBranches() {
		return of({ success: true, statusText: '' });
	},
};
