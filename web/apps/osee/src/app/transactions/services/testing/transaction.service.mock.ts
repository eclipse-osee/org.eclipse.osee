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
import { TransactionService } from '@osee/transactions/services';
import { transactionResultMock } from '@osee/transactions/testing';
import { legacyTransaction } from '@osee/transactions/types';
import { of } from 'rxjs';

export const transactionServiceMock: Partial<TransactionService> = {
	performMutation(body: legacyTransaction) {
		return of(transactionResultMock);
	},
};
