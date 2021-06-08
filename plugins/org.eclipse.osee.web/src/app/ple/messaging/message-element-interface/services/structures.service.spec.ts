import { TestBed } from '@angular/core/testing';
import { HttpClientTestingModule, HttpTestingController } from '@angular/common/http/testing';

import { StructuresService } from './structures.service';
import { HttpClient } from '@angular/common/http';
import { TestScheduler } from 'rxjs/testing';
import { structure } from '../types/structure';
import { apiURL } from 'src/environments/environment';

describe('StructuresService', () => {
  let service: StructuresService;
  let httpClient: HttpClient;
  let httpTestingController: HttpTestingController;
  let scheduler: TestScheduler;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports:[HttpClientTestingModule]
    });
    service = TestBed.inject(StructuresService);
    httpClient = TestBed.inject(HttpClient);
    httpTestingController = TestBed.inject(HttpTestingController);
  });

  beforeEach(() => scheduler = new TestScheduler((actual, expected) => {
    expect(actual).toEqual(expected);
  }));

  it('should be created', () => {
    expect(service).toBeTruthy();
  });

  it('should get filtered structures', () => {
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
    service.getFilteredStructures('0', '0', '1', '2').subscribe();
    const req = httpTestingController.expectOne(apiURL + "/mim/branch/" + 0 + "/messages/" + 1 + "/submessages/" + 2 + "/structures/filter/" + 0);
    expect(req.request.method).toEqual('GET');
    req.flush(testData);
    httpTestingController.verify();
  })
});
