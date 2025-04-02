/*********************************************************************
 * Copyright (c) 2024 Boeing
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
import { headerDetail } from '@osee/shared/types';
import { DefReference, ResultReference } from '../types';

export const scriptDefHeaderDetails: headerDetail<DefReference>[] = [
	{
		header: 'name',
		description: '',
		humanReadable: 'Script Name',
	},
	{
		header: 'team',
		description: '',
		humanReadable: 'Team',
	},
	{
		header: 'subsystem',
		description: '',
		humanReadable: 'Subsystem',
	},
	{
		header: 'safety',
		description: '',
		humanReadable: 'Safety',
	},
	{
		header: 'notes',
		description: '',
		humanReadable: 'Notes',
	},
	{
		header: 'statusBy',
		description: '',
		humanReadable: 'Status By',
	},
	{
		header: 'latestExecutionDate',
		description: '',
		humanReadable: 'Status Date',
	},
	{
		header: 'latestResult',
		description: '',
		humanReadable: 'Result',
	},
	{
		header: 'latestScriptHealth',
		description: '',
		humanReadable: 'Script Health',
	},

	{
		header: 'latestPassedCount',
		description: '',
		humanReadable: 'Passed Count',
	},
	{
		header: 'latestFailedCount',
		description: '',
		humanReadable: 'Failed Count',
	},
	{
		header: 'latestScriptAborted',
		description: '',
		humanReadable: 'Script Aborted',
	},
	{
		header: 'machineName',
		description: '',
		humanReadable: 'Machine',
	},
	{
		header: 'latestMachineName',
		description: '',
		humanReadable: 'Runtime Machine Name',
	},
	{
		header: 'latestElapsedTime',
		description: '',
		humanReadable: 'Elapsed Time',
	},
	{
		header: 'scheduledMachine',
		description: '',
		humanReadable: 'Scheduled Machine',
	},
	{
		header: 'scheduledTime',
		description: '',
		humanReadable: 'Scheduled Time',
	},
	{
		header: 'scheduled',
		description: '',
		humanReadable: 'Scheduled',
	},
	{
		header: 'fullScriptName',
		description: '',
		humanReadable: 'Full ScriptName',
	},
];

export const scriptDefListHeaderDetails: headerDetail<DefReference>[] = [
	{
		header: 'name',
		description: '',
		humanReadable: 'Script Name',
	},
	{
		header: 'fullScriptName',
		description: '',
		humanReadable: 'Full ScriptName',
	},
];

export const scriptResHeaderDetails: headerDetail<ResultReference>[] = [
	{
		header: 'executionDate',
		description: '',
		humanReadable: 'Date',
	},
	{
		header: 'name',
		description: '',
		humanReadable: 'Script Name',
	},
	{
		header: 'processorId',
		description: '',
		humanReadable: 'Processor Id',
	},
	{
		header: 'runtimeVersion',
		description: '',
		humanReadable: 'Runtime Version',
	},
	{
		header: 'executionDate',
		description: '',
		humanReadable: 'Execution Date',
	},
	{
		header: 'executionEnvironment',
		description: '',
		humanReadable: 'Execution Environment',
	},
	{
		header: 'machineName',
		description: 'Machine the script ran on',
		humanReadable: 'Machine',
	},
	{
		header: 'javaVersion',
		description: '',
		humanReadable: 'Java Version',
	},

	{
		header: 'scriptAborted',
		description: 'Script aborted',
		humanReadable: 'Aborted',
	},
	{
		header: 'elapsedTime',
		description: '',
		humanReadable: 'Elapsed Time',
	},
	{
		header: 'osArchitecture',
		description: '',
		humanReadable: 'OS Architecture',
	},
	{
		header: 'osName',
		description: '',
		humanReadable: 'OS Name',
	},
	{
		header: 'osVersion',
		description: '',
		humanReadable: 'OS Version',
	},
	{
		header: 'oseeServerJar',
		description: '',
		humanReadable: 'OSEE ServerJar',
	},
	{
		header: 'oseeServer',
		description: '',
		humanReadable: 'OSEE Server',
	},
	{
		header: 'oseeVersion',
		description: '',
		humanReadable: 'OSEE Version',
	},
	{
		header: 'executedBy',
		description: '',
		humanReadable: 'Executed By',
	},
];

export const scriptResListHeaderDetails: headerDetail<ResultReference>[] = [
	{
		header: 'executionDate',
		description: '',
		humanReadable: 'Date',
	},
	{
		header: 'failedCount',
		description: '',
		humanReadable: 'Status',
	},
];
