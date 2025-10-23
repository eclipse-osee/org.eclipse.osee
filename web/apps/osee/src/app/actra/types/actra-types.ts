/*********************************************************************
 * Copyright (c) 2025 Boeing
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
export type world = {
	orderedHeaders: string[];
	rows: worldRow[];
	title: string;
	atsId: string;
	collectorArt: collectorArt;
};

export type collectorArt = {
	id: string;
	name: string;
	typeId: string;
	typeName: string;
};

export type worldRow = Record<string, string>;

export const worldDataEmpty: world = {
	orderedHeaders: ['ATS Id', 'Name', 'State'],
	rows: [],
	collectorArt: {
		name: 'l',
		id: '-1',
		typeId: '-1',
		typeName: 'Sentinel',
	},
	atsId: '',
	title: 'My World - Empty',
};

export const worldDataMock: world = {
	orderedHeaders: [
		'Goal Order',
		'Name',
		'Type',
		'State',
		'Priority',
		'Change Type',
		'Assignees',
		'ATS Id',
		'Created Date',
		'Targeted Version',
		'Notes',
	],
	rows: [
		{
			Type: 'Decision Review',
			'Targeted Version': '',
			'Created Date': '2024-05-16',
			'Goal Order': 'Unhandled Column',
			State: 'Decision',
			Priority: '',
			Assignees: 'Joe Smith',
			'ATS Id': 'RVW13',
			'Change Type': '',
			Notes: '',
			Name: 'Is the resolution of Action TW24 valid?',
		},
		{
			Type: 'Peer-To-Peer Review',
			'Targeted Version': 'SAW_Bld_2',
			'Created Date': '2024-05-16',
			'Goal Order': 'Unhandled Column',
			State: 'Review',
			Priority: '',
			Assignees: 'Joe Smith; Kay Jones',
			'ATS Id': 'RVW15',
			'Change Type': '',
			Notes: '',
			Name: '2 - Peer Review algorithm used in code',
		},
	],
	collectorArt: {
		name: 'Web Export Goal',
		id: '-1',
		typeId: '-1',
		typeName: 'Sentinel',
	},
	atsId: 'TW15',
	title: 'My World - Joe Smith',
};

export type WorkflowAttachment = {
  id: `${number}`;
  name: string;
  nameAtId: `${number}`;
  nameGamma: `${number}`;
  extension: string;
  extensionAtId: `${number}`;
  extensionGamma: `${number}`;
  sizeInBytes: number;
  attachmentBytes?: string;
  nativeContentAtId: `${number}`;
  nativeContentGamma: `${number}`;
};

// (megabytes) by (bytes per kibibyte) by (kibibytes per mebibyte)
export const MAX_ATTACHMENT_SIZE_BYTES = 50 * 1024 * 1024;