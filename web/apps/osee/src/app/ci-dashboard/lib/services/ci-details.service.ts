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
	debounceTime,
	distinctUntilChanged,
	filter,
	map,
	share,
	shareReplay,
	switchMap,
	take,
} from 'rxjs';
import { CiDashboardUiService } from './ci-dashboard-ui.service';
import { TmoHttpService } from './tmo-http.service';
import { takeUntilDestroyed, toObservable } from '@angular/core/rxjs-interop';
import { forkJoin } from 'rxjs';
import { format } from 'date-fns';

@Injectable({
	providedIn: 'root',
})
export class CiDetailsService {
	private ciDashboardUiService = inject(CiDashboardUiService);
	private tmoHttpService = inject(TmoHttpService);

	private _currentPage$ = new BehaviorSubject<number>(0);
	private _currentPageSize$ = new BehaviorSubject<number>(10);

	private _ciDefId = signal('-1');

	filter = signal('');

	scriptDefs = combineLatest([
		this.branchId,
		this.ciDashboardUiService.ciSetId,
	]).pipe(
		filter(([brid, setId]) => brid !== '' && setId !== '-1'),
		switchMap(([brid, setId]) =>
			this.tmoHttpService.getScriptDefList(brid, setId)
		),
		shareReplay({ bufferSize: 1, refCount: true })
	);

	private _scriptDefCount = combineLatest([
		this.branchId,
		this.ciDashboardUiService.ciSetId,
	]).pipe(
		filter(([brid, setId]) => brid !== '' && setId !== '-1'),
		distinctUntilChanged(),
		switchMap(([brid, setId]) =>
			this.tmoHttpService.getFilteredScriptDefCount(brid, setId)
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
		takeUntilDestroyed(),
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

	get currentPage() {
		return this._currentPage$;
	}

	set page(page: number) {
		this._currentPage$.next(page);
	}

	get currentPageSize() {
		return this._currentPageSize$;
	}

	set pageSize(page: number) {
		this._currentPageSize$.next(page);
	}

	get scriptDefCount() {
		return this._scriptDefCount;
	}
}
