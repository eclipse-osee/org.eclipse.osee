/*********************************************************************
 * Copyright (c) 2021 Boeing
 *
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     Boeing - initial API and implementation
 **********************************************************************/
import { HttpClient } from '@angular/common/http';
import { TestBed } from '@angular/core/testing';
import { of } from 'rxjs';
import { testBranchApplicability, testBranchInfo, testBranchListing } from '../testing/mockBranchService';

import { PlConfigBranchService } from './pl-config-branch-service.service';

describe('PlConfigBranchService', () => {
  let service: PlConfigBranchService;
  let httpClientSpy: jasmine.SpyObj<HttpClient>;

  beforeEach(() => {
    httpClientSpy = jasmine.createSpyObj('HttpClient', ['get', 'put', 'post']);
    TestBed.configureTestingModule({
      providers: [
      {provide: HttpClient, useValue:httpClientSpy}
    ]});
    service = TestBed.inject(PlConfigBranchService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
    it('#getBranchApplicability should return expected branch applicability', done => {
      const expectedBranchApplicability = testBranchApplicability;
      httpClientSpy.get.and.returnValue(of(expectedBranchApplicability));
      service.getBranchApplicability(0).subscribe((value) => {
        expect(value).toEqual(testBranchApplicability);
        done();
      })  
    })
    it('#getBranches should return expected branch listing', done => {
      const expectedBranches=testBranchListing;
      httpClientSpy.get.and.returnValue(of(expectedBranches));
      service.getBranches('all').subscribe((value) => {
        expect(value).toEqual(testBranchListing);
        done();
      })  
    })
    it('#getBranchState should return expected branch state', done => {
      const expectedBranch=testBranchInfo;
      httpClientSpy.get.and.returnValue(of(expectedBranch));
      service.getBranchState('5461321').subscribe((value) => {
        expect(value).toEqual(testBranchInfo);
        done();
      })  
    })
});
