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
export interface SetReference {
	id: string;
	name: string;
	active: boolean;
}

export const setReferenceSentinel: SetReference = {
	id: '',
	name: '',
	active: false,
};

export interface DefReference {
	id: `${number}`;
	name: string;
	fullScriptName: String;
	executionDate: Date;
	executionEnvironment: string;
	machineName: string;
	revision: string;
	repositoryType: string;
	team: string;
	lastAuthor: string;
	lastModified: Date;
	modifiedFlag: string;
	user: string;
	notes: string;
	safety: boolean;
	scheduled: boolean;
	scheduledTime: Date;
	scheduledMachine: string;
	statusBy: string;
	statusDate: Date;
	subsystem: string;
	description: string;
	latestProcessorId: string;
	latestExecutionDate: Date;
	latestExecutionEnvironment: string;
	latestMachineName: string;
	latestPassedCount: number;
	latestFailedCount: number;
	latestInteractiveCount: number;
	latestScriptAborted: boolean;
	latestElapsedTime: number;
	latestResult: string;
	latestScriptHealth: number;
	latestExecutedBy: string;
	latestUserName: string;
}

export interface ResultReference {
	id: string;
	name: string;
	processorId: string;
	runtimeVersion: string;
	executionDate: Date;
	executionEnvironment: string;
	machineName: string;
	passedCount: number;
	failedCount: number;
	totalTestPoints: number;
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
	testPoints: TestPointReference[];
}

export const defReferenceSentinel: DefReference = {
	id: '-1',
	name: '',
	fullScriptName: '',
	executionDate: new Date(),
	executionEnvironment: '',
	machineName: '',
	revision: '',
	repositoryType: '',
	team: '',
	lastAuthor: '',
	lastModified: new Date(),
	modifiedFlag: '',
	user: '',
	notes: '',
	safety: false,
	scheduled: false,
	scheduledTime: new Date(),
	scheduledMachine: '',
	statusBy: '',
	statusDate: new Date(),
	subsystem: '',
	description: '',
	latestProcessorId: '',
	latestExecutionDate: new Date(),
	latestExecutionEnvironment: '',
	latestMachineName: '',
	latestPassedCount: 0,
	latestFailedCount: 0,
	latestInteractiveCount: 0,
	latestScriptAborted: false,
	latestElapsedTime: 0,
	latestResult: '',
	latestScriptHealth: 0,
	latestExecutedBy: '',
	latestUserName: '',
};

export const resultReferenceSentinel: ResultReference = {
	id: '-1',
	name: '',
	processorId: '',
	runtimeVersion: '',
	executionDate: new Date(),
	executionEnvironment: '',
	machineName: '',
	passedCount: 0,
	totalTestPoints: 0,
	failedCount: 0,
	interactiveCount: 0,
	scriptAborted: false,
	elapsedTime: 0,
	startDate: new Date(),
	endDate: new Date(),
	osArchitecture: '',
	osName: '',
	osVersion: '',
	oseeServerJar: '',
	oseeServer: '',
	oseeVersion: '',
	result: '',
	scriptHealth: 0,
	witnesses: [''],
	email: '',
	userId: '',
	userName: '',
	executedBy: '',
	javaVersion: '',
	qualificationLevel: '',
	testPoints: [],
};

export interface TestPointReference {
	id: string;
	name: string;
	testNumber: number;
	result: string;
	overallResult: string;
	resultType: string;
	interactive: boolean;
	groupName: string;
	groupType: string;
	groupOperator: string;
	expected: string;
	actual: string;
	requirement: string;
	elapsedTime: number;
	transmissionCount: number;
	notes: string;
}

export interface ScriptBatch {
	id: string;
	name: string;
	batchId: string;
	executionDate: Date;
	machineName: string;
	testEnvBatchId: string;
}

export const scriptBatchSentinel: ScriptBatch = {
	id: '-1',
	name: '',
	batchId: '',
	executionDate: new Date(),
	machineName: '',
	testEnvBatchId: '',
};

export type SetDiff = {
	name: string;
	equal: boolean;
	results: {
		[key: string]: {
			passes: number;
			fails: number;
			abort: boolean;
		};
	};
};
