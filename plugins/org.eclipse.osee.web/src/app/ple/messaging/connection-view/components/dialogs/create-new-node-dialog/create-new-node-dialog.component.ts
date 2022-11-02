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
import { Component, OnInit } from '@angular/core';
import { MatDialogRef } from '@angular/material/dialog';
import { node, nodeData } from '../../../../shared/types/node.d';

@Component({
	selector: 'osee-create-new-node-dialog',
	templateUrl: './create-new-node-dialog.component.html',
	styleUrls: ['./create-new-node-dialog.component.sass'],
})
export class CreateNewNodeDialogComponent {
	result: Partial<nodeData> = {
		name: '',
		description: '',
		interfaceNodeAddress: '',
		interfaceNodeBgColor: '',
	};
	constructor(public dialogRef: MatDialogRef<CreateNewNodeDialogComponent>) {}

	onNoClick() {
		this.dialogRef.close();
	}
}
