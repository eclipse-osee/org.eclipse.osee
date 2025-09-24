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
import { Injectable, inject } from '@angular/core';
import { CIStats, Timeline } from '../types/ci-stats';
import { apiURL } from '@osee/environments';
import { HttpParamsType, NamedId } from '@osee/shared/types';
import { ScriptTeam } from '../types';

@Injectable({
	providedIn: 'root',
})
export class DashboardHttpService {
	private http = inject(HttpClient);

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

	getTeamTimelines(branchId: string, ciSetId: `${number}`) {
		return this.http.get<Timeline[]>(
			`${apiURL}/script/dashboard/${branchId}/${ciSetId}/timeline/teams`
		);
	}

	getSubsystems(
		branchId: string,
		filter: string,
		pageNum: string | number,
		pageSize: number,
		orderByAttributeId: string
	) {
		let params: HttpParamsType = {
			pageNum: pageNum,
			pageSize: pageSize,
			orderByAttributeType: orderByAttributeId,
		};
		if (filter) {
			params = { ...params, filter };
		}
		return this.http.get<NamedId[]>(
			`${apiURL}/script/dashboard/${branchId}/subsystems`,
			{ params: params }
		);
	}

	getSubsystemsCount(branchId: string, filter: string) {
		let params: HttpParamsType = {};
		if (filter) {
			params = { ...params, filter };
		}
		return this.http.get<number>(
			`${apiURL}/script/dashboard/${branchId}/subsystems/count`,
			{ params: params }
		);
	}

	getTeams(
		branchId: string,
		filter: string,
		pageNum: string | number,
		pageSize: number,
		orderByAttributeId: string
	) {
		const params: HttpParamsType = {
			filter: filter,
			pageNum: pageNum,
			count: pageSize,
			orderByAttributeType: orderByAttributeId,
		};
		return this.http.get<ScriptTeam[]>(
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

	updateTimelines(branchId: string) {
		return this.http.post(
			`${apiURL}/script/dashboard/${branchId}/timeline/update`,
			{}
		);
	}

	exportBranchData(branchId: string) {
		return this.http.get(`${apiURL}/script/dashboard/${branchId}/export`, {
			responseType: 'blob',
		});
	}

	exportSetData(branchId: string, ciSetId: string) {
		return this.http.get(
			`${apiURL}/script/dashboard/${branchId}/${ciSetId}/export`,
			{
				responseType: 'blob',
			}
		);
	}
}
