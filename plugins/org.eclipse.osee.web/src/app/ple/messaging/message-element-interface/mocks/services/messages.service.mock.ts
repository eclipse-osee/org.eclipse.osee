import { of } from "rxjs";
import { messagesMock } from "../../../message-interface/mocks/ReturnObjects/messages.mock";
import { subMessagesMock } from "../../../message-interface/mocks/ReturnObjects/submessages.mock";
import { MessagesService } from "../../services/messages.service";

export const messageServiceMock: Partial<MessagesService> = {
    getSubMessage(branchId: string, messageId: string, subMessageId: string,connectionId:string) {
        return of(subMessagesMock[0]);
    },
    getMessages(branchId: string, connectionId: string) {
        return of(messagesMock);
    }
}