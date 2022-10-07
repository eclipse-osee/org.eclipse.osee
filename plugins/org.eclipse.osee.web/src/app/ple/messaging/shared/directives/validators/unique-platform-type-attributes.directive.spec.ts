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
import { QueryServiceMock } from '../../mocks/query.service.mock';
import { QueryService } from '../../services/http/query.service';
import { CurrentQueryService } from '../../services/ui/current-query.service';
import { UniquePlatformTypeAttributesDirective } from './unique-platform-type-attributes.directive';

describe('UniquePlatformTypeAttributesDirective', () => {

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      providers: [
        { provide: QueryService, useValue: QueryServiceMock },
      ],
    }).compileComponents();

  });
  it('should create an instance', () => {
    const directive = new UniquePlatformTypeAttributesDirective(TestBed.inject(CurrentQueryService));
    expect(directive).toBeTruthy();
  });
});
