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
import { Injectable, computed, inject } from '@angular/core';
import { iif, of } from 'rxjs';
import {
	filter,
	map,
	repeatWhen,
	share,
	shareReplay,
	switchMap,
	take,
	tap,
} from 'rxjs/operators';
import { branch, branchCategorySentinel } from '@osee/shared/types';
import { BranchInfoService } from '../http/branch-info.service';
import { UiService } from '../ui/ui.service';
import { BranchCommitEventService } from '../ui/event/branch-commit-event.service';
import { toSignal } from '@angular/core/rxjs-interop';

export class branchImpl implements branch {
	associatedArtifact = '-1';
	baselineTx = '';
	parentTx = '';
	parentBranch = {
		id: '-1' as `${number}`,
		viewId: '',
	};
	branchState = '-1';
	branchType = '-1';
	inheritAccessControl = false;
	archived = false;
	shortName = '';
	idIntValue = -1;
	name = '';
	id: `${number}` = '-1';
	viewId = '-1';
	categories = [branchCategorySentinel];
}

@Injectable({
	providedIn: 'root',
})
export class CurrentBranchInfoService {
	private _branchService = inject(BranchInfoService);
	private _uiService = inject(UiService);
	private eventService = inject(BranchCommitEventService);

	private readonly _currentBranch = this._uiService.id.pipe(
		filter((val) => val !== '0'),
		switchMap((branchId) =>
			iif(
				() => branchId !== '0' && branchId !== '',
				this._branchService.getBranch(branchId).pipe(
					repeatWhen((_) => this._uiService.update),
					share()
				),
				of(new branchImpl())
			)
		),
		share(),
		shareReplay({ bufferSize: 1, refCount: true })
	);

	get currentBranch() {
		return this._currentBranch;
	}

	get parentBranch() {
		return this.currentBranch.pipe(
			map((branches) => branches.parentBranch.id)
		);
	}

	private readonly _branchCategories = toSignal(
		this.currentBranch.pipe(map((currBranch) => currBranch.categories)),
		{
			initialValue: [],
		}
	);

	get branchHasPleCategory() {
		return computed(() => {
			return this._branchCategories().some(
				(category) => category.name == 'PLE'
			);
		});
	}

	commitBranch(body: { committer: string; archive: string }) {
		return this.currentBranch.pipe(
			take(1),
			switchMap((detail) =>
				iif(
					() =>
						detail.parentBranch.id.length > 0 &&
						detail.id.length > 0,
					this._branchService
						.commitBranch(detail.id, detail.parentBranch.id, body)
						.pipe(
							tap((val) => {
								if (!val.success) {
									this._uiService.ErrorText =
										'Error committing branch';
								} else {
									this.eventService.sendEvent(detail.id);
								}
							})
						),

					of() // @todo replace with a false response
				)
			)
		);
	}
}
