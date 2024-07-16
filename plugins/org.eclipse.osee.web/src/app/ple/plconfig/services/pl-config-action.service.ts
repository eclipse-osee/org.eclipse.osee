import { HttpClient, HttpHeaders } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { map } from 'rxjs/operators';
import { apiURL } from 'src/environments/environment';
import { action, actionableItem, newActionInterface, newActionResponse, targetedVersion, teamWorkflow, teamWorkflowImpl, transitionAction } from '../types/pl-config-actions';
import { response, transitionResponse } from '../types/pl-config-responses';
import { user } from '../types/pl-config-users';

@Injectable({
  providedIn: 'root'
})
export class PlConfigActionService {

  constructor(private http: HttpClient) { }
  public get users():Observable<user[]> {
    return this.http.get<user[]>(apiURL+'/ats/user?active=Active')
  }
  public get ARB(): Observable<actionableItem[]> {
    return this.http.get<actionableItem[]>(apiURL+'/ats/ai/worktype/ARB')
  }
  public getWorkFlow(id: string | number) {
    return this.http.get<teamWorkflow>(apiURL + '/ats/teamwf/' + id);
  }
  public getAction(artifactId: string | number): Observable<action[]> {
    return this.http.get<action[]>(apiURL+'/ats/action/' + artifactId);
  }
  public validateTransitionAction(body:transitionAction) {
    return this.http.post<transitionResponse>(apiURL+'/ats/action/transitionValidate', body);
  }
  public transitionAction(body: transitionAction) {
    return this.http.post<transitionResponse>(apiURL+'/ats/action/transition', body);
  }
  public getVersions(arbId: string): Observable<targetedVersion[]> {
    return this.http.get<targetedVersion[]>(apiURL+'/ats/teamwf/' + arbId + '/version?sort=true');
  }
  public createBranch(body: newActionInterface): Observable<newActionResponse> {
    return this.http.post<newActionResponse>(apiURL+'/ats/action/branch', body);
  }
  public commitBranch(teamWf: string, branchId: string | number): Observable<response> {
    return this.http.put<response>(apiURL+'/ats/action/branch/commit?teamWfId='+teamWf+'&branchId='+branchId,null);
  }
  public getBranchApproved(teamWf: string | number): Observable<response> {
    return this.http.get<response>(apiURL+'/ats/ple/action/'+teamWf+'/approval');
  }
}
