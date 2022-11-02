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
import { of } from 'rxjs';
import { relation, transaction } from 'src/app/transactions/transaction';
import {
	transactionMock,
	transactionResultMock,
} from 'src/app/transactions/transaction.mock';
import { EnumerationSetService } from '../services/http/enumeration-set.service';
import { enumeration, enumSet } from '../types/enum';
import { enumerationSetMock } from '../../types-interface/mocks/returnObjects/enumerationset.mock';
import { response } from '../../connection-view/mocks/Response.mock';

export const enumerationSetServiceMock: Partial<EnumerationSetService> = {
	createEnumSet(
		branchId: string,
		type: enumSet | Partial<enumSet>,
		relations: relation[],
		transaction?: transaction
	) {
		return of(transactionMock);
	},
	changeEnumSet(
		branchId: string,
		type: Partial<enumSet>,
		transaction?: transaction
	) {
		return of(transactionMock);
	},
	createEnum(
		branchId: string,
		type: enumeration | Partial<enumeration>,
		relations: relation[],
		transaction?: transaction
	) {
		return of(transactionMock);
	},
	createEnumSetToPlatformTypeRelation(sideA?: string) {
		return of<relation>({
			typeName: 'Interface Platform Type Enumeration Set',
			sideA: sideA,
		});
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
	getEnumSets(branchId: string) {
		return of(enumerationSetMock);
	},
	getEnumSet(branchId: string, platformTypeId: string) {
		return of(enumerationSetMock[0]);
	},
	performMutation(body: transaction) {
		return of(transactionResultMock);
	},
};
