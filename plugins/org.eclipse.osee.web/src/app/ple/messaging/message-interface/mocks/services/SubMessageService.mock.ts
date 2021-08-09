import { of } from "rxjs";
import { SubMessagesService } from "../../services/sub-messages.service";
import { messageResponseMock } from "../ReturnObjects/response.mock";

export const subMessageServiceMock: Partial<SubMessagesService> = {
    partialUpdateSubMessage(body, branchId, messageId, connectionId) {
        return of(messageResponseMock);
    },
    relateSubMessage(branchId, messageId, subMessageId, connectionId) {
        return of(messageResponseMock);
    },
    addSubMessage(body, branchId, messageId, connectionId) {
        return of(messageResponseMock);
    }
}