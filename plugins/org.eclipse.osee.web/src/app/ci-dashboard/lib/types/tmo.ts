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
	id?: string;
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
	subsystem: String;
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
	id?: string;
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
	testPoints: TestPoint[];
}

export interface TestCaseReference {
	key: string;
	value: string;
}

export interface TestPoint {
	key: string;
	value: string;
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
