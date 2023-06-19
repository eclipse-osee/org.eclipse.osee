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
import { MatButtonModule } from '@angular/material/button';
import { AsyncPipe, NgFor } from '@angular/common';
import type { nodeData } from '@osee/messaging/shared/types';
import { NewNodeFormComponent } from '../../forms/new-node-form/new-node-form.component';

@Component({
	selector: 'osee-edit-node-dialog',
	templateUrl: './edit-node-dialog.component.html',
	standalone: true,
	imports: [
		MatDialogModule,
		MatButtonModule,
		AsyncPipe,
		NgFor,
		NewNodeFormComponent,
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
