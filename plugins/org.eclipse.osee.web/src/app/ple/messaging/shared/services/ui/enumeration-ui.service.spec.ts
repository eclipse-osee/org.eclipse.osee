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
import { UiService } from '../../../../../ple-services/ui/ui.service';
import { transactionResultMock } from '../../../../../transactions/transaction.mock';
import { enumerationSetServiceMock } from '../../mocks/enumeration.set.service.mock';
import { EnumerationSetService } from '../http/enumeration-set.service';

import { EnumerationUIService } from './enumeration-ui.service';

describe('EnumerationUIService', () => {
  let service: EnumerationUIService;
  let uiService: UiService;
  let scheduler: TestScheduler;

  beforeEach(() => {
    TestBed.configureTestingModule({
      providers:[{provide:EnumerationSetService, useValue: enumerationSetServiceMock}]
    });
    service = TestBed.inject(EnumerationUIService);
    uiService = TestBed.inject(UiService);
  });

  beforeEach(() => scheduler = new TestScheduler((actual, expected) => {
    expect(actual).toEqual(expected);
  }));

  it('should be created', () => {
    expect(service).toBeTruthy();
  });

  it('should change an enum set', () => {
    scheduler.run(() => {
      const expectedFilterValues = { a: transactionResultMock };
      const expectedMarble = '(a|)';
      uiService.idValue = '10';
      scheduler.expectObservable(service.changeEnumSet({ name: '', applicability: { id: '1', name: 'Base' }, description: '' },[])).toBe(expectedMarble, expectedFilterValues);
    })
  })
});
