import { of } from "rxjs";
import { subMessagesMock } from "../../../message-interface/mocks/ReturnObjects/submessages.mock";
import { MessagesService } from "../../services/messages.service";

export const messageServiceMock: Partial<MessagesService> = {
    getSubMessage(branchId: string, messageId: string, subMessageId: string,connectionId:string) {
        return of(subMessagesMock[0]);
    }
}