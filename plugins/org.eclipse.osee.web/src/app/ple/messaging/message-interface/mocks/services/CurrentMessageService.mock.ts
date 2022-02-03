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
import { BehaviorSubject, of, ReplaySubject, Subject } from "rxjs";
import { MimPreferencesMock } from "../../../shared/mocks/MimPreferences.mock";
import { applic } from "../../../../../types/applicability/applic";
import { settingsDialogData } from "../../../shared/types/settingsdialog";
import { CurrentMessagesService } from "../../services/current-messages.service";
import { message, messageWithChanges } from "../../types/messages";
import { subMessage } from "../../types/sub-messages";
import { messageResponseMock } from "../ReturnObjects/response.mock";
import { transactionToken } from "src/app/transactions/transaction";


let sideNavContentPlaceholder = new ReplaySubject<{ opened: boolean; field: string; currentValue: string | number | boolean | applic; previousValue?: string | number | boolean | applic | undefined; transaction?: transactionToken | undefined; user?: string | undefined; date?: string | undefined }>();
sideNavContentPlaceholder.next({opened:true,field:'',currentValue:'',previousValue:''})
let expectedData: (message|messageWithChanges)[] = [{
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
  },
  initiatingNode: {
    id: '1',
    name: 'Node 1'
  },
  changes: {
    name: {
      previousValue: '',
      currentValue: 'name',
      transactionToken: {
        id: '-1',
        branchId:'-1'
      } 
    }  
  }
}];
const diffmode= new BehaviorSubject<boolean>(false);
export const CurrentMessageServiceMock: Partial<CurrentMessagesService> = {
  messages: of(expectedData),
  applic: of([{ id: '1', name: 'Base' }, { id: '2', name: 'Second' }]),
  partialUpdateSubMessage(body, messageId) { return of(messageResponseMock) },
  partialUpdateMessage(body){return of(messageResponseMock)},
  createMessage(body: message) {
      return of(messageResponseMock)
  },
  BranchId: new BehaviorSubject("10"),
  preferences: of(MimPreferencesMock),
  updatePreferences(preferences: settingsDialogData) {
    return of(messageResponseMock)
  },
  removeMessage(messageId: string) {
    return of(messageResponseMock);
  },
  removeSubMessage(subMessageId: string, messageId: string) {
    return of(messageResponseMock);
  },
  relateSubMessage(messageId: string, subMessageId: string) {
    return of(messageResponseMock);
  },
  createSubMessage(body: subMessage, messageId: string) {
    return of(messageResponseMock);
  },
  deleteMessage(messageId: string) {
    return of(messageResponseMock);
  },
  deleteSubMessage(subMessageId: string) {
    return of(messageResponseMock);
  },
  done: new Subject(),
  isInDiff:diffmode,
  sideNavContent: sideNavContentPlaceholder,
  set sideNav(value: { opened: boolean; field: string; currentValue: string | number | boolean | applic; previousValue?: string | number | boolean | applic | undefined; transaction?: transactionToken | undefined; user?: string | undefined; date?: string | undefined }) { },
  get initialRoute() { return of('/ple/messaging/' + 'working' + '/' + '10' + '/' + '20' + '/messages/') },
  get endOfRoute(){return of('')}
}