/*********************************************************************
 * Copyright (c) 2022 Boeing
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
import {
	BehaviorSubject,
	Observable,
	combineLatest,
	iif,
	merge,
	of,
} from 'rxjs';
import {
	take,
	map,
	switchMap,
	debounceTime,
	distinctUntilChanged,
	shareReplay,
	tap,
} from 'rxjs/operators';
import { BranchInfoService, WorktypeService } from '@osee/shared/services';
import { UiService } from '@osee/shared/services';
import { BranchCategoryService } from './branch-category.service';
import { BranchPageService } from './branch-page.service';
import { takeUntilDestroyed } from '@angular/core/rxjs-interop';
import { branch } from '@osee/shared/types';

@Injectable({
	providedIn: 'root',
})
export class BranchListService {
	private _type = this.ui.type.pipe(
		map((type) =>
			type === 'baseline' ? '2' : type == 'working' ? '0' : '-1'
		)
	);

	private _branchFilter = new BehaviorSubject<string>('');

	private _branches = combineLatest([
		this._type,
		this.categoryService.branchCategory,
		this._branchFilter,
		this.workTypeService.workType,
	]).pipe(
		switchMap(([type, category, filter, workType]) =>
			iif(
				() => type !== '-1',
				this.branchService.getBranches(
					type,
					category,
					workType,
					filter
				),
				of()
			)
		)
	);

	//TODO align with current-branch-info.service?
	private _currentBranch = this.ui.id.pipe(
		switchMap((id) =>
			iif(
				() => id !== '' && id !== '-1' && id !== '0',
				this.branchService.getBranch(id),
				of({ name: '' } as branch)
			)
		)
	);
	private _currentBranchName = this._currentBranch.pipe(
		map((br) => br.name),
		tap((name) => (this.filter = name))
	);

	private _filter = merge(this._currentBranchName, this._branchFilter).pipe(
		distinctUntilChanged(),
		debounceTime(500),
		shareReplay({ bufferSize: 1, refCount: true }),
		takeUntilDestroyed()
	);
	constructor(
		private branchService: BranchInfoService,
		private ui: UiService,
		private categoryService: BranchCategoryService,
		private workTypeService: WorktypeService,
		private pageSizeService: BranchPageService
	) {}

	get branches() {
		return this._branches;
	}
	get pageSize() {
		return this.pageSizeService.pageSize;
	}
	get filter(): Observable<string> {
		return this._filter;
	}

	set filter(value: string) {
		this._branchFilter.next(value);
	}

	getFilteredPaginatedBranches(pageNum: string | number, filter?: string) {
		return combineLatest([
			this._type,
			this.categoryService.branchCategory,
			this.workTypeService.workType,
			this.pageSizeService.pageSize,
		]).pipe(
			take(1),
			switchMap(([type, category, workType, pageSize]) =>
				this.branchService.getBranches(
					type,
					category,
					workType,
					filter,
					pageSize,
					pageNum
				)
			)
		);
	}

	getFilteredCount(pageNum: string | number, filter?: string) {
		return combineLatest([
			this._type,
			this.categoryService.branchCategory,
			this.workTypeService.workType,
		]).pipe(
			take(1),
			switchMap(([type, category, workType]) =>
				this.branchService.getBranchCount(
					type,
					category,
					workType,
					filter
				)
			)
		);
	}
}
