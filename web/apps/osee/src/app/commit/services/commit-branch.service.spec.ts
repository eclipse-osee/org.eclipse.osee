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
import { CommitBranchService } from './commit-branch.service';
import { BranchInfoService } from '@osee/shared/services';
import { BranchInfoServiceMock } from '@osee/shared/testing';
import { TransactionService } from '@osee/shared/transactions';
import { transactionServiceMock } from '@osee/shared/transactions/testing';
import { UserDataAccountService } from '@osee/auth';
import { userDataAccountServiceMock } from '@osee/auth/testing';

describe('CommitBranchService', () => {
	let service: CommitBranchService;

	beforeEach(() => {
		TestBed.configureTestingModule({
			providers: [
				{
					provide: BranchInfoService,
					useValue: BranchInfoServiceMock,
				},
				{
					provide: TransactionService,
					useValue: transactionServiceMock,
				},
				{
					provide: UserDataAccountService,
					useValue: userDataAccountServiceMock,
				},
			],
		});
		service = TestBed.inject(CommitBranchService);
	});

	it('should be created', () => {
		expect(service).toBeTruthy();
	});
});
