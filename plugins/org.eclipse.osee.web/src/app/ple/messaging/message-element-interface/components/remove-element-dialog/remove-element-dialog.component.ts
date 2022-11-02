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
import { RemoveElementDialogData } from '../../types/RemoveElementDialog';

@Component({
	selector: 'osee-messaging-remove-element-dialog',
	templateUrl: './remove-element-dialog.component.html',
	styleUrls: ['./remove-element-dialog.component.sass'],
})
export class RemoveElementDialogComponent {
	constructor(
		public dialogRef: MatDialogRef<RemoveElementDialogComponent>,
		@Inject(MAT_DIALOG_DATA) public data: RemoveElementDialogData
	) {}
}
