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
import { HttpClient } from '@angular/common/http';
import { Injectable, inject } from '@angular/core';
import { combineLatest, from, iif, Observable, of } from 'rxjs';
import { concatMap, reduce, shareReplay, take } from 'rxjs/operators';
import { user, UserRoles } from '@osee/shared/types/auth';
import {
	UserHeaderService,
	OSEEAuthURL,
	environment,
	AdditionalAuthService,
} from '@osee/environments';

@Injectable({
	providedIn: 'root',
})
export class UserDataAccountService {
	private http = inject(HttpClient);
	private userHeaderService = inject(UserHeaderService);
	private authProvider = inject(AdditionalAuthService);

	private _devUser = of<user>({
		id: '61106791',
		name: 'Joe Smith',
		guid: null,
		active: false,
		description: null,
		workTypes: [],
		tags: [],
		userId: '61106791',
		email: '',
		loginIds: [],
		savedSearches: [],
		userGroups: [],
		artifactId: '',
		idString: '',
		idIntValue: 0,
		uuid: 0,
		roles: [
			{
				id: UserRoles.CI_ADMIN,
				name: 'CI Admin',
			},
			{
				id: UserRoles.MIM_ADMIN,
				name: 'MIM Admin',
			},
			{
				id: UserRoles.OSEE_ADMIN,
				name: 'Osee Admin',
			},
		],
	});
	private _fetchFromApi = iif(
		() => this.userHeaderService.useCustomHeaders,
		this.http.get<user>(OSEEAuthURL, {
			headers: this.userHeaderService.headers,
		}),
		this.http.get<user>(OSEEAuthURL)
	);

	private _demoAuth =
		environment.authScheme === 'DEMO'
			? this.http.get<user>(OSEEAuthURL, {
					headers: this.userHeaderService.headers,
				})
			: this.http.get<user>(OSEEAuthURL);

	private _noneAuth =
		environment.authScheme === 'NONE'
			? this.http.get<user>(OSEEAuthURL)
			: of<user>();

	private _devAuth =
		environment.authScheme === 'DEV' ? this._devUser : this._noneAuth;

	private _forcedSSOAuth =
		environment.authScheme === 'FORCED_SSO'
			? this._fetchFromApi
			: this._noneAuth;

	private getAuthConfig() {
		switch (environment.authScheme) {
			case 'OKTA':
				return this.authProvider.getAuth();
			case 'FORCED_SSO':
				return this._forcedSSOAuth;
			case 'DEV':
				return this._devAuth;
			case 'DEMO':
				return this._demoAuth;
			case 'NONE':
				return this._noneAuth;
			default:
				throw new Error('Auth Configuration not defined somehow?');
		}
	}
	private _user = this.getAuthConfig().pipe(
		shareReplay({ bufferSize: 1, refCount: true })
	);

	public userHasRoles(roles: UserRoles[]) {
		return combineLatest([this.user, from(roles)]).pipe(
			concatMap(([user, role]) =>
				of(user.roles.map((u) => u.id).includes(role))
			),
			take(roles.length),
			reduce((curr, acc) => (acc = acc && curr), true)
		);
	}

	public get user(): Observable<user> {
		return this._user;
	}
}
