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
import { Component, Inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import {
	MAT_DIALOG_DATA,
	MatDialogModule,
	MatDialogRef,
} from '@angular/material/dialog';
import { deleteArtifactDialogData } from '../../../types/artifact-explorer.data';
import { MatIconModule } from '@angular/material/icon';
import { ArtifactDialogTitleComponent } from '../../shared/artifact-dialog-title/artifact-dialog-title.component';
import { MatButtonModule } from '@angular/material/button';

@Component({
	selector: 'osee-delete-artifact-dialog',
	standalone: true,
	imports: [
		CommonModule,
		MatDialogModule,
		MatIconModule,
		ArtifactDialogTitleComponent,
		MatButtonModule,
	],
	templateUrl: './delete-artifact-dialog.component.html',
})
export class DeleteArtifactDialogComponent {
	constructor(
		public dialogRef: MatDialogRef<DeleteArtifactDialogComponent>,
		@Inject(MAT_DIALOG_DATA) public data: deleteArtifactDialogData
	) {}

	onSubmit() {
		this.dialogRef.close('submit');
	}

	onCancel() {
		this.dialogRef.close();
	}
}
