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
import type { nodeData } from '@osee/messaging/shared/types';

export const nodesMock: nodeData[] = [
	{
		id: '123',
		gammaId: '234',
		name: {
			id: '-1',
			typeId: '1152921504606847088',
			gammaId: '-1',
			value: 'Node 1',
		},
		interfaceNodeNumber: {
			id: '-1',
			typeId: '5726596359647826657',
			gammaId: '-1',
			value: '1',
		},
		interfaceNodeGroupId: {
			id: '-1',
			typeId: '5726596359647826658',
			gammaId: '-1',
			value: 'group1',
		},
		interfaceNodeAddress: {
			id: '-1',
			typeId: '5726596359647826656',
			gammaId: '-1',
			value: '12345',
		},
		interfaceNodeBackgroundColor: {
			id: '-1',
			typeId: '5221290120300474048',
			gammaId: '-1',
			value: '',
		},
		interfaceNodeBuildCodeGen: {
			id: '-1',
			typeId: '5806420174793066197',
			gammaId: '-1',
			value: true,
		},
		interfaceNodeCodeGen: {
			id: '-1',
			typeId: '4980834335211418740',
			gammaId: '-1',
			value: true,
		},
		interfaceNodeToolUse: {
			id: '-1',
			typeId: '5863226088234748106',
			gammaId: '-1',
			value: true,
		},
		interfaceNodeCodeGenName: {
			id: '-1',
			typeId: '5390401355909179776',
			gammaId: '-1',
			value: 'NODE_1',
		},
		nameAbbrev: {
			id: '-1',
			typeId: '8355308043647703563',
			gammaId: '-1',
			value: 'node1',
		},
		interfaceNodeType: {
			id: '-1',
			typeId: '6981431177168910500',
			gammaId: '-1',
			value: 'normal',
		},
		notes: {
			id: '-1',
			typeId: '1152921504606847085',
			gammaId: '-1',
			value: 'This is a note',
		},
		description: {
			id: '-1',
			typeId: '1152921504606847090',
			gammaId: '-1',
			value: 'Test node 1',
		},
		applicability: {
			id: '1',
			name: 'Base',
		},
	},
	{
		id: '321',
		gammaId: '432',
		name: {
			id: '-1',
			typeId: '1152921504606847088',
			gammaId: '-1',
			value: 'Node 2',
		},
		interfaceNodeNumber: {
			id: '-1',
			typeId: '5726596359647826657',
			gammaId: '-1',
			value: '2',
		},
		interfaceNodeGroupId: {
			id: '-1',
			typeId: '5726596359647826658',
			gammaId: '-1',
			value: 'group2',
		},
		interfaceNodeAddress: {
			id: '-1',
			typeId: '5726596359647826656',
			gammaId: '-1',
			value: '123456',
		},
		interfaceNodeBackgroundColor: {
			id: '-1',
			typeId: '5221290120300474048',
			gammaId: '-1',
			value: '',
		},
		interfaceNodeBuildCodeGen: {
			id: '-1',
			typeId: '5806420174793066197',
			gammaId: '-1',
			value: false,
		},
		interfaceNodeCodeGen: {
			id: '-1',
			typeId: '4980834335211418740',
			gammaId: '-1',
			value: false,
		},
		interfaceNodeToolUse: {
			id: '-1',
			typeId: '5863226088234748106',
			gammaId: '-1',
			value: false,
		},
		interfaceNodeCodeGenName: {
			id: '-1',
			typeId: '5390401355909179776',
			gammaId: '-1',
			value: 'NODE_2',
		},
		nameAbbrev: {
			id: '-1',
			typeId: '8355308043647703563',
			gammaId: '-1',
			value: 'node2',
		},
		interfaceNodeType: {
			id: '-1',
			typeId: '6981431177168910500',
			gammaId: '-1',
			value: 'abnormal',
		},
		notes: {
			id: '-1',
			typeId: '1152921504606847085',
			gammaId: '-1',
			value: 'This is also a note',
		},
		description: {
			id: '-1',
			typeId: '1152921504606847090',
			gammaId: '-1',
			value: '',
		},
		applicability: {
			id: '1',
			name: 'Base',
		},
	},
];

export const emptyNodeMock: nodeData = {
	id: '-1',
	gammaId: '-1',
	name: {
		id: '-1',
		typeId: '1152921504606847088',
		gammaId: '-1',
		value: '',
	},
	interfaceNodeNumber: {
		id: '-1',
		typeId: '5726596359647826657',
		gammaId: '-1',
		value: '',
	},
	interfaceNodeGroupId: {
		id: '-1',
		typeId: '5726596359647826658',
		gammaId: '-1',
		value: '',
	},
	interfaceNodeAddress: {
		id: '-1',
		typeId: '5726596359647826656',
		gammaId: '-1',
		value: '',
	},
	interfaceNodeBackgroundColor: {
		id: '-1',
		typeId: '5221290120300474048',
		gammaId: '-1',
		value: '',
	},
	interfaceNodeBuildCodeGen: {
		id: '-1',
		typeId: '5806420174793066197',
		gammaId: '-1',
		value: false,
	},
	interfaceNodeCodeGen: {
		id: '-1',
		typeId: '4980834335211418740',
		gammaId: '-1',
		value: false,
	},
	interfaceNodeToolUse: {
		id: '-1',
		typeId: '5863226088234748106',
		gammaId: '-1',
		value: false,
	},
	interfaceNodeCodeGenName: {
		id: '-1',
		typeId: '5390401355909179776',
		gammaId: '-1',
		value: '',
	},
	nameAbbrev: {
		id: '-1',
		typeId: '8355308043647703563',
		gammaId: '-1',
		value: '',
	},
	interfaceNodeType: {
		id: '-1',
		typeId: '6981431177168910500',
		gammaId: '-1',
		value: '',
	},
	notes: {
		id: '-1',
		typeId: '1152921504606847085',
		gammaId: '-1',
		value: '',
	},
	description: {
		id: '-1',
		typeId: '1152921504606847090',
		gammaId: '-1',
		value: '',
	},
	applicability: {
		id: '1',
		name: 'Base',
	},
};
