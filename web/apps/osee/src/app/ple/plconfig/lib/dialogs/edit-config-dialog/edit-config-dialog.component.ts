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
import { Component, inject } from '@angular/core';
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
import { PlConfigCurrentBranchService } from '../../services/pl-config-current-branch.service';
import { ConfigGroup } from '../../types/pl-config-applicui-branch-mapping';
import { PLEditConfigData } from '../../types/pl-edit-config-data';

@Component({
	selector: 'osee-plconfig-edit-configuration-dialog',
	templateUrl: './edit-config-dialog.component.html',
	styles: [],
	imports: [
		FormsModule,
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
		ViewSelectorComponent,
	],
})
export class EditConfigurationDialogComponent {
	dialogRef =
		inject<MatDialogRef<EditConfigurationDialogComponent>>(MatDialogRef);
	data = inject<PLEditConfigData>(MAT_DIALOG_DATA);
	private currentBranchService = inject(PlConfigCurrentBranchService);
	private uiService = inject(UiService);

	cfgGroups = this.currentBranchService.cfgGroups;
	productApplicabilities = this.currentBranchService.productTypes;
	viewId = toSignal(this.uiService.viewId);

	onNoClick(): void {
		this.dialogRef.close();
	}
	valueTracker(index: number, _item: unknown) {
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
