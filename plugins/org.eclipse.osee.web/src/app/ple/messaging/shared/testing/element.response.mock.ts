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

import { PlatformTypeSentinel } from '@osee/messaging/shared/enumerations';
import type { element } from '@osee/messaging/shared/types';

export const elementsMock: element[] = [
	{
		id: '1',
		name: 'hello',
		description: '',
		notes: '',
		interfaceElementAlterable: true,
		interfaceElementArrayHeader: false,
		interfaceElementWriteArrayHeaderName: false,
		interfaceElementIndexEnd: 1,
		interfaceElementIndexStart: 0,
		interfaceDefaultValue: '',
		enumLiteral: '',
		logicalType: 'enumeration',
		units: '',
		platformType: new PlatformTypeSentinel(),
		arrayElements: [],
	},
];
