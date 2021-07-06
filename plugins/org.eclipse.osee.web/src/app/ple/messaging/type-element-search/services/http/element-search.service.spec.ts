import { HttpClient } from '@angular/common/http';
import { HttpClientTestingModule, HttpTestingController } from '@angular/common/http/testing';
import { TestBed } from '@angular/core/testing';
import { apiURL } from 'src/environments/environment';
import { element } from '../../types/element';

import { ElementSearchService } from './element-search.service';

describe('ElementSearchService', () => {
  let service: ElementSearchService;
  let httpClient: HttpClient;
  let httpTestingController: HttpTestingController;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports:[HttpClientTestingModule]
    });
    service = TestBed.inject(ElementSearchService);
    httpClient = TestBed.inject(HttpClient);
    httpTestingController = TestBed.inject(HttpTestingController);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });

  it('should query for elements relating to an id', () => {
    let testData: element[] = [];
    service.getFilteredElements('8', '10').subscribe();
    const req = httpTestingController.expectOne(apiURL+'/mim/branch/'+'8/'+'elements/getType/'+'10');
    expect(req.request.method).toEqual('GET');
    req.flush(testData);
    httpTestingController.verify();
  });
});
