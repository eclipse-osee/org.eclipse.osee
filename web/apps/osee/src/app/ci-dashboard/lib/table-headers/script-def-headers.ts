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
import { headerDetail } from '@osee/shared/types';
import { DefReference, ResultReference } from '../types';

export const scriptDefHeaderDetails: headerDetail<DefReference>[] = [
	{
		header: 'name',
		description: 'Name of test script',
		humanReadable: 'Script Name',
	},
	{
		header: 'team',
		description: 'Team assigned to script',
		humanReadable: 'Team Name',
	},
	{
		header: 'subsystem',
		description: 'Subsystem associated with script',
		humanReadable: 'Subsystem',
	},
	{
		header: 'safety',
		description: 'Is script marked safety',
		humanReadable: 'Safety',
	},
	{
		header: 'notes',
		description: 'Customer Notes',
		humanReadable: 'Notes',
	},
	{
		header: 'statusBy',
		description: 'User statusing script',
		humanReadable: 'Status By',
	},
	{
		header: 'latestExecutionDate',
		description: 'Date script was statused',
		humanReadable: 'Status Date',
	},
	{
		header: 'latestResult',
		description: 'Pass/Fail status from the latest run',
		humanReadable: 'Result',
	},
	{
		header: 'latestScriptHealth',
		description: 'Script health based on latest run',
		humanReadable: 'Script Health',
	},

	{
		header: 'latestPassedCount',
		description: 'Number of passing test points',
		humanReadable: 'Passed Count',
	},
	{
		header: 'latestFailedCount',
		description: 'Number of failed test points',
		humanReadable: 'Failed Count',
	},
	{
		header: 'latestScriptAborted',
		description: 'Was the latest run aborted',
		humanReadable: 'Script Aborted',
	},
	{
		header: 'machineName',
		description: 'Machine the script war run on',
		humanReadable: 'Machine',
	},
	{
		header: 'latestMachineName',
		description: 'Latest machine the script war run on',
		humanReadable: 'Runtime Machine Name',
	},
	{
		header: 'latestElapsedTime',
		description: 'Scripts runtime',
		humanReadable: 'Elapsed Time',
	},
	{
		header: 'scheduledMachine',
		description: 'Machine scheduled to run script',
		humanReadable: 'Scheduled Machine',
	},
	{
		header: 'scheduledTime',
		description: 'Time scheduled to run script',
		humanReadable: 'Scheduled Time',
	},
	{
		header: 'scheduled',
		description: 'Is script scheduled to be run',
		humanReadable: 'Scheduled',
	},
	{
		header: 'fullScriptName',
		description: 'Full qualified name of test script',
		humanReadable: 'Full Script Name',
	},
];

export const scriptDefListHeaderDetails: headerDetail<DefReference>[] = [
	{
		header: 'name',
		description: 'Name of test script',
		humanReadable: 'Script Name',
	},
	{
		header: 'fullScriptName',
		description: 'Full qualified name of test script',
		humanReadable: 'Full Script Name',
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
		description: 'Name of test script',
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
		description: 'Date script was executed',
		humanReadable: 'Execution Date',
	},
	{
		header: 'executionEnvironment',
		description: 'Environment script was executed in',
		humanReadable: 'Execution Environment',
	},
	{
		header: 'machineName',
		description: 'Machine the script executed on',
		humanReadable: 'Machine',
	},
	{
		header: 'javaVersion',
		description: 'Java Version',
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
		description: 'User who executed test script',
		humanReadable: 'Executed By',
	},
];

export const scriptResListHeaderDetails: headerDetail<ResultReference>[] = [
	{
		header: 'executionDate',
		description: 'Date test script was executed',
		humanReadable: 'Date',
	},
];
