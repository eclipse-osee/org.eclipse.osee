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
import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { CIStats, CITimelineStats } from '../types/ci-stats';
import { apiURL } from '@osee/environments';
import { HttpParamsType, NamedId } from '@osee/shared/types';

@Injectable({
	providedIn: 'root',
})
export class DashboardHttpService {
	constructor(private http: HttpClient) {}

	getTeamStats(branchId: string, ciSet: string) {
		return this.http.get<CIStats[]>(
			`${apiURL}/script/dashboard/${branchId}/${ciSet}/teamstats`
		);
	}

	getSubsystemStats(branchId: string, ciSet: string) {
		return this.http.get<CIStats[]>(
			`${apiURL}/script/dashboard/${branchId}/${ciSet}/subsystemstats`
		);
	}

	getTimelineStats(branchId: string, ciSet: string) {
		return this.http.get<CITimelineStats[]>(
			`${apiURL}/script/dashboard/${branchId}/${ciSet}/timelinestats`
		);
	}

	getSubsystems(
		branchId: string,
		filter: string,
		pageNum: number,
		pageSize: number,
		orderByAttributeId: string
	) {
		let params: HttpParamsType = {
			filter: filter,
			pageNum: pageNum,
			pageSize: pageSize,
			orderByAttributeType: orderByAttributeId,
		};
		return this.http.get<NamedId[]>(
			`${apiURL}/script/dashboard/${branchId}/subsystems`,
			{ params: params }
		);
	}

	getSubsystemsCount(branchId: string, filter: string) {
		return this.http.get<number>(
			`${apiURL}/script/dashboard/${branchId}/subsystems/count`,
			{ params: { filter: filter } }
		);
	}

	getTeams(
		branchId: string,
		filter: string,
		pageNum: number,
		pageSize: number,
		orderByAttributeId: string
	) {
		let params: HttpParamsType = {
			filter: filter,
			pageNum: pageNum,
			pageSize: pageSize,
			orderByAttributeType: orderByAttributeId,
		};
		return this.http.get<NamedId[]>(
			`${apiURL}/script/dashboard/${branchId}/teams`,
			{ params: params }
		);
	}

	getTeamsCount(branchId: string, filter: string) {
		return this.http.get<number>(
			`${apiURL}/script/dashboard/${branchId}/teams/count`,
			{ params: { filter: filter } }
		);
	}
}
