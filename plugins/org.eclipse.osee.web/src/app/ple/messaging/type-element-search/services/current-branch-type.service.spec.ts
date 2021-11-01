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
import { of } from 'rxjs';
import { BranchListing } from '../../../../types/branches/BranchListing';

import { CurrentBranchTypeService } from './current-branch-type.service';
import { BranchService } from './http/branch.service';
import { BranchTypeService } from './router/branch-type.service';

describe('CurrentBranchTypeService', () => {
  let service: CurrentBranchTypeService;
  let typeService: BranchTypeService;
  let branchSpy: jasmine.SpyObj<BranchService> = jasmine.createSpyObj('BranchService', ['getBranches']);
  
  beforeEach(() => {
    TestBed.configureTestingModule({
      providers:[{provide:BranchService, useValue:branchSpy}]
    });
    service = TestBed.inject(CurrentBranchTypeService);
    typeService = TestBed.inject(BranchTypeService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });

  it('should call get branches with type working', () => {
    service.branches.subscribe();
    typeService.type = 'working';
    expect(branchSpy.getBranches).toHaveBeenCalledWith('working');
  });

  it('should call get branches with type baseline', () => {
    service.branches.subscribe();
    typeService.type = 'product line';
    expect(branchSpy.getBranches).toHaveBeenCalledWith('baseline');
  });

  // it('should not call get branches', () => {
  //   service.branches.subscribe();
  //   expect(() => { typeService.type = 'asdf' }).toThrow(new Error('Type is not a valid value. Invalid Value:' + 'asdf' + ' Valid values: product line,working'));
  //   console.log(typeService.type);
  //   expect(branchSpy.getBranches).not.toHaveBeenCalled()
  // });
});
