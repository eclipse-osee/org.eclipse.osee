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
import { Component, inject } from '@angular/core';

import { AsyncPipe } from '@angular/common';
import { MatAnchor } from '@angular/material/button';
import { MatIcon } from '@angular/material/icon';
import { RouterLink } from '@angular/router';
import { UserDataAccountService } from '@osee/auth';
import { navigationStructure } from '@osee/layout/routing';
import { navigationElement } from '@osee/shared/types';
import { concatMap, from, iif, of, reduce, skip, switchMap } from 'rxjs';

const _navItems = navigationStructure[0].children.filter(
	(c) => c.label === 'MIM'
)[0].children;
@Component({
	selector: 'osee-messaging',
	templateUrl: './messaging.component.html',
	styles: [
		':host{ height: 94vh; min-height: calc(94vh - 10%); max-height: 94vh; width: 100vw; min-width: calc(100vw - 10%); display: inline-block;}',
	],
	standalone: true,
	imports: [AsyncPipe, RouterLink, MatAnchor, MatIcon],
})
export class MessagingComponent {
	private userService = inject(UserDataAccountService);

	_filteredNavItems = from(this.allNavItems).pipe(
		skip(1), // Skip the messaging home page item
		concatMap((item) =>
			this.userService
				.userHasRoles(item.requiredRoles)
				.pipe(
					switchMap((hasPermission) =>
						iif(() => hasPermission, of(item), of())
					)
				)
		),
		reduce((acc, curr) => [...acc, curr], [] as navigationElement[])
	);

	get allNavItems() {
		return _navItems;
	}

	get navItems() {
		return this._filteredNavItems;
	}
}

export default MessagingComponent;
