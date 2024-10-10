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

import { nodesMock } from '@osee/messaging/shared/testing';
import type { ImportOption, ImportSummary } from '@osee/messaging/shared/types';
import { applicabilitySentinel } from '@osee/applicability/types';
import { PlatformTypeSentinel } from '@osee/messaging/shared/enumerations';

export const importSummaryMock: ImportSummary = {
	nodes: nodesMock,
	connections: [
		{
			id: '200',
			gammaId: '-1',
			name: {
				id: '-1',
				typeId: '1152921504606847088',
				gammaId: '-1',
				value: 'Connection 1',
			},
			description: {
				id: '-1',
				typeId: '1152921504606847090',
				gammaId: '-1',
				value: '',
			},
			transportType: {
				id: '400',
				gammaId: '-1',
				name: {
					id: '-1',
					typeId: '1152921504606847088',
					gammaId: '-1',
					value: 'TT 1',
				},
				byteAlignValidation: {
					id: '-1',
					typeId: '1682639796635579163',
					gammaId: '-1',
					value: true,
				},
				byteAlignValidationSize: {
					id: '-1',
					typeId: '6745328086388470469',
					gammaId: '-1',
					value: 8,
				},
				messageGeneration: {
					id: '-1',
					typeId: '6696101226215576386',
					gammaId: '-1',
					value: true,
				},
				messageGenerationPosition: {
					id: '-1',
					typeId: '7004358807289801815',
					gammaId: '-1',
					value: '0',
				},
				messageGenerationType: {
					id: '-1',
					typeId: '7121809480940961886',
					gammaId: '-1',
					value: 'Operational',
				},
				minimumPublisherMultiplicity: {
					id: '-1',
					typeId: '7904304476851517',
					gammaId: '-1',
					value: 0,
				},
				maximumPublisherMultiplicity: {
					id: '-1',
					typeId: '8536169210675063038',
					gammaId: '-1',
					value: 0,
				},
				minimumSubscriberMultiplicity: {
					id: '-1',
					typeId: '6433031401579983113',
					gammaId: '-1',
					value: 0,
				},
				maximumSubscriberMultiplicity: {
					id: '-1',
					typeId: '7284240818299786725',
					gammaId: '-1',
					value: 0,
				},
				directConnection: false,
				dashedPresentation: {
					id: '-1',
					typeId: '3564212740439618526',
					gammaId: '-1',
					value: false,
				},
				spareAutoNumbering: {
					id: '-1',
					typeId: '6696101226215576390',
					gammaId: '-1',
					value: false,
				},
				availableMessageHeaders: {
					id: '-1',
					typeId: '2811393503797133191',
					gammaId: '-1',
					value: [],
				},
				availableSubmessageHeaders: {
					id: '-1',
					typeId: '3432614776670156459',
					gammaId: '-1',
					value: [],
				},
				availableStructureHeaders: {
					id: '-1',
					typeId: '3020789555488549747',
					gammaId: '-1',
					value: [],
				},
				availableElementHeaders: {
					id: '-1',
					typeId: '3757258106573748121',
					gammaId: '-1',
					value: [],
				},
				interfaceLevelsToUse: {
					id: '-1',
					typeId: '1668394842614655222',
					gammaId: '-1',
					value: ['message', 'submessage', 'structure', 'element'],
				},
				applicability: applicabilitySentinel,
			},
			nodes: nodesMock,
			applicability: applicabilitySentinel,
		},
	],
	messages: [
		{
			id: '3',
			gammaId: '-1',
			name: {
				id: '-1',
				typeId: '1152921504606847088',
				gammaId: '-1',
				value: 'Message 1',
			},
			subMessages: [],
			interfaceMessageExclude: {
				id: '-1',
				typeId: '2455059983007225811',
				gammaId: '-1',
				value: false,
			},
			interfaceMessageIoMode: {
				id: '-1',
				typeId: '2455059983007225813',
				gammaId: '-1',
				value: '',
			},
			interfaceMessageModeCode: {
				id: '-1',
				typeId: '2455059983007225810',
				gammaId: '-1',
				value: '',
			},
			interfaceMessageRateVer: {
				id: '-1',
				typeId: '2455059983007225805',
				gammaId: '-1',
				value: '',
			},
			interfaceMessagePriority: {
				id: '-1',
				typeId: '2455059983007225806',
				gammaId: '-1',
				value: '',
			},
			interfaceMessageProtocol: {
				id: '-1',
				typeId: '2455059983007225809',
				gammaId: '-1',
				value: '',
			},
			interfaceMessageRptWordCount: {
				id: '-1',
				typeId: '2455059983007225807',
				gammaId: '-1',
				value: '',
			},
			interfaceMessageRptCmdWord: {
				id: '-1',
				typeId: '2455059983007225808',
				gammaId: '-1',
				value: '',
			},
			interfaceMessageRunBeforeProc: {
				id: '-1',
				typeId: '2455059983007225812',
				gammaId: '-1',
				value: false,
			},
			interfaceMessageVer: {
				id: '-1',
				typeId: '2455059983007225804',
				gammaId: '-1',
				value: '',
			},
			publisherNodes: [nodesMock[0]],
			subscriberNodes: [nodesMock[1]],
			description: {
				id: '-1',
				typeId: '1152921504606847090',
				gammaId: '-1',
				value: '',
			},
			interfaceMessageNumber: {
				id: '-1',
				typeId: '2455059983007225768',
				gammaId: '-1',
				value: '10',
			},
			interfaceMessageType: {
				id: '-1',
				typeId: '2455059983007225770',
				gammaId: '-1',
				value: 'Operational',
			},
			interfaceMessagePeriodicity: {
				id: '-1',
				typeId: '3899709087455064789',
				gammaId: '-1',
				value: 'Aperiodic',
			},
			interfaceMessageRate: {
				id: '-1',
				typeId: '2455059983007225763',
				gammaId: '-1',
				value: '20',
			},
			interfaceMessageWriteAccess: {
				id: '-1',
				typeId: '2455059983007225754',
				gammaId: '-1',
				value: true,
			},
			applicability: applicabilitySentinel,
		},
	],
	subMessages: [
		{
			id: '4',
			gammaId: '-1',
			applicability: applicabilitySentinel,
			name: {
				id: '-1',
				typeId: '1152921504606847088',
				gammaId: '-1',
				value: 'SubMessage 1',
			},
			description: {
				id: '-1',
				typeId: '1152921504606847090',
				gammaId: '-1',
				value: '',
			},
			interfaceSubMessageNumber: {
				id: '-1',
				typeId: '2455059983007225769',
				gammaId: '-1',
				value: '1',
			},
		},
	],
	structures: [
		{
			id: '5',
			gammaId: '-1',
			applicability: applicabilitySentinel,
			elements: [],
			name: {
				id: '-1',
				typeId: '1152921504606847088',
				gammaId: '-1',
				value: 'Structure 1',
			},
			nameAbbrev: {
				id: '-1',
				typeId: '8355308043647703563',
				gammaId: '-1',
				value: '',
			},
			description: {
				id: '-1',
				typeId: '1152921504606847090',
				gammaId: '-1',
				value: '',
			},
			interfaceMinSimultaneity: {
				id: '-1',
				typeId: '2455059983007225755',
				gammaId: '-1',
				value: '0',
			},
			interfaceMaxSimultaneity: {
				id: '-1',
				typeId: '2455059983007225756',
				gammaId: '-1',
				value: '1',
			},
			interfaceTaskFileType: {
				id: '-1',
				typeId: '2455059983007225760',
				gammaId: '-1',
				value: 0,
			},
			interfaceStructureCategory: {
				id: '-1',
				typeId: '2455059983007225764',
				gammaId: '-1',
				value: 'Category 1',
			},
		},
	],
	elements: [
		{
			id: '6',
			gammaId: '-1',
			applicability: applicabilitySentinel,
			name: {
				id: '-1',
				typeId: '1152921504606847088',
				gammaId: '-1',
				value: 'Element 1',
			},
			description: {
				id: '-1',
				typeId: '1152921504606847090',
				gammaId: '-1',
				value: '',
			},
			notes: {
				id: '-1',
				typeId: '1152921504606847085',
				gammaId: '-1',
				value: '',
			},
			enumLiteral: {
				id: '-1',
				typeId: '2455059983007225803',
				gammaId: '-1',
				value: '',
			},
			interfaceElementIndexStart: {
				id: '-1',
				typeId: '2455059983007225801',
				gammaId: '-1',
				value: 0,
			},
			interfaceElementIndexEnd: {
				id: '-1',
				typeId: '2455059983007225802',
				gammaId: '-1',
				value: 0,
			},
			interfaceElementAlterable: {
				id: '-1',
				typeId: '2455059983007225788',
				gammaId: '-1',
				value: false,
			},
			interfaceDefaultValue: {
				id: '-1',
				typeId: '2886273464685805413',
				gammaId: '-1',
				value: '',
			},
			interfaceElementArrayHeader: {
				id: '-1',
				typeId: '3313203088521964923',
				gammaId: '-1',
				value: false,
			},
			interfaceElementArrayIndexDelimiterOne: {
				id: '-1',
				typeId: '6818939106523472582',
				gammaId: '-1',
				value: ' ',
			},
			interfaceElementArrayIndexDelimiterTwo: {
				id: '-1',
				typeId: '6818939106523472583',
				gammaId: '-1',
				value: ' ',
			},
			interfaceElementArrayIndexOrder: {
				id: '-1',
				typeId: '6818939106523472581',
				gammaId: '-1',
				value: 'OUTER_INNER',
			},
			interfaceElementBlockData: {
				id: '-1',
				typeId: '1523923981411079299',
				gammaId: '-1',
				value: false,
			},
			interfaceElementWriteArrayHeaderName: {
				id: '-1',
				typeId: '3313203088521964924',
				gammaId: '-1',
				value: false,
			},
			platformType: new PlatformTypeSentinel(),
			arrayElements: [],
		},
	],
	platformTypes: [
		{
			id: '7',
			gammaId: '-1',
			applicability: applicabilitySentinel,
			name: {
				id: '-1',
				typeId: '1152921504606847088',
				gammaId: '-1',
				value: 'Enumeration Type 1',
			},
			description: {
				id: '-1',
				typeId: '1152921504606847090',
				gammaId: '-1',
				value: '',
			},
			interfaceDefaultValue: {
				id: '-1',
				typeId: '2886273464685805413',
				gammaId: '-1',
				value: '',
			},
			interfacePlatformTypeMaxval: {
				id: '-1',
				typeId: '3899709087455064783',
				gammaId: '-1',
				value: '',
			},
			interfacePlatformTypeMinval: {
				id: '-1',
				typeId: '3899709087455064782',
				gammaId: '-1',
				value: '',
			},
			interfacePlatformTypeBitSize: {
				id: '-1',
				typeId: '2455059983007225786',
				gammaId: '-1',
				value: '32',
			},
			interfaceLogicalType: {
				id: '-1',
				typeId: '2455059983007225762',
				gammaId: '-1',
				value: 'enumeration',
			},
			interfacePlatformTypeUnits: {
				id: '-1',
				typeId: '4026643196432874344',
				gammaId: '-1',
				value: '',
			},
			interfacePlatformType2sComplement: {
				id: '-1',
				typeId: '3899709087455064784',
				gammaId: '-1',
				value: false,
			},
			interfacePlatformTypeAnalogAccuracy: {
				id: '-1',
				typeId: '3899709087455064788',
				gammaId: '-1',
				value: '',
			},
			interfacePlatformTypeBitsResolution: {
				id: '-1',
				typeId: '3899709087455064786',
				gammaId: '-1',
				value: '',
			},
			interfacePlatformTypeCompRate: {
				id: '-1',
				typeId: '3899709087455064787',
				gammaId: '-1',
				value: '',
			},
			interfacePlatformTypeMsbValue: {
				id: '-1',
				typeId: '3899709087455064785',
				gammaId: '-1',
				value: '',
			},
			enumSet: {
				id: '8',
				gammaId: '-1',
				enumerations: [],
				name: {
					id: '-1',
					typeId: '1152921504606847088',
					gammaId: '-1',
					value: 'Enumeration Set 1',
				},
				applicability: {
					id: '1',
					name: 'Base',
				},
				description: {
					id: '-1',
					typeId: '1152921504606847090',
					gammaId: '-1',
					value: '',
				},
			},
			interfacePlatformTypeValidRangeDescription: {
				id: '-1',
				typeId: '2121416901992068417',
				gammaId: '-1',
				value: '',
			},
		},
	],
	enumSets: [
		{
			id: '8',
			gammaId: '-1',
			enumerations: [],
			name: {
				id: '-1',
				typeId: '1152921504606847088',
				gammaId: '-1',
				value: 'Enumeration Set 1',
			},
			applicability: {
				id: '1',
				name: 'Base',
			},
			description: {
				id: '-1',
				typeId: '1152921504606847090',
				gammaId: '-1',
				value: '',
			},
		},
	],
	enums: [
		{
			id: '9',
			gammaId: '-1',
			name: {
				id: '-1',
				typeId: '1152921504606847088',
				gammaId: '-1',
				value: 'Option 1',
			},
			applicability: {
				id: '1',
				name: 'Base',
			},
			ordinal: {
				id: '-1',
				typeId: '2455059983007225790',
				gammaId: '-1',
				value: 0,
			},
			ordinalType: {
				id: '-1',
				typeId: '2664267173310317306',
				gammaId: '-1',
				value: 'LONG',
			},
		},
		{
			id: '10',
			gammaId: '-1',
			name: {
				id: '-1',
				typeId: '1152921504606847088',
				gammaId: '-1',
				value: 'Option 2',
			},
			applicability: {
				id: '1',
				name: 'Base',
			},
			ordinal: {
				id: '-1',
				typeId: '2455059983007225790',
				gammaId: '-1',
				value: 1,
			},
			ordinalType: {
				id: '-1',
				typeId: '2664267173310317306',
				gammaId: '-1',
				value: 'LONG',
			},
		},
	],
	crossReferences: [
		{
			id: '22',
			name: 'CR1',
			crossReferenceValue: 'Value 1',
			crossReferenceArrayValues: '0=Test;1=Testing',
			crossReferenceAdditionalContent: 'Additional Content',
			connections: [],
			applicability: applicabilitySentinel,
		},
	],
	connectionNodeRelations: {
		'200': ['1', '2'],
	},
	connectionMessageRelations: {
		'200': ['3'],
	},
	messagePublisherNodeRelations: {
		'3': ['1'],
	},
	messageSubscriberNodeRelations: {
		'3': ['2'],
	},
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
		connectionRequired: false,
		transportTypeRequired: true,
	},
];
