import { HttpClientTestingModule, HttpTestingController } from '@angular/common/http/testing';
import { TestBed } from '@angular/core/testing';
import { testDataUser } from 'src/app/ple/plconfig/testing/mockTypes';
import { apiURL } from 'src/environments/environment';

import { MimPreferencesService } from './mim-preferences.service';

describe('MimPreferencesService', () => {
  let service: MimPreferencesService;
  let httpTestingController: HttpTestingController;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports:[HttpClientTestingModule]
    });
    service = TestBed.inject(MimPreferencesService);
    httpTestingController = TestBed.inject(HttpTestingController);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });

  it('should get user prefs', () => {
    service.getUserPrefs('10', testDataUser).subscribe();
    const req = httpTestingController.expectOne(apiURL + '/mim/user/' + 10);
    expect(req.request.method).toEqual('GET');
    req.flush({});
    httpTestingController.verify();
  })

  it('should get branch prefs', () => {
    service.getBranchPrefs(testDataUser).subscribe();
    const req = httpTestingController.expectOne(apiURL + '/mim/user/branches');
    expect(req.request.method).toEqual('GET');
    req.flush({});
    httpTestingController.verify();
  })
});
