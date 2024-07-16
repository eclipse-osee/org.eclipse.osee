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
import {
	MatFormField,
	MatHint,
	MatPrefix,
	MatSuffix,
} from '@angular/material/form-field';
import { MatIcon } from '@angular/material/icon';
import { MatInput } from '@angular/material/input';
import { CurrentUnitsService } from '@osee/messaging/shared/services';
import { MatOptionLoadingComponent } from '@osee/shared/components';
import { NamedId } from '@osee/shared/types';
import {
	BehaviorSubject,
	debounceTime,
	distinctUntilChanged,
	of,
	ReplaySubject,
	Subject,
	switchMap,
} from 'rxjs';

@Component({
	selector: 'osee-unit-dropdown',
	standalone: true,
	changeDetection: ChangeDetectionStrategy.OnPush,
	imports: [
		AsyncPipe,
		FormsModule,
		MatFormField,
		MatInput,
		MatAutocomplete,
		MatAutocompleteTrigger,
		MatHint,
		MatPrefix,
		MatSuffix,
		MatIcon,
		MatIconButton,
		MatOption,
		MatOptionLoadingComponent,
	],
	templateUrl: './unit-dropdown.component.html',
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
export class UnitDropdownComponent implements OnChanges {
	private _currentUnitsService = inject(CurrentUnitsService);

	private _typeAhead = new BehaviorSubject<string>('');
	private _openAutoComplete = new ReplaySubject<void>();

	private _isOpen = new BehaviorSubject<boolean>(false);

	@Input() required: boolean = false;
	@Input() disabled: boolean = false;

	@Input() hintHidden: boolean = false;
	@Input() unit: string = '';

	@Output() unitChange = new Subject<string>();

	@Input() errorMatcher: ErrorStateMatcher =
		new ShowOnDirtyErrorStateMatcher();

	protected _size = this._currentUnitsService.currentPageSize;

	protected _units = this._openAutoComplete.pipe(
		debounceTime(500),
		distinctUntilChanged(),
		switchMap((_) =>
			this._typeAhead.pipe(
				distinctUntilChanged(),
				debounceTime(500),
				switchMap((filter) =>
					of((pageNum: string | number) =>
						this._currentUnitsService.getFilteredPaginatedUnits(
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
					this._currentUnitsService.getFilteredCount(filter)
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
		} else {
			this._typeAhead.next(value.name);
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
		this.unitChange.next(value);
		this.updateTypeAhead(value);
	}

	ngOnChanges(changes: SimpleChanges): void {
		if (
			changes.unit !== undefined &&
			changes.unit.previousValue !== changes.unit.currentValue &&
			changes.unit.currentValue !== undefined
		) {
			this.updateValue(changes.unit.currentValue);
		}
	}
	get isOpen() {
		return this._isOpen;
	}
	clear() {
		this.updateTypeAhead('');
	}
}
