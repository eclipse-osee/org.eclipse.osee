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

import type {
	PlatformType,
	PlatformTypeAttr,
	logicalTypeFormDetail,
} from '@osee/messaging/shared/types';
import { PLATFORMTYPEATTRIBUTETYPEIDENUM } from '@osee/messaging/shared/attr';

export const logicalTypeFormDetailMock: logicalTypeFormDetail<
	keyof PlatformTypeAttr
> = {
	fields: [
		{
			name: 'InterfacePlatformTypeBitSize',
			attributeTypeId: PLATFORMTYPEATTRIBUTETYPEIDENUM.BIT_SIZE,
			attributeType: 'InterfacePlatformTypeBitSize',
			editable: true,
			required: true,
			defaultValue: {
				id: '-1',
				typeId: PLATFORMTYPEATTRIBUTETYPEIDENUM.BIT_SIZE,
				gammaId: '-1',
				value: '8',
			},
			value: {
				id: '-1',
				typeId: PLATFORMTYPEATTRIBUTETYPEIDENUM.BIT_SIZE,
				gammaId: '-1',
				value: '8',
			},
			jsonPropertyName: 'interfacePlatformTypeBitSize',
		},
	],
	id: '-1',
	name: 'enumeration',
	idString: '',
	idIntValue: 0,
};
