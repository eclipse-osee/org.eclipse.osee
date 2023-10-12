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
import { TeamStats } from '../types/team-stats';

export const teamStatsMock: TeamStats[] = [
	{
		teamName: 'Team 1',
		scriptsPass: 100,
		scriptsFail: 20,
		scriptsAbort: 10,
		scriptsDispo: 10,
		testPointsPass: 10000,
		testPointsFail: 2000,
		scriptsRan: 140,
		scriptsNotRan: 1,
	},
	{
		teamName: 'Team 2',
		scriptsPass: 200,
		scriptsFail: 10,
		scriptsAbort: 1,
		scriptsDispo: 0,
		testPointsPass: 100000,
		testPointsFail: 20000,
		scriptsRan: 211,
		scriptsNotRan: 0,
	},
];
