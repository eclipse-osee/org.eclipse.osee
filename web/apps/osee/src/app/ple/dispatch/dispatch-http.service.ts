/*********************************************************************
 * Copyright (c) 2026 Boeing
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
import { HttpClient, HttpParams } from '@angular/common/http';
import { Injectable, inject } from '@angular/core';
import { UiService } from '@osee/shared/services';
import { Observable, catchError, throwError } from 'rxjs';
import type { FormState } from './dispatch.types';

@Injectable({
	providedIn: 'root',
})
export class DispatchHttpService {
	private readonly http = inject(HttpClient);
	private readonly uiService = inject(UiService);

	executeGet(url: string, params: HttpParams): Observable<string> {
		return this.http
			.get(url, {
				params,
				responseType: 'text' as const,
			})
			.pipe(
				catchError((error) => {
					// Surface the error and re-throw so callers can distinguish
					// a failed request from a genuinely empty response body.
					this.uiService.ErrorText = `Request failed: ${error.message}`;
					return throwError(() => error);
				})
			);
	}

	executePost(url: string, body: FormState): Observable<string> {
		return this.http
			.post(url, body, {
				responseType: 'text' as const,
			})
			.pipe(
				catchError((error) => {
					// Surface the error and re-throw so callers can distinguish
					// a failed request from a genuinely empty response body.
					this.uiService.ErrorText = `Request failed: ${error.message}`;
					return throwError(() => error);
				})
			);
	}

	executePostWithFiles(
		url: string,
		body: FormState,
		files: Readonly<Record<string, readonly File[]>>
	): Observable<string> {
		const formData = new FormData();
		formData.append('data', JSON.stringify(body));

		for (const [key, fileList] of Object.entries(files)) {
			for (const file of fileList) {
				formData.append(key, file, file.name);
			}
		}

		return this.http
			.post(url, formData, {
				responseType: 'text' as const,
			})
			.pipe(
				catchError((error) => {
					// Surface the error and re-throw so callers can distinguish
					// a failed request from a genuinely empty response body.
					this.uiService.ErrorText = `Request failed: ${error.message}`;
					return throwError(() => error);
				})
			);
	}

	executePostRawFile(
		url: string,
		file: File,
		contentType: string
	): Observable<string> {
		return this.http
			.post(url, file, {
				headers: { 'Content-Type': contentType },
				responseType: 'text' as const,
			})
			.pipe(
				catchError((error) => {
					// Surface the error and re-throw so callers can distinguish
					// a failed request from a genuinely empty response body.
					this.uiService.ErrorText = `Request failed: ${error.message}`;
					return throwError(() => error);
				})
			);
	}
}
