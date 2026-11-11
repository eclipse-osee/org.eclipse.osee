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
import {
	Injectable,
	signal,
	inject,
	WritableSignal,
	computed,
} from '@angular/core';
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
import { writableSlice } from '@osee/shared/utils';

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

	protected _filters = signal<{
		name: string | undefined;
		team: string | undefined;
		subsystem: string | undefined;
		safety: string | undefined;
		statusBy: string | undefined;
		machineName: string | undefined;
		fullScriptName: string | undefined;
		notes: string | undefined;
	}>({
		name: undefined,
		team: undefined,
		subsystem: undefined,
		safety: undefined,
		statusBy: undefined,
		machineName: undefined,
		fullScriptName: undefined,
		notes: undefined,
	});

	nameFilter = writableSlice(this._filters, 'name');
	teamFilter = writableSlice(this._filters, 'team');
	subsystemFilter = writableSlice(this._filters, 'subsystem');
	safetyFilter = writableSlice(this._filters, 'safety');
	statusByFilter = writableSlice(this._filters, 'statusBy');
	machineNameFilter = writableSlice(this._filters, 'machineName');
	fullScriptNameFilter = writableSlice(this._filters, 'fullScriptName');
	notesFilter = writableSlice(this._filters, 'notes');

	private _queries = computed(() => {
		const filters = this._filters();
		return Object.entries(filters)
			.filter(([_, value]) => value !== undefined && value.trim() !== '')
			.map(([column, value]) => ({ column, value: value! }));
	});

	filterSignals: Record<string, WritableSignal<string | undefined>> = {
		name: this.nameFilter,
		team: this.teamFilter,
		subsystem: this.subsystemFilter,
		safety: this.safetyFilter,
		statusBy: this.statusByFilter,
		machineName: this.machineNameFilter,
		fullScriptName: this.fullScriptNameFilter,
		notes: this.notesFilter,
	};

	query = computed(() => ({ filters: this._queries() }));

	scriptDefs = combineLatest([
		this.branchId,
		this.ciDashboardUiService.ciSetId,
		toObservable(this.query),
		toObservable(this._currentPage),
		toObservable(this._currentPageSize),
	]).pipe(
		filter(([branch, setId]) => branch !== '' && setId !== '-1'),
		switchMap(([branch, setId, query, currentPage, currentPageSize]) =>
			this.tmoHttpService.getScriptDefListArtifactMultiFilter(
				branch,
				setId,
				query,
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

	private _artifactScriptDefCount = combineLatest([
		this.branchId,
		this.ciDashboardUiService.ciSetId,
		toObservable(this.query),
	]).pipe(
		filter(([branch, setId]) => branch !== '' && setId !== '-1'),
		switchMap(([branch, setId, query]) =>
			this.tmoHttpService.getScriptDefListArtifactMultiFilterCount(
				branch,
				setId,
				query
			)
		),
		shareReplay({ bufferSize: 1, refCount: true })
	);

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
		return this._artifactScriptDefCount;
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
