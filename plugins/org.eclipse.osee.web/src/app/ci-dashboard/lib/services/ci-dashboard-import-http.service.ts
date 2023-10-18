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
import { transactionResult } from '@osee/shared/types/change-report';

@Injectable({
	providedIn: 'root',
})
export class CiDashboardImportHttpService {
	constructor(private http: HttpClient) {}

	importFile(branchId: string, ciSetId: string, file: File) {
		const formData = new FormData();
		formData.append('file', new Blob([file]), file.name);
		if (file.name.endsWith('.zip')) {
			return this.importBatch(branchId, ciSetId, formData);
		}
		return this.importSingleFile(branchId, ciSetId, formData);
	}

	importSingleFile(branchId: string, ciSetId: string, formData: FormData) {
		return this.http.post<transactionResult>(
			apiURL + '/script/tmo/' + branchId + '/import/file/' + ciSetId,
			formData
		);
	}

	importBatch(branchId: string, ciSetId: string, formData: FormData) {
		return this.http.post<transactionResult>(
			apiURL + '/script/tmo/' + branchId + '/import/batch/' + ciSetId,
			formData
		);
	}
}
