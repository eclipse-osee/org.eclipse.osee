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
import { platformTypesMock } from '../../shared/mocks/PlatformTypes.mock';

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
