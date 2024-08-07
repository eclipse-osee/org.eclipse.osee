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
import { of } from 'rxjs';
import { enumerationSetMock } from './enumeration-set.response.mock';
import { EnumerationUIService } from '../services/ui/enumeration-ui.service';
import type {
	enumSet,
	enumeration,
	enumerationSet,
} from '@osee/messaging/shared/types';
import {
	createArtifact,
	modifyArtifact,
	modifyRelation,
	relation,
	transaction,
} from '@osee/shared/types';
import {
	transactionMock,
	transactionResultMock,
} from '@osee/shared/transactions/testing';

export const enumerationUiServiceMock: Partial<EnumerationUIService> = {
	createEnumSetToPlatformTypeRelation(sideA?: string) {
		return of<relation>({
			typeName: 'Interface Platform Type Enumeration Set',
			sideB: sideA,
		});
	},
	createEnumSet(
		branchId: string,
		type: enumSet | Partial<enumSet>,
		relations: relation[],
		transaction?: transaction
	) {
		return of(transactionMock);
	},
	createPlatformTypeToEnumSetRelation(sideB?: string) {
		return of<relation>({
			typeName: 'Interface Platform Type Enumeration Set',
			sideB: sideB,
		});
	},
	createEnumToEnumSetRelation(sideA?: string) {
		return of<relation>({
			typeName: 'Interface Enumeration Definition',
			sideA: sideA,
		});
	},
	createEnum(
		branchId: string,
		type: enumeration | Partial<enumeration>,
		relations: relation[],
		transaction?: transaction
	) {
		return of(transactionMock);
	},
	get enumSets() {
		return of(enumerationSetMock);
	},
	getEnumSet(platformTypeId: string) {
		return of(enumerationSetMock[0]);
	},
	changeEnumSet(dialogResponse: {
		createArtifacts: createArtifact[];
		modifyArtifacts: modifyArtifact[];
		deleteRelations: modifyRelation[];
	}) {
		return of(transactionResultMock);
	},
};
