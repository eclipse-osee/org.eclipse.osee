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
import { HttpClientTestingModule, HttpTestingController } from '@angular/common/http/testing';
import { TestBed } from '@angular/core/testing';
import { apiURL } from 'src/environments/environment';
import { messageBranch } from '../../shared/types/branches';

import { BranchListService } from './branch-list.service';
import { RouteStateService } from './route-state-service.service';

describe('BranchListService', () => {
  let service: BranchListService;
  let routeService: RouteStateService;
  let httpTestingController: HttpTestingController;

  beforeEach(() => {
    TestBed.configureTestingModule({imports:[HttpClientTestingModule]});
    service = TestBed.inject(BranchListService);
    routeService = TestBed.inject(RouteStateService);
    httpTestingController = TestBed.inject(HttpTestingController);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
  describe('Core Functionality', () => {
    describe('branches observable',()=> {

      it('should call for baseline branches when set to product line', () => {
        let testData: messageBranch[] = [];
        routeService.branchType = "product line";
        service.branches.subscribe();
        const req = httpTestingController.expectOne(apiURL + "/orcs/branches/"+"baseline");
        expect(req.request.method).toEqual('GET');
        req.flush(testData);
        httpTestingController.verify();
      });
    
      it('should call for working branches when set to working', () => {
        let testData: messageBranch[] = [];
        routeService.branchType = "working";
        service.branches.subscribe();
        const req = httpTestingController.expectOne(apiURL + "/orcs/branches/"+"working");
        expect(req.request.method).toEqual('GET');
        req.flush(testData);
        httpTestingController.verify();
      });
    })
  })
});
