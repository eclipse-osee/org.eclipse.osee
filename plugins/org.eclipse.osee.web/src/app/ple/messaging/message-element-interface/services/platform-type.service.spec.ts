import { HttpClientTestingModule, HttpTestingController } from '@angular/common/http/testing';
import { TestBed } from '@angular/core/testing';
import { apiURL } from 'src/environments/environment';
import { platformTypesMock } from '../mocks/ReturnObjects/PlatformTypes.mock';

import { PlatformTypeService } from './platform-type.service';

describe('PlatformTypeService', () => {
  let service: PlatformTypeService;
  let httpTestingController: HttpTestingController;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports:[HttpClientTestingModule]
    });
    service = TestBed.inject(PlatformTypeService);
    httpTestingController = TestBed.inject(HttpTestingController);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });

  it('should fetch a platform type', () => {
    service.getType('10', '10').subscribe();
    const req = httpTestingController.expectOne(apiURL + "/mim/branch/" + 10 + "/types/"+10);
    expect(req.request.method).toEqual('GET');
    req.flush(platformTypesMock[0]);
    httpTestingController.verify();
  })
});
