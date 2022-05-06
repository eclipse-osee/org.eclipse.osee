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
import { TestBed } from '@angular/core/testing';
import { TestScheduler } from 'rxjs/testing';
import { BranchUIService } from 'src/app/ple-services/ui/branch/branch-ui.service';

import { ReportsService } from './reports.service';

describe('ReportsService', () => {
  let service: ReportsService;
  let scheduler: TestScheduler;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(ReportsService);
  });

  beforeEach(() => scheduler = new TestScheduler((actual, expected) => {
    expect(actual).toEqual(expected);
  }));

  it('should be created', () => {
    expect(service).toBeTruthy();
  });

  it('should get the difference report route', () => {
    scheduler.run(() => {
      service.BranchId = '10';
      service.BranchType = 'working';
      let expectedObservable = { a: "/ple/messaging/reports/working/10/differences" };
      let expectedMarble = '(a)';
      scheduler.expectObservable(service.diffReportRoute).toBe(expectedMarble, expectedObservable);
    })
  })

});
