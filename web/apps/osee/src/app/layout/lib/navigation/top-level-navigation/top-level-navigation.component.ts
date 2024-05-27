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
import { Component, inject } from '@angular/core';
import { Router } from '@angular/router';

import { AsyncPipe, NgClass, NgTemplateOutlet } from '@angular/common';
import { MatDivider } from '@angular/material/divider';
import { MatIcon } from '@angular/material/icon';
import {
	MatListItem,
	MatListItemIcon,
	MatNavList,
} from '@angular/material/list';
import { MatToolbar } from '@angular/material/toolbar';
import { RouterLink } from '@angular/router';
import { UserDataAccountService } from '@osee/auth';
import { navigationStructure } from '@osee/layout/routing';
import { SideNavService } from '@osee/shared/services/layout';
import { navigationElement } from '@osee/shared/types';
import { from, iif, of, reduce, switchMap } from 'rxjs';

@Component({
	selector: 'osee-top-level-navigation',
	templateUrl: './top-level-navigation.component.html',
	standalone: true,
	imports: [
		NgClass,
		AsyncPipe,
		RouterLink,
		NgTemplateOutlet,
		MatToolbar,
		MatNavList,
		MatListItem,
		MatIcon,
		MatListItemIcon,
		MatDivider,
	],
})
export class TopLevelNavigationComponent {
	router = inject(Router);
	private userService = inject(UserDataAccountService);
	sideNavService = inject(SideNavService);

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

	closeTopLevelNav() {
		this.sideNavService.closeLeftSideNav = '';
	}
}

export default TopLevelNavigationComponent;
