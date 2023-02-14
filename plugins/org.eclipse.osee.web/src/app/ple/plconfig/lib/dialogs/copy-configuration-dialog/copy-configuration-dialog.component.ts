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
import { AsyncPipe, NgFor } from '@angular/common';
import { Component, Inject } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { MatButtonModule } from '@angular/material/button';
import { MatOptionModule } from '@angular/material/core';
import {
	MatDialogModule,
	MatDialogRef,
	MAT_DIALOG_DATA,
} from '@angular/material/dialog';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatSelectChange, MatSelectModule } from '@angular/material/select';
import { combineLatest, from, Observable } from 'rxjs';
import { map, reduce, switchMap } from 'rxjs/operators';
import { PlConfigBranchService } from '../../services/pl-config-branch-service.service';
import {
	PlConfigApplicUIBranchMapping,
	view,
	viewWithChanges,
	viewWithChangesAndGroups,
	viewWithGroups,
} from '../../types/pl-config-applicui-branch-mapping';
import {
	configGroup,
	configGroupWithChanges,
} from '../../types/pl-config-configurations';
import { PLEditConfigData } from '../../types/pl-edit-config-data';

@Component({
	selector: 'osee-plconfig-copy-configuration-dialog',
	templateUrl: './copy-configuration-dialog.component.html',
	styleUrls: ['./copy-configuration-dialog.component.sass'],
	standalone: true,
	imports: [
		FormsModule,
		NgFor,
		AsyncPipe,
		MatDialogModule,
		MatFormFieldModule,
		MatSelectModule,
		MatOptionModule,
		MatButtonModule,
		A11yModule,
	],
})
export class CopyConfigurationDialogComponent {
	branchApplicability: Observable<PlConfigApplicUIBranchMapping>;
	private _groups: Observable<(configGroup | configGroupWithChanges)[]>;
	private _untouchedViews: Observable<(view | viewWithChanges)[]>;
	views: Observable<(viewWithChangesAndGroups | viewWithGroups)[]>;
	constructor(
		public dialogRef: MatDialogRef<CopyConfigurationDialogComponent>,
		@Inject(MAT_DIALOG_DATA) public data: PLEditConfigData,
		private branchService: PlConfigBranchService
	) {
		this.branchApplicability = this.branchService.getBranchApplicability(
			data.currentBranch
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
						let newView: viewWithChangesAndGroups | viewWithGroups =
							{ ...view, groups: [] };
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
