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
import {
	action,
	transitionAction,
	teamWorkflow,
	targetedVersion,
	WorkType,
	teamWorkflowToken,
	teamWorkflowDetails,
} from '@osee/shared/types/configuration-management';
import { commitResponse, transitionResponse } from '@osee/shared/types';
import { MockXResultData } from './XResultData.response.mock';
import { MockUserResponse } from './user.response.mock';
import { artifactWithRelationsMock } from '@osee/artifact-with-relations/testing';
import { testBranchInfo } from './branch-info.response.mock';

export const testBranchActions: action[] = [
	{
		id: 200578,
		Name: 'aaa',
		AtsId: 'TW195',
		ActionAtsId: 'ACT154',
		TeamWfAtsId: 'TW195',
		ArtifactType: 'Team Workflow',
		actionLocation: '/ats/ui/action/TW195',
	},
];

export const testCommitResponse: commitResponse = {
	tx: { branchId: '0', id: '0' },
	results: MockXResultData,
	success: true,
	failed: false,
};
export const testTransitionAction: transitionAction = {
	toStateName: 'Review',
	name: 'name',
	transitionUserArtId: '0',
	workItemIds: [],
};
export const testWorkFlow: teamWorkflow = {
	id: 56465132,
	Name: 'name',
	AtsId: 'TW200',
	ActionAtsId: 'TW200-name',
	TeamWfAtsId: 'ACT421',
	ArtifactType: 'TeamWorkflow',
	actionLocation: 'ats/ui/workflow/TW200',
	'ats.Actionable Item Reference': 'string',
	'ats.Log': '',
	'ats.Id': '200',
	'ats.Created Date': '03/08/2021',
	'ats.Created by': 'Example User',
	'ats.Current State Type': 'In Review<1111>',
	'ats.Change Type': 'Improvement',
	'ats.Workflow Definition': '',
	'ats.Percent Complete': 0,
	'ats.Workflow Definition Reference': '',
	'ats.State': 'Review',
	'ats.Current State': 'Review',
	'ats.Team Definition Reference': 'TestTeamDefinition',
	'ats.Description': 'Description',
	TeamName: 'SAW',
	Assignees: [{ id: '54321', name: 'Example User' }],
	ChangeType: 'Improvment',
	Priority: 'low',
	State: 'Review',
	CreatedDate: '03/08/2021',
	CreatedBy: 'Example User',
	TargetedVersion: 'SAW PL ARB',
	TargetedVersionId: '1111',
	previousStates: [],
	toStates: [],
	currentState: {
		state: '',
		rules: [],
		committable: false,
	},
};

export const teamWorkflowDetailsMock: teamWorkflowDetails = {
	...testWorkFlow,
	artifact: artifactWithRelationsMock,
	leads: [MockUserResponse],
	parentBranch: { id: '4', name: 'Parent Branch' },
	workingBranch: { id: '555', name: 'Working Branch', branchState: '0' },
	branchesToCommitTo: [testBranchInfo],
};

export const teamWorkflowTokenMock: teamWorkflowToken = {
	id: '111',
	atsId: 'TW01',
	name: 'Test Workflow',
};

export const testARB = [
	{
		id: '123',
		name: 'First ARB',
		workType: 'ARB',
	},
	{
		id: '456',
		name: 'second ARB',
		workType: 'ARB',
	},
];

export const testDataTransitionResponse: transitionResponse = {
	cancelled: false,
	workItemIds: [],
	results: [],
	transitionWorkItems: [],
	transaction: {
		branchId: '0',
		id: '0',
	},
	empty: true,
};

export const testDataVersion: targetedVersion[] = [
	{
		id: 0,
		name: 'name',
		Description: null,
	},
];

export const testWorkType: WorkType = {
	name: 'Work_Type',
	humanReadableName: 'Work Type',
	description: 'This is a work type',
	createBranchDefault: true,
};

export const testAgilePoints: string[] = ['1', '2', '3', '5'];
