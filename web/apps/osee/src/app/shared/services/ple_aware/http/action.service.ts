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
import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { user } from '@osee/shared/types/auth';
import { apiURL } from '@osee/environments';
import {
	HttpParamsType,
	NamedId,
	response,
	transitionResponse,
} from '@osee/shared/types';
import {
	actionableItem,
	teamWorkflow,
	action,
	transitionAction,
	targetedVersion,
	CreateNewActionInterface,
	newActionResponse,
	WorkType,
	CreateActionField,
	atsLastMod,
} from '@osee/shared/types/configuration-management';
import { ARTIFACTTYPEID } from '@osee/shared/types/constants';

@Injectable({
	providedIn: 'root',
})
export class ActionService {
	constructor(private http: HttpClient) {}

	public get users(): Observable<user[]> {
		return this.http.get<user[]>(apiURL + '/ats/user?active=Active');
	}
	public getActionableItems(workType?: string): Observable<actionableItem[]> {
		let params: HttpParamsType = { orderByName: true };
		if (workType) {
			params = { ...params, workType: workType };
		}
		return this.http.get<actionableItem[]>(apiURL + `/ats/ai/all`, {
			params: params,
		});
	}
	public getWorkTypes() {
		return this.http.get<WorkType[]>(apiURL + `/ats/workType`);
	}
	public getCreateActionFields(actionableItemId: string) {
		return this.http.get<CreateActionField[]>(
			apiURL + `/ats/ai/${actionableItemId}/additionalFields`
		);
	}
	public getWorkFlow(id: string | number) {
		return this.http.get<teamWorkflow>(apiURL + '/ats/teamwf/' + id);
	}
	public getAction(artifactId: string | number): Observable<action[]> {
		return this.http.get<action[]>(apiURL + '/ats/action/' + artifactId);
	}

	public getLastModified(
		artType: ARTIFACTTYPEID,
		pageSize?: string | number,
		pageNumber?: string | number,
		nameFilter?: string
	) {
		let params: HttpParamsType = {
			artType: artType,
		};
		if (pageSize && pageSize !== '') {
			params = { ...params, pageSize: pageSize };
		}
		if (pageNumber && pageNumber !== '') {
			params = { ...params, pageNumber: pageNumber };
		}
		if (nameFilter && nameFilter !== '') {
			params = { ...params, nameFilter: nameFilter };
		}
		return this.http.get<atsLastMod[]>(
			apiURL + '/ats/action/query/workitems',
			{
				params: params,
			}
		);
	}

	public getLastModifiedAtsAction(
		pageSize?: string | number,
		pageNumber?: string | number,
		nameFilter?: string
	) {
		return this.getLastModified('67', pageSize, pageNumber, nameFilter);
	}

	public getLastModifiedCount(artType: ARTIFACTTYPEID, nameFilter?: string) {
		let params: HttpParamsType = {
			artType: artType,
		};
		if (nameFilter && nameFilter !== '') {
			params = { ...params, nameFilter: nameFilter };
		}
		return this.http.get<number>(
			apiURL + '/ats/action/query/workitems/count',
			{
				params: params,
			}
		);
	}
	public getLastModifiedCountAtsAction(nameFilter?: string) {
		return this.getLastModifiedCount('67', nameFilter);
	}
	public validateTransitionAction(body: transitionAction) {
		return this.http.post<transitionResponse>(
			apiURL + '/ats/action/transitionValidate',
			body
		);
	}
	public transitionAction(body: transitionAction) {
		return this.http.post<transitionResponse>(
			apiURL + '/ats/action/transition',
			body
		);
	}
	public getVersions(arbId: string): Observable<targetedVersion[]> {
		return this.http.get<targetedVersion[]>(
			apiURL + '/ats/teamwf/' + arbId + '/version?sort=true'
		);
	}
	public getAllParallelVersions(versionId: string) {
		return this.http.get(apiURL + `ats/config/parallel/${versionId}/all`);
	}
	public getTeamDef(actionableItemId: string) {
		return this.http.get<NamedId[]>(
			apiURL + `/ats/ai/${actionableItemId}/teamdef`
		);
	}
	public getSprints(teamDefId: string) {
		return this.http.get<NamedId[]>(
			apiURL + `/ats/agile/team/${teamDefId}/sprint?active=true`
		);
	}
	public getChangeTypes(arbId: string): Observable<targetedVersion[]> {
		return this.http.get<targetedVersion[]>(
			apiURL + '/ats/teamwf/' + arbId + '/changeTypes?sort=true'
		);
	}
	public getPoints() {
		return this.http.get<string[]>(apiURL + '/ats/action/points');
	}
	public getFeatureGroups(teamDefId: string) {
		return this.http.get<NamedId[]>(
			apiURL + `/ats/agile/team/${teamDefId}/feature`
		);
	}
	public createAction(body: CreateNewActionInterface) {
		return this.http.post<newActionResponse>(apiURL + '/ats/action', body);
	}
	public createBranch(
		body: CreateNewActionInterface
	): Observable<newActionResponse> {
		return this.http.post<newActionResponse>(
			apiURL + '/ats/action/branch',
			body
		);
	}
	public commitBranch(
		teamWf: string,
		branchId: string | number
	): Observable<response> {
		return this.http.put<response>(
			apiURL +
				'/ats/action/branch/commit?teamWfId=' +
				teamWf +
				'&branchId=' +
				branchId,
			null
		);
	}
	public approveBranch(teamWf: string | number): Observable<boolean> {
		return this.http.post<boolean>(
			apiURL + '/ats/action/' + teamWf + '/approval',
			null
		);
	}
	public getTeamLeads(teamDef: string | number): Observable<NamedId[]> {
		return this.http.get<NamedId[]>(
			apiURL + '/ats/config/teamdef/' + teamDef + '/leads'
		);
	}
	public getBranchApproved(teamWf: string | number): Observable<boolean> {
		return this.http.get<boolean>(
			apiURL + '/ats/action/' + teamWf + '/approval'
		);
	}
}
