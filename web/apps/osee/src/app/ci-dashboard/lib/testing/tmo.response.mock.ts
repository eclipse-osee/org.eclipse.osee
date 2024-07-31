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
import { TmoImportResult } from '../types/tmo-import';
import {
	DefReference,
	SetReference,
	ResultReference,
	TestPointReference,
	ScriptBatch,
} from '../types';
import { transactionResultMock } from '@osee/shared/transactions/testing';

export const setsMock: SetReference[] = [
	{
		id: '5555',
		name: 'Set 1',
		active: true,
	},
];

export const defReferenceMock: DefReference[] = [
	{
		id: '1',
		name: 'ScriptName',
		fullScriptName: 'full.ScriptName',
		executionDate: new Date(),
		executionEnvironment: 'Environment 1',
		machineName: 'Machine 1',
		revision: 'revision',
		repositoryType: 'repository',
		team: 'team 1',
		lastAuthor: 'Last Author',
		lastModified: new Date(),
		modifiedFlag: '1',
		user: 'Jim',
		notes: 'Hello World',
		safety: false,
		scheduled: false,
		scheduledTime: new Date(),
		scheduledMachine: 'Machine 2',
		statusBy: 'Jane',
		statusDate: new Date(),
		subsystem: 'SBSSTM1',
		description: 'Script that runs tests',
		latestProcessorId: 'string',
		latestExecutionDate: new Date(),
		latestExecutionEnvironment: 'string',
		latestMachineName: 'string',
		latestPassedCount: 3,
		latestFailedCount: 5,
		latestInteractiveCount: 1,
		latestScriptAborted: false,
		latestElapsedTime: 14652346,
		latestResult: 'string',
		latestScriptHealth: 2,
		latestExecutedBy: 'string',
		latestUserName: 'string',
	},
];

export const resultReferenceMock: ResultReference[] = [
	{
		id: '1',
		name: 'Result Reference',
		processorId: 'processor 1',
		runtimeVersion: 'runtimeVer1',
		executionDate: new Date(),
		executionEnvironment: 'Environment 1',
		machineName: 'Machine 1',
		passedCount: 32,
		failedCount: 12,
		totalTestPoints: 44,
		interactiveCount: 3,
		scriptAborted: false,
		elapsedTime: 4353242,
		startDate: new Date(),
		endDate: new Date(),
		osArchitecture: 'OsArch',
		osName: 'OsName',
		osVersion: 'OsVer',
		oseeServerJar: 'OseeJar',
		oseeServer: 'OseeServ',
		oseeVersion: 'OseeVer',
		result: 'PASS',
		scriptHealth: 5,
		witnesses: ['Tina'],
		email: 'test@email.com',
		userId: '1234',
		userName: 'Bob',
		executedBy: 'Bob',
		javaVersion: '21',
		qualificationLevel: 'DEV',
		testPoints: [],
		definitionId: '2',
	},
];

export const testPointReferenceMock: TestPointReference[] = [
	{
		id: '645',
		name: 'Bob',
		testNumber: 2,
		result: '2',
		overallResult: '5',
		resultType: 'Code',
		interactive: false,
		groupName: 'GroupOne',
		groupType: 'GroupType',
		groupOperator: 'OR',
		expected: 'PASS',
		actual: 'FAIL',
		requirement: 'REQ123',
		elapsedTime: 452435,
		transmissionCount: 3,
		notes: 'notes',
	},
];

export const scriptBatchResultMock: ScriptBatch[] = [
	{
		id: '1',
		name: 'Batch 1',
		batchId: '123',
		executionDate: new Date(1234),
		machineName: 'Machine 1',
		testEnvBatchId: '827345',
	},
];

export const tmoImportResultMock: TmoImportResult = {
	txResult: transactionResultMock,
	workflows: [],
};
