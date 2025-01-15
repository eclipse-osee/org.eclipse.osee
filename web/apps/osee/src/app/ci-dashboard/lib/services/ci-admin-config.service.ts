/*********************************************************************
 * Copyright (c) 2025 Boeing
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
import { inject, Injectable } from '@angular/core';
import { apiURL } from '@osee/environments';
import { CIConfig } from '../types/ci-config';
import { transactionResult } from '@osee/transactions/types';

@Injectable({
	providedIn: 'root',
})
export class CiAdminConfigService {
	private http = inject(HttpClient);

	getCiConfig(branchId: string) {
		return this.http.get<CIConfig>(`${apiURL}/script/config/${branchId}`);
	}

	createCiConfig(branchId: string) {
		return this.http.post<transactionResult>(
			`${apiURL}/script/config/${branchId}`,
			null
		);
	}
}
