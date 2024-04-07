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
import { DeleteSubMessageDialog } from '../../types/DeleteSubMessageDialog';

@Component({
	selector: 'osee-messaging-delete-submessage-dialog',
	templateUrl: './delete-submessage-dialog.component.html',
	standalone: true,
	imports: [
		MatDialogTitle,
		MatDialogContent,
		MatDialogActions,
		MatDialogClose,
		MatButton,
	],
})
export class DeleteSubmessageDialogComponent {
	constructor(
		public dialogRef: MatDialogRef<DeleteSubmessageDialogComponent>,
		@Inject(MAT_DIALOG_DATA) public data: DeleteSubMessageDialog
	) {}
}
