/*********************************************************************
 * Copyright (c) 2021 Boeing
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
import { AsyncPipe, NgClass } from '@angular/common';
import { Component, Input, inject } from '@angular/core';
import { FormsModule } from '@angular/forms';
import {
	MatAutocomplete,
	MatAutocompleteTrigger,
} from '@angular/material/autocomplete';
import {
	ErrorStateMatcher,
	MatOption,
	ShowOnDirtyErrorStateMatcher,
} from '@angular/material/core';
import { MatFormField, MatLabel } from '@angular/material/form-field';
import { MatInput } from '@angular/material/input';
import { HttpLoadingService } from '@osee/shared/services/network';
import { branch } from '@osee/shared/types';
import {
	BehaviorSubject,
	ReplaySubject,
	combineLatest,
	debounceTime,
	distinctUntilChanged,
	of,
	switchMap,
} from 'rxjs';
import { BranchListService } from '../../../../internal/services/branch-list.service';
import { MatOptionLoadingComponent } from '../../../../mat-option-loading/mat-option-loading/mat-option-loading.component';
import { BranchRoutedUIService } from '@osee/shared/services';

@Component({
	selector: 'osee-branch-selector',
	templateUrl: './branch-selector.component.html',
	imports: [
		FormsModule,
		AsyncPipe,
		MatFormField,
		MatLabel,
		MatInput,
		MatAutocomplete,
		MatAutocompleteTrigger,
		MatOption,
		MatOptionLoadingComponent,
		NgClass,
	],
})
export class BranchSelectorComponent {
	private routeState = inject(BranchRoutedUIService);
	private branchListingService = inject(BranchListService);
	private loadingService = inject(HttpLoadingService);

	selectedBranchType = this.routeState.type;
	selectedBranchId = '';
	options = this.branchListingService.branches;
	loading = this.loadingService.isLoading;

	private _typeAhead = this.branchListingService.filter;
	private _isOpen = new BehaviorSubject<boolean>(false);
	private _openAutoComplete = new ReplaySubject<void>();
	protected _size = this.branchListingService.pageSize;
	protected _branches = this._openAutoComplete.pipe(
		debounceTime(500),
		distinctUntilChanged(),
		switchMap((_) =>
			combineLatest([
				this._typeAhead.pipe(distinctUntilChanged(), debounceTime(500)),
				this.selectedBranchType,
			]).pipe(
				switchMap(([filter, _type]) =>
					of((pageNum: string | number) =>
						this.branchListingService.getFilteredPaginatedBranches(
							pageNum,
							filter
						)
					)
				)
			)
		)
	);

	_count = this._openAutoComplete.pipe(
		debounceTime(500),
		distinctUntilChanged(),
		switchMap((_) =>
			combineLatest([
				this._typeAhead.pipe(distinctUntilChanged(), debounceTime(500)),
				this.selectedBranchType,
			]).pipe(
				switchMap(([filter, _type]) =>
					this.branchListingService.getFilteredCount(filter)
				)
			)
		)
	);

	@Input() errorMatcher: ErrorStateMatcher =
		new ShowOnDirtyErrorStateMatcher();

	/** Inserted by Angular inject() migration for backwards compatibility */
	constructor(...args: unknown[]);
	constructor() {
		this.routeState.id.subscribe((val) => {
			this.selectedBranchId = val;
		});
	}
	get filter() {
		return this._typeAhead;
	}
	updateTypeAhead(value: string | branch) {
		if (typeof value === 'string') {
			this.branchListingService.filter = value;
		} else {
			this.branchListingService.filter = value.name;
		}
	}
	autoCompleteOpened() {
		this._openAutoComplete.next();
		this._isOpen.next(true);
	}
	close() {
		this._isOpen.next(false);
	}

	selectBranch(event: branch) {
		this.routeState.branchId = event.id;
		this.updateTypeAhead(event.name);
	}

	get isOpen() {
		return this._isOpen;
	}
	clear() {
		this.updateTypeAhead('');
	}

	displayFn(val: branch) {
		return val?.name || '';
	}
}
