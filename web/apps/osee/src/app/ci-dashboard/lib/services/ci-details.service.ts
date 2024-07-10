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
import { Injectable, signal } from '@angular/core';
import {
	BehaviorSubject,
	combineLatest,
	debounceTime,
	distinctUntilChanged,
	filter,
	share,
	shareReplay,
	switchMap,
} from 'rxjs';
import { CiDashboardUiService } from './ci-dashboard-ui.service';
import { TmoHttpService } from './tmo-http.service';
import { toObservable } from '@angular/core/rxjs-interop';

@Injectable({
	providedIn: 'root',
})
export class CiDetailsService {
	constructor(
		private ciDashboardUiService: CiDashboardUiService,
		private tmoHttpService: TmoHttpService
	) {}

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
		)
	);

	_scriptDefs = combineLatest([
		this.branchId,
		this.ciDashboardUiService.ciSetId,
		this.currentPage,
		this.currentPageSize,
	]).pipe(
		filter(
			([brid, setId, page, pageSize]) => brid !== '' && setId !== '-1'
		),
		share(),
		debounceTime(500),
		distinctUntilChanged(),
		switchMap(([brid, setId, page, pageSize]) =>
			this.tmoHttpService.getScriptDefListPagination(
				brid,
				setId,
				page + 1,
				pageSize
			)
		),
		shareReplay({ bufferSize: 1, refCount: true }) //Same instance for multiple calls using it.
	);

	_scriptDefCount = combineLatest([
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

	_scriptResults = combineLatest([
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
		this.ciDashboardUiService.BranchId;
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
