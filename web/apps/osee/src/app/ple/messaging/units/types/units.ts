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
import { hasApplic } from '@osee/applicability/types';
import { ATTRIBUTETYPEIDENUM } from '@osee/attributes/constants';
import { attribute } from '@osee/attributes/types';

export type unit = {
	id: `${number}`;
	gammaId: `${number}`;
} & UnitAttr &
	hasApplic;

type UnitAttr = {
	name: Required<attribute<string, typeof ATTRIBUTETYPEIDENUM.NAME>>;
	measurement: Required<
		attribute<string, typeof ATTRIBUTETYPEIDENUM.INTERFACEUNITMEASUREMENT>
	>;
};
