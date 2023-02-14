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

import type { ImportOption, ImportSummary } from '@osee/messaging/shared';

export const importSummaryMock: ImportSummary = {
	createPrimaryNode: true,
	createSecondaryNode: true,
	connectionId: '1234',
	primaryNode: {
		id: '1',
		name: 'Node 1',
	},
	secondaryNode: {
		id: '2',
		name: 'Node 2',
	},
	messages: [
		{
			id: '3',
			name: 'Message 1',
			subMessages: [],
			initiatingNode: {
				id: '1',
				name: 'Node 1',
			},
			description: '',
			interfaceMessageNumber: '10',
			interfaceMessageType: 'Operational',
			interfaceMessagePeriodicity: 'Aperiodic',
			interfaceMessageRate: '20',
			interfaceMessageWriteAccess: true,
		},
	],
	subMessages: [
		{
			id: '4',
			name: 'SubMessage 1',
			description: '',
			interfaceSubMessageNumber: '1',
		},
	],
	structures: [
		{
			id: '5',
			name: 'Structure 1',
			description: '',
			interfaceMinSimultaneity: '0',
			interfaceMaxSimultaneity: '1',
			interfaceTaskFileType: 0,
			interfaceStructureCategory: 'Category 1',
		},
	],
	elements: [
		{
			id: '6',
			name: 'Element 1',
			description: '',
			notes: '',
			enumLiteral: '',
			interfaceElementIndexStart: 0,
			interfaceElementIndexEnd: 0,
			interfaceElementAlterable: false,
			interfaceDefaultValue: '',
		},
	],
	platformTypes: [
		{
			id: '7',
			name: 'Enumeration Type 1',
			description: '',
			interfaceDefaultValue: '',
			interfacePlatformTypeMaxval: '',
			interfacePlatformTypeMinval: '',
			interfacePlatformTypeBitSize: '32',
			interfaceLogicalType: 'enumeration',
			interfacePlatformTypeUnits: '',
			interfacePlatformTypeValidRangeDescription: '',
		},
	],
	enumSets: [
		{
			id: '8',
			name: 'Enumeration Set 1',
			applicability: {
				id: '1',
				name: 'Base',
			},
			description: '',
		},
	],
	enums: [
		{
			id: '9',
			name: 'Option 1',
			applicability: {
				id: '1',
				name: 'Base',
			},
			ordinal: 0,
		},
		{
			id: '10',
			name: 'Option 2',
			applicability: {
				id: '1',
				name: 'Base',
			},
			ordinal: 1,
		},
	],
	crossReferences: [
		{
			id: '22',
			name: 'CR1',
			crossReferenceValue: 'Value 1',
			crossReferenceArrayValues: '0=Test;1=Testing',
			crossReferenceAdditionalContent: 'Additional Content',
		},
	],
	messageSubmessageRelations: {
		'3': ['4'],
	},
	subMessageStructureRelations: {
		'4': ['5'],
	},
	structureElementRelations: {
		'5': ['6'],
	},
	elementPlatformTypeRelations: {
		'6': ['7'],
	},
	platformTypeEnumSetRelations: {
		'7': ['8'],
	},
	enumSetEnumRelations: {
		'8': ['9', '10'],
	},
	connectionCrossReferenceRelations: {
		'1234': ['22'],
	},
};

export const importOptionsMock: ImportOption[] = [
	{
		id: '1',
		name: 'Option 1',
		url: '',
		transportType: '12345',
		connectionRequired: false,
	},
];
