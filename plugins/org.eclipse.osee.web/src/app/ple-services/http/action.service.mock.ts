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
import { ActionService } from './action.service';
import { CreateNewActionInterface } from '../../types/configuration-management/create-new-action';
import { transitionAction } from '../../types/configuration-management/transition-action';
import {
	testARB,
	testBranchActions,
	testDataTransitionResponse,
	testDataVersion,
	testWorkFlow,
} from '../../testing/configuration-management.response.mock';
import { MockXResultData } from '../../testing/XResultData.response.mock';
import { MockNamedId } from '../../testing/NamedId.response.mock';
import { testnewActionResponse } from '../../testing/new-action.response.mock';

export const actionServiceMock: Partial<ActionService> = {
	getActionableItems(workType: string) {
		return of(testARB);
	},
	createBranch(body: CreateNewActionInterface) {
		return of(testnewActionResponse);
	},
	commitBranch(teamWf: string, branchId: string | number) {
		return of(MockXResultData);
	},
	getWorkFlow(id: string | number) {
		return of(testWorkFlow);
	},
	getAction(artifactId: string | number) {
		return of(testBranchActions);
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
		return of(MockXResultData);
	},
	getTeamLeads(teamDef: string | number) {
		return of(MockNamedId);
	},
	getBranchApproved(teamWf: string | number) {
		return of(MockXResultData);
	},
};
