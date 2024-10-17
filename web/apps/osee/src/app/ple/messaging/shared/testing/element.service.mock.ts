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
import type { element } from '@osee/messaging/shared/types';
import { transactionResultMock } from '@osee/transactions/testing';
import { of } from 'rxjs';
import { ElementService } from '../services/http/element.service';
import { elementsMock } from './element.response.mock';
import { transactionMock } from '@osee/transactions/testing';
import {
	legacyRelation,
	legacyTransaction,
	transaction,
} from '@osee/transactions/types';

export const elementServiceMock: Partial<ElementService> = {
	performMutation(body: legacyTransaction) {
		return of(transactionResultMock);
	},
	createStructureRelation(structureId: string) {
		return of({
			typeName: 'Interface Structure Content',
			sideA: '10',
		});
	},
	createPlatformTypeRelation(platformTypeId: string) {
		return of({
			typeName: 'Interface Element Platform Type',
			sideB: '10',
		});
	},
	addNewElementToTransaction(
		body: element,
		tx: Required<transaction>,
		key?: string
	) {
		return tx;
	},
	addRelation(
		branchId: string,
		relation: legacyRelation,
		transaction?: legacyTransaction
	) {
		return of(transactionMock);
	},
	deleteRelation(
		branchId: string,
		relation: legacyRelation,
		transaction?: legacyTransaction
	) {
		return of(transactionMock);
	},
	getElement(
		branchId: string,
		messageId: string,
		subMessageId: string,
		structureId: string,
		elementId: string,
		connectionId: string
	) {
		return of(elementsMock[0]);
	},
	getFilteredElements(branchId: string, filter: string) {
		return of(elementsMock);
	},
	deleteElement(branchId: string, elementId: string) {
		return of(transactionMock);
	},
};
