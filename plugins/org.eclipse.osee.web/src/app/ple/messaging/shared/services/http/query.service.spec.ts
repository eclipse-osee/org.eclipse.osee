/*********************************************************************
 * Copyright (c) 2022 Boeing
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
import { apiURL } from '../../../../../../environments/environment';
import { MimQuery, PlatformTypeQuery } from '../../types/MimQuery';

import { QueryService } from './query.service';

describe('QueryService', () => {
  let service: QueryService;
  let httpTestingController: HttpTestingController;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports:[HttpClientTestingModule]
    });
    service = TestBed.inject(QueryService);
    httpTestingController = TestBed.inject(HttpTestingController);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });

  it('should query for a platform type', () => {
    service.query('10',new PlatformTypeQuery()).subscribe();
    const req = httpTestingController.expectOne(apiURL+"/mim/branch/"+10+"/query");
    expect(req.request.method).toEqual('POST');
    expect(req.request.body).toEqual(new PlatformTypeQuery());
    req.flush({});
    httpTestingController.verify();
  })
});
