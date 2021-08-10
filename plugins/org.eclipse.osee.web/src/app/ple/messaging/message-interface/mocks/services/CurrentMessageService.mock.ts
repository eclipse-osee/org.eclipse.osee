import { BehaviorSubject, of } from "rxjs";
import { CurrentMessagesService } from "../../services/current-messages.service";
import { message } from "../../types/messages";
import { messageResponseMock } from "../ReturnObjects/response.mock";

let expectedData: message[] = [{
    id:'10',
    name: 'name',
    description: 'description',
    interfaceMessageRate: '50Hz',
    interfaceMessageNumber: '0',
    interfaceMessagePeriodicity: '1Hz',
    interfaceMessageWriteAccess: true,
    interfaceMessageType: 'Connection',
    subMessages: [{
      id: '5',
      name: 'sub message name',
      description: '',
      interfaceSubMessageNumber: '0',
      applicability: {
        id: '1',
        name: 'Base',
      }
    }],
    applicability: {
      id: '1',
      name:'Base'
    }
  }];
export const CurrentMessageServiceMock: Partial<CurrentMessagesService> = {
    messages: of(expectedData),
    applic: of([{ id: '1', name: 'Base' }, { id: '2', name: 'Second' }]),
    partialUpdateSubMessage(body, messageId) { return of(messageResponseMock) },
    createMessage(body: message) {
        return of(messageResponseMock)
    },
    BranchId: new BehaviorSubject("10"),
}