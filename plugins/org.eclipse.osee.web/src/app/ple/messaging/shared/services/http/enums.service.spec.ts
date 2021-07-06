import { HttpClientTestingModule, HttpTestingController } from '@angular/common/http/testing';
import { TestBed } from '@angular/core/testing';
import { apiURL } from 'src/environments/environment';

import { EnumsService } from './enums.service';

describe('EnumsService', () => {
  let service: EnumsService;
  let httpTestingController: HttpTestingController;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports:[HttpClientTestingModule]
    });
    service = TestBed.inject(EnumsService);
    httpTestingController = TestBed.inject(HttpTestingController);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });

  it('should fetch message rates', () => {
    let testData = ['r1', 'r2', 'r3'];
    service.rates.subscribe();
    const req = httpTestingController.expectOne(apiURL + "/mim/enums/" + 'MessageRates');
    expect(req.request.method).toEqual('GET');
    req.flush(testData);
    httpTestingController.verify();
  })

  it('should fetch message types', () => {
    let testData = ['t1', 't2', 't3'];
    service.types.subscribe();
    const req = httpTestingController.expectOne(apiURL + "/mim/enums/" + 'MessageTypes');
    expect(req.request.method).toEqual('GET');
    req.flush(testData);
    httpTestingController.verify();
  })

  it('should fetch message periodicities', () => {
    let testData = ['p1', 'p2', 'p3'];
    service.periodicities.subscribe();
    const req = httpTestingController.expectOne(apiURL + "/mim/enums/" + 'MessagePeriodicities');
    expect(req.request.method).toEqual('GET');
    req.flush(testData);
    httpTestingController.verify();
  })

  it('should fetch structure categories', () => {
    let testData = ['s1', 's2', 's3'];
    service.categories.subscribe();
    const req = httpTestingController.expectOne(apiURL + "/mim/enums/" + 'StructureCategories');
    expect(req.request.method).toEqual('GET');
    req.flush(testData);
    httpTestingController.verify();
  })
});
