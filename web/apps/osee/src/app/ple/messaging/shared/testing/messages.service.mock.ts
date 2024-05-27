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
import { MessagesService } from '../services/http/messages.service';
import { messagesMock } from './messages.response.mock';
import type { message } from '@osee/messaging/shared/types';
import { transactionResultMock } from '@osee/transactions/testing';
import { transactionMock } from '@osee/transactions/testing';
import {
	legacyRelation,
	legacyTransaction,
	transaction,
} from '@osee/transactions/types';
import { ATTRIBUTETYPEIDENUM } from '@osee/attributes/constants';

export const messageServiceMock: Partial<MessagesService> = {
	getFilteredMessages(filter, branchId, connectionId, pageNum, pageSize) {
		return of(messagesMock);
	},
	getMessage(branchId: string, messageId: string) {
		return of(messagesMock[0]);
	},
	getConnectionName(branchId: string, connectionId: string) {
		return of({
			id: '-1',
			typeId: ATTRIBUTETYPEIDENUM.NAME,
			gammaId: '-1',
			value: 'hello',
		});
	},
	addNewMessageToTransaction(
		message: message,
		tx: Required<transaction>,
		key?: string
	) {
		return tx;
	},
	createConnectionRelation(connectionId: string) {
		return of({
			typeName: 'Interface Connection Message',
			sideA: '10',
		});
	},
	createMessageNodeRelation(
		messageId: string,
		nodeId: string,
		type: boolean
	) {
		return of({
			typeName: 'Interface Message Sending Node',
			sideA: '20',
		});
	},
	performMutation(body: legacyTransaction) {
		return of(transactionResultMock);
	},
	deleteMessage(
		branchId: string,
		messageId: string,
		transaction?: legacyTransaction
	) {
		return of(transactionMock);
	},
	deleteRelation(branchId: string, relation: legacyRelation) {
		return of(transactionMock);
	},
};
