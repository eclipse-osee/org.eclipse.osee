import { of } from "rxjs";
import { MessagesService } from "../../services/messages.service";
import { messagesMock } from "../ReturnObjects/messages.mock";
import { messageResponseMock } from "../ReturnObjects/response.mock";

export const messageServiceMock: Partial<MessagesService> = {
    getFilteredMessages(filter, branchId, connectionId) {
        return of(messagesMock);
    },
    addMessage(body, branchId, connectionId) {
        return of(messageResponseMock);
    },
    partialUpdateMessage(body, branchId, connectionId) {
        return of(messageResponseMock);
    }
}