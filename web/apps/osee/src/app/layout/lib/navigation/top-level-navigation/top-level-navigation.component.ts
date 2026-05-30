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
import { Component, computed, inject, signal } from '@angular/core';
import { NavigationEnd, Router } from '@angular/router';
import { takeUntilDestroyed, toSignal } from '@angular/core/rxjs-interop';

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
import { filter, from, iif, of, reduce, switchMap } from 'rxjs';
import { UiService } from '@osee/shared/services';

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
	private readonly router = inject(Router);
	private readonly userService = inject(UserDataAccountService);
	readonly sideNavService = inject(SideNavService);
	private readonly ui = inject(UiService);

	private readonly _branchType = toSignal(this.ui.type, {
		initialValue: '',
	});
	private readonly _branchId = toSignal(this.ui.id, { initialValue: '' });
	readonly branchPath = computed(() => {
		let path = '';
		if (this._branchType() !== '') {
			path = '/' + this._branchType();
			if (this._branchId() !== '' && this._branchId() !== '-1') {
				path = path + '/' + this._branchId();
			}
		}
		return path;
	});

	readonly navElements = navigationStructure;

	/**
	 * Precomputed set of all active navigation elements (both leaves and dropdowns).
	 * Recomputed once per navigation. Template checks are pure Set.has() — O(1).
	 */
	private readonly _activeItems = signal<Set<navigationElement>>(
		this.buildActiveSet()
	);

	private readonly _navSubscription = this.router.events
		.pipe(
			filter((event) => event instanceof NavigationEnd),
			takeUntilDestroyed()
		)
		.subscribe(() => this._activeItems.set(this.buildActiveSet()));

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

	/** O(1) check — is this element (leaf or dropdown) currently active? */
	isActive(element: navigationElement): boolean {
		return this._activeItems().has(element);
	}

	closeTopLevelNav() {
		this.sideNavService.closeLeftSideNav = '';
	}

	/**
	 * Builds the full set of active elements (leaves + ancestor dropdowns).
	 */
	private buildActiveSet(): Set<navigationElement> {
		const activeSet = new Set<navigationElement>();
		const url = this.getCleanUrl();
		this.findActiveInGroup(this.navElements, activeSet, url);
		return activeSet;
	}

	/** Extracts the decoded primary route path using Angular's URL parser */
	private getCleanUrl(): string {
		const urlTree = this.router.parseUrl(this.router.url);
		const segments = urlTree.root.children['primary']?.segments ?? [];
		return '/' + segments.map((s) => s.path).join('/');
	}

	/**
	 * For a group of siblings, finds the active leaf (longest prefix match)
	 * and marks ancestor dropdowns as active.
	 * Returns true if this group (or a nested group) contains an active item.
	 */
	private findActiveInGroup(
		siblings: navigationElement[],
		activeSet: Set<navigationElement>,
		url: string
	): boolean {
		// First, recurse into all dropdown children
		let dropdownHasActive = false;
		for (const item of siblings) {
			if (item.isDropdown) {
				if (this.findActiveInGroup(item.children, activeSet, url)) {
					activeSet.add(item);
					dropdownHasActive = true;
				}
			}
		}

		// If a nested dropdown claimed the URL, don't highlight leaf items here
		if (dropdownHasActive) {
			return true;
		}

		// Find the longest prefix match among leaf siblings
		let longestMatch: navigationElement | null = null;
		let longestLength = 0;

		for (const item of siblings) {
			if (
				!item.isDropdown &&
				item.routerLink !== '' &&
				item.routerLink.length > longestLength &&
				(url === item.routerLink ||
					url.startsWith(item.routerLink + '/'))
			) {
				longestMatch = item;
				longestLength = item.routerLink.length;
			}
		}

		if (longestMatch !== null) {
			activeSet.add(longestMatch);
			return true;
		}

		return false;
	}
}

export default TopLevelNavigationComponent;
