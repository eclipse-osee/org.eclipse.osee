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
import type { subMessage } from '@osee/messaging/shared/types';
import {
	transactionMock,
	transactionResultMock,
} from '@osee/transactions/testing';
import {
	legacyRelation,
	legacyTransaction,
	transaction,
} from '@osee/transactions/types';
import { of } from 'rxjs';
import { SubMessagesService } from '../services/http/sub-messages.service';
import { subMessagesMock } from './sub-messages.response.mock';

export const subMessageServiceMock: Partial<SubMessagesService> = {
	getSubMessage(branchId, connectionId, messageId, submessageId) {
		return of(subMessagesMock[0]);
	},
	createSubMessage(
		submessage: subMessage,
		tx: Required<transaction>,
		key?: string
	) {
		return tx;
	},
	addRelation(branchId: string, relation: legacyRelation) {
		return of(transactionMock);
	},
	createMessageRelation(messageId: string) {
		return of({
			typeName: 'Interface Message SubMessage Content',
			sideA: '10',
		});
	},
	performMutation(
		branchId: string,
		connectionId: string,
		messageId: string,
		body: legacyTransaction
	) {
		return of(transactionResultMock);
	},
	deleteSubMessage(
		branchId: string,
		submessageId: string,
		transaction?: legacyTransaction
	) {
		return of(transactionMock);
	},
	deleteRelation(branchId: string, relation: legacyRelation) {
		return of(transactionMock);
	},
};
