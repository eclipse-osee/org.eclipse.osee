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
import { transportTypeServiceMock } from '@osee/messaging/shared/testing';
import { TransportTypeService } from '@osee/messaging/shared/services';
import { CurrentTransportTypePageService } from './current-transport-type-page.service';
import { TransactionService } from '@osee/transactions/services';
import { transactionServiceMock } from '@osee/transactions/services/testing';

describe('CurrentTransportTypePageService', () => {
	let service: CurrentTransportTypePageService;

	beforeEach(() => {
		TestBed.configureTestingModule({
			providers: [
				{
					provide: TransportTypeService,
					useValue: transportTypeServiceMock,
				},
				{
					provide: TransactionService,
					useValue: transactionServiceMock,
				},
			],
		});
		service = TestBed.inject(CurrentTransportTypePageService);
	});

	it('should be created', () => {
		expect(service).toBeTruthy();
	});
});
