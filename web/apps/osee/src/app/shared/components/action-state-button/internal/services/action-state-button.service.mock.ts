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
import { of } from 'rxjs';

import { ActionStateButtonService } from './action-state-button.service';
import {
	testBranchActions,
	testWorkFlow,
	testBranchInfo,
	MockNamedId,
	testCommitResponse,
	testDataTransitionResponse,
} from '@osee/shared/testing';

export const actionStateButtonServiceMock: Partial<ActionStateButtonService> = {
	approvedState: of('false'),
	branchAction: of(testBranchActions),
	branchWorkFlow: of(testWorkFlow),
	branchState: of(testBranchInfo),
	branchApproved: of('true'),
	teamsLeads: of(MockNamedId),
	branchTransitionable: of('true'),
	commitBranch(body: { committer: string; archive: string }) {
		return of(testCommitResponse);
	},
	doCommitBranch: of(testDataTransitionResponse),
	doTransition: of(testDataTransitionResponse),
	doApproveBranch: of(true),
	nextStates: of([]),
	previousStates: of([]),
	currentState: of({ state: '', rules: [], committable: false }),
};
export const actionStateButtonServiceMockApprove: Partial<ActionStateButtonService> =
	{
		approvedState: of('approvable'),
		branchAction: of(testBranchActions),
		branchWorkFlow: of(testWorkFlow),
		branchState: of(testBranchInfo),
		branchApproved: of('true'),
		teamsLeads: of(MockNamedId),
		branchTransitionable: of('true'),
		commitBranch(body: { committer: string; archive: string }) {
			return of(testCommitResponse);
		},
		doCommitBranch: of(testDataTransitionResponse),
		doTransition: of(testDataTransitionResponse),
		doApproveBranch: of(true),
		nextStates: of([]),
		previousStates: of([]),
		currentState: of({ state: '', rules: [], committable: false }),
	};
export const actionStateButtonServiceMockCommit: Partial<ActionStateButtonService> =
	{
		approvedState: of('committable'),
		branchAction: of(testBranchActions),
		branchWorkFlow: of(testWorkFlow),
		branchState: of(testBranchInfo),
		branchApproved: of('true'),
		teamsLeads: of(MockNamedId),
		branchTransitionable: of('true'),
		commitBranch(body: { committer: string; archive: string }) {
			return of(testCommitResponse);
		},
		doCommitBranch: of(testDataTransitionResponse),
		doTransition: of(testDataTransitionResponse),
		doApproveBranch: of(true),
		nextStates: of([]),
		previousStates: of([]),
		currentState: of({ state: '', rules: [], committable: false }),
	};
