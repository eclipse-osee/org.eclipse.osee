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

import type { enumerationSet } from '@osee/messaging/shared/types';

export const enumerationSetMock: enumerationSet[] = [
	{
		id: '2',
		gammaId: '2',
		name: {
			id: '-1',
			typeId: '1152921504606847088',
			gammaId: '-1',
			value: 'Hello',
		},
		description: {
			id: '-1',
			typeId: '1152921504606847090',
			gammaId: '-1',
			value: 'World',
		},
		applicability: {
			id: '1',
			name: 'Base',
		},
		enumerations: [
			{
				id: '1',
				gammaId: '1',
				name: {
					id: '-1',
					typeId: '1152921504606847088',
					gammaId: '-1',
					value: 'a',
				},
				ordinal: {
					id: '-1',
					typeId: '2455059983007225790',
					gammaId: '-1',
					value: 0,
				},
				applicability: { id: '1', name: 'Base' },
			},
		],
	},
	{
		id: '3',
		gammaId: '3',
		name: {
			id: '-1',
			typeId: '1152921504606847088',
			gammaId: '-1',
			value: 'enumset',
		},
		description: {
			id: '-1',
			typeId: '1152921504606847090',
			gammaId: '-1',
			value: '',
		},
		enumerations: [],
		applicability: {
			id: '1',
			name: 'Base',
		},
	},
];
