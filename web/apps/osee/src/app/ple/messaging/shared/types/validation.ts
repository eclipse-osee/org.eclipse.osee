/*********************************************************************
 * Copyright (c) 2023 Boeing
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
export type ConnectionValidationResult = {
	branch: string;
	viewId: string;
	connectionName: string;
	passed: boolean;
	structureByteAlignmentErrors: Record<`${number}`, string>;
	structureWordAlignmentErrors: Record<`${number}`, string>;
	duplicateStructureNameErrors: Record<`${number}`, string>;
	messageTypeErrors: Record<`${number}`, string>;
	affectedConfigurations: {
		id: `${number}`;
		gammaId: `${number}`;
		name: attribute<string, typeof ATTRIBUTETYPEIDENUM.NAME>;
	}[];
};

export const connectionValidationResultSentinel: ConnectionValidationResult = {
	branch: '-1',
	viewId: '-1',
	connectionName: '',
	passed: true,
	structureByteAlignmentErrors: {},
	structureWordAlignmentErrors: {},
	duplicateStructureNameErrors: {},
	messageTypeErrors: {},
	affectedConfigurations: [],
};
