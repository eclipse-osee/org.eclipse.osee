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
import { UnitsService } from './units.service';
import { unitsServiceMock } from '@osee/messaging/units/services/testing';

import { CurrentUnitsService } from './current-units.service';
import { TransactionService } from '@osee/transactions/services';
import { transactionServiceMock } from '@osee/transactions/services/testing';

describe('CurrentUnitsService', () => {
	let service: CurrentUnitsService;

	beforeEach(() => {
		TestBed.configureTestingModule({
			providers: [
				{
					provide: UnitsService,
					useValue: unitsServiceMock,
				},
				{
					provide: TransactionService,
					useValue: transactionServiceMock,
				},
			],
		});
		service = TestBed.inject(CurrentUnitsService);
	});

	it('should be created', () => {
		expect(service).toBeTruthy();
	});
});
