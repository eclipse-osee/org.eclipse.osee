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
import { MatOption } from '@angular/material/core';
import { MatFormField, MatSuffix } from '@angular/material/form-field';
import { MatIcon } from '@angular/material/icon';
import { MatInput } from '@angular/material/input';
import { applic } from '@osee/applicability/types';
import { MatOptionLoadingComponent } from '@osee/shared/components';
import { ApplicabilityListUIService } from '@osee/shared/services';
import {
	ReplaySubject,
	combineLatest,
	debounceTime,
	distinctUntilChanged,
	distinctUntilKeyChanged,
	of,
	switchMap,
} from 'rxjs';

/**
 * Component used for selecting an applicability.
 *
 * @example HTML:	
		Typescript:
		@Component({
		selector: 'example-component'
		template: '<osee-applicability-selector
			[applicability]="value"
			(applicabilityChange)="updateValue($event)">
		</osee-applicability-selector>'
		styles: ''
		standalone: true,
		imports: ApplicabilityDropdownComponent
		})
		export class ExampleComponent {}
 */
@Component({
	selector: 'osee-applicability-dropdown',
	template: `<mat-form-field
		subscriptSizing="dynamic"
		id="applicability-selector"
		class="tw-w-full [&>.mdc-text-field--filled]:tw-bg-inherit [&>.mdc-text-field--filled]:tw-text-inherit">
		<input
			type="text"
			matInput
			class="tw-text-inherit"
			name="applicability"
			[required]="required()"
			[disabled]="disabled()"
			[(ngModel)]="filter"
			(focusin)="autoCompleteOpened()"
			[matAutocomplete]="autoApplicability" />
		<mat-icon matIconSuffix>arrow_drop_down</mat-icon>
		<mat-autocomplete
			autoActiveFirstOption
			#autoApplicability="matAutocomplete"
			(optionSelected)="applicability.set($event.option.value)">
			@if (applicabilities | async; as _applics) {
				@if (applicabilityCount | async; as _count) {
					<osee-mat-option-loading
						[data]="_applics"
						objectName="applicability"
						[paginationSize]="3"
						paginationMode="AUTO"
						[count]="_count">
						<ng-template let-option>
							<mat-option
								[attr.data-cy]="'option-' + option.name"
								[value]="option"
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
		</mat-autocomplete>
	</mat-form-field>`,
	changeDetection: ChangeDetectionStrategy.OnPush,
	imports: [
		AsyncPipe,
		FormsModule,
		MatFormField,
		MatInput,
		MatAutocomplete,
		MatAutocompleteTrigger,
		MatIcon,
		MatSuffix,
		MatOption,
		MatOptionLoadingComponent,
	],
})
export class ApplicabilityDropdownComponent {
	private applicService = inject(ApplicabilityListUIService);

	protected filter = signal('');
	private _typeAhead = toObservable(this.filter);
	private _openAutoComplete = new ReplaySubject<void>();

	applicability = model<applic>({ id: '-1', name: '' });
	required = input(false);
	disabled = input(false);

	private _updateFilterBasedOnApplic = effect(
		() => {
			this.filter.set(this.applicability().name);
		},
		{ allowSignalWrites: true }
	);

	count = input(3);

	private _count = toObservable(this.count);

	applicabilities = this._openAutoComplete.pipe(
		distinctUntilChanged(),
		switchMap((_) =>
			combineLatest([this._typeAhead, this._count]).pipe(
				distinctUntilKeyChanged(0),
				debounceTime(500),
				switchMap(([filter, count]) =>
					of((pageNum: string | number) =>
						this.applicService.getApplicabilities(
							pageNum,
							count,
							filter
						)
					)
				)
			)
		)
	);

	applicabilityCount = this._openAutoComplete.pipe(
		distinctUntilChanged(),
		switchMap((_) =>
			this._typeAhead.pipe(
				switchMap((filter) =>
					this.applicService.getApplicabilityCount(filter)
				)
			)
		)
	);

	autoCompleteOpened() {
		this._openAutoComplete.next();
	}
}
