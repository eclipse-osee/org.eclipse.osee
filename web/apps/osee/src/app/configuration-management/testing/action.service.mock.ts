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
import { ActionService } from '@osee/configuration-management/services';
import { transitionAction } from '@osee/shared/types/configuration-management';
import { CreateNewActionInterface } from '@osee/configuration-management/types';
import {
	MockNamedId,
	MockXResultData,
	teamWorkflowTokenMock,
	testARB,
	testAgilePoints,
	testBranchActions,
	testDataTransitionResponse,
	testDataVersion,
	testWorkFlow,
	testWorkType,
} from '@osee/shared/testing';
import { testnewActionResponse } from './new-action.response.mock';

export const actionServiceMock: Partial<ActionService> = {
	getActionableItems(workType: string) {
		return of(testARB);
	},
	getWorkTypes() {
		return of([testWorkType]);
	},
	createAction(body: CreateNewActionInterface) {
		return of(testnewActionResponse);
	},
	createActionAndWorkingBranch(body: CreateNewActionInterface) {
		return of(testnewActionResponse);
	},
	commitBranch(teamWf: string, branchId: string | number) {
		return of(MockXResultData);
	},
	getWorkFlow(id: string | number) {
		return of(testWorkFlow);
	},
	getTeamWorkflowsForUser(
		search: string,
		userId: `${number}`,
		count?: number,
		pageNum?: number
	) {
		return of([teamWorkflowTokenMock]);
	},
	getTeamWorkflowsForUserCount(userId: `${number}`) {
		return of(1);
	},
	getAction(artifactId: string | number) {
		return of(testBranchActions);
	},
	getPoints() {
		return of(testAgilePoints);
	},
	validateTransitionAction(body: transitionAction) {
		return of(testDataTransitionResponse);
	},
	transitionAction(body: transitionAction) {
		return of(testDataTransitionResponse);
	},
	getVersions(arbId: string) {
		return of(testDataVersion);
	},
	approveBranch(teamWf: string | number) {
		return of(true);
	},
	getTeamLeads(teamDef: string | number) {
		return of(MockNamedId);
	},
	getBranchApproved(teamWf: string | number) {
		return of(true);
	},
};
