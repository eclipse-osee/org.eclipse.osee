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
import { Component, inject, input, model, signal } from '@angular/core';
import { toObservable } from '@angular/core/rxjs-interop';
import { FormsModule } from '@angular/forms';
import {
	MatAutocomplete,
	MatAutocompleteSelectedEvent,
	MatAutocompleteTrigger,
} from '@angular/material/autocomplete';
import {
	MatChipGrid,
	MatChipRow,
	MatChipRemove,
	MatChipInput,
} from '@angular/material/chips';
import { MatOption } from '@angular/material/core';
import { MatFormField, MatLabel, MatError } from '@angular/material/form-field';
import { MatIcon } from '@angular/material/icon';
import { MatTooltip } from '@angular/material/tooltip';
import { MatOptionLoadingComponent } from '@osee/shared/components';
import { applicWithGamma } from '@osee/shared/types/applicability';
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
	selector: 'osee-plconfig-multi-select-tuple',
	standalone: true,
	imports: [
		MatFormField,
		MatLabel,
		MatError,
		FormsModule,
		MatChipGrid,
		MatChipRow,
		MatChipRemove,
		MatChipInput,
		MatAutocomplete,
		MatAutocompleteTrigger,
		MatOption,
		MatOptionLoadingComponent,
		AsyncPipe,
		MatTooltip,
		MatIcon,
		SplitApplicabilityPipe,
	],
	template: `<mat-form-field
		class="tw-w-full tw-bg-inherit [&>.mdc-text-field--filled]:tw-bg-inherit">
		<mat-chip-grid
			#chipGrid
			[ngModel]="value()"
			#nodeSelector="ngModel"
			[name]="'node-dropdown-chip-' + _componentId()"
			required>
			@for (v of value(); track v.id) {
				<mat-chip-row
					(removed)="remove(v)"
					[disabled]="v.deleted && !v.added"
					[class]="
						v.added
							? 'tw-bg-success-300 tw-text-success-300-contrast'
							: v.deleted
							  ? 'tw-bg-warning-300 tw-text-warning-300-contrast'
							  : ''
					">
					{{ v.name | splitApplicability }}
					<button
						matChipRemove
						[disabled]="v.deleted && !v.added">
						<mat-icon>cancel</mat-icon>
					</button>
				</mat-chip-row>
			}
		</mat-chip-grid>
		<input
			matInput
			type="text"
			#input
			[ngModel]="filter"
			class="tw-text-inherit tw-placeholder-inherit"
			(ngModelChange)="filter.set($event)"
			placeholder="Filter applicabilities"
			(focusin)="autoCompleteOpened()"
			(focusout)="close()"
			[matChipInputFor]="chipGrid"
			[matAutocomplete]="autoSingleSelectTuple" />
		<mat-autocomplete
			#autoSingleSelectTuple
			(optionSelected)="add($event)"
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
export class PlconfigMultiSelectTupleComponent {
	protected _componentId = signal(`${nextUniqueId++}`);
	public value = model.required<applicWithGamma[]>();
	protected filter = signal('');

	private filter$ = toObservable(this.filter);

	private _openAutoComplete = new ReplaySubject<void>();

	private _isOpen = signal(false);
	protected paginationSize = signal(10);

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
		debounceTime(500),
		distinctUntilChanged(),
		switchMap((_) =>
			this.filter$.pipe(
				distinctUntilChanged(),
				debounceTime(500),
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
		debounceTime(10),
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

	add(event: MatAutocompleteSelectedEvent) {
		this.value.update((tuples) => [
			...tuples,
			{
				gammaId: event.option.value.gammaId,
				id: event.option.value.e2,
				name: event.option.value.value,
			},
		]);
		this.filter.set('');
	}

	remove(applic: applicWithGamma) {
		this.value.update((rows) => rows.filter((value) => applic !== value));
	}
}
