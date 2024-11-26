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
import {
	animate,
	state,
	style,
	transition,
	trigger,
} from '@angular/animations';
import { AsyncPipe } from '@angular/common';
import {
	ChangeDetectionStrategy,
	Component,
	inject,
	Input,
	OnChanges,
	Output,
	SimpleChanges,
} from '@angular/core';
import { ControlContainer, FormsModule, NgForm } from '@angular/forms';
import {
	MatAutocomplete,
	MatAutocompleteTrigger,
} from '@angular/material/autocomplete';
import { MatIconButton } from '@angular/material/button';
import {
	ErrorStateMatcher,
	MatOption,
	ShowOnDirtyErrorStateMatcher,
} from '@angular/material/core';
import { MatFormField, MatHint, MatSuffix } from '@angular/material/form-field';
import { MatIcon } from '@angular/material/icon';
import { MatInput } from '@angular/material/input';
import { MatTooltip } from '@angular/material/tooltip';
import { CrossReferenceService } from '@osee/messaging/shared/services';
import { MatOptionLoadingComponent } from '@osee/shared/components';
import { NamedId } from '@osee/shared/types';
import {
	BehaviorSubject,
	debounceTime,
	distinctUntilChanged,
	of,
	ReplaySubject,
	skip,
	Subject,
	switchMap,
} from 'rxjs';

@Component({
	selector: 'osee-cross-reference-dropdown',
	changeDetection: ChangeDetectionStrategy.OnPush,
	imports: [
		AsyncPipe,
		FormsModule,
		MatOptionLoadingComponent,
		MatFormField,
		MatInput,
		MatAutocomplete,
		MatAutocompleteTrigger,
		MatHint,
		MatSuffix,
		MatIcon,
		MatIconButton,
		MatOption,
		MatTooltip,
	],
	templateUrl: './cross-reference-dropdown.component.html',
	styles: [],
	animations: [
		trigger('dropdownOpen', [
			state(
				'open',
				style({
					opacity: 0,
				})
			),
			state(
				'closed',
				style({
					opacity: 1,
				})
			),
			transition('open=>closed', [animate('0.5s')]),
			transition('closed=>open', [animate('0.5s 0.25s')]),
		]),
	],
	viewProviders: [{ provide: ControlContainer, useExisting: NgForm }],
})
export class CrossReferenceDropdownComponent implements OnChanges {
	private _currentCrossRefService = inject(CrossReferenceService);

	private _typeAhead = new BehaviorSubject<string>('');
	private _openAutoComplete = new ReplaySubject<void>();

	private _isOpen = new BehaviorSubject<boolean>(false);

	@Input() id = '';
	@Input() name = '';
	@Input() required = false;

	@Input() disabled = false;

	@Input() hintHidden = false;
	@Input() crossRef = '';

	_crossRefChange = new Subject<string>();
	@Output() crossRefChange = this._crossRefChange.pipe(
		debounceTime(100),
		skip(1)
	);

	@Input() errorMatcher: ErrorStateMatcher =
		new ShowOnDirtyErrorStateMatcher();

	@Input() allowOutsideValues = false;

	@Input() alternateObjectType = '';

	@Input() maximum = '';

	@Input() minimum = '';

	private _previousValue = '';

	protected _size = this._currentCrossRefService.currentPageSize;

	protected _crossRefs = this._openAutoComplete.pipe(
		debounceTime(500),
		distinctUntilChanged(),
		switchMap((_) =>
			this._typeAhead.pipe(
				distinctUntilChanged(),
				debounceTime(500),
				switchMap((filter) =>
					of((pageNum: string | number) =>
						this._currentCrossRefService.getFilteredPaginatedCrossRefs(
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
			this._typeAhead.pipe(
				switchMap((filter) =>
					this._currentCrossRefService.getFilteredCount(filter)
				)
			)
		)
	);

	get filter() {
		return this._typeAhead;
	}

	updateTypeAhead(value: string | NamedId) {
		if (typeof value === 'string') {
			this._typeAhead.next(value);
			if (this.allowOutsideValues && value !== this._previousValue) {
				this.updateValue(value);
			}
		} else {
			this._typeAhead.next(value.name);
			if (this.allowOutsideValues && value.name !== this._previousValue) {
				this.updateValue(value.name);
			}
		}
	}
	autoCompleteOpened() {
		this._openAutoComplete.next();
		this._isOpen.next(true);
	}
	close() {
		this._isOpen.next(false);
	}
	updateValue(value: string) {
		this._crossRefChange.next(value);
		if (this._typeAhead.getValue() !== value) {
			this.updateTypeAhead(value);
		}
		this._previousValue = value;
	}

	ngOnChanges(changes: SimpleChanges): void {
		if (
			changes.crossRef !== undefined &&
			changes.crossRef.previousValue !== changes.crossRef.currentValue &&
			changes.crossRef.currentValue !== undefined
		) {
			this.updateValue(changes.crossRef.currentValue);
		}
	}
	get isOpen() {
		return this._isOpen;
	}
	clear() {
		this.updateTypeAhead('');
	}
}
