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
import { Injectable, inject } from '@angular/core';
import { changeReportRow } from '@osee/shared/types/change-report';
import { apiURL } from '@osee/environments';

@Injectable({
	providedIn: 'root',
})
export class ChangeReportHttpService {
	private http = inject(HttpClient);

	getBranchChangeReport(branch1Id: string, branch2Id: string) {
		return this.http.get<changeReportRow[]>(
			`${apiURL}/orcs/branches/${branch1Id}/changes/${branch2Id}`
		);
	}

	getTxChangeReport(branchId: string, tx1: string, tx2: string) {
		return this.http.get<changeReportRow[]>(
			`${apiURL}/orcs/branches/${branchId}/changes/${tx1}/${tx2}`
		);
	}
}
