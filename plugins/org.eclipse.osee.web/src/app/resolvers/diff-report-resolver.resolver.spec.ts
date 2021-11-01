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
import { BranchInfoService } from '../ple-services/http/branch-info.service';
import { BranchInfoServiceMock } from '../ple-services/http/branch-info.service.mock';
import { DifferenceReportService } from '../ple-services/http/difference-report.service';
import { DifferenceReportServiceMock } from '../ple-services/http/difference-report.service.mock';

import { DiffReportResolver } from './diff-report-resolver.resolver';

describe('DiffReportResolverResolver', () => {
  let resolver: DiffReportResolver;

  beforeEach(() => {
    TestBed.configureTestingModule({
      providers: [
        { provide: BranchInfoService, useValue: BranchInfoServiceMock },
        { provide: DifferenceReportService,useValue:DifferenceReportServiceMock}
      ]
    });
    resolver = TestBed.inject(DiffReportResolver);
  });

  it('should be created', () => {
    expect(resolver).toBeTruthy();
  });
});
