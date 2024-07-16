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
import { CdkTrapFocus } from '@angular/cdk/a11y';
import { NgFor, NgIf } from '@angular/common';
import { Component, Inject } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { MatButton } from '@angular/material/button';
import {
	MAT_DIALOG_DATA,
	MatDialogActions,
	MatDialogClose,
	MatDialogContent,
	MatDialogRef,
	MatDialogTitle,
} from '@angular/material/dialog';
import { MatFormField, MatLabel } from '@angular/material/form-field';
import { MatInput } from '@angular/material/input';
import { MatListOption, MatSelectionList } from '@angular/material/list';
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
		MatDialogTitle,
		MatDialogContent,
		MatFormField,
		MatLabel,
		MatInput,
		MatSelectionList,
		MatListOption,
		MatDialogActions,
		MatButton,
		MatDialogClose,
		CdkTrapFocus,
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
