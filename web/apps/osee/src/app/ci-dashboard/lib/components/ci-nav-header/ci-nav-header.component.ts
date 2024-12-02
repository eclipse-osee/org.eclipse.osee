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
import { Component, inject } from '@angular/core';
import { Router, RouterLink } from '@angular/router';
import { UserDataAccountService } from '@osee/auth';
import { navigationElement } from '@osee/shared/types';
import {
	combineLatest,
	concatMap,
	from,
	iif,
	of,
	reduce,
	switchMap,
} from 'rxjs';
import { ciNavigationStructure } from '../../navigation/ci-dashboard-navigation-structure';
import { CiDashboardUiService } from '../../services/ci-dashboard-ui.service';

@Component({
	selector: 'osee-ci-nav-header',
	imports: [AsyncPipe, NgClass, RouterLink],
	templateUrl: './ci-nav-header.component.html',
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

	routeSuffix = combineLatest([
		this.ui.branchType,
		this.ui.branchId,
		this.ui.ciSetId,
	]).pipe(
		switchMap(([branchType, branchId, ciSetId]) => {
			if (branchType === undefined || branchType === '') {
				return of('');
			}
			if (
				branchId === undefined ||
				branchId === '' ||
				branchId === '-1'
			) {
				return of(`/${branchType}`);
			}
			if (ciSetId === undefined || ciSetId === '' || ciSetId === '-1') {
				return of(`/${branchType}/${branchId}`);
			}
			return of(`/${branchType}/${branchId}/${ciSetId}`);
		})
	);
}
