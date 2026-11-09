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
import { AsyncPipe, NgClass } from '@angular/common';
import {
	ChangeDetectionStrategy,
	Component,
	inject,
	Signal,
} from '@angular/core';
import { Router, RouterLink } from '@angular/router';
import { UserDataAccountService } from '@osee/auth';
import { navigationElement } from '@osee/shared/types';
import {
	combineLatest,
	concatMap,
	from,
	iif,
	map,
	of,
	reduce,
	switchMap,
} from 'rxjs';
import { ciNavigationStructure } from '../../navigation/ci-dashboard-navigation-structure';
import { CiDashboardUiService } from '../../services/ci-dashboard-ui.service';
import { toSignal } from '@angular/core/rxjs-interop';

@Component({
	selector: 'osee-ci-nav-header',
	imports: [AsyncPipe, NgClass, RouterLink],
	changeDetection: ChangeDetectionStrategy.OnPush,
	template: `<nav
		class="tw-flex tw-items-center"
		aria-label="Zenith">
		<ul class="tw-flex tw-gap-4 tw-text-sm">
			@for (navItem of navItems | async; track navItem) {
				<li>
					<a
						class="tw-p-2"
						[ngClass]="{
							'tw-border-b-2 tw-border-primary tw-text-primary':
								router.url.includes(navItem.routerLink),
						}"
						[routerLink]="navItem.routerLink"
						[queryParams]="branchQueryParams()"
						>{{ navItem.label }}</a
					>
				</li>
			}
		</ul>
	</nav>`,
})
export default class CiNavHeaderComponent {
	private ui = inject(CiDashboardUiService);
	router = inject(Router);
	private userService = inject(UserDataAccountService);

	navItems = from(ciNavigationStructure[0].children).pipe(
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

	// Branch and CI set are carried as query params (branchType, branchId, set),
	// not path segments, so nav links preserve the active branch/set selection.
	branchQueryParams: Signal<Record<string, string>> = toSignal(
		combineLatest([
			this.ui.branchType,
			this.ui.branchId,
			this.ui.ciSetId,
		]).pipe(
			map(([branchType, branchId, ciSetId]) => {
				const queryParams: Record<string, string> = {};
				if (branchType === undefined || branchType === '') {
					return queryParams;
				}
				queryParams['branchType'] = branchType;
				if (
					branchId !== undefined &&
					branchId !== '' &&
					branchId !== '-1'
				) {
					queryParams['branchId'] = branchId;
				}
				if (ciSetId !== undefined && ciSetId !== '-1') {
					queryParams['set'] = ciSetId;
				}
				return queryParams;
			})
		),
		{ initialValue: {} }
	);
}
