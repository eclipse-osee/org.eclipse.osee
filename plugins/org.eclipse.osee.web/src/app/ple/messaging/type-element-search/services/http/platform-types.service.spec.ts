import { HttpClient } from '@angular/common/http';
import { HttpClientTestingModule, HttpTestingController } from '@angular/common/http/testing';
import { TestBed } from '@angular/core/testing';
import { apiURL } from 'src/environments/environment';
import { PlatformType } from '../../types/PlatformType';

import { PlatformTypesService } from './platform-types.service';

describe('PlatformTypesService', () => {
  let service: PlatformTypesService;
  let httpClient: HttpClient;
  let httpTestingController: HttpTestingController;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports:[HttpClientTestingModule]
    });
    service = TestBed.inject(PlatformTypesService);
    httpClient = TestBed.inject(HttpClient);
    httpTestingController = TestBed.inject(HttpTestingController);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });

  it('should query for platform types', () => {
    let testData: PlatformType[] = [];
    service.getFilteredTypes('filter', '8').subscribe();
    const req = httpTestingController.expectOne(apiURL + "/mim/branch/" + '8' + "/types/filter/" + 'filter');
    expect(req.request.method).toEqual('GET');
    req.flush(testData);
    httpTestingController.verify();
  });
});
