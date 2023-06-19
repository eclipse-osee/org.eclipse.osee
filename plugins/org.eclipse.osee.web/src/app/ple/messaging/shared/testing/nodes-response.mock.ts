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

export const nodesMock: Required<nodeData>[] = [
	{
		id: '123',
		name: 'Node 1',
		interfaceNodeNumber: '1',
		interfaceNodeGroupId: 'group1',
		interfaceNodeAddress: '12345',
		interfaceNodeBackgroundColor: '',
		interfaceNodeBuildCodeGen: true,
		interfaceNodeCodeGen: true,
		interfaceNodeToolUse: true,
		interfaceNodeCodeGenName: 'NODE_1',
		interfaceNodeNameAbbrev: 'node1',
		interfaceNodeType: 'normal',
		notes: 'This is a note',
		description: 'Test node 1',
		applicability: {
			id: '1',
			name: 'Base',
		},
	},
	{
		id: '321',
		name: 'Node 2',
		interfaceNodeNumber: '2',
		interfaceNodeGroupId: 'group2',
		interfaceNodeAddress: '123456',
		interfaceNodeBackgroundColor: '',
		interfaceNodeBuildCodeGen: false,
		interfaceNodeCodeGen: false,
		interfaceNodeToolUse: false,
		interfaceNodeCodeGenName: 'NODE_2',
		interfaceNodeNameAbbrev: 'node2',
		interfaceNodeType: 'abnormal',
		notes: 'This is also a note',
		description: 'Test node 2',
		applicability: {
			id: '1',
			name: 'Base',
		},
	},
];

export const emptyNodeMock: nodeData = {
	id: '',
	name: '',
	interfaceNodeNumber: '',
	interfaceNodeGroupId: '',
	interfaceNodeAddress: '',
	interfaceNodeBackgroundColor: '',
	interfaceNodeBuildCodeGen: false,
	interfaceNodeCodeGen: false,
	interfaceNodeToolUse: false,
	interfaceNodeCodeGenName: '',
	interfaceNodeNameAbbrev: '',
	interfaceNodeType: '',
	notes: '',
	description: '',
	applicability: {
		id: '1',
		name: 'Base',
	},
};
