/*********************************************************************
 * Copyright (c) 2023 Boeing
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
import { apiURL } from '@osee/environments';
import { ConnectionValidationResult } from '@osee/messaging/shared/types';

@Injectable({
	providedIn: 'root',
})
export class ValidationService {
	constructor(private http: HttpClient) {}

	getConnectionValidation(
		branchId: string,
		connectionId: string,
		viewId: string
	) {
		return this.http.get<ConnectionValidationResult>(
			`${apiURL}/mim/branch/${branchId}/validation/connection/${connectionId}`,
			{
				params: {
					viewId: viewId !== '' ? viewId : '-1',
				},
			}
		);
	}
}
