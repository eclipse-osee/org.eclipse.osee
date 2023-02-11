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
import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { apiURL } from '../../../environments/environment';
import { transactionResult } from '@osee/shared/types/change-report';

@Injectable({
	providedIn: 'root',
})
export class BranchTransactionService {
	constructor(private http: HttpClient) {}

	undoLatest(branchId: string | number) {
		return this.http.delete<transactionResult>(
			apiURL + '/orcs/branches/' + branchId + '/undo/'
		);
	}
}
