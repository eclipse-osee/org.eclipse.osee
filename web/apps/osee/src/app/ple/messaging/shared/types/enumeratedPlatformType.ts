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
import type { enumerationSet } from './enum';
import type { PlatformType } from './platformType';
import { ATTRIBUTETYPEIDENUM } from '@osee/attributes/constants';

type _id = `${number}`;
export type enumeratedPlatformType = {
	interfaceLogicalType: {
		id: _id | '';
		value: 'enumeration';
		gammaId: _id;
		typeId: typeof ATTRIBUTETYPEIDENUM.LOGICALTYPE;
	};
	enumerationSet: enumerationSet;
} & PlatformType;
