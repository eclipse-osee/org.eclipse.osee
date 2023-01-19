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

import { Command } from '../types/grid-commander-types/gc-user-and-contexts-relationships';

export const MOCKCOMMAND: Command = {
	contextGroup: 'test',
	name: 'testCommand',
	id: '2020349',
	attributes: {
		description: 'Test',
	},
	idIntValue: 0,
	idString: '',
	parameter: {
		name: 'test',
		id: '1',
		idIntValue: 0,
		idString: '',
		typeAsString: 'ParameterString',
		attributes: {
			description: 'test param',
			'default value': '0',
		},
	},
};

export const MOCKCOMMANDS: Command[] = [
	{
		contextGroup: 'test',
		name: 'test',
		id: '2020349',
		attributes: {
			description: 'Test',
		},
		idIntValue: 0,
		idString: '',
		parameter: {
			name: 'test',
			id: '1',
			idIntValue: 0,
			idString: '',
			typeAsString: 'ParameterString',
			attributes: {
				description: 'test param',
				'default value': '0',
			},
		},
	},
	{
		contextGroup: 'command',
		name: 'Command',
		id: '2020349',
		attributes: {
			description: 'Test',
		},
		idIntValue: 0,
		idString: '',
		parameter: {
			name: 'test',
			id: '1',
			idIntValue: 0,
			idString: '',
			typeAsString: 'ParameterString',
			attributes: {
				description: 'test param',
				'default value': '0',
			},
		},
	},
	{
		contextGroup: 'defaultUser',
		name: 'Help',
		id: '0',
		attributes: {
			description: 'Help Command',
		},
		idIntValue: 0,
		idString: '',
		parameter: {
			name: '',
			id: '',
			idIntValue: 0,
			idString: '',
			typeAsString: '',
			attributes: {
				description: '',
				'default value': '0',
			},
		},
	},
	{
		contextGroup: 'defaultUser',
		name: 'Filter',
		id: '0',
		attributes: {
			description: 'Filter Command',
		},
		idIntValue: 0,
		idString: '',
		parameter: {
			name: '',
			id: '',
			idIntValue: 0,
			idString: '',
			typeAsString: '',
			attributes: {
				description: '',
				'default value': '',
			},
		},
	},
];
