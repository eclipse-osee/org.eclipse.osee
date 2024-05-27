/*********************************************************************
 * Copyright (c) 2023 Boeing
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
	artifactWithRelations,
	artifactWithRelationsSentinel,
} from '@osee/artifact-with-relations/types';
import { NamedId } from '../named-id';

export type teamWorkflow = {
	id: number;
	Name: string;
	AtsId: string;
	ActionAtsId: string;
	TeamWfAtsId: string;
	ArtifactType: string;
	actionLocation: string;
	'ats.Actionable Item Reference': string;
	'ats.Log': string;
	'ats.Id': string;
	'ats.Created Date': string;
	'ats.Created by': string;
	'ats.Current State Type': string;
	'ats.Change Type': string;
	'ats.Workflow Definition': string;
	'ats.Percent Complete': number;
	'ats.Workflow Definition Reference': string;
	'ats.State': string;
	'ats.Current State': string;
	'ats.Team Definition Reference': string;
	'ats.Description': string;
	TeamName: string;
	Assignees: teamWorkflowUser[];
	ChangeType: string;
	Priority: string;
	State: string;
	CreatedDate: string;
	CreatedBy: string;
	TargetedVersion: string;
	TargetedVersionId: string;
	previousStates: teamWorkflowState[];
	toStates: teamWorkflowState[];
	currentState: teamWorkflowState;
};

export type teamWorkflowState = {
	state: string;
	rules: string[];
	committable: boolean;
};
export class teamWorkflowImpl implements teamWorkflow {
	id = 0;
	Name = '';
	AtsId = '';
	ActionAtsId = '';
	TeamWfAtsId = '';
	ArtifactType = '';
	actionLocation = '';
	'ats.Actionable Item Reference' = '';
	'ats.Log' = '';
	'ats.Id' = '';
	'ats.Created Date' = '';
	'ats.Created by' = '';
	'ats.Current State Type' = '';
	'ats.Change Type' = '';
	'ats.Workflow Definition' = '';
	'ats.Percent Complete' = 0;
	'ats.Workflow Definition Reference' = '';
	'ats.State' = '';
	'ats.Current State' = '';
	'ats.Team Definition Reference' = '';
	'ats.Description' = '';
	TeamName = '';
	Assignees: teamWorkflowUser[] = [];
	ChangeType = '';
	Priority = '';
	State = '';
	CreatedDate = '';
	CreatedBy = '';
	TargetedVersion = '';
	TargetedVersionId = '';
	previousStates: teamWorkflowState[] = [];
	toStates: teamWorkflowState[] = [];
	currentState: teamWorkflowState = {
		state: '',
		rules: [],
		committable: false,
	};
}

type teamWorkflowUser = { id: `${number}`; name: string };

export type teamWorkflowDetails = {
	artifact: artifactWithRelations;
	leads: NamedId[];
	parentBranch: NamedId;
	workingBranch: { id: `${number}`; name: string; branchState: string };
	branchesToCommitTo: NamedId[];
	branchEditable: boolean;
} & teamWorkflow;

export class teamWorkflowDetailsImpl
	extends teamWorkflowImpl
	implements teamWorkflowDetails
{
	artifact: artifactWithRelations = artifactWithRelationsSentinel;
	leads: NamedId[] = [];
	parentBranch: NamedId = { id: '-1', name: '' };
	workingBranch = { id: '-1' as `${number}`, name: '', branchState: '' };
	branchesToCommitTo: NamedId[] = [];
	branchEditable = false;
}

export type workDefinition = {
	id: `${number}`;
	name: string;
	states: workDefinitionState[];
};

export type workDefinitionState = {
	id: `${number}`;
	name: string;
	completed: boolean;
	cancelled: boolean;
	toStateNames: string[];
	completedOrCancelled: boolean;
	stateType: 'Working' | 'Completed' | 'Cancelled';
	layoutItems: workDefinitonLayoutItem[];
};

export type workDefinitonLayoutItem = {
	id: `${number}`;
	name: string;
	attributeType: `${number}`;
	attributeType2: `${number}` | null;
	options: {
		xoptions: workDefinitonLayoutOption[];
	};
};

export type workDefinitonLayoutOption =
	| 'REQUIRED_FOR_TRANSITION'
	| 'NOT_REQUIRED_FOR_TRANSITION';

export type teamWorkflowToken = {
	id: `${number}`;
	name: string;
	atsId: string;
};

export type TeamWorkflowSearchCriteria = {
	search?: string;
	assignees?: `${number}`[];
	originators?: `${number}`[];
	inProgressOnly?: boolean;
	searchByArtId?: boolean;
};

export class TeamWorkflowSearchCriteriaImpl
	implements TeamWorkflowSearchCriteria
{
	search = '';
	assignees: `${number}`[] = [];
	originators: `${number}`[] = [];
	inProgressOnly = true;
	searchById?: boolean = false;
}
