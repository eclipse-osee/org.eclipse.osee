import { user } from "src/app/userdata/types/user-data-user"
import { NameValuePair } from "../types/base-types/NameValuePair";
import { action, newActionResponse, targetedVersion, teamWorkflow, transitionAction } from "../types/pl-config-actions"
import { PlConfigBranchListingBranch } from "../types/pl-config-branch";
import { commitResponse, response, transitionResponse } from "../types/pl-config-responses";

export const testBranchActions : action[] =
  [
    {
      id: 200578,
      Name: "aaa",
      AtsId: "TW195",
      ActionAtsId: "ACT154",
      TeamWfAtsId: "TW195",
      ArtifactType: "Team Workflow",
      actionLocation: "/ats/ui/action/TW195"
    }
  ]
  
  export const testDataResponse: response = {
    empty: false,
    errorCount: 0,
    errors: false,
    failed: false,
    ids: [],
    infoCount: 0,
    numErrors: 0,
    numErrorsViaSearch: 0,
    numWarnings: 0,
    numWarningsViaSearch: 0,
    results: [],
    success: true,
    tables: [],
    title: null,
    txId: '0',
    warningCount: 0
  };
  
  export const testCommitResponse: commitResponse = {
    tx: {branchId: '0', id:'0'},
    results: testDataResponse,
    success: true,
    failed:false,
    
}
export const testTransitionAction : transitionAction = {
  toStateName : 'Review',
  name: 'name',
  transitionUserArtId: '0',
  workItemIds:[]  
}
export const testWorkFlow : teamWorkflow =
{
  id: 56465132,
  Name: "name",
  AtsId: "TW200",
  ActionAtsId: "TW200-name",
  TeamWfAtsId: "ACT421",
  ArtifactType: "TeamWorkflow",
  actionLocation: "ats/ui/workflow/TW200",
  "ats.Actionable Item Reference": "string",
  "ats.Log": "",
  "ats.Id": "200",
  "ats.Created Date": "03/08/2021",
  "ats.Created by": "Example User",
  "ats.Current State Type": "In Review<1111>",
  "ats.Change Type": "Improvement",
  "ats.Workflow Definition": "",
  "ats.Percent Complete": 0,
  "ats.Workflow Definition Reference": "",
  "ats.State": "Review",
  "ats.Current State": "Review",
  "ats.Team Definition Reference": "",
  "ats.Description": "Description",
  TeamName: "SAW",
  Assignees: "Example User",
  ChangeType: "Improvment",
  Priority: "low",
  State: "Review",
  CreatedDate: "03/08/2021",
  CreatedBy: "Example User",
  TargetedVersion:"SAW PL ARB",
}
export const testARB = [
  {
    id: "123",
    name:"First ARB"
  },
  {
    id: "456",
    name:"second ARB"
  }
]
export const testDataUser : user = {
  id: '0',
  name: 'name',
  guid: null,
  active: true,
  description: null,
  workTypes: [],
  tags: [],
  userId: '0',
  email: '0',
  loginIds: [],
  savedSearches: [],
  userGroups: [],
  artifactId: '0',
  idString: '0',
  idIntValue: 0,
  uuid: 0
}

export const testUsers = [{testDataUser}]

export const testDataTransitionResponse : transitionResponse= {
  cancelled: false,
  workItemIds: [],
  results: [],
  transitionWorkItems: [],
  transaction: {
    branchId: '0',
    id: '0'
  },
  empty: true
}

export const testNameValuePairArray: NameValuePair[] = [{
    id: '0',
    name: 'name'
  }]

export const testDataVersion : targetedVersion[] = [{
  id: 0,
  name: 'name',
  Description: null,
  workflow: [],
  Name: 'name',
  "ats.Released": true,
  "ats.Next Version": true,
  "ats.Baseline Branch Id": '0',
  "ats.Allow Create Branch": true,
  "ats. Allow Commit Branch": true
}]
export const testNewActionData = {
    title: 'title',
    description: 'string',
    aiIds: [],
    asUserId: '0',
    createdByUserId: '0',
    versionId: '0',
  };
  export const testDataPlConfigBranchListingBranch: PlConfigBranchListingBranch =
  {
    name: 'name',
    idIntValue: 0,
    id: '0',
    viewId: '0',
    associatedArtifact: '0',
    baselineTx: '0',
    parentTx: '0',
    parentBranch: { id: '0', viewId: '0' },
    branchState: '0',
    branchType: '0',
    inheritAccessControl: false,
    archived: false,
    shortName: 'name'
  }
  
  export const testnewActionResponse: newActionResponse = {
    action: null,
    results: testDataResponse,
    teamWfs: [],
    workingBranchId: testDataPlConfigBranchListingBranch
  }