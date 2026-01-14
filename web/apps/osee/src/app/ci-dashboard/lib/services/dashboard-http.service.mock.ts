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
import { of } from 'rxjs';
import { DashboardHttpService } from '../services/dashboard-http.service';
import {
	teamStatsMock,
	subsystemStatsMock,
	timelineStatsMock,
} from '../testing/dashboard.response.mock';

export const dashboardHttpServiceMock: Partial<DashboardHttpService> = {
	getTeamStats(branchId: string, ciSet: string) {
		return of(teamStatsMock);
	},

	getSubsystemStats(branchId: string, ciSet: string) {
		return of(subsystemStatsMock);
	},

	getTeamTimelines(branchId: string, ciSetId: `${number}`) {
		return of(timelineStatsMock);
	},

	getTimelineCompare(branchId: string) {
		return of(timelineStatsMock);
	},

	getTeamsCount(branchId: string, filter: string) {
		return of(1);
	},
};
