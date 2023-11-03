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

export const setDiffHeaderDetails: headerDetail<any>[] = [
	{
		header: 'name',
		description: 'Name of the script',
		humanReadable: 'Script Name',
	},
	{
		header: 'equal',
		description: 'Set results are equal',
		humanReadable: 'Equal',
	},
	{
		header: 'passes',
		description: 'Number of passing test points',
		humanReadable: 'Passes',
	},
	{
		header: 'fails',
		description: 'Number of failing test points',
		humanReadable: 'Fails',
	},
	{
		header: 'abort',
		description: 'Test script aborted',
		humanReadable: 'Abort',
	},
];
