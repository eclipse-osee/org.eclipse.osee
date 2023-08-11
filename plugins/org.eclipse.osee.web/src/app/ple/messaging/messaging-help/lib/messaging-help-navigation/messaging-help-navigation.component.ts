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
import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { from, iif, of, reduce, switchMap, tap } from 'rxjs';
import { ActivatedRoute, RouterLink } from '@angular/router';
import { MatListModule } from '@angular/material/list';
import { MatIconModule } from '@angular/material/icon';
import helpNavigationStructure from 'src/app/ple/messaging/messaging-help/lib/messaging-help-navigation/messaging-help-navigation-structure';
import { navigationElement } from '@osee/shared/types';
import { UserDataAccountService } from '@osee/auth';
import { MatDividerModule } from '@angular/material/divider';
import { HelpService } from '@osee/shared/services/help';

@Component({
	selector: 'osee-messaging-help-navigation',
	standalone: true,
	imports: [
		CommonModule,
		RouterLink,
		MatListModule,
		MatIconModule,
		MatDividerModule,
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
