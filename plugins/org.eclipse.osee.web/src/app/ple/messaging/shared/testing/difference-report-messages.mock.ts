/*********************************************************************
 * Copyright (c) 2022 Boeing
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
import { messageDiffItem } from '@osee/messaging/shared/types';
export const messageDiffsMock: messageDiffItem[] = [
	{
		id: '200402',
		name: 'Message D',
		subMessages: [],
		applicability: {
			id: '1',
			name: 'Base',
		},
		initiatingNode: null,
		description: 'Delete this message',
		interfaceMessageNumber: '4',
		interfaceMessagePeriodicity: 'Periodic',
		interfaceMessageType: 'Operational',
		interfaceMessageRate: '10',
		interfaceMessageWriteAccess: false,
		diffInfo: {
			added: false,
			deleted: true,
			fieldsChanged: {},
			url: {
				label: '',
				url: '',
			},
		},
	},
	{
		id: '200432',
		name: 'Message A',
		subMessages: [],
		applicability: {
			id: '1',
			name: 'Base',
		},
		initiatingNode: null,
		description: 'Added this message',
		interfaceMessageNumber: '3',
		interfaceMessagePeriodicity: 'Periodic',
		interfaceMessageType: 'Connection',
		interfaceMessageRate: '20',
		interfaceMessageWriteAccess: true,
		diffInfo: {
			added: true,
			deleted: false,
			fieldsChanged: {},
			url: {
				label: '',
				url: '',
			},
		},
	},
	{
		id: '200399',
		name: 'Message1',
		subMessages: [],
		applicability: {
			id: '1',
			name: 'Base',
		},
		initiatingNode: null,
		description: 'This is message 1',
		interfaceMessageNumber: '1',
		interfaceMessagePeriodicity: 'Aperiodic',
		interfaceMessageType: 'Connection',
		interfaceMessageRate: '5',
		interfaceMessageWriteAccess: false,
		diffInfo: {
			added: false,
			deleted: false,
			fieldsChanged: {
				interfaceMessagePeriodicity: 'OnDemand',
			},
			url: {
				label: '',
				url: '',
			},
		},
	},
];
