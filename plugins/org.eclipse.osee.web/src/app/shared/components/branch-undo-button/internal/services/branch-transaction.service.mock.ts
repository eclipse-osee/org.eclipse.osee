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
import { of } from 'rxjs';
import { transactionResultMock } from '@osee/shared/transactions/testing';
import { BranchTransactionService } from './branch-transaction.service';

export const branchTransactionServiceMock: Partial<BranchTransactionService> = {
	undoLatest(branchId: string) {
		return of(transactionResultMock);
	},
};
