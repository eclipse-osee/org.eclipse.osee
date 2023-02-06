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
import { TestScheduler } from 'rxjs/testing';
import { transactionResultMock } from '@osee/shared/transactions/testing';
import { BranchTransactionService } from '../http/branch-transaction.service';
import { branchTransactionServiceMock } from '../http/branch-transaction.service.mock';

import { CurrentBranchTransactionService } from './current-branch-transaction.service';

describe('CurrentBranchTransactionService', () => {
	let service: CurrentBranchTransactionService;
	let scheduler: TestScheduler;

	beforeEach(() => {
		TestBed.configureTestingModule({
			providers: [
				{
					provide: BranchTransactionService,
					useValue: branchTransactionServiceMock,
				},
			],
		});
		service = TestBed.inject(CurrentBranchTransactionService);
	});
	beforeEach(
		() =>
			(scheduler = new TestScheduler((actual, expected) => {
				expect(actual).toEqual(expected);
			}))
	);
	it('should be created', () => {
		expect(service).toBeTruthy();
	});

	it('should undo latest transaction', () => {
		scheduler.run(({ expectObservable }) => {
			const expectedValues = { a: transactionResultMock, b: undefined };
			expectObservable(service.undoLatest).toBe('(a|)', expectedValues);
		});
	});
});
