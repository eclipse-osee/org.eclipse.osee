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
import {
	HttpTestingController,
	provideHttpClientTesting,
} from '@angular/common/http/testing';
import { TestBed } from '@angular/core/testing';
import { apiURL } from '@osee/environments';
import { transactionInfoMock } from '@osee/transaction-history/testing';
import { transactionInfo } from '@osee/transaction-history/types';

import { provideHttpClient } from '@angular/common/http';
import { TransactionHistoryService } from './transaction-history.service';

describe('TransactionHistoryService', () => {
	let service: TransactionHistoryService;
	let httpTestingController: HttpTestingController;

	beforeEach(() => {
		TestBed.configureTestingModule({
			providers: [provideHttpClient(), provideHttpClientTesting()],
		});
		service = TestBed.inject(TransactionHistoryService);
		httpTestingController = TestBed.inject(HttpTestingController);
	});

	it('should be created', () => {
		expect(service).toBeTruthy();
	});

	it('should get transaction info', () => {
		const testInfo: transactionInfo = transactionInfoMock;
		service.getTransaction('10').subscribe();
		const req = httpTestingController.expectOne(apiURL + '/orcs/txs/' + 10);
		expect(req.request.method).toEqual('GET');
		req.flush(testInfo);
		httpTestingController.verify();
	});

	it('should get latest transaction info for branch', () => {
		service.getLatestBranchTransaction('10').subscribe();
		const req = httpTestingController.expectOne(
			apiURL + '/orcs/branches/' + 10 + '/txs/latest'
		);
		expect(req.request.method).toEqual('GET');
		req.flush(transactionInfoMock);
		httpTestingController.verify();
	});
});
