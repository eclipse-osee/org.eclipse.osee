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
import { BranchInfoService } from '../http/branch-info.service';
import { BranchInfoServiceMock } from '../http/branch-info.service.mock';

import { CurrentBranchInfoService } from './current-branch-info.service';

describe('CurrentBranchInfoService', () => {
  let service: CurrentBranchInfoService;

  beforeEach(() => {
    TestBed.configureTestingModule({
      providers:[{ provide: BranchInfoService, useValue: BranchInfoServiceMock },]
    });
    service = TestBed.inject(CurrentBranchInfoService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});
