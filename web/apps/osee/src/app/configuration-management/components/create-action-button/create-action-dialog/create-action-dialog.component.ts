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
import { Component, inject } from '@angular/core';
import {
	MatDialogRef,
	MAT_DIALOG_DATA,
	MatDialogActions,
	MatDialogContent,
	MatDialogTitle,
} from '@angular/material/dialog';
import { CreateAction } from '@osee/configuration-management/types';
import { CreateActionFormComponent } from '../create-action-form/create-action-form.component';
import { MatButton } from '@angular/material/button';
import { FormsModule } from '@angular/forms';

/**
 * Dialog for creating a new action with the correct workType and category.
 */
@Component({
	selector: 'osee-create-action-dialog',
	templateUrl: './create-action-dialog.component.html',
	styles: [],
	imports: [
		CreateActionFormComponent,
		MatDialogActions,
		MatButton,
		MatDialogContent,
		MatDialogTitle,
		FormsModule,
	],
})
export class CreateActionDialogComponent {
	dialogRef = inject<MatDialogRef<CreateActionDialogComponent>>(MatDialogRef);
	data = inject<CreateAction>(MAT_DIALOG_DATA);

	cancel() {
		this.dialogRef.close();
	}
	submit(value: CreateAction) {
		this.dialogRef.close(value);
	}
}
