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
import { transactionMock, transactionResultMock } from 'src/app/transactions/transaction.mock';
import { typesServiceMock } from '../../mocks/types.service.mock';
import { TypesService } from '../http/types.service';

import { TypesUIService } from './types-ui.service';

describe('TypesUIService', () => {
  let service: TypesUIService;
  let scheduler: TestScheduler;

  beforeEach(() => {
    TestBed.configureTestingModule({
      providers:[{provide: TypesService, useValue: typesServiceMock}]
    });
    service = TestBed.inject(TypesUIService);
  });
  beforeEach(() => scheduler = new TestScheduler((actual, expected) => {
    expect(actual).toEqual(expected);
  }));
  it('should be created', () => {
    expect(service).toBeTruthy();
  });

  it('should create a transaction', () => {
    scheduler.run(({ expectObservable }) => {
      expectObservable(service.changeType({})).toBe('(a|)',{a:transactionMock})
    })
  })

  it('should perform a mutation', () => {
    scheduler.run(({ expectObservable }) => {
      expectObservable(service.performMutation(transactionMock)).toBe('(a|)',{a:transactionResultMock})
    })
  })
});
