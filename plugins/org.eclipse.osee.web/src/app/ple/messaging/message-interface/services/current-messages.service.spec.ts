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
import { HttpClientTestingModule, HttpTestingController } from '@angular/common/http/testing';
import { TestBed } from '@angular/core/testing';
import { TestScheduler } from 'rxjs/testing';
import { userDataAccountServiceMock } from 'src/app/ple/plconfig/testing/mockUserDataAccountService';
import { UserDataAccountService } from 'src/app/userdata/services/user-data-account.service';
import { applicabilityListServiceMock } from '../../shared/mocks/ApplicabilityListService.mock';
import { response } from '../../connection-view/mocks/Response.mock';
import { MimPreferencesMock } from '../../shared/mocks/MimPreferences.mock';
import { MimPreferencesServiceMock } from '../../shared/mocks/MimPreferencesService.mock';
import { ApplicabilityListService } from '../../shared/services/http/applicability-list.service';
import { MimPreferencesService } from '../../shared/services/http/mim-preferences.service';
import { messagesMock } from '../mocks/ReturnObjects/messages.mock';
import { messageResponseMock } from '../mocks/ReturnObjects/response.mock';
import { messageServiceMock } from '../mocks/services/MessageService.mock';
import { subMessageServiceMock } from '../mocks/services/SubMessageService.mock';

import { CurrentMessagesService } from './current-messages.service';
import { MessagesService } from './messages.service';
import { SubMessagesService } from './sub-messages.service';
import { UiService } from './ui.service';
import { subMessagesMock } from '../mocks/ReturnObjects/submessages.mock';

describe('CurrentMessagesService', () => {
  let service: CurrentMessagesService;
  let httpTestingController: HttpTestingController;
  let uiService: UiService;
  let scheduler: TestScheduler;

  beforeEach(() => {
    TestBed.configureTestingModule({
      providers: [
        { provide: MessagesService, useValue: messageServiceMock },
        { provide: SubMessagesService, useValue: subMessageServiceMock },
        { provide: ApplicabilityListService, useValue: applicabilityListServiceMock },
        { provide: MimPreferencesService, useValue: MimPreferencesServiceMock },
        {provide:UserDataAccountService,useValue:userDataAccountServiceMock}],
      imports:[HttpClientTestingModule]
    });
    service = TestBed.inject(CurrentMessagesService);
    httpTestingController = TestBed.inject(HttpTestingController);
    uiService = TestBed.inject(UiService);
  });

  beforeEach(() => scheduler = new TestScheduler((actual, expected) => {
    expect(actual).toEqual(expected);
  }));

  it('should be created', () => {
    expect(service).toBeTruthy();
  });

  it('should fetch filtered messages', () => {
    scheduler.run(() => {
      service.filter = 'filter';
      service.branch = '10';
      service.connection = '10';
      let expectedObservable = { a: messagesMock }
      let expectedMarble = '500ms a';
      scheduler.expectObservable(service.messages).toBe(expectedMarble,expectedObservable)
    })
  });

  it('should update the list of all messages twice', () => {
    scheduler.run(({cold}) => {
      service.branch = '10';
      service.connection = '10';
      let expectedObservable = { a: messagesMock }
      let expectedMarble = 'aa';
      cold(expectedMarble).subscribe(() => uiService.updateMessages = true);
      scheduler.expectObservable(service.allMessages).toBe(expectedMarble,expectedObservable)
    })
  })
  it('should partially update a message', () => {
    scheduler.run(() => {
      service.branch = '10';
      service.connection = '10';
      let expectedObservable = { a: response }
      let expectedMarble = '(a|)';
      scheduler.expectObservable(service.partialUpdateMessage({})).toBe(expectedMarble,expectedObservable)
    })
  })
  it('should partially update a sub message', () => {
    scheduler.run(() => {
      service.branch = '10';
      service.connection = '10';
      let expectedObservable = { a: response }
      let expectedMarble = '(a|)';
      scheduler.expectObservable(service.partialUpdateSubMessage({},'10')).toBe(expectedMarble,expectedObservable)
    })
  })

  it('should relate a sub message', () => {
    scheduler.run(() => {
      service.branch = '10';
      service.connection = '10';
      let expectedObservable = { a: response }
      let expectedMarble = '(a|)';
      scheduler.expectObservable(service.relateSubMessage('15','10')).toBe(expectedMarble,expectedObservable)
    })
  })

  it('should create a sub message', () => {
    scheduler.run(() => {
      service.branch = '10';
      service.connection = '10';
      let expectedObservable = { a: response }
      let expectedMarble = '(a|)';
      scheduler.expectObservable(service.createSubMessage(messagesMock[0].subMessages[0],'10')).toBe(expectedMarble,expectedObservable)
    })
  })

  it('should create a message', () => {
    scheduler.run(() => {
      service.branch = '10';
      service.connection = '10';
      let expectedObservable = { a: response }
      let expectedMarble = '(a|)';
      scheduler.expectObservable(service.createMessage(messagesMock[0])).toBe(expectedMarble,expectedObservable)
    })
  })
  it('should delete a message', () => {
    scheduler.run(() => {
      service.branch = '10';
      service.connection = '10';
      let expectedObservable = { a: response }
      let expectedMarble = 'a';
      scheduler.expectObservable(service.deleteMessage(messagesMock[0].id)).toBe(expectedMarble,expectedObservable)
    })
  })

  it('should remove a message', () => {
    scheduler.run(() => {
      service.branch = '10';
      service.connection = '10';
      let expectedObservable = { a: response }
      let expectedMarble = 'a';
      scheduler.expectObservable(service.removeMessage(messagesMock[0].id)).toBe(expectedMarble,expectedObservable)
    })
  })

  it('should delete a submessage', () => {
    scheduler.run(() => {
      service.branch = '10';
      service.connection = '10';
      let expectedObservable = { a: response }
      let expectedMarble = 'a';
      scheduler.expectObservable(service.deleteSubMessage(subMessagesMock[0].id)).toBe(expectedMarble,expectedObservable)
    })
  })

  it('should remove a submessage', () => {
    scheduler.run(() => {
      service.branch = '10';
      service.connection = '10';
      let expectedObservable = { a: response }
      let expectedMarble = 'a';
      scheduler.expectObservable(service.removeSubMessage(subMessagesMock[0].id,messagesMock[0].id)).toBe(expectedMarble,expectedObservable)
    })
  })

  it('should fetch preferences', () => {
    scheduler.run(() => {
      const expectedFilterValues = { a: MimPreferencesMock };
      const expectedMarble = 'a';
      service.branch = '10'
      scheduler.expectObservable(service.preferences).toBe(expectedMarble, expectedFilterValues);
    })
  })

  it('should update user preferences', () => {
    scheduler.run(() => {
      service.branch='10'
      let expectedObservable = { a: response };
      let expectedMarble = '(a|)';
      scheduler.expectObservable(service.updatePreferences({branchId:'10',allowedHeaders1:['hello','hello3'],allowedHeaders2:['hello2','hello3'],allHeaders1:['hello'],allHeaders2:['hello2'],editable:true,headers1Label:'',headers2Label:'',headersTableActive:false})).toBe(expectedMarble, expectedObservable);
    })
  })
});
