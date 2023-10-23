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
import { BehaviorSubject, combineLatest, switchMap } from 'rxjs';
import { TmoHttpService } from './tmo-http.service';
import { CiDashboardUiService } from './ci-dashboard-ui.service';

@Injectable({
	providedIn: 'root',
})
export class TmoService {
	constructor(
		private ciDashboardUiService: CiDashboardUiService,
		private tmoHttpService: TmoHttpService
	) {}

	private _filterValue = new BehaviorSubject<string>('');

	scriptDefs = combineLatest([this.branchId, this.setId]).pipe(
		switchMap(([brid, pid]) =>
			this.tmoHttpService.getScriptDefList(brid, pid)
		)
	);

	scriptDefss = this.setId.pipe(
		switchMap((pid) => this.tmoHttpService.getScriptDefList(3, pid))
	);
	scriptResults = this.branchId.pipe(
		switchMap((brid) => this.tmoHttpService.getScriptResultList(brid))
	);
	testCases = this.branchId.pipe(
		switchMap((brid) => this.tmoHttpService.getTestCaseList(brid))
	);
	testPoints = this.branchId.pipe(
		switchMap((brid) => this.tmoHttpService.getTestPointList(brid))
	);

	get setId() {
		return this.ciDashboardUiService.ciSetId;
	}

	set SetId(id: string) {
		this.ciDashboardUiService.CiSetId;
	}

	get filterValue() {
		return this._filterValue;
	}

	set FilterValue(value: string) {
		this._filterValue.next(value);
	}

	get branchId() {
		return this.ciDashboardUiService.branchId;
	}

	set BranchId(branchId: string) {
		this.ciDashboardUiService.BranchId = branchId;
	}

	get branchType() {
		return this.ciDashboardUiService.branchType;
	}

	set BranchType(branchType: 'working' | 'baseline' | '') {
		this.ciDashboardUiService.BranchType = branchType;
	}
}
