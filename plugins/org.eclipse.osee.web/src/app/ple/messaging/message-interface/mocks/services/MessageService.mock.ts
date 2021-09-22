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
import { of } from "rxjs";
import { MessagesService } from "../../services/messages.service";
import { messagesMock } from "../ReturnObjects/messages.mock";
import { messageResponseMock } from "../ReturnObjects/response.mock";
import { message } from '../../types/messages';
import { transactionMock } from "src/app/transactions/transaction.mock";
import { response } from "../../../connection-view/mocks/Response.mock";
import { transaction } from "src/app/transactions/transaction";

export const messageServiceMock: Partial<MessagesService> = {
    getFilteredMessages(filter, branchId, connectionId) {
        return of(messagesMock);
    },
    getMessage(branchId: string, messageId: string) {
        return of(messagesMock[0]);
    },
    getConnectionName(branchId: string, connectionId: string) {
        return of("hello")
    },
    createMessage(branchId: string, message: Partial<message>) {
      return of(transactionMock)  
    },
    changeMessage(branchId: string, message: Partial<message>) {
        return of(transactionMock)
    },
    createConnectionRelation(connectionId: string) {
        return of({
            typeName: 'Interface Connection Content',
            sideA:'10'
          })
    },
    performMutation(branchId: string, connectionId: string, body: transaction) {
        return of(response)
    }
}