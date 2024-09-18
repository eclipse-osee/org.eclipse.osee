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
import { AsyncPipe } from '@angular/common';
import { Component, Inject } from '@angular/core';
import { toSignal } from '@angular/core/rxjs-interop';
import { FormsModule } from '@angular/forms';
import { MatButton } from '@angular/material/button';
import { MatOption } from '@angular/material/core';
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
import { MatSelect } from '@angular/material/select';
import { ViewSelectorComponent } from '@osee/shared/components';
import { UiService } from '@osee/shared/services';
import { PlConfigBranchService } from '../../services/pl-config-branch-service.service';
import { PlConfigCurrentBranchService } from '../../services/pl-config-current-branch.service';
import { PlConfigTypesService } from '../../services/pl-config-types.service';
import { PLAddConfigData } from '../../types/pl-edit-config-data';

@Component({
	selector: 'osee-plconfig-add-configuration-dialog',
	templateUrl: './add-configuration-dialog.component.html',
	styles: [],
	standalone: true,
	imports: [
		AsyncPipe,
		FormsModule,
		MatDialogTitle,
		MatDialogContent,
		MatFormField,
		MatLabel,
		MatInput,
		MatSelect,
		MatOption,
		MatSelectionList,
		MatListOption,
		MatDialogActions,
		MatButton,
		MatDialogClose,
		ViewSelectorComponent,
	],
})
export class AddConfigurationDialogComponent {
	cfgGroups = this.currentBranchService.cfgGroups;
	productApplicabilities = this.currentBranchService.productTypes;
	viewId = toSignal(this.uiService.viewId);
	constructor(
		public dialogRef: MatDialogRef<AddConfigurationDialogComponent>,
		@Inject(MAT_DIALOG_DATA) public data: PLAddConfigData,
		private currentBranchService: PlConfigCurrentBranchService,
		private uiService: UiService
	) {}

	onNoClick(): void {
		this.dialogRef.close();
	}
	valueTracker(index: any, item: any) {
		return index;
	}
}
