/*********************************************************************
 * Copyright (c) 2023 Boeing
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
import { ratesServiceMock } from '@osee/messaging/shared/testing';
import { RatesService } from './rates.service';

import { CurrentRatesService } from './current-rates.service';
import { TransactionService } from '@osee/transactions/services';
import { transactionServiceMock } from '@osee/transactions/services/testing';

describe('CurrentRatesService', () => {
	let service: CurrentRatesService;

	beforeEach(() => {
		TestBed.configureTestingModule({
			providers: [
				{
					provide: RatesService,
					useValue: ratesServiceMock,
				},
				{
					provide: TransactionService,
					useValue: transactionServiceMock,
				},
			],
		});
		service = TestBed.inject(CurrentRatesService);
	});

	it('should be created', () => {
		expect(service).toBeTruthy();
	});
});
