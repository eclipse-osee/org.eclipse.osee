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
import { nodeDiffItem } from '@osee/messaging/shared';
import { headerDetail } from '@osee/shared/types';

export const nodeDiffHeaderDetails: headerDetail<nodeDiffItem>[] = [
	{ header: 'name', description: 'Name of node', humanReadable: 'Name' },
	{
		header: 'description',
		description: 'Description of node',
		humanReadable: 'Description',
	},
	{
		header: 'address',
		description: 'Address of node',
		humanReadable: 'Address',
	},
	{
		header: 'color',
		description: 'Color of node',
		humanReadable: 'Color',
	},
	{
		header: 'applicability',
		description: 'Applicability of node',
		humanReadable: 'Applicability',
	},
];
