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
import { Component, computed, inject } from '@angular/core';
import { IsActiveMatchOptions, Router } from '@angular/router';

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
import { UiService } from '@osee/shared/services';
import { toSignal } from '@angular/core/rxjs-interop';

@Component({
	selector: 'osee-top-level-navigation',
	templateUrl: './top-level-navigation.component.html',
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
	ui = inject(UiService);

	private _branchType = toSignal(this.ui.type, { initialValue: '' });
	private _branchId = toSignal(this.ui.id, { initialValue: '' });
	branchPath = computed(() => {
		let path = '';
		if (this._branchType() !== '') {
			path = '/' + this._branchType();
			if (this._branchId() !== '' && this._branchId() !== '-1') {
				path = path + '/' + this._branchId();
			}
		}
		return path;
	});

	navElements = navigationStructure; // structure that stores the navigation elements

	/** Match options for leaf items — exact primary path match */
	private readonly exactMatchOptions: IsActiveMatchOptions = {
		paths: 'exact',
		queryParams: 'subset',
		fragment: 'ignored',
		matrixParams: 'ignored',
	};

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

	/**
	 * Determines if a dropdown should be highlighted by checking whether
	 * any of its descendant leaf routes are currently active.
	 * This removes the need to maintain a separate routerLink on dropdowns.
	 */
	isDropdownActive(element: navigationElement): boolean {
		return this.hasActiveChild(element.children);
	}

	/** Recursively checks if any descendant non-dropdown item is active */
	private hasActiveChild(children: navigationElement[]): boolean {
		for (const child of children) {
			if (child.isDropdown) {
				if (this.hasActiveChild(child.children)) {
					return true;
				}
			} else {
				if (
					child.routerLink !== '' &&
					this.router.isActive(
						child.routerLink,
						this.exactMatchOptions
					)
				) {
					return true;
				}
			}
		}
		return false;
	}

	/** For non-dropdown items — highlights only on exact route match */
	isItemActive(routerLink: string): boolean {
		if (routerLink === '') {
			return false;
		}
		return this.router.isActive(routerLink, this.exactMatchOptions);
	}

	closeTopLevelNav() {
		this.sideNavService.closeLeftSideNav = '';
	}
}

export default TopLevelNavigationComponent;
