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
import { Component, Inject, OnInit } from '@angular/core';
import { MatDialogRef, MAT_DIALOG_DATA } from '@angular/material/dialog';
import { PreferencesUIService } from '../../../services/ui/preferences-ui.service';
import { EditViewFreeTextDialog } from '../../../types/EditViewFreeTextDialog';

@Component({
	selector: 'osee-messaging-edit-view-free-text-field-dialog',
	templateUrl: './edit-view-free-text-field-dialog.component.html',
	styleUrls: ['./edit-view-free-text-field-dialog.component.sass'],
})
export class EditViewFreeTextFieldDialogComponent {
	editMode = this.preferencesService.inEditMode;
	constructor(
		public dialogRef: MatDialogRef<EditViewFreeTextFieldDialogComponent>,
		@Inject(MAT_DIALOG_DATA) public data: EditViewFreeTextDialog,
		private preferencesService: PreferencesUIService
	) {}
}
