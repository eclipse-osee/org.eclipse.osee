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
import { TextFieldModule } from '@angular/cdk/text-field';
import { AsyncPipe, NgIf } from '@angular/common';
import { Component, Inject, OnInit } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { MatButtonModule } from '@angular/material/button';
import {
	MatDialogModule,
	MatDialogRef,
	MAT_DIALOG_DATA,
} from '@angular/material/dialog';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatIconModule } from '@angular/material/icon';
import { MatInputModule } from '@angular/material/input';
import { PreferencesUIService } from '../../services/ui/preferences-ui.service';
import { EditViewFreeTextDialog } from '../../types/EditViewFreeTextDialog';

@Component({
	selector: 'osee-messaging-edit-view-free-text-field-dialog',
	templateUrl: './edit-view-free-text-field-dialog.component.html',
	styleUrls: ['./edit-view-free-text-field-dialog.component.sass'],
	standalone: true,
	imports: [
		MatDialogModule,
		NgIf,
		AsyncPipe,
		MatFormFieldModule,
		FormsModule,
		MatInputModule,
		TextFieldModule,
		MatButtonModule,
		MatIconModule,
	],
})
export class EditViewFreeTextFieldDialogComponent {
	editMode = this.preferencesService.inEditMode;
	constructor(
		public dialogRef: MatDialogRef<EditViewFreeTextFieldDialogComponent>,
		@Inject(MAT_DIALOG_DATA) public data: EditViewFreeTextDialog,
		private preferencesService: PreferencesUIService
	) {}
}
