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

import { BranchUIService } from './branch-ui.service';

describe('BranchUIService', () => {
  let service: BranchUIService;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(BranchUIService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});
