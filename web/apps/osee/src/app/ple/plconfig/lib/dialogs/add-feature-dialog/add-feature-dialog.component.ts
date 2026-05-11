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
import { AsyncPipe } from '@angular/common';
import {
	Component,
	computed,
	effect,
	inject,
	linkedSignal,
	signal,
} from '@angular/core';
import { FormsModule } from '@angular/forms';
import { MatButton } from '@angular/material/button';
import { MatOption } from '@angular/material/core';
import {
	MAT_DIALOG_DATA,
	MatDialogActions,
	MatDialogClose,
	MatDialogContent,
	MatDialogRef,
	MatDialogTitle,
} from '@angular/material/dialog';
import { MatFormField, MatLabel } from '@angular/material/form-field';
import { MatInput } from '@angular/material/input';
import { MatListOption, MatSelectionList } from '@angular/material/list';
import { MatSelect, MatSelectChange } from '@angular/material/select';
import { MatSlideToggle } from '@angular/material/slide-toggle';
import { combineLatest, Observable, of, switchMap } from 'rxjs';
import { PlConfigBranchService } from '../../services/pl-config-branch-service.service';
import { PlConfigCurrentBranchService } from '../../services/pl-config-current-branch.service';
import { PLAddFeatureData, writeFeature } from '../../types/pl-config-features';
import { UiService } from '@osee/shared/services';
import { toObservable, toSignal } from '@angular/core/rxjs-interop';

@Component({
	selector: 'osee-plconfig-add-feature-dialog',
	templateUrl: './add-feature-dialog.component.html',
	styles: [],
	imports: [
		AsyncPipe,
		FormsModule,
		MatDialogTitle,
		MatDialogContent,
		MatFormField,
		MatLabel,
		MatInput,
		MatSelect,
		MatOption,
		MatSlideToggle,
		MatButton,
		MatSelectionList,
		MatListOption,
		MatDialogActions,
		MatButton,
		MatDialogClose,
	],
})
export class AddFeatureDialogComponent {
	dialogRef = inject<MatDialogRef<AddFeatureDialogComponent>>(MatDialogRef);
	dialogData = signal(inject<PLAddFeatureData>(MAT_DIALOG_DATA));
	private _dialogData$ = toObservable(this.dialogData);
	private branchService = inject(PlConfigBranchService);
	private currentBranchService = inject(PlConfigCurrentBranchService);
	private uiService = inject(UiService);
	private _viewId$ = this.uiService.viewId;
	viewId = toSignal(this._viewId$);

	branchApplicability = combineLatest([
		this._dialogData$,
		this._viewId$,
	]).pipe(
		switchMap(([data, viewId]) =>
			this.branchService.getBranchApplicability(
				data.currentBranch,
				viewId || ''
			)
		)
	);
	allProductApplicabilities = this.currentBranchService.productTypes;
	private _valueTypes: string[] = ['String', 'Integer', 'Decimal', 'Boolean'];
	valueTypes: Observable<string[]> = of(this._valueTypes);

	private feature = linkedSignal(() => this.dialogData().feature);
	protected name = linkedSignal(() => this.feature().name);
	protected description = linkedSignal(() => this.feature().description);
	protected valueType = linkedSignal(() => this.feature().valueType);
	protected multiValued = linkedSignal(() => this.feature().multiValued);
	protected values = linkedSignal(() => this.feature().values);
	protected valueStr = computed(() => this.values().toString());
	protected defaultValue = linkedSignal(() => this.feature().defaultValue);
	protected productApplicabilities = linkedSignal(
		() => this.feature().productApplicabilities
	);
	protected results = computed<PLAddFeatureData>(() => {
		return {
			feature: {
				name: this.name(),
				description: this.description(),
				valueType: this.valueType(),
				valueStr: this.valueStr(),
				defaultValue: this.defaultValue(),
				productAppStr: this.feature().productAppStr,
				values: this.values(),
				productApplicabilities: this.productApplicabilities(),
				multiValued: this.multiValued(),
				setValueStr: this.feature().setValueStr,
				setProductAppStr: this.feature().setProductAppStr,
			},
			currentBranch: this.dialogData().currentBranch,
		};
	});

	private _autoSetValuesIfBoolean = effect(() => {
		const oldData = this.results();
		if (
			this.valueType() === 'Boolean' &&
			!this.multiValued() &&
			this.values().length <= 2
		) {
			oldData.feature.values = ['Included', 'Excluded'];
			this.dialogData.set(oldData);
		}
	});

	onNoClick(): void {
		this.dialogRef.close();
	}
	increaseValueArray() {
		const oldValues = this.results().feature.values;
		oldValues.length = oldValues.length + 1;
		this.values.set(oldValues);
	}
	selectDefaultValue(event: MatSelectChange) {
		let oldDefaultValue = this.results().feature.defaultValue;
		oldDefaultValue = event.value;
		this.defaultValue.set(oldDefaultValue);
	}
	valueTracker<T>(index: number, _item: T) {
		return index;
	}
	clearFeatureData() {
		let oldFeature = this.results().feature;
		oldFeature = new writeFeature();
		this.feature.set(oldFeature);
	}
	updateValue(index: number, event: string) {
		const oldValues = this.results().feature.values;
		oldValues[index] = event;
		this.values.set(oldValues);
	}
}
