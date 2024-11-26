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
import { RemoveMessageDialogData } from '../../types/RemoveMessageDialog';
import { AttributeToValuePipe } from '@osee/attributes/pipes';

@Component({
	selector: 'osee-messaging-remove-message-dialog',
	templateUrl: './remove-message-dialog.component.html',
	imports: [
		MatDialogTitle,
		MatDialogContent,
		MatDialogActions,
		MatDialogClose,
		MatButton,
		AttributeToValuePipe,
	],
})
export class RemoveMessageDialogComponent {
	dialogRef =
		inject<MatDialogRef<RemoveMessageDialogComponent>>(MatDialogRef);
	data = inject<RemoveMessageDialogData>(MAT_DIALOG_DATA);
}
