/*********************************************************************
 * Copyright (c) 2025 Boeing
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
import { computed, effect, inject, Injectable } from '@angular/core';
import { CiDashboardUiService } from './ci-dashboard-ui.service';
import { Router } from '@angular/router';
@Injectable({
	providedIn: 'root',
})
export class CiSetRoutedUiService {
	private uiService = inject(CiDashboardUiService);
	private router = inject(Router);
	// The root ActivatedRoute emits empty params before the first navigation completes, so
	// read the committed URL only once a navigation has succeeded.
	private settledQueryParams = computed(() =>
		this.router.lastSuccessfulNavigation()
			? this.router.parseUrl(this.router.url).queryParamMap
			: undefined
	);
	/** Set id in the URL ('-1' when absent); undefined until the router has settled. */
	readonly urlSetId = computed<`${number}` | undefined>(() => {
		const params = this.settledQueryParams();
		if (!params) {
			return undefined;
		}
		const setId = params.get('set');
		return setId !== null && !isNaN(Number(setId))
			? (setId as `${number}`)
			: '-1';
	});
	private _queryParamEffect = effect(() => {
		const setId = this.urlSetId();
		if (setId !== undefined) {
			this.uiService.CiSetId = setId;
		}
	});
	setCISetAndNavigate(setId: `${number}`) {
		const tree = this.router.parseUrl(this.router.url);
		const queryParams = tree.queryParams;
		if (!setId || setId === '-1') {
			delete queryParams['set'];
		} else {
			queryParams['set'] = setId;
		}
		this.router.navigate([], { queryParams: queryParams });
	}
}
