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
import {
	HttpClient,
	HttpParams,
	httpResource,
	HttpResourceRef,
} from '@angular/common/http';
import { Injectable, Signal, inject } from '@angular/core';
import { UiService } from '@osee/shared/services';
import { Observable, catchError, throwError } from 'rxjs';
import { apiURL } from '@osee/environments';
import type { DropdownApiItem, FormState } from './dispatch.types';

@Injectable({
	providedIn: 'root',
})
export class DispatchHttpService {
	private readonly http = inject(HttpClient);
	private readonly uiService = inject(UiService);

	/**
	 * Resource that converts a tab's markdown instructions to preview HTML.
	 * Read-only text endpoint — exposed as a resource so the component can bind
	 * to its value signal without touching HttpClient/httpResource directly.
	 */
	getInstructionsPreviewResource(
		markdown: Signal<string>
	): HttpResourceRef<string | undefined> {
		return httpResource.text(() => ({
			url: apiURL + '/define/word/convertMarkdownToHtmlPreview',
			method: 'POST' as const,
			body: markdown(),
			headers: { 'Content-Type': 'text/plain' },
		}));
	}

	/**
	 * Fetches dropdown options from a fully-resolved content API URL.
	 * Used by the tab's multi-dropdown loader, which fans out over a dynamic
	 * number of these calls, so this returns a plain typed Observable rather
	 * than a resource.
	 */
	getDropdownOptions(url: string): Observable<DropdownApiItem[]> {
		return this.http.get<DropdownApiItem[]>(url);
	}

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

	executePut(url: string): Observable<string> {
		return this.http
			.put(url, null, {
				responseType: 'text' as const,
			})
			.pipe(
				catchError((error) => {
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
