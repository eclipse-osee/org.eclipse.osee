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
import { messageTypesServiceMock } from '@osee/messaging/shared/testing';
import { MessageTypesService } from './message-types.service';

import { CurrentMessageTypesService } from './current-message-types.service';
import { TransactionService } from '@osee/transactions/services';
import { transactionServiceMock } from '@osee/transactions/services/testing';

describe('CurrentMessageTypesService', () => {
	let service: CurrentMessageTypesService;

	beforeEach(() => {
		TestBed.configureTestingModule({
			providers: [
				{
					provide: MessageTypesService,
					useValue: messageTypesServiceMock,
				},
				{
					provide: TransactionService,
					useValue: transactionServiceMock,
				},
			],
		});
		service = TestBed.inject(CurrentMessageTypesService);
	});

	it('should be created', () => {
		expect(service).toBeTruthy();
	});
});
