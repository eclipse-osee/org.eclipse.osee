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
import { HttpRequest, HttpHandlerFn } from '@angular/common/http';
import { inject } from '@angular/core';
import { UserDataAccountService } from '@osee/auth';
import { map, of, switchMap, take } from 'rxjs';
import { apiURL, OSEEAuthURL } from '@osee/environments';

export const OseeAuthInterceptor = (
	req: HttpRequest<unknown>,
	next: HttpHandlerFn
) => {
	const user = inject(UserDataAccountService).user;
	return user.pipe(
		take(1),
		switchMap((user) =>
			of(req).pipe(
				map((currentRequest) => {
					if (
						currentRequest.url.includes(apiURL) &&
						user !== undefined &&
						currentRequest.url !== OSEEAuthURL
					) {
						currentRequest = currentRequest.clone({
							headers: currentRequest.headers
								.set('osee.account.id', user?.id || '')
								.set('Authorization', user?.id || ''),
						});
					}
					return currentRequest;
				})
			)
		),
		switchMap((authorizedRequest) => next(authorizedRequest))
	);
};
