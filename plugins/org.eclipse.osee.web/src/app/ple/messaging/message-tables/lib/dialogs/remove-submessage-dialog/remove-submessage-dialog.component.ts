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
import { RemoveSubMessageDialogData } from '../../types/RemoveSubMessageDialog';

@Component({
	selector: 'osee-messaging-remove-submessage-dialog',
	templateUrl: './remove-submessage-dialog.component.html',
	standalone: true,
	imports: [
		MatDialogTitle,
		MatDialogContent,
		MatDialogActions,
		MatDialogClose,
		MatButton,
	],
})
export class RemoveSubmessageDialogComponent {
	constructor(
		public dialogRef: MatDialogRef<RemoveSubmessageDialogComponent>,
		@Inject(MAT_DIALOG_DATA) public data: RemoveSubMessageDialogData
	) {}
}
