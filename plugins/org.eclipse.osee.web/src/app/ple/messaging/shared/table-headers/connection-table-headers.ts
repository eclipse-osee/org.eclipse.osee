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
import { connection } from '../types';

export const connectionHeaderDetails: headerDetail<connection>[] = [
	{
		header: 'name',
		description: 'Name of the connection',
		humanReadable: 'Name',
	},
	{
		header: 'description',
		description: 'Description of the connection',
		humanReadable: 'Description',
	},
	{
		header: 'applicability',
		description: 'Applicability of the connection',
		humanReadable: 'Applicability',
	},
];
