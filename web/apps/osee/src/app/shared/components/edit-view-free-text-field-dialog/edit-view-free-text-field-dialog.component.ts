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
import { MatFormField, MatLabel } from '@angular/material/form-field';
import { MatIcon } from '@angular/material/icon';
import { MatInput } from '@angular/material/input';
import type { EditViewFreeTextDialog } from '@osee/messaging/shared/types';

@Component({
	selector: 'osee-messaging-edit-view-free-text-field-dialog',
	templateUrl: './edit-view-free-text-field-dialog.component.html',
	styles: [],
	imports: [
		FormsModule,
		MatDialogTitle,
		MatDialogContent,
		MatFormField,
		MatLabel,
		MatInput,
		CdkTextareaAutosize,
		MatDialogActions,
		MatButton,
		MatIcon,
		MatDialogClose,
	],
})
export class EditViewFreeTextFieldDialogComponent {
	dialogRef =
		inject<MatDialogRef<EditViewFreeTextFieldDialogComponent>>(
			MatDialogRef
		);
	data = inject<EditViewFreeTextDialog>(MAT_DIALOG_DATA);
}
