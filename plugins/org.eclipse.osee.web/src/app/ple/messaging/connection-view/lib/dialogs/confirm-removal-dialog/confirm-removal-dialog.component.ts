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
import { NgFor, NgIf, TitleCasePipe } from '@angular/common';
import { Component, Inject } from '@angular/core';
import { MatButton } from '@angular/material/button';
import {
	MAT_DIALOG_DATA,
	MatDialogActions,
	MatDialogClose,
	MatDialogContent,
	MatDialogRef,
	MatDialogTitle,
} from '@angular/material/dialog';
import { MatLabel } from '@angular/material/form-field';
import { RemovalDialog } from '../../types/ConfirmRemovalDialog';

@Component({
	selector: 'osee-confirm-removal-dialog',
	templateUrl: './confirm-removal-dialog.component.html',
	standalone: true,
	imports: [
		MatDialogTitle,
		MatDialogContent,
		MatDialogActions,
		MatDialogClose,
		MatLabel,
		NgIf,
		NgFor,
		TitleCasePipe,
		MatButton,
	],
})
export class ConfirmRemovalDialogComponent {
	constructor(
		public dialogRef: MatDialogRef<ConfirmRemovalDialogComponent>,
		@Inject(MAT_DIALOG_DATA) public data: RemovalDialog
	) {}

	onNoClick() {
		this.dialogRef.close();
	}
}
