/*********************************************************************
 * Copyright (c) 2021 Boeing
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
import { NamedId } from '@osee/shared/types';
import { PlConfigApplicUIBranchMapping } from '../types/pl-config-applicui-branch-mapping';
import { applicWithConstraints } from '../types/pl-config-feature-constraints';

export const testApplicsWithFeatureConstraints: applicWithConstraints[] = [
	{
		id: '1',
		name: 'test1',
		constraints: [
			{
				id: '2',
				name: 'test2',
				constraints: [],
			},
		],
	},
	{
		id: '3',
		name: 'test3',
		constraints: [
			{
				id: '4',
				name: 'test4',
				constraints: [],
			},
		],
	},
];
export const testBranchApplicabilityIdName: NamedId[] = [
	{
		id: '1',
		name: 'TEST1 = INCLUDED',
	},
	{
		id: '2',
		name: 'TEST2 = INCLUDED',
	},
	{
		id: '3',
		name: 'TEST3 = INCLUDED | TEST4 = EXCLUDED',
	},
];
export const testApplicabilityTag: NamedId = {
	id: '1234',
	name: 'BROKENFEATURE = INCLUDED',
};
export const testCfgGroups = [
	{
		id: '1',
		name: 'Group 1',
	},
	{
		id: '2',
		name: 'Group 2',
	},
	{
		id: '3',
		name: 'Group 3',
	},
	{
		id: '4',
		name: 'Group 4',
	},
	{
		id: '5',
		name: 'Group 5',
	},
	{
		id: '6',
		name: 'Group 6',
	},
];
export const testBranchApplicability: PlConfigApplicUIBranchMapping = {
	associatedArtifactId: '200578',
	branch: {
		id: '3182843164128526558',
		name: 'TW195 aaa',
		viewId: '-1',
		idIntValue: -1918287650,
	},
	editable: true,
	features: [
		{
			id: '1939294030',
			name: 'ENGINE_5',
			values: ['A2543', 'B5543'],
			defaultValue: 'A2543',
			description: 'Used select type of engine',
			multiValued: false,
			valueType: 'String',
			type: null,
			productApplicabilities: ['OFP'],
			idIntValue: 1939294030,
			idString: '1939294030',
			configurations: [
				{
					id: '12345',
					name: 'group1',
					value: '',
					values: [],
				},
				{
					id: '200047',
					name: 'Product-C',
					value: 'A2453',
					values: ['A2453'],
				},
			],
			setValueStr(): void {},
			setProductAppStr(): void {},
		},
		{
			id: '758071644',
			name: 'JHU_CONTROLLER',
			values: ['Included', 'Excluded'],
			defaultValue: 'Included',
			description: 'A small point of variation',
			multiValued: false,
			valueType: 'String',
			type: null,
			productApplicabilities: [],
			idIntValue: 758071644,
			idString: '758071644',
			configurations: [
				{
					id: '12345',
					name: 'group1',
					value: '',
					values: [],
				},
			],
			setValueStr(): void {},
			setProductAppStr(): void {},
		},
		{
			id: '130553732',
			name: 'ROBOT_ARM_LIGHT',
			values: ['Included', 'Excluded'],
			defaultValue: 'Included',
			description: 'A significant capability',
			multiValued: false,
			valueType: 'String',
			type: null,
			productApplicabilities: ['OFP'],
			idIntValue: 130553732,
			idString: '130553732',
			configurations: [
				{
					id: '12345',
					name: 'group1',
					value: '',
					values: [],
				},
			],
			setValueStr(): void {},
			setProductAppStr(): void {},
		},
		{
			id: '293076452',
			name: 'ROBOT_SPEAKER',
			values: ['SPKR_A', 'SPKR_B', 'SPKR_C'],
			defaultValue: 'SPKR_A',
			description: 'This feature is multi-select.',
			multiValued: true,
			valueType: 'String',
			type: null,
			productApplicabilities: [],
			idIntValue: 293076452,
			idString: '293076452',
			configurations: [
				{
					id: '12345',
					name: 'group1',
					value: '',
					values: [],
				},
			],
			setValueStr(): void {},
			setProductAppStr(): void {},
		},
		{
			id: '201342',
			name: 'BROKENFEATURE',
			values: ['Included', 'Excluded'],
			defaultValue: 'Included',
			description: 'yiuyoiyoi',
			multiValued: false,
			valueType: 'String',
			type: null,
			productApplicabilities: ['Unspecified'],
			idIntValue: 201342,
			idString: '201342',
			configurations: [
				{
					id: '12345',
					name: 'group1',
					value: '',
					values: [],
				},
			],
			setValueStr(): void {},
			setProductAppStr(): void {},
		},
	],
	groups: [
		{
			id: '736857919',
			name: 'abGroup',
			description: '',
			//hasFeatureApplicabilities:true,
			configurations: ['200045', '200046'],
			//productApplicabilities:[]
		},
		{
			id: '201322',
			name: 'deleted group',
			description: '',
			configurations: [],
		},
		{
			id: '201321',
			name: 'test',
			description: '',
			configurations: [],
		},
	],
	parentBranch: {
		id: '8',
		name: 'SAW Product Line',
		viewId: '-1',
		idIntValue: 8,
	},
	views: [
		{
			id: '200045',
			name: 'Product A',
			description: '',
			hasFeatureApplicabilities: true,
		},
		{
			id: '200046',
			name: 'Product B',
			description: '',
			hasFeatureApplicabilities: true,
		},
		{
			id: '200047',
			name: 'Product C',
			description: '',
			hasFeatureApplicabilities: true,
		},
		{
			id: '200048',
			name: 'Product D',
			description: '',
			hasFeatureApplicabilities: true,
		},
		{
			id: '201325',
			name: 'added view',
			description: '',
			hasFeatureApplicabilities: true,
		},
		{
			id: '201334',
			name: 'modified product app',
			description: '',
			hasFeatureApplicabilities: true,
			productApplicabilities: ['hello world'],
		},
		{
			id: '201343',
			name: 'newconfig',
			description: '',
			hasFeatureApplicabilities: true,
			productApplicabilities: ['Unspecified'],
		},
	],
};
