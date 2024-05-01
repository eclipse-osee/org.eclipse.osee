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
import { filter, iif, map, switchMap, take } from 'rxjs';
import { apiURL, environment, OSEEAuthURL } from '@osee/environments';

export const OseeAuthInterceptor = (
	req: HttpRequest<unknown>,
	next: HttpHandlerFn
) => {
	const user = inject(UserDataAccountService).user;
	return iif(
		() => req.url.includes(apiURL) && req.url !== OSEEAuthURL,
		user.pipe(
			filter((user) => user !== undefined),
			take(1),
			map((user) => {
				return req.clone({
					headers:
						environment.authScheme !== 'NONE'
							? req.headers
									.set('osee.account.id', user?.id || '')
									.set('Authorization', user?.id || '')
							: req.headers,
				});
			}),
			switchMap((authorizedReq) => next(authorizedReq))
		),
		next(req)
	);
};
