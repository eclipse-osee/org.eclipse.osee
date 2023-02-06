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

import { enumerationSet } from '@osee/messaging/shared/types';

export const enumerationSetMock: enumerationSet[] = [
	{
		name: 'Hello',
		description: 'World',
		applicability: {
			id: '1',
			name: 'Base',
		},
		enumerations: [
			{
				id: '1',
				name: 'a',
				ordinal: 0,
				applicability: { id: '1', name: 'Base' },
			},
		],
	},
	{
		name: 'enumset',
		description: '',
		applicability: {
			id: '1',
			name: 'Base',
		},
	},
];
