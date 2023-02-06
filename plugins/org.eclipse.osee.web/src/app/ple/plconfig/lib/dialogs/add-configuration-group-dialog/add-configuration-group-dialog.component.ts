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
import { Component, Inject, OnInit } from '@angular/core';
import { MatDialogRef, MAT_DIALOG_DATA } from '@angular/material/dialog';
import { addCfgGroup } from '../../types/pl-config-cfggroups';

@Component({
	selector: 'osee-plconfig-add-configuration-group-dialog',
	templateUrl: './add-configuration-group-dialog.component.html',
	styleUrls: ['./add-configuration-group-dialog.component.sass'],
})
export class AddConfigurationGroupDialogComponent {
	constructor(
		public dialogRef: MatDialogRef<AddConfigurationGroupDialogComponent>,
		@Inject(MAT_DIALOG_DATA) public data: addCfgGroup
	) {}

	onNoClick(): void {
		this.dialogRef.close();
	}
}
