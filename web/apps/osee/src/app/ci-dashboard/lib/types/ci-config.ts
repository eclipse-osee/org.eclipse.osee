/*********************************************************************
 * Copyright (c) 2025 Boeing
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

import { applic, applicabilitySentinel } from '@osee/applicability/types';
import { ATTRIBUTETYPEIDENUM } from '@osee/attributes/constants';
import { attribute } from '@osee/attributes/types';

export type CIConfig = {
	id: `${number}`;
	gammaId: `${number}`;
	applicability: applic;
	testResultsToKeep: Required<
		attribute<number, typeof ATTRIBUTETYPEIDENUM.TESTRESULTSTOKEEP>
	>;
	branch: { id: `${number}`; viewId: `${number}` };
};

export const ciConfigSentinel: CIConfig = {
	id: '-1',
	gammaId: '-1',
	applicability: applicabilitySentinel,
	testResultsToKeep: {
		id: '-1',
		gammaId: '-1',
		typeId: '6846375894770628832',
		value: 10,
	},
	branch: { id: '-1', viewId: '-1' },
};
