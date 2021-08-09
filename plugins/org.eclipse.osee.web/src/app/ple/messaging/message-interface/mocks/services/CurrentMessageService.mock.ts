import { of } from "rxjs";
import { CurrentMessagesService } from "../../services/current-messages.service";
import { messageResponseMock } from "../ReturnObjects/response.mock";

export const CurrentMessageServiceMock: Partial<CurrentMessagesService> = {
    applic: of([{ id: '1', name: 'Base' }, { id: '2', name: 'Second' }]),
    partialUpdateSubMessage(body,messageId){return of(messageResponseMock)}
}