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
import type { enumeration, enumerationSet } from '@osee/messaging/shared/types';
import { transactionResultMock } from '@osee/transactions/testing';
import {
	legacyRelation,
	legacyTransaction,
	transaction,
} from '@osee/transactions/types';
import { of } from 'rxjs';
import { EnumerationSetService } from '../services/http/enumeration-set.service';
import { enumerationSetMock } from './enumeration-set.response.mock';

export const enumerationSetServiceMock: Partial<EnumerationSetService> = {
	createEnumSet(
		set: enumerationSet,
		tx: Required<transaction>,
		key?: string
	) {
		return tx;
	},
	createEnum(
		enumeration: enumeration,
		tx: Required<transaction>,
		key?: string
	) {
		return tx;
	},
	createPlatformTypeToEnumSetRelation(sideB?: string) {
		return of<legacyRelation>({
			typeName: 'Interface Platform Type Enumeration Set',
			sideB: sideB,
		});
	},
	createEnumToEnumSetRelation(sideA?: string) {
		return of<legacyRelation>({
			typeName: 'Interface Enumeration Definition',
			sideA: sideA,
		});
	},
	getEnumSets(branchId: string) {
		return of(enumerationSetMock);
	},
	getEnumSet(branchId: string, platformTypeId: string) {
		return of(enumerationSetMock[0]);
	},
	performMutation(body: legacyTransaction) {
		return of(transactionResultMock);
	},
};
