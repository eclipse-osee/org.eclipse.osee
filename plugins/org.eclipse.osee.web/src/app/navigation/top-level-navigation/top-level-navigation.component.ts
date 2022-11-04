import { UrlSegment, NavigationEnd } from '@angular/router';
/*********************************************************************
 * Copyright (c) 2022 Boeing
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
import { Router } from '@angular/router';

import navigationStructure, {
	navigationElement,
} from './top-level-navigation-structure';
import { UserDataAccountService } from 'src/app/userdata/services/user-data-account.service';
import { filter, from, iif, of, reduce, switchMap } from 'rxjs';

@Component({
	selector: 'osee-top-level-navigation',
	templateUrl: './top-level-navigation.component.html',
	styleUrls: ['./top-level-navigation.component.sass'],
})
export class TopLevelNavigationComponent {
	constructor(
		public router: Router,
		private userService: UserDataAccountService
	) {}

	navElements = navigationStructure; // structure that stores the navigation elements

	getElementsWithPermission(elements: navigationElement[]) {
		return from(elements).pipe(
			switchMap((item) =>
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
	}
}
