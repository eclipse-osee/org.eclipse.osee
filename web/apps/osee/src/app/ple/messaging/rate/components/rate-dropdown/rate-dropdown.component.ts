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
	effect,
	inject,
	input,
	model,
	signal,
} from '@angular/core';
import { toObservable } from '@angular/core/rxjs-interop';
import { FormsModule } from '@angular/forms';
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
import { ATTRIBUTETYPEIDENUM } from '@osee/attributes/constants';
import { attribute } from '@osee/attributes/types';
import { CurrentRatesService } from '@osee/messaging/rate/services';
import { MatOptionLoadingComponent } from '@osee/shared/components';
import {
	provideOptionalControlContainerNgForm,
	provideOptionalControlContainerNgModelGroup,
	writableSlice,
} from '@osee/shared/utils';
import {
	BehaviorSubject,
	ReplaySubject,
	debounceTime,
	distinctUntilChanged,
	of,
	switchMap,
} from 'rxjs';

@Component({
	selector: 'osee-rate-dropdown',
	changeDetection: ChangeDetectionStrategy.OnPush,
	imports: [
		AsyncPipe,
		FormsModule,
		MatFormField,
		MatInput,
		MatAutocomplete,
		MatAutocompleteTrigger,
		MatHint,
		MatSuffix,
		MatIcon,
		MatIconButton,
		MatOption,
		MatOptionLoadingComponent,
	],
	template: `@if (
		{
			openedState: isOpen | async,
		};
		as autoCompleteState
	) {
		<mat-form-field
			subscriptSizing="dynamic"
			id="rates-selector"
			class="tw-w-full [&>.mdc-text-field--filled]:tw-bg-inherit">
			<input
				type="text"
				matInput
				#input
				[(ngModel)]="filter$"
				[required]="required()"
				[disabled]="disabled()"
				placeholder="Rates"
				[errorStateMatcher]="errorMatcher()"
				id="txRates"
				name="txRates"
				(focusin)="autoCompleteOpened()"
				(focusout)="close()"
				#txRates="ngModel"
				[matAutocomplete]="autoRates" />
			@if (!hintHidden()) {
				<mat-hint align="end">Select a Rate</mat-hint>
			}
			@if (!autoCompleteState.openedState) {
				<mat-icon
					[@dropdownOpen]="
						autoCompleteState.openedState ? 'open' : 'closed'
					"
					matIconSuffix
					>arrow_drop_down</mat-icon
				>
			}
			@if (autoCompleteState.openedState && filter$() !== '') {
				<button
					mat-icon-button
					[@dropdownOpen]="
						!autoCompleteState.openedState ? 'open' : 'closed'
					"
					matIconSuffix
					(mousedown)="clear()">
					<mat-icon>close</mat-icon>
				</button>
			}
			<mat-autocomplete
				autoActiveFirstOption="true"
				autoSelectActiveOption="true"
				#autoRates="matAutocomplete"
				(optionSelected)="updateValue($event.option.value)">
				@if (_rates | async; as rates) {
					@if (_count | async; as count) {
						@if (_size | async; as size) {
							<osee-mat-option-loading
								[data]="rates"
								objectName="rates"
								[paginationSize]="size"
								paginationMode="AUTO"
								[count]="count">
								<ng-template let-option>
									<mat-option
										[attr.data-cy]="'option-' + option.name"
										[value]="option.name"
										[id]="option.id">
										{{ option.name }}
									</mat-option>
								</ng-template>
							</osee-mat-option-loading>
						} @else {
							<mat-option
								id="-1"
								[value]="{ id: '-1', name: 'invalid' }">
								Loading...
							</mat-option>
						}
					} @else {
						<mat-option
							id="-1"
							[value]="{ id: '-1', name: 'invalid' }">
							Loading...
						</mat-option>
					}
				} @else {
					<mat-option
						id="-1"
						[value]="{ id: '-1', name: 'invalid' }">
						Loading...
					</mat-option>
				}
			</mat-autocomplete>
		</mat-form-field>
	}`,
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
	viewProviders: [
		provideOptionalControlContainerNgForm(),
		provideOptionalControlContainerNgModelGroup(),
	],
})
export class RateDropdownComponent {
	private _currentRatesService = inject(CurrentRatesService);

	private _openAutoComplete = new ReplaySubject<void>();

	private _isOpen = new BehaviorSubject<boolean>(false);

	required = input(false);
	disabled = input(false);

	hintHidden = input(false);
	value = model<
		attribute<string, typeof ATTRIBUTETYPEIDENUM.INTERFACEMESSAGERATE>
	>({ id: '-1', typeId: '2455059983007225763', gammaId: '-1', value: '' });
	private _rateValue = writableSlice(this.value, 'value');

	protected filter$ = signal(this._rateValue());

	private _syncFilter = effect(
		() => {
			this.filter$.set(this._rateValue());
		},
		{ allowSignalWrites: true }
	);

	private _typeAhead = toObservable(this.filter$);

	errorMatcher = input<ErrorStateMatcher>(new ShowOnDirtyErrorStateMatcher());

	protected _size = this._currentRatesService.currentPageSize;

	protected _rates = this._openAutoComplete.pipe(
		debounceTime(500),
		distinctUntilChanged(),
		switchMap((_) =>
			this._typeAhead.pipe(
				distinctUntilChanged(),
				debounceTime(500),
				switchMap((filter) =>
					of((pageNum: string | number) =>
						this._currentRatesService.getFilteredPaginatedRates(
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
					this._currentRatesService.getFilteredCount(filter)
				)
			)
		)
	);

	autoCompleteOpened() {
		this._openAutoComplete.next();
		this._isOpen.next(true);
	}
	close() {
		this._isOpen.next(false);
	}
	updateValue(value: string) {
		this._rateValue.set(value);
	}

	get isOpen() {
		return this._isOpen;
	}
	clear() {
		this.filter$.set('');
	}
}
