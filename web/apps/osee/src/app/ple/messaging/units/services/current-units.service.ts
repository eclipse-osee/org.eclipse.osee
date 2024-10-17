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
import { ARTIFACTTYPEIDENUM } from '@osee/shared/types/constants';
import {
	combineLatest,
	debounceTime,
	distinctUntilChanged,
	filter,
	repeat,
	shareReplay,
	switchMap,
	take,
} from 'rxjs';
import { UnitsService } from './units.service';
import { UnitsUiService } from './units-ui.service';
import { CurrentTransactionService } from '@osee/transactions/services';
import { unit } from '@osee/messaging/units/types';

@Injectable({
	providedIn: 'root',
})
export class CurrentUnitsService {
	private unitsService = inject(UnitsService);
	private uiService = inject(UnitsUiService);
	private _currentTx = inject(CurrentTransactionService);

	private _units = combineLatest([
		this.uiService.BranchId,
		this.uiService.filter,
		this.uiService.viewId,
		this.uiService.currentPage,
		this.uiService.currentPageSize,
	]).pipe(
		filter(
			([branchId, _filter, _viewId, _page, _pageSize]) => branchId !== ''
		),
		debounceTime(500),
		distinctUntilChanged(),
		switchMap(([branchId, filter, viewId, page, pageSize]) =>
			this.unitsService
				.getFiltered(branchId, filter, viewId, page + 1, pageSize)
				.pipe(repeat({ delay: () => this.uiService.UpdateRequired }))
		),
		shareReplay({ bufferSize: 1, refCount: true })
	);
	private _count = combineLatest([
		this.uiService.BranchId,
		this.uiService.filter,
		this.uiService.viewId,
	]).pipe(
		filter(([branchId, _filter, _viewId]) => branchId !== ''),
		debounceTime(500),
		distinctUntilChanged(),
		switchMap(([branchId, filter, viewId]) =>
			this.unitsService
				.getCount(branchId, filter, viewId)
				.pipe(repeat({ delay: () => this.uiService.UpdateRequired }))
		),
		shareReplay({ bufferSize: 1, refCount: true })
	);

	get current() {
		return this._units;
	}

	get count() {
		return this._count;
	}

	get units() {
		return this._units;
	}

	getUnit(id: string) {
		return this.uiService.BranchId.pipe(
			take(1),
			switchMap((branchId) => this.unitsService.getOne(branchId, id))
		);
	}

	/**
	 * Used to fetch units with mat-option-loading
	 */
	getFilteredPaginatedUnits(pageNum: string | number, filter?: string) {
		return combineLatest([
			this.uiService.BranchId,
			this.uiService.viewId,
			this.uiService.currentPageSize,
		]).pipe(
			take(1),
			switchMap(([id, viewId, pageSize]) =>
				this.unitsService.getFiltered(
					id,
					filter,
					viewId,
					pageNum,
					pageSize
				)
			)
		);
	}

	/**
	 * Used to fetch count with mat-option-loading
	 */
	getFilteredCount(filter?: string) {
		return combineLatest([
			this.uiService.BranchId,
			this.uiService.viewId,
		]).pipe(
			take(1),
			switchMap(([id, viewId]) =>
				this.unitsService.getCount(id, filter, viewId)
			)
		);
	}

	get currentPageSize() {
		return this.uiService.currentPageSize;
	}

	get currentPage() {
		return this.uiService.currentPage;
	}

	set page(pg: number) {
		this.uiService.page = pg;
	}

	set pageSize(pg: number) {
		this.uiService.pageSize = pg;
	}

	set filter(f: string) {
		this.uiService.filterString = f;
	}

	createUnit(unit: unit) {
		const { id, gammaId, applicability, ...remainingAttributes } = unit;
		const attributeKeys = Object.keys(
			remainingAttributes
		) as (keyof typeof remainingAttributes)[];
		const attributes = attributeKeys.map((k) => remainingAttributes[k]);
		return this._currentTx.createArtifactAndMutate(
			`Creating Unit ${unit.name.value}`,
			ARTIFACTTYPEIDENUM.UNIT,
			applicability,
			[],
			...attributes
		);
	}

	deleteUnit(unit: unit) {
		return this._currentTx
			.deleteArtifactAndMutate(`Delete Unit ${unit.name.value}`, unit.id)
			.pipe(take(1))
			.subscribe();
	}
}
