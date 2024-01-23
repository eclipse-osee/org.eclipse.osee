/*********************************************************************
 * Copyright (c) 2024 Boeing
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
import { HttpClient, HttpHeaders } from '@angular/common/http';
import { Injectable, inject } from '@angular/core';
import { OktaAuthStateService } from '@okta/okta-angular';
import { AuthState } from '@okta/okta-auth-js';
import { user } from '@osee/shared/types/auth';
import { filter, map, of, switchMap } from 'rxjs';
import { environment, OSEEAuthURL } from './environment';

@Injectable({
	providedIn: 'root',
})
export class AdditionalAuthService {
	constructor(private http: HttpClient) {}
	private _oktaAuthService = inject(OktaAuthStateService);
	private _isOktaAuthenticated =
		environment.authScheme === 'OKTA'
			? this._oktaAuthService.authState$.pipe(
					filter((s: AuthState) => !!s),
					map((s: AuthState) => s.isAuthenticated ?? false)
			  )
			: of(false);
	private _oktaAuth = this._isOktaAuthenticated.pipe(
		filter((authenticated) => authenticated === true),
		switchMap((_) =>
			this._oktaAuthService.authState$.pipe(
				filter(
					(authState: AuthState) =>
						!!authState && !!authState.isAuthenticated
				),
				map(
					(authState: AuthState) =>
						authState.idToken?.claims.preferred_username ?? ''
				)
			)
		),
		switchMap((name) =>
			this.http.get<user>(OSEEAuthURL, {
				headers: new HttpHeaders({
					Authorization: 'Basic ' + name,
					'osee.account.id': name,
					'osee.user.id': name,
				}),
			})
		)
	);

	public getAuth() {
		return this._oktaAuth;
	}
}
