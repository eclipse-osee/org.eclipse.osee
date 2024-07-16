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
import { DeleteElementDialogData } from './delete-element-dialog';

@Component({
	selector: 'osee-messaging-delete-element-dialog',
	templateUrl: './delete-element-dialog.component.html',
	styles: [],
	standalone: true,
	imports: [
		MatDialogTitle,
		MatDialogContent,
		MatDialogActions,
		MatButton,
		MatDialogClose,
	],
})
export class DeleteElementDialogComponent {
	constructor(
		public dialogRef: MatDialogRef<DeleteElementDialogComponent>,
		@Inject(MAT_DIALOG_DATA) public data: DeleteElementDialogData
	) {}
}
