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
export interface teamWorkflow {
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
	Assignees: string;
	ChangeType: string;
	Priority: string;
	State: string;
	CreatedDate: string;
	CreatedBy: string;
	TargetedVersion: string;
}
export class teamWorkflowImpl implements teamWorkflow {
	id: number = 0;
	Name: string = '';
	AtsId: string = '';
	ActionAtsId: string = '';
	TeamWfAtsId: string = '';
	ArtifactType: string = '';
	actionLocation: string = '';
	'ats.Actionable Item Reference': string = '';
	'ats.Log': string = '';
	'ats.Id': string = '';
	'ats.Created Date': string = '';
	'ats.Created by': string = '';
	'ats.Current State Type': string = '';
	'ats.Change Type': string = '';
	'ats.Workflow Definition': string = '';
	'ats.Percent Complete': number = 0;
	'ats.Workflow Definition Reference': string = '';
	'ats.State': string = '';
	'ats.Current State': string = '';
	'ats.Team Definition Reference': string = '';
	'ats.Description': string = '';
	TeamName: string = '';
	Assignees: string = '';
	ChangeType: string = '';
	Priority: string = '';
	State: string = '';
	CreatedDate: string = '';
	CreatedBy: string = '';
	TargetedVersion: string = '';
}
