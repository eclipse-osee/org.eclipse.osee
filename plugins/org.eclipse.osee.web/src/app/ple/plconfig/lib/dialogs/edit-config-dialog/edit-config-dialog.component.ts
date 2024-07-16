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
import { AsyncPipe, NgFor, NgIf } from '@angular/common';
import { Component, Inject } from '@angular/core';
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
import { Observable } from 'rxjs';
import { PlConfigBranchService } from '../../services/pl-config-branch-service.service';
import { PlConfigCurrentBranchService } from '../../services/pl-config-current-branch.service';
import { PlConfigTypesService } from '../../services/pl-config-types.service';
import {
	ConfigGroup,
	PlConfigApplicUIBranchMapping,
} from '../../types/pl-config-applicui-branch-mapping';
import { cfgGroup } from '../../types/pl-config-branch';
import { PLEditConfigData } from '../../types/pl-edit-config-data';

@Component({
	selector: 'osee-plconfig-edit-configuration-dialog',
	templateUrl: './edit-config-dialog.component.html',
	styles: [],
	standalone: true,
	imports: [
		FormsModule,
		NgIf,
		NgFor,
		AsyncPipe,
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
		CdkTrapFocus,
	],
})
export class EditConfigurationDialogComponent {
	branchApplicability: Observable<PlConfigApplicUIBranchMapping>;
	cfgGroups: Observable<cfgGroup[]>;
	productApplicabilities = this.currentBranchService.productTypes;
	constructor(
		private typeService: PlConfigTypesService,
		public dialogRef: MatDialogRef<EditConfigurationDialogComponent>,
		@Inject(MAT_DIALOG_DATA) public data: PLEditConfigData,
		private branchService: PlConfigBranchService,
		private currentBranchService: PlConfigCurrentBranchService
	) {
		this.branchApplicability = this.branchService.getBranchApplicability(
			data.currentBranch
		);
		this.cfgGroups = this.currentBranchService.cfgGroups;
	}
	onNoClick(): void {
		this.dialogRef.close();
	}
	valueTracker(index: any, item: any) {
		return index;
	}
	compareCfgGroup(o1: ConfigGroup, o2: ConfigGroup) {
		return o1.id === o2.id;
	}
	/**istanbul ignore next */
	containsGroup(groups: ConfigGroup[], group: ConfigGroup) {
		return groups.map((g) => g.id).includes(group.id);
	}
}
