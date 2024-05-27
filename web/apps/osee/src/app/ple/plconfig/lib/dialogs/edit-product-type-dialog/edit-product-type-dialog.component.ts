/*********************************************************************
 * Copyright (c) 2022 Boeing
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
import { CdkTextareaAutosize } from '@angular/cdk/text-field';
import { Component, inject } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { MatButton } from '@angular/material/button';
import {
	MAT_DIALOG_DATA,
	MatDialogActions,
	MatDialogClose,
	MatDialogContent,
	MatDialogRef,
	MatDialogTitle,
} from '@angular/material/dialog';
import { MatFormField, MatHint, MatLabel } from '@angular/material/form-field';
import { MatInput } from '@angular/material/input';
import { ParentErrorStateMatcher } from '@osee/shared/matchers';
import { productType } from '../../types/pl-config-product-types';

@Component({
	selector: 'osee-edit-product-type-dialog',
	templateUrl: './edit-product-type-dialog.component.html',
	styles: [],
	standalone: true,
	imports: [
		FormsModule,
		MatDialogTitle,
		MatDialogContent,
		MatFormField,
		MatLabel,
		CdkTextareaAutosize,
		MatInput,
		MatHint,
		MatDialogActions,
		MatButton,
		MatDialogClose,
	],
})
export class EditProductTypeDialogComponent {
	dialogRef =
		inject<MatDialogRef<EditProductTypeDialogComponent>>(MatDialogRef);
	data = inject<Required<productType>>(MAT_DIALOG_DATA);

	parentMatcher = new ParentErrorStateMatcher();

	onNoClick(): void {
		this.dialogRef.close();
	}
}
