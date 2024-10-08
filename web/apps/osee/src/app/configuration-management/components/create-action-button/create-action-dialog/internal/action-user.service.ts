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
import { Injectable, inject } from '@angular/core';
import { Observable } from 'rxjs';
import { map, shareReplay } from 'rxjs/operators';
import { user } from '@osee/shared/types/auth';
import { ActionService } from '@osee/configuration-management/services';

@Injectable({
	providedIn: 'root',
})
export class ActionUserService {
	private actionService = inject(ActionService);

	private _getSortedUsers = this.actionService.users.pipe(
		map((results) =>
			results.sort((a, b) => {
				return a.name < b.name ? -1 : 1;
			})
		),
		shareReplay({ bufferSize: 1, refCount: true })
	);
	public get usersSorted(): Observable<user[]> {
		return this._getSortedUsers;
	}
}
