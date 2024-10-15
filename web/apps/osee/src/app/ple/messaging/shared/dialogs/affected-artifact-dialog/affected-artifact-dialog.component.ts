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
import { MatLabel } from '@angular/material/form-field';
import type { affectedArtifactWarning } from '@osee/messaging/shared/types';

@Component({
	selector: 'osee-affected-artifact-dialog',
	templateUrl: './affected-artifact-dialog.component.html',
	styles: [],
	standalone: true,
	imports: [
		MatDialogTitle,
		MatDialogContent,
		MatLabel,
		MatDialogActions,
		MatButton,
		MatDialogClose,
	],
})
export class AffectedArtifactDialogComponent<T = unknown> {
	dialogRef =
		inject<MatDialogRef<AffectedArtifactDialogComponent<T>>>(MatDialogRef);
	data = inject<affectedArtifactWarning<T>>(MAT_DIALOG_DATA);

	onNoClick() {
		this.dialogRef.close();
	}
}
