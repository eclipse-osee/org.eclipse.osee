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
import { connectionNodesMock } from '@osee/messaging/shared/testing';
import type { message } from '@osee/messaging/shared/types';
import { transaction, relation } from '@osee/shared/types';
import {
	transactionMock,
	transactionResultMock,
} from '@osee/shared/transactions/testing';

export const messageServiceMock: Partial<MessagesService> = {
	getFilteredMessages(filter, branchId, connectionId, pageNum, pageSize) {
		return of(messagesMock);
	},
	getMessage(branchId: string, messageId: string) {
		return of(messagesMock[0]);
	},
	getConnectionName(branchId: string, connectionId: string) {
		return of('hello');
	},
	getConnectionNodes(branchId: string, connectionId: string) {
		return of(connectionNodesMock);
	},
	createMessage(branchId: string, message: Partial<message>) {
		return of(transactionMock);
	},
	changeMessage(branchId: string, message: Partial<message>) {
		return of(transactionMock);
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
	performMutation(body: transaction) {
		return of(transactionResultMock);
	},
	deleteMessage(
		branchId: string,
		messageId: string,
		transaction?: transaction
	) {
		return of(transactionMock);
	},
	deleteRelation(branchId: string, relation: relation) {
		return of(transactionMock);
	},
};
