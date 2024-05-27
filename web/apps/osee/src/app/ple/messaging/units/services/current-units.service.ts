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
import { NamedId } from '@osee/shared/types';
import { ARTIFACTTYPEIDENUM } from '@osee/shared/types/constants';
import { ATTRIBUTETYPEIDENUM } from '@osee/attributes/constants';
import {
	combineLatest,
	debounceTime,
	distinctUntilChanged,
	filter,
	repeat,
	shareReplay,
	switchMap,
	take,
	tap,
} from 'rxjs';
import { UnitsService } from './units.service';
import { UnitsUiService } from './units-ui.service';
import { TransactionService } from '@osee/transactions/services';

@Injectable({
	providedIn: 'root',
})
export class CurrentUnitsService {
	private unitsService = inject(UnitsService);
	private uiService = inject(UnitsUiService);

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

	private transactionService = inject(TransactionService);

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

	modifyUnit(value: NamedId) {
		return this.uiService.BranchId.pipe(
			take(1),
			switchMap((id) =>
				this.transactionService.performMutation({
					branch: id,
					txComment: `Modifying ${value.id}`,
					modifyArtifacts: [
						{
							id: value.id,
							setAttributes: [
								{
									typeId: ATTRIBUTETYPEIDENUM.NAME,
									value: value.name,
								},
							],
						},
					],
				})
			),
			tap((_) => {
				this.uiService.update = true;
			})
		);
	}

	createUnit(value: string) {
		return this.uiService.BranchId.pipe(
			take(1),
			switchMap((id) =>
				this.transactionService.performMutation({
					branch: id,
					txComment: `Creating Unit ${value}`,
					createArtifacts: [
						{
							typeId: ARTIFACTTYPEIDENUM.UNIT,
							name: value,
						},
					],
				})
			),
			tap((_) => {
				this.uiService.update = true;
			})
		);
	}
}
