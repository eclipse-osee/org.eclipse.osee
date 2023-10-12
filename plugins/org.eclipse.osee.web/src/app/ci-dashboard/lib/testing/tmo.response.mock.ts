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
import {
	DefReference,
	ProgramReference,
	ResultReference,
	TestCaseReference,
	TestPointReference,
} from '../types';

export const programsMock: ProgramReference[] = [
	{
		name: 'Program 1',
		import: 'url',
		active: true,
		startDate: new Date(),
		endDate: new Date(),
	},
];

export const defReferenceMock: DefReference[] = [
	{
		id: '1',
		name: 'Definition Reference',
		programName: 'url',
		executionDate: new Date(),
		executionEnvironment: 'Environment 1',
		machineName: 'Machine 1',
		revision: 'revision',
		repositoryType: 'repository',
		team: 'team 1',
		lastAuthor: 'Last Author',
		lastModified: new Date(),
		modifiedFlag: '1',
		repositoryUrl: 'url',
		user: 'Jim',
		qualification: 'Level',
		property: 'Key',
		notes: 'Hello World',
		safety: false,
		scheduled: false,
		scheduledTime: new Date(),
		scheduledMachine: 'Machine 2',
		statusBy: 'Jane',
		statusDate: new Date(),
		description: 'Script that runs tests',
		scriptResults: [],
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
	},
];

export const testCaseReferenceMock: TestCaseReference[] = [
	{
		key: 'key1',
		value: 'value1',
	},
];

export const testPointReferenceMock: TestPointReference[] = [
	{
		key: 'key1',
		value: 'value1',
	},
];
