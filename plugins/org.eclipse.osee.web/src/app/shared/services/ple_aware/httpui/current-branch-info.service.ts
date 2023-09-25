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
import { branch } from '@osee/shared/types';
import { BranchInfoService } from '../http/branch-info.service';
import { UiService } from '../ui/ui.service';

export class branchImpl implements branch {
	associatedArtifact = '-1';
	baselineTx = '';
	parentTx = '';
	parentBranch = {
		id: '',
		viewId: '',
	};
	branchState = '-1';
	branchType = '-1';
	inheritAccessControl = false;
	archived = false;
	shortName = '';
	idIntValue = -1;
	name = '';
	id = '-1';
	viewId = '-1';
}

@Injectable({
	providedIn: 'root',
})
export class CurrentBranchInfoService {
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
	constructor(
		private _branchService: BranchInfoService,
		private _uiService: UiService
	) {}

	get currentBranch() {
		return this._currentBranch;
	}

	get parentBranch() {
		return this.currentBranch.pipe(
			//take(1),
			map((branches) => branches.parentBranch.id)
		);
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
								if (val.results.results.length > 0) {
									this._uiService.ErrorText =
										val.results.results[0];
								}
							})
						),

					of() // @todo replace with a false response
				)
			)
		);
	}
}
