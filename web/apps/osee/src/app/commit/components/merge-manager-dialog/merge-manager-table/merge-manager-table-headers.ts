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

import { attrMergeData, mergeData } from '@osee/commit/types';
import { headerDetail } from '@osee/shared/types';

export const mergeManagerHeaderDetails: headerDetail<
	mergeData & attrMergeData
>[] = [
	{
		header: 'name',
		description: 'Artifact Name',
		humanReadable: 'Artifact Name',
	},
	{
		header: 'conflictType',
		description: 'Conflict Type',
		humanReadable: 'Conflict Type',
	},
	{
		header: 'attrTypeName',
		description: 'Attribute Type',
		humanReadable: 'Attribute Type',
	},
	{
		header: 'sourceValue',
		description: 'Value from the source branch',
		humanReadable: 'Source Value',
	},
	{
		header: 'mergeValue',
		description: 'Value that will be set after merging',
		humanReadable: 'Merge Value',
	},
	{
		header: 'destValue',
		description: 'Value from the destination branch',
		humanReadable: 'Destination Value',
	},
];
