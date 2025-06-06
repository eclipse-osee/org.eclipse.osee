/*********************************************************************
 * Copyright (c) 2024 Boeing
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
	MatDialogContent,
	MatDialogRef,
} from '@angular/material/dialog';
import { deleteArtifactDialogData } from '../../../../../types/artifact-explorer';
import { ArtifactDialogTitleComponent } from '../../../../shared/artifact-dialog-title/artifact-dialog-title.component';

@Component({
	selector: 'osee-delete-artifact-dialog',
	imports: [
		ArtifactDialogTitleComponent,
		MatDialogContent,
		MatDialogActions,
		MatButton,
	],
	templateUrl: './delete-artifact-dialog.component.html',
})
export class DeleteArtifactDialogComponent {
	dialogRef =
		inject<MatDialogRef<DeleteArtifactDialogComponent>>(MatDialogRef);
	data = inject<deleteArtifactDialogData>(MAT_DIALOG_DATA);

	onSubmit() {
		this.dialogRef.close('submit');
	}

	onCancel() {
		this.dialogRef.close();
	}
}
