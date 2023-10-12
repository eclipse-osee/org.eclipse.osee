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
export interface ProgramReference {
	name: string;
	import: string;
	active: boolean;
	startDate: Date;
	endDate: Date;
}

export interface DefReference {
	id?: string;
	name: string;
	programName: string;
	executionDate: Date;
	executionEnvironment: string;
	machineName: string;
	revision: string;
	repositoryType: string;
	team: string;
	lastAuthor: string;
	lastModified: Date;
	modifiedFlag: string;
	repositoryUrl: string;
	user: string;
	qualification: string;
	property: string;
	notes: string;
	safety: boolean;
	scheduled: boolean;
	scheduledTime: Date;
	scheduledMachine: string;
	statusBy: string;
	statusDate: Date;
	description: string;
	scriptResults: ResultReference[];
}

export interface ResultReference {
	id?: string;
	name: string;
	processorId: string;
	runtimeVersion: string;
	executionDate: Date;
	executionEnvironment: string;
	machineName: string;
	passedCount: number;
	failedCount: number;
	interactiveCount: number;
	javaVersion: string;
	scriptAborted: boolean;
	elapsedTime: number;
	startDate: Date;
	endDate: Date;
	osArchitecture: string;
	osName: string;
	osVersion: string;
	oseeServerJar: string;
	oseeServer: string;
	oseeVersion: string;
	result: string;
	scriptHealth: number;
	qualificationLevel: string;
	executedBy: string;
	userId: string;
	userName: string;
	email: string;
	witnesses: string[];
}

export interface TestCaseReference {
	key: string;
	value: string;
}

export interface TestPointReference {
	key: string;
	value: string;
}
