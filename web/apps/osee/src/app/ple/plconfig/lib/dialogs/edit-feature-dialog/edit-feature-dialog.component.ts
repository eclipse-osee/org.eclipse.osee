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
import { Component, inject, signal } from '@angular/core';
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
import { PlConfigCurrentBranchService } from '../../services/pl-config-current-branch.service';
import { PLEditFeatureData } from '../../types/pl-config-features';
import { toSignal } from '@angular/core/rxjs-interop';
import { UiService } from '@osee/shared/services';

@Component({
	selector: 'osee-plconfig-edit-feature-dialog',
	templateUrl: './edit-feature-dialog.component.html',
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
		MatDialogClose,
	],
})
export class EditFeatureDialogComponent {
	dialogRef = inject<MatDialogRef<EditFeatureDialogComponent>>(MatDialogRef);
	data = inject<PLEditFeatureData>(MAT_DIALOG_DATA);
	private currentBranchService = inject(PlConfigCurrentBranchService);
	private uiService = inject(UiService);

	private _valueTypes: string[] = ['String', 'Integer', 'Decimal', 'Boolean'];
	productApplicabilities = this.currentBranchService.productTypes;
	editable = signal(inject<PLEditFeatureData>(MAT_DIALOG_DATA).editable);
	valueTypes: Observable<string[]> = of(this._valueTypes);
	viewId = toSignal(this.uiService.viewId);

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
	valueTracker(index: number, _item: unknown) {
		return index;
	}
}
