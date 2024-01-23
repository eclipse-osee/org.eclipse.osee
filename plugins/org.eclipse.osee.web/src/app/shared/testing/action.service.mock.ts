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
import { ActionService } from '@osee/shared/services';
import {
	CreateNewActionInterface,
	transitionAction,
} from '@osee/shared/types/configuration-management';
import {
	testARB,
	testBranchActions,
	testDataTransitionResponse,
	testDataVersion,
	testWorkFlow,
	testWorkType,
} from './configuration-management.response.mock';
import { MockXResultData } from './XResultData.response.mock';
import { MockNamedId } from './NamedId.response.mock';
import { testnewActionResponse } from './new-action.response.mock';

export const actionServiceMock: Partial<ActionService> = {
	getActionableItems(workType: string) {
		return of(testARB);
	},
	getWorkTypes() {
		return of([testWorkType]);
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
