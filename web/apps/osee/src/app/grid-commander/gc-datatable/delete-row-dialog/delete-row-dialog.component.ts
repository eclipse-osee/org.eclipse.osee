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
import { Component, inject } from '@angular/core';
import { MatButton } from '@angular/material/button';
import {
	MAT_DIALOG_DATA,
	MatDialogActions,
	MatDialogClose,
	MatDialogContent,
	MatDialogRef,
	MatDialogTitle,
} from '@angular/material/dialog';
import { RowObj } from '../../types/grid-commander-types/table-data-types';

@Component({
	selector: 'osee-delete-row-dialog',
	templateUrl: './delete-row-dialog.component.html',
	styles: [],
	imports: [
		MatDialogTitle,
		MatDialogContent,
		MatDialogActions,
		MatButton,
		MatDialogClose,
	],
})
export class DeleteRowDialogComponent {
	dialogRef = inject<MatDialogRef<DeleteRowDialogComponent>>(MatDialogRef);
	data = inject(MAT_DIALOG_DATA);

	doAction(action: string, rowObj: RowObj) {
		this.dialogRef.close({ event: action, data: rowObj });
	}

	closeDialog(action: string) {
		this.dialogRef.close({ event: action });
	}
}
