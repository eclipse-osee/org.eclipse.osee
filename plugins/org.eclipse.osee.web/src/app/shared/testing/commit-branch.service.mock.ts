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
import { CommitBranchService } from '@osee/shared/services';
import { mergeDataMock } from '@osee/shared/testing';
import { branch } from '@osee/shared/types';
import { of } from 'rxjs';

export const commitBranchServiceMock: Partial<CommitBranchService> = {
	getMergeData(branchId: string) {
		return of(mergeDataMock);
	},
	getMergeBranch(branchId: string, parentBranchId: string) {
		return of({ id: '123', viewId: '-1' });
	},
	validateCommit(branch: branch) {
		return of({
			commitable: true,
			conflictCount: 0,
			conflictsResolved: 0,
		});
	},
};
