import { HttpClientTestingModule, HttpTestingController } from '@angular/common/http/testing';
import { TestBed } from '@angular/core/testing';
import { TestScheduler } from 'rxjs/testing';
import { response } from '../../connection-view/mocks/Response.mock';
import { messagesMock } from '../mocks/ReturnObjects/messages.mock';
import { messageResponseMock } from '../mocks/ReturnObjects/response.mock';
import { messageServiceMock } from '../mocks/services/MessageService.mock';
import { subMessageServiceMock } from '../mocks/services/SubMessageService.mock';

import { CurrentMessagesService } from './current-messages.service';
import { MessagesService } from './messages.service';
import { SubMessagesService } from './sub-messages.service';
import { UiService } from './ui.service';

describe('CurrentMessagesService', () => {
  let service: CurrentMessagesService;
  let httpTestingController: HttpTestingController;
  let uiService: UiService;
  let scheduler: TestScheduler;

  beforeEach(() => {
    TestBed.configureTestingModule({
      providers: [{ provide: MessagesService, useValue: messageServiceMock },
      {provide:SubMessagesService,useValue:subMessageServiceMock}],
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

});
