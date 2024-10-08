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
import { AsyncPipe } from '@angular/common';
import { Component, inject } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { MatButton } from '@angular/material/button';
import { MatOption } from '@angular/material/core';
import {
	MAT_DIALOG_DATA,
	MatDialogActions,
	MatDialogClose,
	MatDialogRef,
	MatDialogTitle,
} from '@angular/material/dialog';
import { MatFormField, MatLabel } from '@angular/material/form-field';
import { MatSelect, MatSelectChange } from '@angular/material/select';
import { Observable, combineLatest, from } from 'rxjs';
import { map, reduce, switchMap } from 'rxjs/operators';
import { PlConfigBranchService } from '../../services/pl-config-branch-service.service';
import {
	PlConfigApplicUIBranchMapping,
	view,
	viewWithChanges,
	viewWithChangesAndGroups,
	viewWithGroups,
} from '../../types/pl-config-applicui-branch-mapping';
import { configGroup } from '../../types/pl-config-configurations';
import { PLEditConfigData } from '../../types/pl-edit-config-data';
import { toSignal } from '@angular/core/rxjs-interop';
import { UiService } from '@osee/shared/services';

@Component({
	selector: 'osee-plconfig-copy-configuration-dialog',
	templateUrl: './copy-configuration-dialog.component.html',
	styles: [],
	standalone: true,
	imports: [
		FormsModule,
		AsyncPipe,
		MatDialogTitle,
		MatLabel,
		MatFormField,
		MatSelect,
		MatOption,
		MatDialogActions,
		MatButton,
		MatDialogClose,
		CdkTrapFocus,
	],
})
export class CopyConfigurationDialogComponent {
	dialogRef =
		inject<MatDialogRef<CopyConfigurationDialogComponent>>(MatDialogRef);
	data = inject<PLEditConfigData>(MAT_DIALOG_DATA);
	private branchService = inject(PlConfigBranchService);
	private uiService = inject(UiService);

	branchApplicability: Observable<PlConfigApplicUIBranchMapping>;
	private _groups: Observable<configGroup[]>;
	private _untouchedViews: Observable<(view | viewWithChanges)[]>;
	views: Observable<(viewWithChangesAndGroups | viewWithGroups)[]>;
	viewId = toSignal(this.uiService.viewId);

	/** Inserted by Angular inject() migration for backwards compatibility */
	constructor(...args: unknown[]);
	constructor() {
		const data = this.data;

		this.branchApplicability = this.branchService.getBranchApplicability(
			data.currentBranch,
			this.viewId() || ''
		);
		this._groups = this.branchApplicability.pipe(
			map((applic) => applic.groups)
		);
		this._untouchedViews = this.branchApplicability.pipe(
			map((applic) => applic.views)
		);
		this.views = combineLatest([this._groups, this._untouchedViews]).pipe(
			switchMap(([groups, notModified]) =>
				from(notModified).pipe(
					map((view) => {
						const newView:
							| viewWithChangesAndGroups
							| viewWithGroups = { ...view, groups: [] };
						if (
							groups
								.map((g) => g.configurations)
								.flat()
								.includes(view.id)
						) {
							newView.groups = groups.filter((g) =>
								g.configurations.includes(view.id)
							);
						}
						return newView;
					})
				)
			),
			reduce(
				(acc, curr) => [...acc, curr],
				[] as (viewWithChangesAndGroups | viewWithGroups)[]
			)
		);
	}

	selectDestinationBranch(event: MatSelectChange) {
		this.data.currentConfig = event.value;
	}
	selectBranch(event: MatSelectChange) {
		this.data.copyFrom = event.value;
	}
	onNoClick(): void {
		this.dialogRef.close();
	}
}
