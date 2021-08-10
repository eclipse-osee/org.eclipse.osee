import { of } from "rxjs";
import { relation, transaction } from "src/app/transactions/transaction";
import { transactionMock } from "src/app/transactions/transaction.mock";
import { response } from "../../../connection-view/mocks/Response.mock";
import { SubMessagesService } from "../../services/sub-messages.service";
import { subMessage } from "../../types/sub-messages";
import { messageResponseMock } from "../ReturnObjects/response.mock";

export const subMessageServiceMock: Partial<SubMessagesService> = {
    changeSubMessage(branchId: string, submessage: Partial<subMessage>) {
        return of(transactionMock);
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
    }
}