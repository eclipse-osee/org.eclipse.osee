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
import {
	Component,
	inject,
	signal,
	computed,
	linkedSignal,
	untracked,
} from '@angular/core';
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
import { CfgGroupDialog } from '../../types/pl-config-cfggroups';

@Component({
	selector: 'osee-plconfig-config-group-dialog',
	templateUrl: './config-group-dialog.component.html',
	styles: [],
	imports: [
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
	],
})
export class ConfigGroupDialogComponent {
	dialogRef = inject<MatDialogRef<ConfigGroupDialogComponent>>(MatDialogRef);
	private data = signal(inject<CfgGroupDialog>(MAT_DIALOG_DATA));

	protected name = linkedSignal(() => this.data().configGroup.name);
	protected description = linkedSignal(
		() => this.data().configGroup.description
	);
	protected views = linkedSignal(() => this.data().configGroup.views);
	protected results = computed<CfgGroupDialog>(() => {
		return {
			configGroup: {
				id: this.data().configGroup.id,
				name: this.name(),
				description: this.description(),
				views: this.views(),
				configurations: this.data().configGroup.configurations,
			},
			editable: this.data().editable,
		};
	});
	protected totalConfigurations = computed(() => {
		return untracked(() => this.data().configGroup.views);
	});

	onNoClick(): void {
		this.dialogRef.close();
	}
}
