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
import { Component, computed, inject, input, signal } from '@angular/core';
import { toObservable, toSignal } from '@angular/core/rxjs-interop';
import { OperatorFunction, debounceTime, filter, switchMap } from 'rxjs';
import { PlConfigCurrentBranchService } from '../../services/pl-config-current-branch.service';
import {
	isValidPLConfigAttr,
	plconfigTableEntry,
} from '../../types/pl-config-table';
import { PlconfigMultiSelectTupleComponent } from '../plconfig-multi-select-tuple/plconfig-multi-select-tuple.component';
import { PLConfigSelectSingleTupleComponent } from '../plconfig-select-single-tuple/plconfig-select-single-tuple.component';
import { SplitApplicabilityPipe } from '../split-applicability.pipe';
import { ATTRIBUTETYPEIDENUM } from '@osee/attributes/constants';
import { applicWithGamma } from '@osee/applicability/types';

@Component({
	selector: 'osee-plconfig-cell',
	imports: [
		PLConfigSelectSingleTupleComponent,
		PlconfigMultiSelectTupleComponent,
		SplitApplicabilityPipe,
	],
	template: `
		@if (editMode() && allowEdits()) {
			@if (!multiValued()) {
				<osee-plconfig-select-single-tuple
					[featureId]="featureId()"
					[configId]="configId()"
					[value]="applicability()"
					(valueChange)="updateValue($event)"
					[class]="
						hasTupleChanges()
							? 'tw-bg-accent-100 tw-text-accent-100-contrast tw-placeholder-accent-100-contrast hover:tw-bg-accent-50 hover:tw-text-accent-50-contrast hover:tw-placeholder-accent-50-contrast'
							: ''
					" />
			} @else {
				<!-- multi select handles tuple diffs on its own, using the added/deleted properties on applicWithGamma -->
				<osee-plconfig-multi-select-tuple
					[featureId]="featureId()"
					[configId]="configId()"
					[value]="applicabilityMultiValue()"
					(valueChange)="updateValue($event)"
					[class]="
						hasTupleChanges()
							? 'tw-bg-accent-100 tw-text-accent-100-contrast tw-placeholder-accent-100-contrast hover:tw-bg-accent-50 hover:tw-text-accent-50-contrast hover:tw-placeholder-accent-50-contrast'
							: ''
					"></osee-plconfig-multi-select-tuple>
			}
		} @else {
			@if (!multiValued()) {
				<span
					[class]="
						hasTupleChanges()
							? 'tw-bg-accent-100 tw-text-accent-100-contrast tw-placeholder-accent-100-contrast hover:tw-bg-accent-50 hover:tw-text-accent-50-contrast hover:tw-placeholder-accent-50-contrast'
							: ''
					"
					>{{ value() | splitApplicability }}</span
				>
			} @else {
				<span
					[class]="
						hasTupleChanges()
							? 'tw-bg-accent-100 tw-text-accent-100-contrast tw-placeholder-accent-100-contrast hover:tw-bg-accent-50 hover:tw-text-accent-50-contrast hover:tw-placeholder-accent-50-contrast'
							: ''
					"
					>{{ valueMultiValued() }}</span
				>
			}
		}
	`,
})
export class PlconfigCellComponent {
	public feature = input.required<plconfigTableEntry>();
	protected featureId = computed(() => this.feature().id);
	public configId = input.required<string>();

	/*
	 * For configuration groups, we should disable editing, for configurations we should allow it
	 */
	public allowEdits = input.required<boolean>();
	protected applicability = computed(() => {
		const applic: applicWithGamma = { id: '-1', name: '', gammaId: '-1' };
		return (
			this.feature().configurationValues.find(
				(x) => x.id === this.configId()
			)?.applicability || applic
		);
	});
	protected applicabilityMultiValue = computed(() =>
		this.feature()
			.configurationValues.filter((x) => x.id === this.configId())
			.map((x) => x.applicability)
	);
	protected valueMultiValued = computed(() =>
		this.applicabilityMultiValue().map((x) => {
			const pipe = new SplitApplicabilityPipe();

			return pipe.transform(x.name);
		})
	);
	protected value = computed(() => this.applicability().name);
	protected multiValued = computed(() => {
		if (this.feature().id === '-1') {
			return false;
		}
		const attr = this.feature().attributes.find(
			(x) =>
				isValidPLConfigAttr(x) &&
				x.attributeType === ATTRIBUTETYPEIDENUM.MULTIVALUED
		);
		if (attr !== undefined && isValidPLConfigAttr(attr)) {
			return (attr.value as string) === 'true';
		}
		return false;
	});
	public editMode = input.required<boolean>();

	protected hasTupleChanges = computed(
		() =>
			this.feature().configurationValues.filter(
				(x) =>
					x.id === this.configId() &&
					(x.added || x.deleted || x.changes !== undefined)
			).length > 0
	);
	private _updatedValue = signal<
		applicWithGamma | applicWithGamma[] | undefined
	>(undefined);
	private updatedValue = toObservable(this._updatedValue).pipe(
		debounceTime(500),
		filter((v) => v !== undefined)
	);

	private currentBranchService = inject(PlConfigCurrentBranchService);
	private valueMultiValued$ = toObservable(this.valueMultiValued);
	private updateMultiValue = this.updatedValue.pipe(
		filter((v) => Array.isArray(v)) as OperatorFunction<
			applicWithGamma | applicWithGamma[] | undefined,
			applicWithGamma[]
		>,
		switchMap((v) =>
			this.currentBranchService.setApplicability(
				this.featureId(),
				this.configId(),
				v.map((x) => x.name)
			)
		)
	);
	private updateMultiValue$ = toSignal(this.updateMultiValue);
	private updateSingleValue = this.updatedValue.pipe(
		filter((v) => !Array.isArray(v)) as OperatorFunction<
			applicWithGamma | applicWithGamma[] | undefined,
			applicWithGamma
		>,
		switchMap((v) =>
			this.currentBranchService.setApplicability(
				this.featureId(),
				this.configId(),
				[v.name]
			)
		)
	);
	private updateSingleValue$ = toSignal(this.updateSingleValue);
	updateValue(newValue: applicWithGamma | applicWithGamma[]) {
		//newValue must be valid at this point because single-select and multi-select should only present valid values

		if (Array.isArray(newValue)) {
			//do not allow multi value features to get zeroized
			if (newValue.length > 0) {
				this._updatedValue.set(
					newValue.filter(
						(x) =>
							x.id !== '-1' &&
							x.id !== '0' &&
							x.gammaId !== '' &&
							x.gammaId !== '-1' &&
							x.gammaId !== '0' &&
							x.deleted !== true
					)
				);
			}
		} else if (
			newValue.id !== this.applicability().id &&
			newValue.id !== '-1' &&
			newValue.id !== '0' &&
			newValue.gammaId !== '-1' &&
			newValue.gammaId !== '' &&
			newValue.gammaId !== '0' &&
			newValue.deleted !== true
		) {
			this._updatedValue.set(newValue);
		}
	}
}
