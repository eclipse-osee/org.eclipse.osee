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
import { Injectable, signal, inject, WritableSignal } from '@angular/core';
import {
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
export abstract class CiDetailsService {
	protected ciDashboardUiService = inject(CiDashboardUiService);
	protected tmoHttpService = inject(TmoHttpService);

	protected _currentDefFilter = signal('');
	protected _ciDefId = signal('-1');

	protected _currentPage = signal(0);
	protected _currentPageSize = signal(25);

	scriptDefs = combineLatest([
		this.branchId,
		this.ciDashboardUiService.ciSetId,
		toObservable(this._currentDefFilter),
		toObservable(this._currentPage),
		toObservable(this._currentPageSize),
	]).pipe(
		filter(([branch, setId]) => branch !== '' && setId !== '-1'),
		switchMap(([branch, setId, filter, currentPage, currentPageSize]) =>
			this.tmoHttpService.getScriptDefListPagination(
				branch,
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
		filter(([branch]) => branch !== '' && branch !== '0'),
		distinctUntilChanged(),
		switchMap(([branch, filter]) =>
			this.tmoHttpService.getFilteredScriptDefCount(branch, filter)
		),
		shareReplay({ bufferSize: 1, refCount: true })
	);

	scriptDef = combineLatest([this.branchId, toObservable(this.ciDefId)]).pipe(
		filter(([branch, defId]) => branch !== '' && defId !== '-1'),
		switchMap(([branch, defId]) =>
			this.tmoHttpService.getScriptDef(branch, defId)
		)
	);

	private _scriptResults = combineLatest([
		this.branchId,
		toObservable(this.ciDefId),
	]).pipe(
		filter(([branch, defId]) => branch !== '' && defId !== '-1'),
		switchMap(([branch, defId]) =>
			this.tmoHttpService.getScriptResults(branch, defId)
		),
		shareReplay({ bufferSize: 1, refCount: true })
	);

	private _scriptResultsBySet = combineLatest([
		this.branchId,
		toObservable(this.ciDefId),
		this.ciDashboardUiService.ciSetId,
	]).pipe(
		filter(
			([branch, defId, setId]) =>
				branch !== '' && defId !== '-1' && setId !== '-1'
		),
		switchMap(([branch, defId, setId]) =>
			this.tmoHttpService.getScriptResultsBySet(branch, defId, setId)
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
			switchMap((branch) =>
				this.tmoHttpService.downloadTmo(branch, resultId)
			)
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

	get scriptResults() {
		return this._scriptResults;
	}

	get scriptResultsBySet() {
		return this._scriptResultsBySet;
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

	get currentDefFilter(): WritableSignal<string> {
		return this._currentDefFilter;
	}

	set currentDefFilter(value: string) {
		this._currentDefFilter.set(value);
	}

	get scriptDefCount() {
		return this._scriptDefCount;
	}

	get currentPage(): WritableSignal<number> {
		return this._currentPage;
	}

	get currentPageSize(): WritableSignal<number> {
		return this._currentPageSize;
	}

	abstract set page(page: number);

	abstract set pageSize(size: number);
}
