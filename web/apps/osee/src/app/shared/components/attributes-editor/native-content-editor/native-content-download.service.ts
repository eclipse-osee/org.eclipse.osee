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
import { Injectable, inject } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { map } from 'rxjs/operators';
import { apiURL } from '@osee/environments';
import { ATTRIBUTETYPEIDENUM } from '@osee/attributes/constants';

@Injectable({ providedIn: 'root' })
export class NativeContentDownloadService {
	private readonly http = inject(HttpClient);

	/**
	 * Fetches the native content blob for a given artifact on a branch.
	 */
	fetchNativeContent(branchId: string, artifactId: string): Observable<Blob> {
		const url = `${apiURL}/orcs/branch/${branchId}/artifact/${artifactId}/attribute/type/${ATTRIBUTETYPEIDENUM.NATIVE_CONTENT}`;
		return this.http.get(url, { responseType: 'blob' });
	}

	/**
	 * Downloads the native content as a file with the given name.
	 */
	downloadNativeContent(
		branchId: string,
		artifactId: string,
		fileName: string
	): Observable<void> {
		return this.fetchNativeContent(branchId, artifactId).pipe(
			map((blob) => {
				const href = URL.createObjectURL(blob);
				const anchor = document.createElement('a');
				anchor.href = href;
				anchor.download = fileName;
				anchor.click();
				URL.revokeObjectURL(href);
			})
		);
	}
}
