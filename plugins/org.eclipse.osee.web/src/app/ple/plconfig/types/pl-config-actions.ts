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
import { user } from "src/app/userdata/types/user-data-user";
import { IdNameDescription, NameValuePair } from "./base-types/NameValuePair";
import { PlConfigBranchListingBranch } from "./pl-config-branch";
import { response } from "../../../types/responses";
import { pluser } from "./pl-config-users";

export enum PRIORITY{
    LowestPriority="1",
    LowPriority="2",
    MediumPriority="3",
    HighPriority = "4",
    HighestPriority="5" 
}
export interface PLConfigCreateActionInterface {
    originator: user,
    actionableItem: actionableItem,
    targetedVersion: string,
    title: string,
    description: string,
    priority: PRIORITY,
    changeType: {
        id: string,
        name: string,
        idString: string,
        idIntValue: number,
        description: string
    }
    
}
export class PLConfigCreateAction implements PLConfigCreateActionInterface{
    constructor(currentUser: user) {
        this.originator = currentUser;
    }
    priority=PRIORITY.LowestPriority;
    originator: user;
    actionableItem: actionableItem = new actionableItem();
    targetedVersion: string = '';
    title: string = '';
    description: string = '';
    changeType = {
        id: '-1',
        name: '',
        idString: '-1',
        idIntValue: -1,
        description:''
    }
}
export interface actionableItemInterface extends NameValuePair {
}
export class actionableItem implements actionableItemInterface {
    id = "";
    name = "";
}
export interface action {
    id: number,
    Name: string,
    AtsId: string,
    ActionAtsId: string,
    TeamWfAtsId: string,
    ArtifactType: string,
    actionLocation: string,
}
export class actionImpl implements action {
    id: number =0;
    Name: string ='';
    AtsId: string ='';
    ActionAtsId: string ='';
    TeamWfAtsId: string ='';
    ArtifactType: string =' ';
    actionLocation: string ='';

}
export interface TransitionActionDialogData {
    actions: action[],
    selectedUser: user,
}
export interface transitionActionInterface {
    toStateName: string,
    name: string,
    transitionUserArtId: string,
    workItemIds:workItem[]
}
export class transitionAction implements transitionActionInterface{
    constructor(toStateName?: string,name?:string,actions?: action[], currentUser?: user) {
        this.toStateName = toStateName || '';
        this.name = name || '';
        this.transitionUserArtId = currentUser && currentUser.id || '';
        if (actions?.values) {
            actions?.forEach((element) => {
                this.workItemIds.push({
                    id: element.id.toString(),
                    name: element.Name
                })
            })
        } else {
            this.workItemIds = [];
        }
    }
    toStateName: string ='';
    name: string ='';
    transitionUserArtId: string ='';
    workItemIds: workItem[]=[];
    
}
interface workItem extends NameValuePair{
}
export interface targetedVersion {
    id: number,
    name: string,
    Description: string | null,
    workflow: [],
    Name: string,
    "ats.Released": boolean,
    "ats.Next Version": boolean,
    "ats.Baseline Branch Id": string,
    "ats.Allow Create Branch": boolean,
    "ats. Allow Commit Branch": boolean
}
export interface newActionInterface {
    title: string,
    description: string,
    aiIds: string[],
    asUserId: string,
    createdByUserId: string,
    versionId: string,
    priority: PRIORITY,
    changeType: IdNameDescription
}
export class CreateAction implements newActionInterface{
    constructor(config?: PLConfigCreateAction) {
        this.title = config && config.title || '';
        this.description = config && config.description || '';
        if (config) {
            this.aiIds=[config.actionableItem.id]    
        }
        this.asUserId = config && config.originator.id || '';
        this.createdByUserId= config && config.originator.id || '';
        this.versionId = config && config.targetedVersion || '';
        this.priority = config && config.priority || PRIORITY.LowestPriority;
        this.changeType = config && { id:config.changeType.id,name:config.changeType.name,description:config.changeType.description } || { id: '-1', name: '',description:'' };
    }
    changeType: IdNameDescription = {id:'-1',name:'',description:''};
    priority: PRIORITY = PRIORITY.LowestPriority;
    title: string = '';
    description: string = '';
    aiIds: string[] = [];
    asUserId: string = '';
    createdByUserId: string = '';
    versionId: string = '';

}
export interface newActionResponse {
    action: null,
    results: response,
    teamWfs: [],
    workingBranchId: PlConfigBranchListingBranch
}
export interface teamWorkflow {
    id: number,
    Name: string,
    AtsId: string,
    ActionAtsId: string,
    TeamWfAtsId: string,
    ArtifactType: string,
    actionLocation: string,
    "ats.Actionable Item Reference": string,
    "ats.Log": string,
    "ats.Id": string,
    "ats.Created Date": string,
    "ats.Created by": string,
    "ats.Current State Type": string,
    "ats.Change Type": string,
    "ats.Workflow Definition": string,
    "ats.Percent Complete": number,
    "ats.Workflow Definition Reference": string,
    "ats.State": string,
    "ats.Current State": string,
    "ats.Team Definition Reference": string,
    "ats.Description": string,
    TeamName: string,
    Assignees: string,
    ChangeType: string,
    Priority: string,
    State: string,
    CreatedDate: string,
    CreatedBy: string,
    TargetedVersion:string,
}
export class teamWorkflowImpl implements teamWorkflow {
    id: number =0;
    Name: string ='';
    AtsId: string ='';
    ActionAtsId: string ='';
    TeamWfAtsId: string ='';
    ArtifactType: string ='';
    actionLocation: string ='';
    "ats.Actionable Item Reference": string ='';
    "ats.Log": string ='';
    "ats.Id": string ='';
    "ats.Created Date": string ='';
    "ats.Created by": string ='';
    "ats.Current State Type": string ='';
    "ats.Change Type": string ='';
    "ats.Workflow Definition": string ='';
    "ats.Percent Complete": number =0;
    "ats.Workflow Definition Reference": string ='';
    "ats.State": string ='';
    "ats.Current State": string ='';
    "ats.Team Definition Reference": string ='';
    "ats.Description": string ='';
    TeamName: string ='';
    Assignees: string ='';
    ChangeType: string ='';
    Priority: string ='';
    State: string ='';
    CreatedDate: string ='';
    CreatedBy: string ='';
    TargetedVersion: string ='';
    
}