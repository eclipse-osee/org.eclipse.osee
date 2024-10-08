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
import { CiSetsService } from './ci-sets.service';
import { CiSetsHttpService } from './ci-sets-http.service';
import { ciSetsHttpServiceMock } from './ci-sets-http.service.mock';
import { CurrentTransactionService } from '@osee/transactions/services';
import { currentTransactionServiceMock } from '@osee/transactions/services/testing';

describe('CiSetsService', () => {
	let service: CiSetsService;

	beforeEach(() => {
		TestBed.configureTestingModule({
			providers: [
				{ provide: CiSetsHttpService, useValue: ciSetsHttpServiceMock },
				{
					provide: CurrentTransactionService,
					useValue: currentTransactionServiceMock,
				},
			],
		});
		service = TestBed.inject(CiSetsService);
	});

	it('should be created', () => {
		expect(service).toBeTruthy();
	});
});
