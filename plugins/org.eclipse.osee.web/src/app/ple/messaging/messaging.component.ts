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
import { Component, OnInit } from '@angular/core';
import { UserDataAccountService } from 'src/app/userdata/services/user-data-account.service';
import navigationStructure, {
	navigationElement,
} from 'src/app/navigation/top-level-navigation/top-level-navigation-structure';

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

	get allNavItems() {
		return _navItems.slice(1);
	}

	get navItems() {
		return this.allNavItems.filter(
			(item) => item.isAdminRequired === false
		);
	}

	get adminNavItemsStartingPosition() {
		return this.navItems.length;
	}

	get adminNavItems() {
		return this.allNavItems.filter((item) => item.isAdminRequired === true);
	}

	userIsAdmin = this.userService.userIsAdmin;
}
