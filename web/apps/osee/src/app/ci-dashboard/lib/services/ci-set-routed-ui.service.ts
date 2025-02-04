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
import { effect, inject, Injectable } from '@angular/core';
import { CiDashboardUiService } from './ci-dashboard-ui.service';
import { ActivatedRoute, Router } from '@angular/router';
import { toSignal } from '@angular/core/rxjs-interop';

@Injectable({
	providedIn: 'root',
})
export class CiSetRoutedUiService {
	private uiService = inject(CiDashboardUiService);
	private router = inject(Router);
	private route = inject(ActivatedRoute);

	private _queryParams = toSignal(this.route.queryParamMap);
	private _queryParamEffect = effect(() => {
		const params = this._queryParams();
		if (!params) {
			return;
		}
		const setId = params.get('set');
		if (setId !== null && !isNaN(Number(setId))) {
			this.uiService.CiSetId = setId as `${number}`;
		} else {
			this.uiService.CiSetId = '-1';
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
