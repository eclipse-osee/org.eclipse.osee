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

import { CurrentMessagePeriodicityService } from './current-message-periodicity.service';
import { TransactionService } from '@osee/transactions/services';
import { transactionServiceMock } from '@osee/transactions/services/testing';
import { messagePeriodicityServiceMock } from '@osee/messaging/message-periodicity/services/testing';
import { MessagePeriodicityService } from '@osee/messaging/message-periodicity/services';

describe('CurrentMessagePeriodicityService', () => {
	let service: CurrentMessagePeriodicityService;

	beforeEach(() => {
		TestBed.configureTestingModule({
			providers: [
				{
					provide: TransactionService,
					useValue: transactionServiceMock,
				},
				{
					provide: MessagePeriodicityService,
					useValue: messagePeriodicityServiceMock,
				},
			],
		});
		service = TestBed.inject(CurrentMessagePeriodicityService);
	});

	it('should be created', () => {
		expect(service).toBeTruthy();
	});
});
