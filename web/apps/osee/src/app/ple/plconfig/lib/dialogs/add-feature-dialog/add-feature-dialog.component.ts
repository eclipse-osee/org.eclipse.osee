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
import { Component, inject } from '@angular/core';
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
import { Observable, of } from 'rxjs';
import { PlConfigBranchService } from '../../services/pl-config-branch-service.service';
import { PlConfigCurrentBranchService } from '../../services/pl-config-current-branch.service';
import { PlConfigApplicUIBranchMapping } from '../../types/pl-config-applicui-branch-mapping';
import { PLAddFeatureData, writeFeature } from '../../types/pl-config-features';
import { UiService } from '@osee/shared/services';
import { toSignal } from '@angular/core/rxjs-interop';

@Component({
	selector: 'osee-plconfig-add-feature-dialog',
	templateUrl: './add-feature-dialog.component.html',
	styles: [],
	standalone: true,
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
	data = inject<PLAddFeatureData>(MAT_DIALOG_DATA);
	private branchService = inject(PlConfigBranchService);
	private currentBranchService = inject(PlConfigCurrentBranchService);
	private uiService = inject(UiService);

	branchApplicability: Observable<PlConfigApplicUIBranchMapping>;
	productApplicabilities = this.currentBranchService.productTypes;
	private _valueTypes: string[] = ['String', 'Integer', 'Decimal', 'Boolean'];
	valueTypes: Observable<string[]> = of(this._valueTypes);
	viewId = toSignal(this.uiService.viewId);

	/** Inserted by Angular inject() migration for backwards compatibility */
	constructor(...args: unknown[]);
	constructor() {
		const data = this.data;

		this.branchApplicability = this.branchService.getBranchApplicability(
			data.currentBranch,
			this.viewId() || ''
		);
	}
	onNoClick(): void {
		this.dialogRef.close();
	}
	selectMultiValued() {
		this.autoSetValueIfBoolean();
	}
	autoSetValueIfBoolean() {
		if (
			this.data.feature.valueType === 'Boolean' &&
			!this.data.feature.multiValued &&
			this.data.feature.values.length <= 2
		) {
			this.data.feature.values = ['Included', 'Excluded'];
		}
	}
	increaseValueArray() {
		this.data.feature.values.length = this.data.feature.values.length + 1;
	}
	selectDefaultValue(event: MatSelectChange) {
		this.data.feature.defaultValue = event.value;
	}
	valueTracker<T>(index: number, _item: T) {
		return index;
	}
	clearFeatureData() {
		this.data.feature = new writeFeature();
	}
}
