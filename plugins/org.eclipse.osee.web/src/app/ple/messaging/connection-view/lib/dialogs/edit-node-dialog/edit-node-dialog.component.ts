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
import { Component, Inject } from '@angular/core';
import {
	MatDialogModule,
	MatDialogRef,
	MAT_DIALOG_DATA,
} from '@angular/material/dialog';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatInputModule } from '@angular/material/input';
import { FormsModule } from '@angular/forms';
import { MatButtonModule } from '@angular/material/button';
import { MatOptionModule } from '@angular/material/core';
import { MatSelectModule } from '@angular/material/select';
import { AsyncPipe, NgFor } from '@angular/common';
import type { nodeData } from '@osee/messaging/shared/types';
import { ApplicabilitySelectorComponent } from '@osee/shared/components';
import { MatSlideToggleModule } from '@angular/material/slide-toggle';

@Component({
	selector: 'osee-edit-node-dialog',
	templateUrl: './edit-node-dialog.component.html',
	styleUrls: ['./edit-node-dialog.component.sass'],
	standalone: true,
	imports: [
		MatDialogModule,
		MatFormFieldModule,
		MatInputModule,
		FormsModule,
		MatButtonModule,
		MatSelectModule,
		MatOptionModule,
		MatSlideToggleModule,
		AsyncPipe,
		NgFor,
		ApplicabilitySelectorComponent,
	],
})
export class EditNodeDialogComponent {
	title: string = '';
	constructor(
		public dialogRef: MatDialogRef<EditNodeDialogComponent>,
		@Inject(MAT_DIALOG_DATA) public data: nodeData
	) {
		this.title = data.name;
	}
	onNoClick() {
		this.dialogRef.close();
	}
}
