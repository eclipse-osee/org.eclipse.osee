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
import { AsyncPipe, NgClass } from '@angular/common';
import {
	Component,
	computed,
	inject,
	input,
	model,
	signal,
} from '@angular/core';
import { toObservable } from '@angular/core/rxjs-interop';
import { FormsModule } from '@angular/forms';
import {
	MatAutocomplete,
	MatAutocompleteSelectedEvent,
	MatAutocompleteTrigger,
} from '@angular/material/autocomplete';
import { MatFormField } from '@angular/material/form-field';
import { MatInput } from '@angular/material/input';
import { MatOption, MatSelect } from '@angular/material/select';
import { MatTooltip } from '@angular/material/tooltip';
import { MatOptionLoadingComponent } from '@osee/shared/components';
import { applicWithGamma } from '@osee/applicability/types';
import { writableSlice } from '@osee/shared/utils';
import {
	ReplaySubject,
	combineLatest,
	debounceTime,
	distinctUntilChanged,
	filter,
	of,
	switchMap,
} from 'rxjs';
import { PlConfigCurrentBranchService } from '../../services/pl-config-current-branch.service';
import { SplitApplicabilityPipe } from '../split-applicability.pipe';
let nextUniqueId = 0;
@Component({
	selector: 'osee-plconfig-select-single-tuple',
	imports: [
		MatFormField,
		NgClass,
		MatSelect,
		MatInput,
		MatAutocomplete,
		MatAutocompleteTrigger,
		MatOptionLoadingComponent,
		MatTooltip,
		FormsModule,
		MatOption,
		AsyncPipe,
		SplitApplicabilityPipe,
	],
	template: ` <mat-form-field
		subscriptSizing="dynamic"
		id="selectableTableOption"
		class="tw-w-full tw-bg-inherit [&>.mdc-text-field--filled]:tw-bg-inherit">
		<input
			matInput
			type="text"
			#input
			placeholder="Select a feature value"
			[name]="'select-single-tuple-' + _componentId()"
			class="tw-text-inherit tw-placeholder-inherit"
			[(ngModel)]="filterString"
			(focusin)="autoCompleteOpened()"
			(focusout)="close()"
			[matAutocomplete]="autoSingleSelectTuple" />
		<mat-autocomplete
			#autoSingleSelectTuple
			[displayWith]="displayFn"
			(optionSelected)="set($event)"
			hideSingleSelectionIndicator>
			@if (tuples | async; as _tuples) {
				@if (availableTuplesCount | async; as _count) {
					<osee-mat-option-loading
						[data]="_tuples"
						objectName="Feature"
						[paginationSize]="10"
						paginationMode="AUTO"
						[count]="_count">
						<ng-template let-option>
							<mat-option
								[value]="option"
								[id]="option.e2"
								[disabled]="option.constrained">
								<span
									[matTooltip]="
										option.constrained
											? 'Requires ' +
												option.constrainedBy +
												' to be set'
											: ''
									"
									matTooltipDisabled="false"
									class="tw-pointer-events-auto">
									{{ option.value }}
								</span>
							</mat-option>
						</ng-template>
					</osee-mat-option-loading>
				}
			}
		</mat-autocomplete>
	</mat-form-field>`,
})
export class PLConfigSelectSingleTupleComponent {
	protected _componentId = signal(`${nextUniqueId++}`);
	public value = model.required<applicWithGamma>();

	protected filterString = writableSlice(this.value, 'name');
	protected computedFilter = computed(() =>
		this.filterString().split(new RegExp('s?=s?'))
	);
	private _openAutoComplete = new ReplaySubject<void>();

	private _isOpen = signal(false);
	private filter$ = toObservable(this.filterString);
	public featureId = input.required<string>();
	public configId = input.required<string>();

	private currentBranchService = inject(PlConfigCurrentBranchService);

	private _configId$ = toObservable(this.configId).pipe(
		filter((v) => v !== undefined && v !== '' && v !== '-1')
	);
	private _featureId$ = toObservable(this.featureId).pipe(
		filter((v) => v !== undefined && v !== '' && v !== '-1')
	);

	public tuples = this._openAutoComplete.pipe(
		distinctUntilChanged(),
		switchMap((_) =>
			this.filter$.pipe(
				distinctUntilChanged(),
				debounceTime(250),
				switchMap((filter) =>
					of((pageNum: string | number) =>
						this.currentBranchService.getFeatureValues(
							this.configId(),
							this.featureId(),
							10,
							pageNum,
							filter
						)
					)
				)
			)
		)
	);
	availableTuplesCount = this._openAutoComplete.pipe(
		distinctUntilChanged(),
		switchMap((_) =>
			combineLatest([this._configId$, this._featureId$]).pipe(
				switchMap(([cfg, feature]) =>
					this.filter$.pipe(
						distinctUntilChanged(),
						debounceTime(250),
						switchMap((filter) =>
							this.currentBranchService.getFeatureValuesCount(
								cfg,
								feature,
								filter
							)
						)
					)
				)
			)
		)
	);

	autoCompleteOpened() {
		this._openAutoComplete.next();
		this._isOpen.set(true);
	}
	close() {
		this._isOpen.set(false);
	}

	set(event: MatAutocompleteSelectedEvent) {
		this.value.set({
			gammaId: event.option.value.gammaId,
			id: event.option.value.e2,
			name: event.option.value.value,
		});
	}

	displayFn(
		value:
			| string
			| {
					e1: number;
					e2: number;
					gammaId: number;
					value: string;
					constrained: boolean;
					constrainedBy: string;
			  }
	) {
		const splitApplic = new SplitApplicabilityPipe();
		if (
			value !== null &&
			value !== undefined &&
			typeof value === 'string'
		) {
			return splitApplic.transform(value);
		}
		if (value !== null && value !== undefined) {
			return splitApplic.transform(value.value);
		}
		return '';
	}
}
