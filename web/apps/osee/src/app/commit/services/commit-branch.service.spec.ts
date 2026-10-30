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
import { UserDataAccountService } from '@osee/auth';
import { userDataAccountServiceMock } from '@osee/auth/testing';
import { BranchInfoService, UiService } from '@osee/shared/services';
import {
	BranchInfoServiceMock,
	testCommitResponse,
} from '@osee/shared/testing';
import { TransactionService } from '@osee/transactions/services';
import { transactionServiceMock } from '@osee/transactions/services/testing';
import { CommitBranchService } from './commit-branch.service';
import { firstValueFrom, of } from 'rxjs';

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

	afterEach(() => vi.restoreAllMocks());

	it('should refresh after a successful commit', async () => {
		const uiService = TestBed.inject(UiService);
		const refresh = vi.spyOn(uiService, 'updated', 'set');

		const response = await firstValueFrom(service.commitBranch('10', '8'));

		expect(response.success).toBe(true);
		expect(refresh).toHaveBeenCalledWith(true);
	});

	it('should surface a failed commit without refreshing', async () => {
		vi.spyOn(
			TestBed.inject(BranchInfoService),
			'commitBranch'
		).mockReturnValue(of({ ...testCommitResponse, success: false }));
		const uiService = TestBed.inject(UiService);
		const refresh = vi.spyOn(uiService, 'updated', 'set');
		const error = vi.spyOn(uiService, 'ErrorText', 'set');

		await firstValueFrom(service.commitBranch('10', '8'));

		expect(refresh).not.toHaveBeenCalled();
		expect(error).toHaveBeenCalledWith('Error committing branch');
	});
});
