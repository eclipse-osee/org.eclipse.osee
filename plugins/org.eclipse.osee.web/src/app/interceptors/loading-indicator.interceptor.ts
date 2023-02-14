/*********************************************************************
 * Copyright (c) 2021 Boeing
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
import { HttpRequest, HttpHandlerFn, HttpResponse } from '@angular/common/http';
import { inject } from '@angular/core';
import { finalize, tap } from 'rxjs';
import { HttpLoadingService } from '../services/http-loading.service';

let requests: HttpRequest<any>[] = [];
export const LoadingIndicatorInterceptor = (
	req: HttpRequest<unknown>,
	next: HttpHandlerFn
) => {
	requests.push(req);
	const loadingService = inject(HttpLoadingService);
	loadingService.loading = true;
	return next(req).pipe(
		tap(
			(event) => {
				if (event instanceof HttpResponse) {
					removeRequest(req, loadingService);
				}
			},
			(error) => {
				alert('Request ' + req.url + ' returned an error.');
				removeRequest(req, loadingService);
			}
		),
		finalize(() => {
			removeRequest(req, loadingService);
		})
	);
};
function removeRequest(
	req: HttpRequest<any>,
	loadingService: HttpLoadingService
) {
	const index = requests.indexOf(req);
	if (index >= 0) {
		requests.splice(index, 1);
	}
	loadingService.loading = requests.length > 0;
}
