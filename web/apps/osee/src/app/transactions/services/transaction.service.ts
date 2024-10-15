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
import { HttpClient } from '@angular/common/http';
import { Injectable, inject } from '@angular/core';
import { apiURL } from '@osee/environments';
import { legacyTransaction, transactionResult } from '@osee/transactions/types';

@Injectable({
	providedIn: 'root',
})
export class TransactionService {
	private http = inject(HttpClient);

	performMutation(body: legacyTransaction) {
		return this.http.post<transactionResult>(apiURL + '/orcs/txs', body);
	}
}
