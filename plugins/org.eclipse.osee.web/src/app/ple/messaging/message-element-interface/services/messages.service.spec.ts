import { HttpClientTestingModule, HttpTestingController } from '@angular/common/http/testing';
import { TestBed } from '@angular/core/testing';
import { apiURL } from 'src/environments/environment';
import { messagesMock } from '../../message-interface/mocks/ReturnObjects/messages.mock';

import { MessagesService } from './messages.service';

describe('MessagesService', () => {
  let service: MessagesService;
  let httpTestingController: HttpTestingController;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports:[HttpClientTestingModule]
    });
    service = TestBed.inject(MessagesService);
    httpTestingController = TestBed.inject(HttpTestingController);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });

  it('should get a sub message', () => {
    service.getSubMessage('10', '20', '30', '1').subscribe();
    const req = httpTestingController.expectOne(apiURL + "/mim/branch/" + 10 + "/connections/"+1+"/messages/" + 20 + "/submessages/" + 30);
    expect(req.request.method).toEqual('GET');
    req.flush(messagesMock[0].subMessages[0]);
    httpTestingController.verify();
  })
});
