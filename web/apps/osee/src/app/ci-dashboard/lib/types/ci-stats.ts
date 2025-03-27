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
export type CIStats = {
	name: string;
	scriptsPass: number;
	scriptsFail: number;
	scriptsAbort: number;
	scriptsDispo: number;
	testPointsPass: number;
	testPointsFail: number;
	scriptsRan: number;
	scriptsNotRan: number;
	scriptsExecutionDate: Date;
};

export const teamStatsSentinel: CIStats = {
	name: '',
	scriptsPass: 0,
	scriptsFail: 0,
	scriptsAbort: 0,
	scriptsDispo: 0,
	testPointsPass: 0,
	testPointsFail: 0,
	scriptsRan: 0,
	scriptsNotRan: 0,
	scriptsExecutionDate: new Date(),
};

export type Timeline = {
	id: `${number}`;
	updatedAt: Date;
	setId: `${number}`;
	team: string;
	days: TimelineDay[];
};

type TimelineDay = {
	executionDate: Date;
	scriptsPass: number;
	scriptsFail: number;
	pointsPass: number;
	pointsFail: number;
	abort: number;
};
