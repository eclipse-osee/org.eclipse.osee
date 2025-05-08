/*********************************************************************
 * Copyright (c) 2024 Boeing
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
import { Injectable, signal, inject } from '@angular/core';
import {
	BehaviorSubject,
	combineLatest,
	distinctUntilChanged,
	filter,
	map,
	shareReplay,
	switchMap,
	take,
} from 'rxjs';
import { CiDashboardUiService } from './ci-dashboard-ui.service';
import { TmoHttpService } from './tmo-http.service';
import { toObservable } from '@angular/core/rxjs-interop';
import { forkJoin } from 'rxjs';
import { format } from 'date-fns';

@Injectable({
	providedIn: 'root',
})
export class CiDetailsService {
	private ciDashboardUiService = inject(CiDashboardUiService);
	private tmoHttpService = inject(TmoHttpService);

	private _currentDefFilter = signal('');
	private _currentPage = signal(0);
	private _currentPageSize = signal(25);

	private _ciDefId = signal('-1');

	scriptDefs = combineLatest([
		this.branchId,
		this.ciDashboardUiService.ciSetId,
		toObservable(this._currentDefFilter),
		toObservable(this._currentPage),
		toObservable(this._currentPageSize),
	]).pipe(
		filter(([brid, setId]) => brid !== '' && setId !== '-1'),
		switchMap(([brid, setId, filter, currentPage, currentPageSize]) =>
			this.tmoHttpService.getScriptDefListPagination(
				brid,
				setId,
				filter,
				currentPage + 1,
				currentPageSize
			)
		),
		shareReplay({ bufferSize: 1, refCount: true })
	);

	private _scriptDefCount = combineLatest([
		this.branchId,
		toObservable(this._currentDefFilter),
	]).pipe(
		filter(([brid, setId]) => brid !== '' && brid !== '0'),
		distinctUntilChanged(),
		switchMap(([brid, filter]) =>
			this.tmoHttpService.getFilteredScriptDefCount(brid, filter)
		),
		shareReplay({ bufferSize: 1, refCount: true })
	);

	scriptDef = combineLatest([this.branchId, toObservable(this.ciDefId)]).pipe(
		filter(([brid, defId]) => brid !== '' && defId !== '-1'),
		switchMap(([brid, defId]) =>
			this.tmoHttpService.getScriptDef(brid, defId)
		)
	);

	private _scriptResults = combineLatest([
		this.branchId,
		toObservable(this.ciDefId),
	]).pipe(
		filter(([brid, defId]) => brid !== '' && defId !== '-1'),
		switchMap(([brId, defId]) =>
			this.tmoHttpService.getScriptResults(brId, defId)
		),
		shareReplay({ bufferSize: 1, refCount: true })
	);

	getScriptResult(resultId: string) {
		return this.branchId.pipe(
			filter((id) => id !== '' && id !== '-1'),
			switchMap((id) => this.tmoHttpService.getScriptResult(id, resultId))
		);
	}

	downloadTmo(resultId: string) {
		const tmoNameFromDef = this.scriptDef.pipe(
			take(1),
			map((x) => x.name)
		);
		const tmoNameFromResult = this.getScriptResult(resultId).pipe(
			take(1),
			map((x) => format(new Date(x.executionDate), '_yyyy-MM-dd'))
		);
		const tmoName = forkJoin([tmoNameFromDef, tmoNameFromResult]).pipe(
			map(([def, result]) => (def += result))
		);

		const tmo = this.branchId.pipe(
			take(1),
			switchMap((brid) => this.tmoHttpService.downloadTmo(brid, resultId))
		);

		return combineLatest([tmo, tmoName]).pipe(
			map(([res, name]) => {
				if (res.size !== 0) {
					const blob = new Blob([res], {
						type: 'application/xml',
					});
					const url = URL.createObjectURL(blob);
					const link = document.createElement('a');
					link.href = url;
					link.setAttribute('download', name + '.tmo');
					document.body.appendChild(link);
					link.click();
					link.remove();
				}
			})
		);
	}

	resetCurrentDefFilter() {
		this._currentDefFilter.set('');
	}

	get scriptResults() {
		return this._scriptResults;
	}

	get ciDefId() {
		return this._ciDefId;
	}

	set CiDefId(id: string) {
		this._ciDefId.set(id);
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

	get currentDefFilter(): string {
		return this._currentDefFilter();
	}

	get currentPage() {
		return this._currentPage;
	}

	get currentPageSize() {
		return this._currentPageSize;
	}

	get scriptDefCount() {
		return this._scriptDefCount;
	}

	set page(page: number) {
		this._currentPage.set(page);
	}

	set currentDefFilter(value: string) {
		this._currentDefFilter.set(value);
	}

	set pageSize(page: number) {
		this._currentPageSize.set(page);
	}
}
