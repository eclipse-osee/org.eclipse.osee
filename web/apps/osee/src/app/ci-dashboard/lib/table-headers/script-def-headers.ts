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
import { DefReference } from '../types';

export const scriptDefHeaderDetails: headerDetail<DefReference>[] = [
	{
		header: 'name',
		description: '',
		humanReadable: 'Script Name',
	},
	{
		header: 'team',
		description: '',
		humanReadable: 'Team Name',
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
		header: 'statusDate',
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
