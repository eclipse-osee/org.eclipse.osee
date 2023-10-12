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
export type TeamStats = {
	teamName: string;
	scriptsPass: number;
	scriptsFail: number;
	scriptsAbort: number;
	scriptsDispo: number;
	testPointsPass: number;
	testPointsFail: number;
	scriptsRan: number;
	scriptsNotRan: number;
};

export const teamStatsSentinel: TeamStats = {
	teamName: '',
	scriptsPass: 0,
	scriptsFail: 0,
	scriptsAbort: 0,
	scriptsDispo: 0,
	testPointsPass: 0,
	testPointsFail: 0,
	scriptsRan: 0,
	scriptsNotRan: 0,
};
