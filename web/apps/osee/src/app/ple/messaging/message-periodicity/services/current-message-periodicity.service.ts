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
import { MessagePeriodicityService } from './message-periodicity.service';
import { MessagePeriodicityUiService } from './message-periodicity-ui.service';
import { TransactionService } from '@osee/transactions/services';

@Injectable({
	providedIn: 'root',
})
export class CurrentMessagePeriodicityService {
	private MessagePeriodicityService = inject(MessagePeriodicityService);
	private uiService = inject(MessagePeriodicityUiService);

	private _MessagePeriodicities = combineLatest([
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
			this.MessagePeriodicityService.getFiltered(
				branchId,
				filter,
				viewId,
				page + 1,
				pageSize
			).pipe(repeat({ delay: () => this.uiService.UpdateRequired }))
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
			this.MessagePeriodicityService.getCount(
				branchId,
				filter,
				viewId
			).pipe(repeat({ delay: () => this.uiService.UpdateRequired }))
		),
		shareReplay({ bufferSize: 1, refCount: true })
	);

	private transactionService = inject(TransactionService);

	get current() {
		return this._MessagePeriodicities;
	}

	get count() {
		return this._count;
	}

	get MessagePeriodicities() {
		return this._MessagePeriodicities;
	}

	getMessagePeriodicity(id: string) {
		return this.uiService.BranchId.pipe(
			take(1),
			switchMap((branchId) =>
				this.MessagePeriodicityService.getOne(branchId, id)
			)
		);
	}

	/**
	 * Used to fetch units with mat-option-loading
	 */
	getFilteredPaginatedMessagePeriodicities(
		pageNum: string | number,
		filter?: string
	) {
		return combineLatest([
			this.uiService.BranchId,
			this.uiService.viewId,
			this.uiService.currentPageSize,
		]).pipe(
			take(1),
			switchMap(([id, viewId, pageSize]) =>
				this.MessagePeriodicityService.getFiltered(
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
				this.MessagePeriodicityService.getCount(id, filter, viewId)
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

	modifyMessagePeriodicity(value: NamedId) {
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

	createMessagePeriodicity(value: string) {
		return this.uiService.BranchId.pipe(
			take(1),
			switchMap((id) =>
				this.transactionService.performMutation({
					branch: id,
					txComment: `Creating Message Periodicity ${value}`,
					createArtifacts: [
						{
							typeId: ARTIFACTTYPEIDENUM.MESSAGEPERIODICITY,
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
