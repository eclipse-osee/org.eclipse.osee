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
import { TestScheduler } from 'rxjs/testing';

import { UiService } from './ui.service';

describe('UiService', () => {
  let service: UiService;
  let scheduler: TestScheduler;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(UiService);
  });

  beforeEach(() => scheduler = new TestScheduler((actual, expected) => {
    expect(actual).toEqual(expected);
  }));

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
  
  it('should set filter value', () => {
    scheduler.run(() => {
      const expectedfilterValues = { a: 'a', b: 'b' };
      const expectedMarble = 'a'
      scheduler.expectObservable(service.filter).toBe(expectedMarble, expectedfilterValues);
      service.filterString = "a";
      service.filterString = "a";
      service.filterString = "b";
      service.filterString = "a";
    })
  });
  
  it('should set branch value', () => {
    scheduler.run(() => {
      const expectedfilterValues = { a: '1', b: '2' };
      const expectedMarble = 'a'
      scheduler.expectObservable(service.BranchId).toBe(expectedMarble, expectedfilterValues);
      service.BranchIdString='1'
      service.BranchIdString='1'
      service.BranchIdString='2'
      service.BranchIdString='1'
    })
  });
  
  it('should set update value', () => {
    scheduler.run(({ cold }) => {
      const expectedfilterValues = { a: true, b:false };
      const expectedMarble = '-a'
      cold(expectedMarble).subscribe(() => service.updateMessages = true);
      scheduler.expectObservable(service.UpdateRequired).toBe(expectedMarble, expectedfilterValues);
    })
  })
});
