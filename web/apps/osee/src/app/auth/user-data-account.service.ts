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
import {
	BehaviorSubject,
	combineLatest,
	from,
	iif,
	Observable,
	of,
} from 'rxjs';
import {
	catchError,
	concatMap,
	reduce,
	shareReplay,
	switchMap,
	take,
} from 'rxjs/operators';
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
				id: UserRoles.CI_TEST_MASTER,
				name: 'CI Test Master',
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
	// refCount: false so the auth result (success OR error) is fetched at most once and replayed to
	// all subscribers. With refCount: true, an error drops the subscriber count to zero, discards
	// the cached result, and the next subscriber re-triggers the request — which on a down server
	// produces a tight infinite retry storm on /orcs/datastore/user (every consumer + the auth
	// interceptor resubscribing). Fetching once and replaying the outcome removes the storm.
	/**
	 * Sentinel emitted when the auth request fails. Its id is the invalid sentinel ('-1') so
	 * consumers that gate on a valid user (e.g. the SSE connect) correctly treat it as "not
	 * authenticated" rather than acting on it.
	 */
	private readonly _invalidUser: user = {
		id: '-1',
		name: '',
		guid: null,
		active: false,
		description: null,
		workTypes: [],
		tags: [],
		userId: '',
		email: '',
		loginIds: [],
		savedSearches: [],
		userGroups: [],
		artifactId: '',
		idString: '-1',
		idIntValue: -1,
		uuid: -1,
		roles: [],
	};

	/** Emit to re-run the auth fetch (e.g. after the server recovers from being down at startup). */
	private readonly _refresh = new BehaviorSubject<void>(undefined);

	// Trigger-driven so refresh() can re-fetch after a failed startup, but each fetch resolves via
	// catchError to a value (never a terminal error). On error shareReplay does NOT cache the
	// error — it re-runs the source for every new subscriber, which with eager consumers + the auth
	// interceptor produces an infinite request storm on /orcs/datastore/user when the server is
	// down. Completing each attempt with a value and replaying it removes the storm; a new attempt
	// happens only when refresh() fires.
	private _user = this._refresh.pipe(
		switchMap(() =>
			this.getAuthConfig().pipe(catchError(() => of(this._invalidUser)))
		),
		shareReplay({ bufferSize: 1, refCount: false })
	);

	/**
	 * Re-fetches the authenticated user. Use after the server was unavailable at startup (which
	 * cached the invalid-user sentinel) so the app can recover without a full page reload.
	 */
	public refresh(): void {
		this._refresh.next();
	}

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
