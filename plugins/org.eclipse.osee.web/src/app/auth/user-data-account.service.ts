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
import { Injectable } from '@angular/core';
import { combineLatest, from, iif, Observable, of } from 'rxjs';
import { concatMap, reduce, shareReplay, take } from 'rxjs/operators';
import { environment, OSEEAuthURL } from 'src/environments/environment';
import { user, UserRoles } from '@osee/shared/types/auth';
import { UserHeaderService } from './user-header.service';

@Injectable({
	providedIn: 'root',
})
export class UserDataAccountService {
	constructor(
		private http: HttpClient,
		private userHeaderService: UserHeaderService
	) {}

	private _fetchFromApi = iif(
		() => this.userHeaderService.useCustomHeaders,
		this.http.get<user>(OSEEAuthURL, {
			headers: this.userHeaderService.headers,
		}),
		this.http.get<user>(OSEEAuthURL)
	);

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
				id: '52247',
				name: 'Osee Admin',
			},
		],
	});

	private _user = iif(
		() => environment.production,
		this._fetchFromApi,
		this._devUser
	).pipe(shareReplay({ bufferSize: 1, refCount: true }));

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
