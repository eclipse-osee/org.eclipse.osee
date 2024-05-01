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
import { subMessage } from '../types';

export const subMessageHeaderDetails: headerDetail<subMessage>[] = [
	{
		header: 'name',
		description: 'Name of submessage',
		humanReadable: 'Name',
	},
	{
		header: 'description',
		description: 'Description of submessage',
		humanReadable: 'Description',
	},
	{
		header: 'interfaceSubMessageNumber',
		description: 'Submessage number',
		humanReadable: 'SubMessage Number',
	},
	{
		header: 'applicability',
		description: 'Applicability of submessage',
		humanReadable: 'Applicability',
	},
];
