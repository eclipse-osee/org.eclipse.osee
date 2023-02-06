import { RouterLink } from '@angular/router';
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
import { from, iif, of, reduce, switchMap } from 'rxjs';
import { MatToolbarModule } from '@angular/material/toolbar';
import { MatSidenavModule } from '@angular/material/sidenav';
import { AsyncPipe, NgFor, NgIf, NgTemplateOutlet } from '@angular/common';
import { MatIconModule } from '@angular/material/icon';
import { MatListModule } from '@angular/material/list';
import { MatDividerModule } from '@angular/material/divider';
import { SideNavService } from 'src/app/shared-services/ui/side-nav.service';
import { UserDataAccountService } from '@osee/auth';

@Component({
	selector: 'osee-top-level-navigation',
	templateUrl: './top-level-navigation.component.html',
	styleUrls: ['./top-level-navigation.component.sass'],
	standalone: true,
	imports: [
		NgIf,
		NgFor,
		AsyncPipe,
		RouterLink,
		NgTemplateOutlet,
		MatToolbarModule,
		MatSidenavModule,
		MatIconModule,
		MatListModule,
		MatDividerModule,
	],
})
export class TopLevelNavigationComponent {
	constructor(
		public router: Router,
		private userService: UserDataAccountService,
		public sideNavService: SideNavService
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

	closeTopLevelNav() {
		this.sideNavService.closeLeftSideNav = '';
	}
}

export default TopLevelNavigationComponent;
