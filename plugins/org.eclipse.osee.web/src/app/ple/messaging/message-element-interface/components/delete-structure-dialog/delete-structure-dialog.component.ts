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
import { DeleteStructureDialogData } from '../../types/DeleteStructureDialog';

@Component({
	selector: 'osee-messaging-delete-structure-dialog',
	templateUrl: './delete-structure-dialog.component.html',
	styleUrls: ['./delete-structure-dialog.component.sass'],
})
export class DeleteStructureDialogComponent {
	constructor(
		public dialogRef: MatDialogRef<DeleteStructureDialogComponent>,
		@Inject(MAT_DIALOG_DATA) public data: DeleteStructureDialogData
	) {}
}
