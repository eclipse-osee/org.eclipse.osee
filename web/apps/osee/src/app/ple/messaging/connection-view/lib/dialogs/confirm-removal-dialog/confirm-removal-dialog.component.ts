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
import { TitleCasePipe } from '@angular/common';
import {
	ChangeDetectionStrategy,
	Component,
	inject,
	signal,
} from '@angular/core';
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
	imports: [
		MatDialogTitle,
		MatDialogContent,
		MatDialogActions,
		MatDialogClose,
		MatLabel,
		TitleCasePipe,
		MatButton,
	],
	changeDetection: ChangeDetectionStrategy.OnPush,
})
export class ConfirmRemovalDialogComponent {
	dialogRef =
		inject<MatDialogRef<ConfirmRemovalDialogComponent>>(MatDialogRef);

	protected data = signal(inject<RemovalDialog>(MAT_DIALOG_DATA));

	onNoClick() {
		this.dialogRef.close();
	}
}
