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

import { PlConfigTypesService } from './pl-config-types.service';

describe('PlConfigTypesService', () => {
  let service: PlConfigTypesService;
  let httpTestingController: HttpTestingController;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports:[HttpClientTestingModule]
    });
    service = TestBed.inject(PlConfigTypesService);
    httpTestingController = TestBed.inject(HttpTestingController);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });

  it('should get product applicability types', () => {
    service.productApplicabilityTypes.subscribe();
    const req = httpTestingController.expectOne(apiURL + '/orcs/types/productApplicability');
    expect(req.request.method).toEqual('GET');
    req.flush({});
    httpTestingController.verify();
  })
});
