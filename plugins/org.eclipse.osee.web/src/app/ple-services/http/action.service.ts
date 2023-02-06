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
import { HttpClient, HttpHeaders } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { user } from 'src/app/types/user';
import { apiURL } from 'src/environments/environment';
import { NamedId } from '../../types/NamedId';
import { newActionResponse } from '../../types/configuration-management/new-action-response';
import { teamWorkflow } from '../../types/configuration-management/team-workflow';
import { CreateNewActionInterface } from '../../types/configuration-management/create-new-action';
import { targetedVersion } from '../../types/configuration-management/targeted-version';
import { transitionAction } from '../../types/configuration-management/transition-action';
import { action } from '../../types/configuration-management/action';
import { actionableItem } from '../../types/configuration-management/actionable-item';
import { response, transitionResponse } from '../../types/responses';

@Injectable({
	providedIn: 'root',
})
export class ActionService {
	constructor(private http: HttpClient) {}

	public get users(): Observable<user[]> {
		return this.http.get<user[]>(apiURL + '/ats/user?active=Active');
	}
	public getActionableItems(workType: string): Observable<actionableItem[]> {
		return this.http.get<actionableItem[]>(
			apiURL + `/ats/ai/worktype/${workType}`
		);
	}
	public getWorkFlow(id: string | number) {
		return this.http.get<teamWorkflow>(apiURL + '/ats/teamwf/' + id);
	}
	public getAction(artifactId: string | number): Observable<action[]> {
		return this.http.get<action[]>(apiURL + '/ats/action/' + artifactId);
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

	public getChangeTypes(arbId: string): Observable<targetedVersion[]> {
		return this.http.get<targetedVersion[]>(
			apiURL + '/ats/teamwf/' + arbId + '/changeTypes?sort=true'
		);
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
	public approveBranch(teamWf: string | number): Observable<response> {
		return this.http.post<response>(
			apiURL + '/ats/ple/action/' + teamWf + '/approval',
			null
		);
	}
	public getTeamLeads(teamDef: string | number): Observable<NamedId[]> {
		return this.http.get<NamedId[]>(
			apiURL + '/ats/config/teamdef/' + teamDef + '/leads'
		);
	}
	public getBranchApproved(teamWf: string | number): Observable<response> {
		return this.http.get<response>(
			apiURL + '/ats/ple/action/' + teamWf + '/approval'
		);
	}
}
