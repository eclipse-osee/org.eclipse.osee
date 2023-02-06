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
import { transaction } from '../transaction';
import { transactionInfoMock, transactionResultMock } from './transaction.mock';
import { TransactionService } from '../transaction.service';

export const transactionServiceMock: Partial<TransactionService> = {
	getTransaction(id: string | number) {
		return of(transactionInfoMock);
	},

	getLatestBranchTransaction(branchId: string) {
		return of(transactionInfoMock);
	},

	performMutation(body: transaction) {
		return of(transactionResultMock);
	},
};
