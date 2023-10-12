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
import { Injectable } from '@angular/core';
import { combineLatest, filter, switchMap } from 'rxjs';
import { DashboardHttpService } from '../services/dashboard-http.service';
import { CiDashboardUiService } from 'src/app/ci-dashboard/lib/services/ci-dashboard-ui.service';

@Injectable({
	providedIn: 'root',
})
export class DashboardService {
	constructor(
		private uiService: CiDashboardUiService,
		private dashboardHttpService: DashboardHttpService
	) {}

	private _teamStats = combineLatest([
		this.uiService.branchId,
		this.uiService.ciSetId,
	]).pipe(
		filter(([branchId, ciSetId]) => branchId !== '' && ciSetId !== ''),
		switchMap(([branchId, ciSetId]) =>
			this.dashboardHttpService.getTeamStats(branchId, ciSetId)
		)
	);

	get teamStats() {
		return this._teamStats;
	}
}
