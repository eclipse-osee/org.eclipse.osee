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
import type { ImportSummary, ImportOption } from '@osee/messaging/shared/types';
import { shareReplay } from 'rxjs/operators';
import { apiURL } from '@osee/environments';

@Injectable({
	providedIn: 'root',
})
export class ImportHttpService {
	constructor(private http: HttpClient) {}

	getImportSummary(url: string, fileName: string, formData: FormData | File) {
		return this.http.post<ImportSummary>(apiURL + url, formData, {
			params: {
				fileName: fileName,
			},
		});
	}

	getImportOptions() {
		return this.http
			.get<ImportOption[]>(apiURL + '/mim/import')
			.pipe(shareReplay(1));
	}
}
