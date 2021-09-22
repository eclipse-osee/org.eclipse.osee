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

import { PlConfigTypesService } from './pl-config-types.service';

describe('PlConfigTypesService', () => {
  let service: PlConfigTypesService;
  let httpClientSpy: jasmine.SpyObj<HttpClient>;

  beforeEach(() => {
    httpClientSpy = jasmine.createSpyObj('HttpClient', ['get', 'put', 'post']);
    TestBed.configureTestingModule({
      providers: [
        {provide: HttpClient, useValue:httpClientSpy}
      ]
    });
    service = TestBed.inject(PlConfigTypesService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});
