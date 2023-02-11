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
import { testBranchInfo } from '../../../../../testing/branch-info.response.mock';
import {
	testBranchActions,
	testCommitResponse,
	testDataTransitionResponse,
	testWorkFlow,
} from '../../../../../testing/configuration-management.response.mock';
import { MockXResultData } from '../../../../../testing/XResultData.response.mock';
import { MockNamedId } from '../../../../../testing/NamedId.response.mock';
import { testnewActionResponse } from '../../../../../testing/new-action.response.mock';
import { MockUserResponse } from '../../../../../testing/user.response.mock';
import { CreateAction } from '@osee/shared/types/configuration-management';
import { ActionStateButtonService } from './action-state-button.service';

export const actionStateButtonServiceMock: Partial<ActionStateButtonService> = {
	approvedState: of('false'),
	branchAction: of(testBranchActions),
	branchWorkFlow: of(testWorkFlow),
	branchState: of(testBranchInfo),
	branchApproved: of('true'),
	teamsLeads: of(MockNamedId),
	branchTransitionable: of('true'),
	addActionInitialStep: of(MockUserResponse),
	commitBranch(body: { committer: string; archive: string }) {
		return of(testCommitResponse);
	},
	doCommitBranch: of(testDataTransitionResponse),
	doTransition: of(testDataTransitionResponse),
	doApproveBranch: of(MockXResultData),
	doAddAction(value: CreateAction, category: string) {
		return of(testnewActionResponse);
	},
	actionableItems: of([
		{
			id: '123',
			name: 'First ARB',
		},
		{
			id: '456',
			name: 'Second ARB',
		},
	]),
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
		addActionInitialStep: of(MockUserResponse),
		commitBranch(body: { committer: string; archive: string }) {
			return of(testCommitResponse);
		},
		doCommitBranch: of(testDataTransitionResponse),
		doTransition: of(testDataTransitionResponse),
		doApproveBranch: of(MockXResultData),
		doAddAction(value: CreateAction, category: string) {
			return of(testnewActionResponse);
		},
		actionableItems: of([
			{
				id: '123',
				name: 'First ARB',
			},
			{
				id: '456',
				name: 'Second ARB',
			},
		]),
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
		addActionInitialStep: of(MockUserResponse),
		commitBranch(body: { committer: string; archive: string }) {
			return of(testCommitResponse);
		},
		doCommitBranch: of(testDataTransitionResponse),
		doTransition: of(testDataTransitionResponse),
		doApproveBranch: of(MockXResultData),
		doAddAction(value: CreateAction, category: string) {
			return of(testnewActionResponse);
		},
		actionableItems: of([
			{
				id: '123',
				name: 'First ARB',
			},
			{
				id: '456',
				name: 'Second ARB',
			},
		]),
	};
