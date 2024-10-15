/*********************************************************************
 * Copyright (c) 2023 Boeing
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
import { Component, inject, signal } from '@angular/core';
import {
	MAT_DIALOG_DATA,
	MatDialog,
	MatDialogActions,
	MatDialogClose,
	MatDialogContent,
	MatDialogRef,
	MatDialogTitle,
} from '@angular/material/dialog';
import { ElementFormComponent } from '../../forms/element-form/element-form.component';

import { FormsModule } from '@angular/forms';
import { MatButton } from '@angular/material/button';
import { ElementDialog } from '@osee/messaging/shared/types';

@Component({
	selector: 'osee-edit-element-dialog',
	standalone: true,
	imports: [
		MatDialogTitle,
		MatDialogContent,
		MatDialogActions,
		MatButton,
		MatDialogClose,
		ElementFormComponent,
		FormsModule,
	],
	templateUrl: './edit-element-dialog.component.html',
	styles: [],
})
export class EditElementDialogComponent {
	dialog = inject(MatDialog);
	dialogRef = inject<MatDialogRef<EditElementDialogComponent>>(MatDialogRef);

	protected data = signal(inject<ElementDialog>(MAT_DIALOG_DATA));

	closeDialog() {
		this.dialogRef.close();
	}
}
