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
import { ATTRIBUTETYPEIDENUM } from '@osee/attributes/constants';
import { attribute } from '@osee/attributes/types';
import type { applic } from '@osee/applicability/types';

export type enumeration = {
	id: `${number}`;
	gammaId: `${number}`;
	name: Required<attribute<string, typeof ATTRIBUTETYPEIDENUM.NAME>>;
	ordinal: Required<
		attribute<number, typeof ATTRIBUTETYPEIDENUM.INTERFACEENUMORDINAL>
	>;
	applicability: applic;
};

export type enumerationSet = {
	id: `${number}`;
	gammaId: `${number}`;
	name: Required<attribute<string, typeof ATTRIBUTETYPEIDENUM.NAME>>;
	description: Required<
		attribute<string, typeof ATTRIBUTETYPEIDENUM.DESCRIPTION>
	>;
	applicability: applic;
	enumerations: enumeration[];
};
