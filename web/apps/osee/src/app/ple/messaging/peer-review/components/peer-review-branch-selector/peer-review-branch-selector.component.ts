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
import { Component, inject, input, linkedSignal } from '@angular/core';
import { FormsModule } from '@angular/forms';
import {
	MatAutocomplete,
	MatAutocompleteTrigger,
	MatOption,
} from '@angular/material/autocomplete';
import {
	ErrorStateMatcher,
	ShowOnDirtyErrorStateMatcher,
} from '@angular/material/core';
import { MatFormField, MatLabel } from '@angular/material/form-field';
import { MatInput } from '@angular/material/input';
import { MatOptionLoadingComponent } from '@osee/shared/components';
import { branch } from '@osee/shared/types';
import { PeerReviewUiService } from '../../services/peer-review-ui.service';
import { toObservable, toSignal } from '@angular/core/rxjs-interop';
import {
	debounceTime,
	distinctUntilChanged,
	of,
	repeat,
	switchMap,
} from 'rxjs';
import { workType } from '@osee/shared/types/configuration-management';
import { UiService } from '@osee/shared/services';

@Component({
	selector: 'osee-peer-review-branch-selector',
	imports: [
		MatFormField,
		MatInput,
		MatAutocomplete,
		MatOptionLoadingComponent,
		MatOption,
		FormsModule,
		MatLabel,
		MatAutocompleteTrigger,
	],
	templateUrl: './peer-review-branch-selector.component.html',
})
export class PeerReviewBranchSelectorComponent {
	pageSize = input(10);
	workType = input<workType>('MIM');

	private peerReviewUiService = inject(PeerReviewUiService);
	private uiService = inject(UiService);

	private _prBranchId = toSignal(this.peerReviewUiService.prBranchId);
	private _prBranch = toSignal(this.peerReviewUiService.prBranch);

	filter = linkedSignal(() => {
		if (this._prBranchId() === '-1') {
			return '';
		}
		return this._prBranch()?.name || '';
	});
	private _filter$ = toObservable(this.filter);

	branchCount = toSignal(
		toObservable(this.filter).pipe(
			debounceTime(250),
			distinctUntilChanged(),
			switchMap((filter) =>
				this.peerReviewUiService.getPeerReviewBranchesCount(
					filter,
					'0',
					this.workType()
				)
			),
			repeat({ delay: () => this.uiService.update })
		),
		{ initialValue: 0 }
	);

	branches = toSignal(
		this._filter$.pipe(
			debounceTime(250),
			distinctUntilChanged(),
			switchMap((filter) =>
				of((pageNum: string | number) =>
					this.peerReviewUiService.getPeerReviewBranches(
						filter,
						'0',
						this.workType(),
						this.pageSize(),
						pageNum
					)
				)
			),
			repeat({ delay: () => this.uiService.update })
		),
		{ initialValue: (_: string | number) => of([] as branch[]) }
	);

	errorMatcher: ErrorStateMatcher = new ShowOnDirtyErrorStateMatcher();

	updateFilter(value: string | branch) {
		if (typeof value === 'string') {
			this.filter.set(value);
		} else {
			this.filter.set(value.name);
		}
	}

	displayFn(val: branch) {
		return val?.name || '';
	}

	selectBranch(branch: branch) {
		this.updateFilter(branch);
		this.peerReviewUiService.PRBranchId = branch.id;
	}
}
