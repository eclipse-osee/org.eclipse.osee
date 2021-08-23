import { of } from "rxjs";
import { user } from "src/app/userdata/types/user-data-user";
import { PlConfigActionService } from "../services/pl-config-action.service";
import { newActionInterface, transitionAction } from "../types/pl-config-actions";
import { testARB, testBranchActions, testDataResponse, testDataTransitionResponse, testDataVersion, testNameValuePairArray, testnewActionResponse, testWorkFlow } from "./mockTypes";

export const plActionServiceMock: Partial<PlConfigActionService> = {
    ARB: of(testARB),
    createBranch(body: newActionInterface, thisUser: user)
    { return of(testnewActionResponse)},
    commitBranch(teamWf: string, branchId: string | number)
    { return of(testDataResponse)},
    getWorkFlow(id: string | number) {
       return of(testWorkFlow)},
    getAction(artifactId: string | number){
       return of(testBranchActions)},
    validateTransitionAction(body:transitionAction) {
      return of(testDataTransitionResponse)},
    transitionAction(body: transitionAction) {
      return of(testDataTransitionResponse)},
    getVersions(arbId: string){
      return of(testDataVersion)},
    approveBranch(teamWf: string | number, thisUser: user) {
       return of(testDataResponse)},
    getTeamLeads(teamDef : string | number){
      return of(testNameValuePairArray)},
    getBranchApproved(teamWf: string | number) {
      return of(testDataResponse)},
  
   
  }