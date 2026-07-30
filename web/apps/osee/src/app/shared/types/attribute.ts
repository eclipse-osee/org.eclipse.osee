/*********************************************************************
 * Copyright (c) 2024 Boeing
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

/**
 * @deprecated Import directly from `@osee/attributes/types` and `@osee/attributes/constants` instead.
 */
export { type storeType, type multiplicity } from '@osee/attributes/types';
import { attribute } from '@osee/attributes/types';
import { ATTRIBUTETYPEIDENUM } from '@osee/attributes/constants';

export const mockAttribute: attribute<string, typeof ATTRIBUTETYPEIDENUM.NAME> =
	{
		name: 'name',
		value: 'requirement',
		typeId: ATTRIBUTETYPEIDENUM.NAME,
		id: '7777',
		gammaId: '-1',
		storeType: 'String',
	};
