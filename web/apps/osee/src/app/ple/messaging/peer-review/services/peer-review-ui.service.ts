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
import { inject, Injectable, signal } from '@angular/core';
import { workType } from '@osee/shared/types/configuration-management';
import { BranchInfoService, UiService } from '@osee/shared/services';
import {
	BehaviorSubject,
	combineLatest,
	debounceTime,
	filter,
	repeat,
	switchMap,
	tap,
} from 'rxjs';
import { PeerReviewService } from './peer-review.service';
import { branchSelected, peerReviewApplyData } from '../types/peer-review';
import { branch } from '@osee/shared/types';
import { toObservable } from '@angular/core/rxjs-interop';

@Injectable({
	providedIn: 'root',
})
export class PeerReviewUiService {
	private branchInfoService = inject(BranchInfoService);
	private prService = inject(PeerReviewService);
	private uiService = inject(UiService);

	private _prBranchId$ = new BehaviorSubject<`${number}`>('-1');

	workingBranchFilter = signal('');
	branchesToAdd = signal<branch[]>([]);
	branchesToRemove = signal<branch[]>([]);

	private _workingBranchFilter$ = toObservable(this.workingBranchFilter);

	prBranch = this._prBranchId$.pipe(
		tap(() => this.resetBranchSelections()),
		filter((id) => id !== '-1'),
		switchMap((id) => this.branchInfoService.getBranch(id))
	);

	workingBranches = combineLatest([
		this._workingBranchFilter$,
		this._prBranchId$,
	]).pipe(
		debounceTime(250),
		filter(([_, id]) => id !== '-1'),
		switchMap(([filter, id]) =>
			this.prService
				.getWorkingBranches(id, '0', '3', 'MIM', filter, 0, 0)
				.pipe(repeat({ delay: () => this.uiService.update }))
		)
	);

	resetBranchSelections() {
		this.branchesToAdd.set([]);
		this.branchesToRemove.set([]);
	}

	handleBranchSelection(branchSelection: branchSelected, selected: boolean) {
		if (selected) {
			if (!branchSelection.selected) {
				this.branchesToAdd.update((curr) => [
					...curr,
					branchSelection.branch,
				]);
			} else {
				this.branchesToRemove.update((curr) =>
					curr.filter(
						(branch) => branch.id !== branchSelection.branch.id
					)
				);
			}
		} else {
			if (!branchSelection.selected) {
				this.branchesToAdd.update((curr) =>
					curr.filter(
						(branch) => branch.id !== branchSelection.branch.id
					)
				);
			} else {
				this.branchesToRemove.update((curr) => [
					...curr,
					branchSelection.branch,
				]);
			}
		}
	}

	getPeerReviewBranches(
		filter: string,
		branchType: string,
		workType: workType,
		pageSize: number,
		pageNum: string | number
	) {
		return this.branchInfoService.getBranches(
			branchType,
			'5',
			'-1',
			workType,
			filter,
			pageSize,
			pageNum
		);
	}

	getPeerReviewBranchesCount(
		filter: string,
		branchType: string,
		workType: workType
	) {
		return this.branchInfoService.getBranchCount(
			branchType,
			'5',
			'-1',
			workType,
			filter
		);
	}

	applyWorkingBranches() {
		const data: peerReviewApplyData = {
			addBranches: this.branchesToAdd().map((b) => b.id),
			removeBranches: this.branchesToRemove().map((b) => b.id),
		};
		return this._prBranchId$.pipe(
			filter((id) => id !== '-1'),
			switchMap((id) => this.prService.applyWorkingBranches(id, data)),
			tap((res) => {
				if (res.success) {
					this.uiService.updated = true;
					this.resetBranchSelections();
				}
			})
		);
	}

	get prBranchId() {
		return this._prBranchId$.asObservable();
	}

	set PRBranchId(id: `${number}`) {
		this._prBranchId$.next(id);
	}
}
