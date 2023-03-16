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
import { NgFor } from '@angular/common';
import { Component, Inject } from '@angular/core';
import { MatButtonModule } from '@angular/material/button';
import {
	MatDialogModule,
	MatDialogRef,
	MAT_DIALOG_DATA,
} from '@angular/material/dialog';
import { MatFormFieldModule } from '@angular/material/form-field';
import type { affectedArtifactWarning } from '@osee/messaging/shared/types';

@Component({
	selector: 'osee-affected-artifact-dialog',
	templateUrl: './affected-artifact-dialog.component.html',
	styleUrls: ['./affected-artifact-dialog.component.sass'],
	standalone: true,
	imports: [MatDialogModule, MatFormFieldModule, MatButtonModule, NgFor],
})
export class AffectedArtifactDialogComponent<T = unknown> {
	constructor(
		public dialogRef: MatDialogRef<AffectedArtifactDialogComponent<T>>,
		@Inject(MAT_DIALOG_DATA) public data: affectedArtifactWarning<T>
	) {}

	onNoClick() {
		this.dialogRef.close();
	}
}
