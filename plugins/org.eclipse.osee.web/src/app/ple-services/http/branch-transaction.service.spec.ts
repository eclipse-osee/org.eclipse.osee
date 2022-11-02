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
import {
	HttpClientTestingModule,
	HttpTestingController,
} from '@angular/common/http/testing';
import { TestBed } from '@angular/core/testing';
import { apiURL } from '../../../environments/environment';

import { BranchTransactionService } from './branch-transaction.service';

describe('BranchTransactionService', () => {
	let service: BranchTransactionService;
	let httpTestingController: HttpTestingController;

	beforeEach(() => {
		TestBed.configureTestingModule({
			imports: [HttpClientTestingModule],
		});
		service = TestBed.inject(BranchTransactionService);
		httpTestingController = TestBed.inject(HttpTestingController);
	});

	it('should be created', () => {
		expect(service).toBeTruthy();
	});

	it('should undo a transaction', () => {
		service.undoLatest('10').subscribe();
		const req = httpTestingController.expectOne(
			apiURL + '/orcs/branches/' + 10 + '/undo/'
		);
		expect(req.request.method).toEqual('DELETE');
		req.flush({});
		httpTestingController.verify();
	});
});
