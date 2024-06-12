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
import { TestPointReference } from '../types';

export const testPointDetails: headerDetail<TestPointReference>[] = [
	{
		header: 'name',
		description: '',
		humanReadable: 'Name',
	},
	{
		header: 'testNumber',
		description: '',
		humanReadable: 'Test Point',
	},
	{
		header: 'result',
		description: '',
		humanReadable: 'Result',
	},
	{
		header: 'overallResult',
		description: '',
		humanReadable: 'Overall Result',
	},
	{
		header: 'resultType',
		description: '',
		humanReadable: 'Result Type',
	},
	{
		header: 'interactive',
		description: '',
		humanReadable: 'Interactive',
	},
	{
		header: 'groupName',
		description: '',
		humanReadable: 'Group',
	},
	{
		header: 'groupType',
		description: '',
		humanReadable: 'Group Type',
	},

	{
		header: 'groupOperator',
		description: '',
		humanReadable: 'Operator',
	},
	{
		header: 'expected',
		description: '',
		humanReadable: 'Expected',
	},
	{
		header: 'actual',
		description: '',
		humanReadable: 'Actual',
	},
	{
		header: 'requirement',
		description: '',
		humanReadable: 'Requirement',
	},
	{
		header: 'elapsedTime',
		description: '',
		humanReadable: 'Elapsed Time',
	},
	{
		header: 'transmissionCount',
		description: '',
		humanReadable: 'Transmission Count',
	},
	{
		header: 'notes',
		description: '',
		humanReadable: 'Notes',
	},
];
