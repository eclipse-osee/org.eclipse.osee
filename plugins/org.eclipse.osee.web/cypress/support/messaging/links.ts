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
import { nodes } from './nodes';
import { types } from './types';

export const links = [
	{
		fromNode: nodes[0].name,
		toNode: nodes[2].name,
		name: `${nodes[0].name}To${nodes[2].name}`,
		description: `${nodes[0].name} to ${nodes[2].name} description`,
		transportType: 'ETHERNET',
		messages: [
			{
				name: 'Message 1',
				description: 'Message 1 Description',
				rate: '5',
				periodicity: 'OnDemand',
				messageType: 'Connection',
				messageNumber: '1',
				nodeIsFirst: true,
				subMessages: [
					{
						name: 'SubMessage 1',
						description: 'SubMessage 1 Description',
						subMessageNumber: '0',
						editedDescription: 'Edited SubMessage 1 Description',
						structures: [
							{
								name: 'Structure 1',
								description: 'Structure 1 Description',
								maxSimultaneity: '1',
								minSimultaneity: '0',
								taskFileType: '0',
								category: 'N/A',
								elements: [
									{
										name: 'Element 1',
										description: 'Element 1 Description',
										notes: 'Notes regarding Element 1',
										indexStart: '0',
										indexEnd: '0',
										alterable: false,
										logicalType: types[0],
									},
								],
							},
							{
								name: 'Structure 2',
								description: 'Structure 2 Description',
								maxSimultaneity: '0',
								minSimultaneity: '0',
								taskFileType: '0',
								category: 'N/A',
							},
							{
								name: 'Structure 3',
								description: 'Structure 3 Description',
								maxSimultaneity: '0',
								minSimultaneity: '1',
								taskFileType: '0',
								category: 'N/A',
							},
							{
								name: 'Structure 4',
								description: 'Structure 4 Description',
								maxSimultaneity: '1',
								minSimultaneity: '1',
								taskFileType: '0',
								category: 'N/A',
							},
							{
								name: 'Structure 5',
								description: 'Structure 5 Description',
								maxSimultaneity: '10',
								minSimultaneity: '3',
								taskFileType: '0',
								category: 'N/A',
							},
							{
								name: 'Structure 6',
								description: 'Structure 6 Description',
								maxSimultaneity: '3',
								minSimultaneity: '10',
								taskFileType: '0',
								category: 'N/A',
							},
						],
					},
					{
						name: 'SubMessage 2',
						description: 'SubMessage 2 Description',
						subMessageNumber: '1',
						editedDescription: 'Edited SubMessage 2 Description',
					},
					{
						name: 'SubMessage 3',
						description: 'SubMessage 3 Description',
						subMessageNumber: '2',
						editedDescription: 'Edited SubMessage 3 Description',
					},
				],
			},
			{
				name: 'Message 2',
				description: 'Message 2 Description',
				rate: '1',
				periodicity: 'Aperiodic',
				messageType: 'Operational',
				messageNumber: '2',
				nodeIsFirst: true,
			},
			{
				name: 'Message 3',
				description: 'Message 3 Description',
				rate: '10',
				periodicity: 'Periodic',
				messageType: 'Connection',
				messageNumber: '3',
				nodeIsFirst: true,
			},
			{
				name: 'Message 1',
				description: 'Message 1 Description',
				rate: '5',
				periodicity: 'OnDemand',
				messageType: 'Connection',
				messageNumber: '1',
				nodeIsFirst: false,
			},
			{
				name: 'Message 2',
				description: 'Message 2 Description',
				rate: '1',
				periodicity: 'Aperiodic',
				messageType: 'Operational',
				messageNumber: '2',
				nodeIsFirst: false,
			},
			{
				name: 'Message 3',
				description: 'Message 3 Description',
				rate: '10',
				periodicity: 'Periodic',
				messageType: 'Connection',
				messageNumber: '3',
				nodeIsFirst: false,
			},
		],
	},
	{
		fromNode: nodes[0].name,
		toNode: nodes[3].name,
		name: `${nodes[0].name}To${nodes[3].name}`,
		description: `${nodes[0].name} to ${nodes[3].name} description`,
		transportType: 'ETHERNET',
	},
	{
		fromNode: nodes[0].name,
		toNode: nodes[4].name,
		name: `${nodes[0].name}To${nodes[4].name}`,
		description: `${nodes[0].name} to ${nodes[4].name} description`,
		transportType: 'ETHERNET',
	},
	{
		fromNode: nodes[1].name,
		toNode: nodes[5].name,
		name: `${nodes[1].name}To${nodes[5].name}`,
		description: `${nodes[1].name} to ${nodes[5].name} description`,
		transportType: 'ETHERNET',
	},
];
