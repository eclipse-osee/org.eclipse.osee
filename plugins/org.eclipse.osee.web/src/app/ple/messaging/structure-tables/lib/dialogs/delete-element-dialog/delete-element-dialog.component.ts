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
import { MatButtonModule } from '@angular/material/button';
import {
	MatDialogModule,
	MatDialogRef,
	MAT_DIALOG_DATA,
} from '@angular/material/dialog';
import { DeleteElementDialogData } from './delete-element-dialog';

@Component({
	selector: 'osee-messaging-delete-element-dialog',
	templateUrl: './delete-element-dialog.component.html',
	styleUrls: ['./delete-element-dialog.component.sass'],
	standalone: true,
	imports: [MatDialogModule, MatButtonModule],
})
export class DeleteElementDialogComponent {
	constructor(
		public dialogRef: MatDialogRef<DeleteElementDialogComponent>,
		@Inject(MAT_DIALOG_DATA) public data: DeleteElementDialogData
	) {}
}
