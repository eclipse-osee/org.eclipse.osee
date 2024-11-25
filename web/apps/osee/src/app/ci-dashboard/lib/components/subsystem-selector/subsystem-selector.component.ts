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
	ChangeDetectionStrategy,
	Component,
	effect,
	inject,
	input,
	signal,
} from '@angular/core';
import { FormsModule } from '@angular/forms';
import {
	MatAutocomplete,
	MatAutocompleteTrigger,
	MatOption,
} from '@angular/material/autocomplete';
import {
	MatFormField,
	MatPrefix,
	MatSuffix,
} from '@angular/material/form-field';
import { MatInput } from '@angular/material/input';
import { MatOptionLoadingComponent } from '@osee/shared/components';
import { DashboardService } from '../../services/dashboard.service';
import { toObservable, toSignal } from '@angular/core/rxjs-interop';
import {
	debounceTime,
	distinctUntilChanged,
	filter,
	of,
	switchMap,
} from 'rxjs';
import { MatIcon } from '@angular/material/icon';
import { DefReference } from '../../types/tmo';
import { ATTRIBUTETYPEIDENUM } from '@osee/attributes/constants';

@Component({
	selector: 'osee-subsystem-selector',
	changeDetection: ChangeDetectionStrategy.OnPush,
	imports: [
		MatFormField,
		MatInput,
		MatAutocomplete,
		MatAutocompleteTrigger,
		MatOptionLoadingComponent,
		FormsModule,
		MatOption,
		MatIcon,
		MatSuffix,
		MatPrefix,
	],
	template: `
		<mat-form-field
			subscriptSizing="dynamic"
			class="tw-w-full">
			<input
				type="text"
				matInput
				[(ngModel)]="filter"
				placeholder="Select a Subsystem"
				(focusin)="openAutoComplete()"
				(focusout)="closeAutoComplete()"
				[matAutocomplete]="autocomplete" />
			@if (!autoCompleteOpened()) {
				<mat-icon matIconSuffix>arrow_drop_down</mat-icon>
			}
			@if (autoCompleteOpened() && filter() !== '') {
				<button
					mat-icon-button
					matIconSuffix
					class="tw-px-2"
					(mousedown)="clearFilter()">
					<mat-icon>close</mat-icon>
				</button>
			}
			<mat-autocomplete
				#autocomplete="matAutocomplete"
				(optionSelected)="selectValue($event.option.value)">
				@if (
					subsystems() === undefined &&
					subsystemsCount() === undefined
				) {
					<mat-option
						id="-1"
						disabled
						[value]="{ id: '-1', name: 'invalid' }">
						Loading...
					</mat-option>
				} @else {
					<osee-mat-option-loading
						[data]="subsystems()!"
						objectName="subsystems"
						[paginationSize]="pageSize"
						paginationMode="AUTO"
						[count]="subsystemsCount()!"
						[noneOption]="filter() === '' ? noneOption : undefined">
						<ng-template let-option>
							<mat-option
								[attr.data-cy]="'option-' + option.name"
								[value]="option.name"
								[id]="option.id">
								@if (option.name === '') {
									None
								} @else {
									{{ option.name }}
								}
							</mat-option>
						</ng-template>
					</osee-mat-option-loading>
				}
			</mat-autocomplete>
		</mat-form-field>
	`,
})
export class SubsystemSelectorComponent {
	script = input.required<DefReference>();

	dashboardService = inject(DashboardService);

	pageSize = 100;
	noneOption = { id: '-1', name: '' };

	filter = signal('');
	private _filter$ = toObservable(this.filter).pipe(debounceTime(250));
	autoCompleteOpened = signal(false);
	private _autoCompleteOpened$ = toObservable(this.autoCompleteOpened);

	private _scriptEffect = effect(
		() => this.filter.set(this.script().subsystem),
		{
			allowSignalWrites: true,
		}
	);

	private _subsystems$ = this._autoCompleteOpened$.pipe(
		debounceTime(250),
		distinctUntilChanged(),
		filter((opened) => opened === true),
		switchMap((_) =>
			this._filter$.pipe(
				switchMap((filter) =>
					of((pageNum: string | number) =>
						this.dashboardService.getSubsystemsPaginated(
							filter,
							pageNum,
							this.pageSize,
							ATTRIBUTETYPEIDENUM.NAME
						)
					)
				)
			)
		)
	);

	subsystems = toSignal(this._subsystems$);

	private _subsystemsCount$ = this._autoCompleteOpened$.pipe(
		debounceTime(250),
		distinctUntilChanged(),
		filter((opened) => opened === true),
		switchMap((_) =>
			this._filter$.pipe(
				switchMap((filter) =>
					this.dashboardService.getSubsystemsCount(filter)
				)
			)
		)
	);

	subsystemsCount = toSignal(this._subsystemsCount$);

	selectValue(value: string) {
		this.filter.set(value);
		if (this.script().id !== '-1' && this.script().subsystem !== value) {
			this.dashboardService
				.updateAttribute(
					this.script().id,
					ATTRIBUTETYPEIDENUM.SCRIPTSUBSYSTEM,
					value
				)
				.subscribe();
		}
	}

	openAutoComplete() {
		this.autoCompleteOpened.set(true);
	}

	closeAutoComplete() {
		this.autoCompleteOpened.set(false);
	}

	clearFilter() {
		this.filter.set('');
	}
}
