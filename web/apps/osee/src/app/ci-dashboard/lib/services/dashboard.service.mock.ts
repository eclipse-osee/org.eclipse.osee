/*********************************************************************
 * Copyright (c) 2025 Boeing
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

import { DashboardService } from './dashboard.service';
import {
	namedIdMock,
	teamStatsMock,
	subsystemStatsMock,
	timelineStatsMock,
} from '../testing/dashboard.response.mock';

export const dashboardServiceMock: Partial<DashboardService> = {
	getSubsystemsPaginated(
		filterText: string,
		pageNum: string | number,
		pageSize: number,
		orderByAttributeId: string
	) {
		return of(namedIdMock);
	},

	getSubsystemsCount(filterText: string) {
		return of(1);
	},

	getTeamsPaginated(
		filterText: string,
		pageNum: string | number,
		pageSize: number,
		orderByAttributeId: string
	) {
		return of();
	},

	getTeamsCount(filterText: string) {
		return of(4);
	},

	get teamStats() {
		return of(teamStatsMock);
	},

	get subsystemStats() {
		return of(subsystemStatsMock);
	},

	get timelines() {
		return of(timelineStatsMock);
	},
};
