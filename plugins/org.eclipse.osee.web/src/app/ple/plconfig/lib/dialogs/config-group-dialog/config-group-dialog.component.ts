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
import { A11yModule } from '@angular/cdk/a11y';
import { NgFor, NgIf } from '@angular/common';
import { Component, Inject } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { MatButtonModule } from '@angular/material/button';
import {
	MatDialogModule,
	MatDialogRef,
	MAT_DIALOG_DATA,
} from '@angular/material/dialog';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatInputModule } from '@angular/material/input';
import { MatListModule } from '@angular/material/list';
import { view } from '../../types/pl-config-applicui-branch-mapping';
import { CfgGroupDialog } from '../../types/pl-config-cfggroups';

@Component({
	selector: 'osee-plconfig-config-group-dialog',
	templateUrl: './config-group-dialog.component.html',
	styles: [],
	standalone: true,
	imports: [
		NgIf,
		NgFor,
		FormsModule,
		MatFormFieldModule,
		MatInputModule,
		MatListModule,
		MatButtonModule,
		MatDialogModule,
		A11yModule,
	],
})
export class ConfigGroupDialogComponent {
	totalConfigurations: view[] = [];
	constructor(
		public dialogRef: MatDialogRef<ConfigGroupDialogComponent>,
		@Inject(MAT_DIALOG_DATA) public data: CfgGroupDialog
	) {
		this.totalConfigurations = data.configGroup.views;
	}
	onNoClick(): void {
		this.dialogRef.close();
	}
}
