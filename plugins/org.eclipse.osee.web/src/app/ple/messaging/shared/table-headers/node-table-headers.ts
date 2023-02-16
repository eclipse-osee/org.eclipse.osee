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
import { nodeToken } from '../types';

export const nodeHeaderDetails: headerDetail<nodeToken>[] = [
	{
		header: 'name',
		description: 'Name of the node',
		humanReadable: 'Name',
	},
	{
		header: 'description',
		description: 'Description of the node',
		humanReadable: 'Description',
	},
	{
		header: 'applicability',
		description: 'Applicability of the node',
		humanReadable: 'Applicability',
	},
];
