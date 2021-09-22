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
import { TestBed } from '@angular/core/testing';
import { PlConfigActionService } from './pl-config-action.service';
import { PlConfigBranchService } from './pl-config-branch-service.service';
import { PlConfigCurrentBranchService } from './pl-config-current-branch.service';


describe('PlConfigCurrentBranchService', () => {
  let service: PlConfigCurrentBranchService;
  let branchServiceSpy: jasmine.SpyObj<PlConfigBranchService>;
  let actionServiceSpy:jasmine.SpyObj<PlConfigActionService>;

  beforeEach(() => {
    branchServiceSpy = jasmine.createSpyObj(
      'PlConfigBranchService',
      [ //functions required to test
        'getBranchApplicability',
        'getAction',
        'getBranchState',
        'modifyConfiguration',
        'synchronizeGroup',
        'addFeature',
        'modifyFeature',
        'deleteFeature',
        'commitBranch'
      ]
    );
    actionServiceSpy = jasmine.createSpyObj(
      'PlConfigActionService',
      [ //functions required to test
        'getAction',
        'getWorkFlow'
      ]
    );
    TestBed.configureTestingModule({
      providers: [
        { provide: PlConfigBranchService, useValue: branchServiceSpy },
        { provide: PlConfigActionService, useValue: actionServiceSpy },
      ]
    });
    service = TestBed.inject(PlConfigCurrentBranchService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
  // it('#branchApplicability should return expected branch applicability', done => {
  //   const expectedBranchApplicability = testBranchApplicability;
  //   branchServiceSpy.getBranchApplicability.and.returnValue(of(expectedBranchApplicability))
  //   service.branchApplicability.subscribe((value) => {
  //     expect(value).toEqual(testBranchApplicability);
  //     done();
  //   })
  // })
});
