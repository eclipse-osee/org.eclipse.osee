import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { share } from 'rxjs/operators';
import { apiURL } from 'src/environments/environment';
import { PlConfigApplicUIBranchMapping } from '../types/pl-config-applicui-branch-mapping';
import { cfgGroup, PlConfigBranchListingBranch } from '../types/pl-config-branch';
import { ConfigurationGroupDefinition } from '../types/pl-config-cfggroups';
import { configuration, configurationGroup, editConfiguration } from '../types/pl-config-configurations';
import { modifyFeature, writeFeature } from '../types/pl-config-features';
import { commitResponse, response } from '../types/pl-config-responses';

@Injectable({
  providedIn: 'root'
})
export class PlConfigBranchService {

  constructor(private http: HttpClient) { }
  public getBranches(type: string): Observable<PlConfigBranchListingBranch[]> {
    return this.http.get<PlConfigBranchListingBranch[]>(apiURL+'/ats/ple/branches/'+type);
  }
  public getBranchApplicability(id: number | string | undefined): Observable<PlConfigApplicUIBranchMapping> {
    return this.http.get<PlConfigApplicUIBranchMapping>(apiURL+'/orcs/applicui/branch/' + id).pipe(share());
  }
  public getBranchState(branchId: number | string | undefined) {
    return this.http.get<PlConfigBranchListingBranch>(apiURL+'/orcs/branches/' + branchId);
  }
  public addConfiguration(branchId: string|number|undefined, body:configuration): Observable<response> {
    return this.http.post<response>(apiURL+'/orcs/branch/' + branchId + '/applic/view/', body);
  }
  public deleteConfiguration(configurationId: string , branchId?: string):Observable<response> {
    return this.http.delete<response>(apiURL+'/orcs/branch/' + branchId + '/applic/view/' + configurationId);
  }
  public editConfiguration(branchId: string | number | undefined,body:editConfiguration) {
    if (body.copyFrom === '' || body.copyFrom === null || body.copyFrom === undefined) {
      body.copyFrom = '';
    }
    if (body.configurationGroup === '' || body.configurationGroup === null || body.configurationGroup === undefined) {
      body.configurationGroup= '';
    }
    return this.http.put<response>(apiURL+'/orcs/branch/' + branchId + '/applic/view',body);
  }
  public addFeature(branchId: string | number | undefined, feature: writeFeature) {
    let body = feature;
    body.name=feature.name.toUpperCase().replace(/[^a-zA-Z0-9-_() ]/g,"");
    return this.http.post<response>(apiURL+'/orcs/branch/' + branchId + '/applic/feature',body);
  }
  public modifyFeature(branchId: string | number | undefined, feature: modifyFeature) {
    return this.http.put<response>(apiURL+'/orcs/branch/' + branchId + '/applic/feature', feature);
  }
  public deleteFeature(branchId: string | number | undefined, featureId: number | string) {
    return this.http.delete<response>(apiURL+'/orcs/branch/' + branchId + '/applic/feature/' + featureId);
  }
  public modifyConfiguration(branchId: string |number |undefined, featureId: string, body: string): Observable<response> {
    return this.http.put<response>(apiURL+"/orcs/branch/" + branchId + "/applic/view/" + featureId + "/applic", body);
  }
  public synchronizeGroup(branchId: string |number |undefined, configId: string):Observable<response> {
    return this.http.post<response>(apiURL+'/orcs/branch/'+branchId+'/applic/cfggroup/sync/'+configId,null)
  }
  public commitBranch(branchId: string | number | undefined, parentBranchId: string | number | undefined,body:{committer:string, archive:string}) {
    return this.http.post<commitResponse>(apiURL + '/orcs/branches/' + branchId + '/commit/' + parentBranchId,body);
  }
  public getCfgGroups(branchId: string | number | undefined): Observable<cfgGroup[]> {
    return this.http.get<cfgGroup[]>(apiURL + '/orcs/branch/'+branchId+'/applic/cfggroup/');
  }
  public getCfgGroupDetail(branchId: string | number | undefined, cfgGroupId: string | number | undefined) {
    return this.http.get<configurationGroup>(apiURL + '/orcs/branch/' + branchId + '/applic/cfggroup/def/' + cfgGroupId);
  }
  public addConfigurationGroup(branchId: string | number | undefined, cfgGroup:ConfigurationGroupDefinition): Observable<response> {
    return this.http.post<response>(apiURL + '/orcs/branch/' + branchId + '/applic/cfggroup/', cfgGroup);
  }
  public deleteConfigurationGroup(branchId: string | number | undefined, id: string) {
    return this.http.delete<response>(apiURL + '/orcs/branch/' + branchId + '/applic/cfggroup/' + id);
  }
  public updateConfigurationGroup(branchId: string | number | undefined, cfgGroup: ConfigurationGroupDefinition) {
    return this.http.put<response>(apiURL + '/orcs/branch/' + branchId + '/applic/cfggroup/', cfgGroup);
  }
}

