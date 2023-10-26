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
import { ResultReference } from '../types';

export const resultHeaderDetails: headerDetail<ResultReference>[] = [
	{
		header: 'name',
		description: '',
		humanReadable: 'Script Name',
	},
	{
		header: 'totalTestPoints',
		description: 'Total number of test points',
		humanReadable: 'Total',
	},
	{
		header: 'passedCount',
		description: 'Number of passing test points',
		humanReadable: 'Passes',
	},
	{
		header: 'failedCount',
		description: 'Number of failing test points',
		humanReadable: 'Fails',
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
		header: 'machineName',
		description: 'Machine the script ran on',
		humanReadable: 'Machine',
	},
];
