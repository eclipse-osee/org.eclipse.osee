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
import { Component } from '@angular/core';
import { TestBed } from '@angular/core/testing';
import { RouterTestingModule } from '@angular/router/testing';

import { RoutingService } from './routing.service';

@Component({
  selector: 'dummy-comp',
  template:'<p>Dummy</p>'
})
export class DummyComponent { }

describe('RoutingService', () => {
  let service: RoutingService;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports:[RouterTestingModule.withRoutes(
        [
          { path: ':branchType/:branchId/typeSearch',component:DummyComponent },
          { path: ':branchType/typeSearch',component:DummyComponent },
          { path: 'typeSearch',component:DummyComponent },
        ]
      )]
    });
    service = TestBed.inject(RoutingService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });

  it('should set base url to "working/product line" when type = "working/product line"', () => {
    expect(service.type).toEqual("");
    service.type = 'product line';
    service.type = 'working';
    expect(service.type).toEqual("working");
  });

  it('should set base url to "working/product line /id" when type = "working/product line /id"', () => {
    expect(service.type).toEqual("");
    expect(service.id).toEqual("");
    service.id = '8';
    expect(service.id).toEqual("8");
    service.type = 'product line';
    service.type = 'working';
    expect(service.type).toEqual("working");
  });
});
