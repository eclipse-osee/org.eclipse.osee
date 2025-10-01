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
import { NamedId } from '@osee/shared/types';
import { CIStats, Timeline } from '../types/ci-stats';

export const namedIdMock: NamedId[] = [
	{
		id: '123',
		name: 'Name',
	},
];

export const teamStatsMock: CIStats[] = [
	{
		name: 'Team 1',
		scriptsPass: 100,
		scriptsFail: 20,
		scriptsAbort: 10,
		scriptsDispo: 10,
		testPointsPass: 10000,
		testPointsFail: 2000,
		scriptsRan: 140,
		scriptsNotRan: 1,
		scriptsExecutionDate: new Date(),
	},
	{
		name: 'Team 2',
		scriptsPass: 200,
		scriptsFail: 10,
		scriptsAbort: 1,
		scriptsDispo: 0,
		testPointsPass: 100000,
		testPointsFail: 20000,
		scriptsRan: 211,
		scriptsNotRan: 0,
		scriptsExecutionDate: new Date(),
	},
];

export const subsystemStatsMock: CIStats[] = [
	{
		name: 'Subsystem 1',
		scriptsPass: 100,
		scriptsFail: 20,
		scriptsAbort: 10,
		scriptsDispo: 10,
		testPointsPass: 10000,
		testPointsFail: 2000,
		scriptsRan: 140,
		scriptsNotRan: 1,
		scriptsExecutionDate: new Date(),
	},
	{
		name: 'Subsystem 2',
		scriptsPass: 200,
		scriptsFail: 10,
		scriptsAbort: 1,
		scriptsDispo: 0,
		testPointsPass: 100000,
		testPointsFail: 20000,
		scriptsRan: 211,
		scriptsNotRan: 0,
		scriptsExecutionDate: new Date(),
	},
];

export const timelineStatsMock: Timeline[] = [
	{
		id: '1',
		setId: '11',
		updatedAt: new Date(Date.parse('02/04/2025')),
		team: 'All',
		days: [
			{
				executionDate: new Date(Date.parse('01/01/2025')),
				scriptsPass: 20,
				scriptsFail: 1,
				pointsPass: 100,
				pointsFail: 5,
				abort: 0,
			},
			{
				executionDate: new Date(Date.parse('01/02/2025')),
				scriptsPass: 10,
				scriptsFail: 2,
				pointsPass: 55,
				pointsFail: 15,
				abort: 1,
			},
		],
	},
	{
		id: '2',
		setId: '22',
		updatedAt: new Date(Date.parse('02/05/2025')),
		team: 'Team 1',
		days: [
			{
				executionDate: new Date(Date.parse('01/01/2025')),
				scriptsPass: 12,
				scriptsFail: 1,
				pointsPass: 54,
				pointsFail: 2,
				abort: 0,
			},
		],
	},
];
