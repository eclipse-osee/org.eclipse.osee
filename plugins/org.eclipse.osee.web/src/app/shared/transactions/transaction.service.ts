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
import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { apiURL } from 'src/environments/environment';
import {
	transactionInfo,
	transactionResult,
} from '@osee/shared/types/change-report';
import { transaction } from '../types/transaction';

@Injectable({
	providedIn: 'root',
})
export class TransactionService {
	constructor(private http: HttpClient) {}

	getTransaction(id: string | number) {
		return this.http.get<transactionInfo>(apiURL + '/orcs/txs/' + id);
	}

	getLatestBranchTransaction(branchId: string) {
		return this.http.get<transactionInfo>(
			apiURL + '/orcs/branches/' + branchId + '/txs/latest'
		);
	}

	performMutation(body: transaction) {
		return this.http.post<transactionResult>(apiURL + '/orcs/txs', body);
	}
}
