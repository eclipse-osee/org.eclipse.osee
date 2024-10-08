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
import { Injectable, inject } from '@angular/core';
import {
	BehaviorSubject,
	combineLatest,
	filter,
	shareReplay,
	switchMap,
} from 'rxjs';
import { CISet } from '../types';
import { TmoHttpService } from './tmo-http.service';
import { CiDashboardUiService } from './ci-dashboard-ui.service';

@Injectable({
	providedIn: 'root',
})
export class CiSetDiffService {
	private uiService = inject(CiDashboardUiService);
	private tmoHttp = inject(TmoHttpService);

	private _selectedSets = new BehaviorSubject<CISet[]>([]);

	setDiffs = combineLatest([this.uiService.branchId, this.selectedSets]).pipe(
		filter(
			([branchId, sets]) =>
				branchId !== '' && branchId !== '-1' && sets.length > 0
		),
		switchMap(([branchId, sets]) =>
			this.tmoHttp.getSetDiffs(
				branchId,
				sets.map((set) => set.id)
			)
		),
		shareReplay({ bufferSize: 1, refCount: true })
	);

	get selectedSets() {
		return this._selectedSets;
	}

	set SelectedSets(sets: CISet[]) {
		this._selectedSets.next(sets);
	}
}
