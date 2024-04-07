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
import { DeleteMessageDialogData } from '../../types/DeleteMessageDialog';

@Component({
	selector: 'osee-messaging-delete-message-dialog',
	templateUrl: './delete-message-dialog.component.html',
	standalone: true,
	imports: [
		MatDialogTitle,
		MatDialogContent,
		MatDialogActions,
		MatDialogClose,
		MatButton,
	],
})
export class DeleteMessageDialogComponent {
	constructor(
		public dialogRef: MatDialogRef<DeleteMessageDialogComponent>,
		@Inject(MAT_DIALOG_DATA) public data: DeleteMessageDialogData
	) {}
}
