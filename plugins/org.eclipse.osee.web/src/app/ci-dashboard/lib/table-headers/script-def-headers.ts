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
		header: 'programName',
		description: '',
		humanReadable: 'Program Name',
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
		description: '',
		humanReadable: 'Machine',
	},
	{
		header: 'revision',
		description: '',
		humanReadable: 'Revision',
	},
	{
		header: 'repositoryType',
		description: '',
		humanReadable: 'Repository Type',
	},
	{
		header: 'team',
		description: '',
		humanReadable: 'Team Name',
	},
	{
		header: 'lastAuthor',
		description: '',
		humanReadable: 'Last Author',
	},
	{
		header: 'lastModified',
		description: '',
		humanReadable: 'Last Modified',
	},
	{
		header: 'modifiedFlag',
		description: '',
		humanReadable: 'Modified',
	},
	{
		header: 'repositoryUrl',
		description: '',
		humanReadable: 'Repository Url',
	},
	{
		header: 'user',
		description: '',
		humanReadable: 'User',
	},
	{
		header: 'qualification',
		description: '',
		humanReadable: 'Qualification Level',
	},
	{
		header: 'property',
		description: '',
		humanReadable: 'Property',
	},
	{
		header: 'notes',
		description: '',
		humanReadable: 'Notes',
	},
	{
		header: 'safety',
		description: '',
		humanReadable: 'Safety',
	},
	{
		header: 'scheduled',
		description: '',
		humanReadable: 'Scheduled',
	},
	{
		header: 'scheduledTime',
		description: '',
		humanReadable: 'Scheduled Time',
	},
	{
		header: 'scheduledMachine',
		description: '',
		humanReadable: 'Scheduled Machine',
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
];
