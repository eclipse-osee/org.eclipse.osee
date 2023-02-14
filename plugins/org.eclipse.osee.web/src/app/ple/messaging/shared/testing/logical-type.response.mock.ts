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

import type { logicalType } from '@osee/messaging/shared/types';

export const logicalTypeMock: logicalType[] = [
	{
		id: '0',
		name: 'enumeration',
		idString: '0',
		idIntValue: 0,
	},
	{
		id: '1',
		name: 'boolean',
		idIntValue: 1,
		idString: '1',
	},
];
