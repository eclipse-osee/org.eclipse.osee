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
	testCommitResponse,
	testDataTransitionResponse,
} from '@osee/shared/testing';
import {
	teamWorkflowState,
	action,
} from '@osee/shared/types/configuration-management';

export const actionStateButtonServiceMock: Partial<ActionStateButtonService> = {
	isTransitionApproved(action: action) {
		return of(true);
	},
	commitBranch(body: { committer: string; archive: string }) {
		return of(testCommitResponse);
	},
	doCommitBranch: of(testDataTransitionResponse),
	transition(state: teamWorkflowState, action: action) {
		return of(testDataTransitionResponse);
	},
	approveBranch(action: action) {
		return of(true);
	},
};
export const actionStateButtonServiceMockApprove: Partial<ActionStateButtonService> =
	{
		isTransitionApproved(action: action) {
			return of(true);
		},
		commitBranch(body: { committer: string; archive: string }) {
			return of(testCommitResponse);
		},
		doCommitBranch: of(testDataTransitionResponse),
		transition(state: teamWorkflowState, action: action) {
			return of(testDataTransitionResponse);
		},
		approveBranch(action: action) {
			return of(true);
		},
	};
export const actionStateButtonServiceMockCommit: Partial<ActionStateButtonService> =
	{
		isTransitionApproved(action: action) {
			return of(true);
		},
		commitBranch(body: { committer: string; archive: string }) {
			return of(testCommitResponse);
		},
		doCommitBranch: of(testDataTransitionResponse),
		transition(state: teamWorkflowState, action: action) {
			return of(testDataTransitionResponse);
		},
		approveBranch(action: action) {
			return of(true);
		},
	};
