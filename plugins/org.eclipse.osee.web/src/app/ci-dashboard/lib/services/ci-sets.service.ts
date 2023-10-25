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
import { Injectable } from '@angular/core';
import { CiSetsHttpService } from './ci-sets-http.service';
import { CiDashboardUiService } from './ci-dashboard-ui.service';
import {
	BehaviorSubject,
	combineLatest,
	filter,
	shareReplay,
	switchMap,
} from 'rxjs';

@Injectable({
	providedIn: 'root',
})
export class CiSetsService {
	constructor(
		private ciSetsService: CiSetsHttpService,
		private ui: CiDashboardUiService
	) {}

	private _activeOnly = new BehaviorSubject<boolean>(false);

	private _ciSets = combineLatest([this._activeOnly, this.ui.branchId]).pipe(
		filter(([_, branchId]) => branchId !== '' && branchId !== '-1'),
		switchMap(([active, branchId]) =>
			this.ciSetsService.getCiSets(branchId, active)
		),
		shareReplay({ bufferSize: 1, refCount: true })
	);

	get ciSets() {
		return this._ciSets;
	}

	get activeOnly() {
		return this._activeOnly;
	}

	set ActiveOnly(activeOnly: boolean) {
		this._activeOnly.next(activeOnly);
	}
}
