/*********************************************************************
 * Copyright (c) 2023 Boeing
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
import { CommonModule } from '@angular/common';
import { Component } from '@angular/core';
import { MatDivider } from '@angular/material/divider';
import { MatIcon } from '@angular/material/icon';
import {
	MatListItem,
	MatListItemIcon,
	MatListItemMeta,
	MatNavList,
} from '@angular/material/list';
import { ActivatedRoute, RouterLink } from '@angular/router';
import { UserDataAccountService } from '@osee/auth';
import { HelpService } from '@osee/shared/services/help';
import { navigationElement } from '@osee/shared/types';
import { from, iif, of, reduce, switchMap, tap } from 'rxjs';
import helpNavigationStructure from './messaging-help-navigation-structure';

@Component({
	selector: 'osee-messaging-help-navigation',
	standalone: true,
	imports: [
		CommonModule,
		RouterLink,
		MatNavList,
		MatListItem,
		MatIcon,
		MatListItemIcon,
		MatListItemMeta,
		MatDivider,
	],
	templateUrl: './messaging-help-navigation.component.html',
})
export class MessagingHelpNavigationComponent {
	constructor(
		private route: ActivatedRoute,
		private userService: UserDataAccountService,
		private helpService: HelpService
	) {
		this.route.url.subscribe((url) => {
			if (url.length > 0) {
				this.currentPath = url.map((u) => u.path).join('/');
			}
		});
	}

	currentPath = '';
	staticNavElements = helpNavigationStructure;
	loadedNavElements = this.helpService
		.getHelpNavElements('MIM')
		.pipe(tap((navElements) => this.openDropdowns(navElements)));
	routePrefix = '/ple/messaging/help/';

	private openDropdowns(navElements: navigationElement[]) {
		navElements.forEach((e) => {
			if (
				e.isDropdown &&
				e.children.find((c) => c.routerLink === this.currentPath)
			) {
				e.isDropdownOpen = true;
			}
			if (e.children.length > 0) {
				this.openDropdowns(e.children);
			}
		});
	}

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
