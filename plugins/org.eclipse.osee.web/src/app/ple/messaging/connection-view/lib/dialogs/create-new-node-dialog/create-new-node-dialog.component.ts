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
import { Component } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { MatButton } from '@angular/material/button';
import {
	MatDialogActions,
	MatDialogClose,
	MatDialogContent,
	MatDialogRef,
	MatDialogTitle,
} from '@angular/material/dialog';
import { MatInput } from '@angular/material/input';
import { MatSlideToggle } from '@angular/material/slide-toggle';
import type { nodeData } from '@osee/messaging/shared/types';
import { NewNodeFormComponent } from '../../forms/new-node-form/new-node-form.component';

@Component({
	selector: 'osee-create-new-node-dialog',
	templateUrl: './create-new-node-dialog.component.html',
	standalone: true,
	imports: [
		MatDialogTitle,
		MatDialogContent,
		MatDialogActions,
		MatDialogClose,
		MatInput,
		FormsModule,
		MatButton,
		MatSlideToggle,
		NewNodeFormComponent,
	],
})
export class CreateNewNodeDialogComponent {
	result: nodeData = {
		id: '',
		name: '',
		description: '',
		interfaceNodeNumber: '',
		interfaceNodeGroupId: '',
		interfaceNodeBackgroundColor: '',
		interfaceNodeAddress: '',
		interfaceNodeBuildCodeGen: false,
		interfaceNodeCodeGen: false,
		interfaceNodeCodeGenName: '',
		nameAbbrev: '',
		interfaceNodeToolUse: false,
		interfaceNodeType: '',
		notes: '',
		applicability: {
			id: '1',
			name: 'Base',
		},
	};
	constructor(public dialogRef: MatDialogRef<CreateNewNodeDialogComponent>) {}

	onNoClick() {
		this.dialogRef.close();
	}
}
