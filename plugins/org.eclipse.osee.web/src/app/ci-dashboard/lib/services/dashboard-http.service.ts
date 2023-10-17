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
import { CIStats } from '../types/ci-stats';
import { apiURL } from '@osee/environments';

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
}
