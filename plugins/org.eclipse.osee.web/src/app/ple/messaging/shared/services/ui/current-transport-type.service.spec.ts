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
import { TransactionService } from '../../../../../transactions/transaction.service';
import { transactionServiceMock } from '../../../../../transactions/transaction.service.mock';
import { transportTypeServiceMock } from '../../mocks/transport-type.http.service.mock';
import { TransportTypeService } from '../http/transport-type.service';

import { CurrentTransportTypeService } from './current-transport-type.service';

describe('CurrentTransportTypeService', () => {
  let service: CurrentTransportTypeService;

  beforeEach(() => {
    TestBed.configureTestingModule({
      providers: [
        { provide: TransportTypeService, useValue: transportTypeServiceMock },
        { provide: TransactionService, useValue: transactionServiceMock }
      ]
    });
    service = TestBed.inject(CurrentTransportTypeService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});
