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
import { of } from 'rxjs';
import { transactionInfoMock } from '@osee/transaction-history/testing';
import { TransactionHistoryService } from '@osee/transaction-history/services';

export const transactionInfoServiceMock: Partial<TransactionHistoryService> = {
	getTransaction(id: string | number) {
		return of(transactionInfoMock);
	},

	getLatestBranchTransaction(branchId: string) {
		return of(transactionInfoMock);
	},
};
