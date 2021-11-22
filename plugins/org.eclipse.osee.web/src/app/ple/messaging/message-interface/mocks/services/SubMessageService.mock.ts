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
import { relation, transaction } from "src/app/transactions/transaction";
import { transactionMock } from "src/app/transactions/transaction.mock";
import { response } from "../../../connection-view/mocks/Response.mock";
import { SubMessagesService } from "../../services/sub-messages.service";
import { subMessage } from "../../types/sub-messages";
import { messageResponseMock } from "../ReturnObjects/response.mock";
import { subMessagesMock } from "../ReturnObjects/submessages.mock";

export const subMessageServiceMock: Partial<SubMessagesService> = {
    changeSubMessage(branchId: string, submessage: Partial<subMessage>) {
        return of(transactionMock);
    },
    getSubMessage(branchId, connectionId, messageId, submessageId) {
        return of(subMessagesMock[0])
    },
    createSubMessage(branchId: string, submessage: Partial<subMessage>, relations: relation[]) {
        return of(transactionMock);
    },
    addRelation(branchId: string, relation: relation) {
        return of(transactionMock);
    },
    createMessageRelation(messageId: string) {
        return of ({
            typeName: 'Interface Message SubMessage Content',
            sideA:'10'
          })
    },
    performMutation(branchId: string, connectionId: string, messageId: string, body: transaction) {
        return of(response)
    },
    deleteSubMessage(branchId: string, submessageId: string, transaction?: transaction) {
        return of(transactionMock);
    },
    deleteRelation(branchId: string, relation: relation) {
        return of(transactionMock);
    }
}