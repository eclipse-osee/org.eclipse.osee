/*********************************************************************
 * Copyright (c) 2024 Boeing
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

import { CurrentTransactionService } from './current-transaction.service';
import { TransactionService } from './transaction.service';
import { transactionServiceMock } from '@osee/transactions/services/testing';

describe('CurrentTransactionService', () => {
	let service: CurrentTransactionService;

	beforeEach(() => {
		TestBed.configureTestingModule({
			providers: [
				{
					provide: TransactionService,
					useValue: transactionServiceMock,
				},
			],
		});
		service = TestBed.inject(CurrentTransactionService);
	});

	it('should be created', () => {
		expect(service).toBeTruthy();
	});
});
