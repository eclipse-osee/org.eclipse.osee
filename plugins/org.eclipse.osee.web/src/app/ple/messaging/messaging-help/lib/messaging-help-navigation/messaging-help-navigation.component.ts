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
import { from, iif, of, reduce, switchMap } from 'rxjs';
import { ActivatedRoute, RouterLink } from '@angular/router';
import { MatListModule } from '@angular/material/list';
import { MatIconModule } from '@angular/material/icon';
import helpNavigationStructure from 'src/app/ple/messaging/messaging-help/lib/messaging-help-navigation/messaging-help-navigation-structure';
import { navigationElement } from '@osee/shared/types';
import { UserDataAccountService } from '@osee/auth';
import { MatDividerModule } from '@angular/material/divider';

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
	styleUrls: ['./messaging-help-navigation.component.sass'],
})
export class MessagingHelpNavigationComponent {
	constructor(
		private route: ActivatedRoute,
		private userService: UserDataAccountService
	) {
		this.route.url.subscribe((url) => {
			if (url.length > 0) {
				this.currentPath = url[0].path;
			}
		});
	}

	currentPath = '';

	navElements = helpNavigationStructure;

	routePrefix = '/ple/messaging/help/';

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
