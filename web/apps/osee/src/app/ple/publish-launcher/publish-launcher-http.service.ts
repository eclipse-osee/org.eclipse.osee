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
import { Observable, catchError, of } from 'rxjs';
import type { FormState } from './publish-launcher.types';

@Injectable({
	providedIn: 'root',
})
export class PublishLauncherHttpService {
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
					this.uiService.ErrorText = `Request failed: ${error.message}`;
					return of('');
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
					this.uiService.ErrorText = `Request failed: ${error.message}`;
					return of('');
				})
			);
	}
}
