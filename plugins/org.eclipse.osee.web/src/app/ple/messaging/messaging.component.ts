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
import { Component } from '@angular/core';
import { UserDataAccountService } from 'src/app/userdata/services/user-data-account.service';
import navigationStructure, {
	navigationElement,
} from 'src/app/navigation/top-level-navigation/top-level-navigation-structure';
import { concatMap, from, iif, of, reduce, skip, switchMap } from 'rxjs';

const _navItems = navigationStructure[0].children.filter(
	(c) => c.label === 'Messaging Configuration'
)[0].children;
@Component({
	selector: 'osee-messaging',
	templateUrl: './messaging.component.html',
	styleUrls: ['./messaging.component.sass'],
})
export class MessagingComponent {
	constructor(private userService: UserDataAccountService) {}

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
