/*********************************************************************
 * Copyright (c) 2022 Boeing
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
import {
	MatDialogRef,
	MAT_DIALOG_DATA,
	MatDialogModule,
} from '@angular/material/dialog';
import { RowObj } from '../../types/grid-commander-types/table-data-types';
import { MatButtonModule } from '@angular/material/button';

@Component({
	selector: 'osee-delete-row-dialog',
	templateUrl: './delete-row-dialog.component.html',
	styles: [],
	standalone: true,
	imports: [MatDialogModule, MatButtonModule],
})
export class DeleteRowDialogComponent {
	constructor(
		public dialogRef: MatDialogRef<DeleteRowDialogComponent>,
		@Inject(MAT_DIALOG_DATA) public data: { action: string; object: RowObj }
	) {}

	doAction(action: string, rowObj: RowObj) {
		this.dialogRef.close({ event: action, data: rowObj });
	}

	closeDialog(action: string) {
		this.dialogRef.close({ event: action });
	}
}
