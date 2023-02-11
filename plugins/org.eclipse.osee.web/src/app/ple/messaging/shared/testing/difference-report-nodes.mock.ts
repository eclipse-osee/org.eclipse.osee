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
import { nodeDiffItem } from '@osee/messaging/shared/types';
export const nodeDiffsMock: nodeDiffItem[] = [
	{
		id: '200394',
		name: 'Node4',
		applicability: {
			id: '1009971623404681232',
			name: 'Config = Product C',
		},
		address: '444',
		color: 'Default',
		description: 'Node      4',
		diffInfo: {
			added: false,
			deleted: false,
			fieldsChanged: {
				applicability: {
					id: '1',
					name: 'Base',
				},
			},
			url: {
				label: '',
				url: '',
			},
		},
	},
	{
		id: '200396',
		name: 'Node D',
		applicability: {
			id: '1',
			name: 'Base',
		},
		address: '000',
		color: 'Default',
		description: 'Delete this',
		diffInfo: {
			added: false,
			deleted: true,
			fieldsChanged: {
				name: 'Node D',
				description: 'Delete this',
				address: '000',
				color: 'Default',
			},
			url: {
				label: '',
				url: '',
			},
		},
	},
	{
		id: '200430',
		name: 'Node A',
		applicability: {
			id: '1',
			name: 'Base',
		},
		address: '555',
		color: '#2c5926',
		description: 'Added this node',
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
		id: '200391',
		name: 'Node2(Edit)',
		applicability: {
			id: '1',
			name: 'Base',
		},
		address: '',
		color: '#7993b4',
		description: 'Node 2 description',
		diffInfo: {
			added: false,
			deleted: false,
			fieldsChanged: {
				name: 'Node2',
			},
			url: {
				label: '',
				url: '',
			},
		},
	},
	{
		id: '200390',
		name: 'Node1',
		applicability: {
			id: '1',
			name: 'Base',
		},
		address: '1111',
		color: '#893e3e',
		description: 'Edited this node',
		diffInfo: {
			added: false,
			deleted: false,
			fieldsChanged: {
				description: 'Node 1 description',
				address: '111',
				color: '#854c4c',
			},
			url: {
				label: '',
				url: '',
			},
		},
	},
];
