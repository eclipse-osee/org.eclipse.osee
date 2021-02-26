import { NameValuePair } from "./base-types/NameValuePair";
import { PlConfigBranchListingBranch } from "./pl-config-branch";
import { response } from "./pl-config-responses";
import { user } from "./pl-config-users";

export interface PLConfigCreateActionInterface {
    originator: user,
    actionableItem: actionableItem,
    targetedVersion: string,
    title: string,
    description:string,
}
export class PLConfigCreateAction implements PLConfigCreateActionInterface{
    originator: user = new user();
    actionableItem: actionableItem = new actionableItem();
    targetedVersion: string = '';
    title: string = '';
    description: string = '';
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
    constructor(toStateName?: string,name?:string,transition?: TransitionActionDialogData) {
        this.toStateName = toStateName || '';
        this.name = name || '';
        this.transitionUserArtId = transition && transition.selectedUser.artifactId || '';
        if (transition?.actions) {
            transition.actions.forEach((element) => {
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
}
export class CreateAction implements newActionInterface{
    constructor(config?: PLConfigCreateAction) {
        this.title = config && config.title || '';
        this.description = config && config.description || '';
        if (config) {
            this.aiIds=[config.actionableItem.id]    
        }
        this.asUserId = config && config.originator.userId || '';
        this.createdByUserId= config && config.originator.userId || '';
        this.versionId= config && config.targetedVersion || '';
    }
    title: string ='';
    description: string='';
    aiIds: string[]=[];
    asUserId: string='';
    createdByUserId: string='';
    versionId: string='';

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