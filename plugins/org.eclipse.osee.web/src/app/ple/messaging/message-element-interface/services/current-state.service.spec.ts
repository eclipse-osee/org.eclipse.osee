import { HttpClient } from '@angular/common/http';
import { HttpClientTestingModule, HttpTestingController } from '@angular/common/http/testing';
import { fakeAsync, TestBed, tick } from '@angular/core/testing';
import { apiURL } from 'src/environments/environment';
import { structure } from '../types/structure';

import { CurrentStateService } from './current-state.service';

describe('CurrentStateService', () => {
  let service: CurrentStateService;
  let httpClient: HttpClient;
  let httpTestingController: HttpTestingController;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports:[HttpClientTestingModule]
    });
    service = TestBed.inject(CurrentStateService);
    httpClient = TestBed.inject(HttpClient);
    httpTestingController = TestBed.inject(HttpTestingController);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });

  it('should get filtered structures', fakeAsync(() => {
    let testData: structure[] = [{
      id: '0',
      name: 'name',
      elements: [],
      description: 'description',
      interfaceMaxSimultaneity: '1',
      interfaceMinSimultaneity: '0',
      interfaceStructureCategory: '1',
      interfaceTaskFileType:1
    }]

    service.structures.subscribe();
    service.branchId = "0";
    service.filter = "0";
    service.messageId = "1";
    service.subMessageId = "2";
    tick(500);
    const req = httpTestingController.expectOne(apiURL + "/mim/branch/" + 0 + "/messages/" + 1 + "/submessages/" + 2 + "/structures/filter/" + 0);
    expect(req.request.method).toEqual('GET');
    req.flush(testData);
    httpTestingController.verify();
  }));
});
