import { HttpClientModule } from '@angular/common/http';
import { HttpClientTestingModule, HttpTestingController } from '@angular/common/http/testing';
import { fakeAsync, TestBed, tick } from '@angular/core/testing';
import { apiURL } from 'src/environments/environment';
import { message } from '../types/messages';

import { CurrentMessagesService } from './current-messages.service';
import { MessagesService } from './messages.service';
import { UiService } from './ui.service';

describe('CurrentMessagesService', () => {
  let service: CurrentMessagesService;
  let httpTestingController: HttpTestingController;
  let uiService: UiService;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports:[HttpClientTestingModule]
    });
    service = TestBed.inject(CurrentMessagesService);
    httpTestingController = TestBed.inject(HttpTestingController);
    uiService = TestBed.inject(UiService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });

  it('should fetch filtered messages', fakeAsync(() => {
    const testData: message[] = [];
    service.filter = 'filter';
    service.branch = '10';
    service.connection = '10';
    service.messages.subscribe();
    tick(500);
    const req=httpTestingController.expectOne(apiURL + "/mim/branch/" + '10' + "/connections/"+'10'+"/messages/filter/" + 'filter');
    expect(req.request.method).toEqual("GET");
    req.flush(testData);
    httpTestingController.verify();
  }));

});
