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

import { CurrentStructureCategoriesService } from './current-structure-categories.service';
import { TransactionService } from '@osee/transactions/services';
import { transactionServiceMock } from '@osee/transactions/services/testing';
import { StructureCategoriesService } from './structure-categories.service';
import { structureCategoriesServiceMock } from '@osee/messaging/structure-category/services/testing';

describe('CurrentStructureCategoriesService', () => {
	let service: CurrentStructureCategoriesService;

	beforeEach(() => {
		TestBed.configureTestingModule({
			providers: [
				{
					provide: StructureCategoriesService,
					useValue: structureCategoriesServiceMock,
				},
				{
					provide: TransactionService,
					useValue: transactionServiceMock,
				},
			],
		});
		service = TestBed.inject(CurrentStructureCategoriesService);
	});

	it('should be created', () => {
		expect(service).toBeTruthy();
	});
});
