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
import { messageToken } from '../types';

export const importMessageHeaderDetails: headerDetail<messageToken>[] = [
	{
		header: 'name',
		description: 'Name of message',
		humanReadable: 'Name',
	},
	{
		header: 'description',
		description: 'Description of message',
		humanReadable: 'Description',
	},
	{
		header: 'interfaceMessageNumber',
		description: 'Message Number',
		humanReadable: 'Message Number',
	},
	{
		header: 'interfaceMessagePeriodicity',
		description: 'Periodicity of message',
		humanReadable: 'Periodicity',
	},
	{
		header: 'interfaceMessageRate',
		description: 'Transmission rate of message',
		humanReadable: 'TxRate',
	},
	{
		header: 'interfaceMessageWriteAccess',
		description: 'Write access of message',
		humanReadable: 'Write Access',
	},
	{
		header: 'interfaceMessageType',
		description: 'Type of message',
		humanReadable: 'Type',
	},
	{
		header: 'applicability',
		description: 'Applicability of message',
		humanReadable: 'Applicability',
	},
];
