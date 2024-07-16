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
import type { DifferenceReport } from '@osee/messaging/shared/types';
import { apiURL } from '@osee/environments';

@Injectable({
	providedIn: 'root',
})
export class DiffReportHttpService {
	constructor(private http: HttpClient) {}

	getDifferenceReport(
		sourceBranch: string | number,
		destBranch: string | number
	) {
		return this.http.get<DifferenceReport>(
			apiURL + '/mim/branch/' + sourceBranch + '/diff/' + destBranch
		);
	}
}
