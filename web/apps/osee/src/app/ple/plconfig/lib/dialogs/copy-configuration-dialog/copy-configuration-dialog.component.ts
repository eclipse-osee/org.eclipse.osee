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
import { Component, inject, signal } from '@angular/core';
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
import { combineLatest, from } from 'rxjs';
import { map, reduce, switchMap } from 'rxjs/operators';
import { PlConfigBranchService } from '../../services/pl-config-branch-service.service';
import {
	viewWithChangesAndGroups,
	viewWithGroups,
} from '../../types/pl-config-applicui-branch-mapping';
import { PLEditConfigData } from '../../types/pl-edit-config-data';
import { toObservable, toSignal } from '@angular/core/rxjs-interop';
import { UiService } from '@osee/shared/services';

@Component({
	selector: 'osee-plconfig-copy-configuration-dialog',
	templateUrl: './copy-configuration-dialog.component.html',
	styles: [],
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
	],
})
export class CopyConfigurationDialogComponent {
	dialogRef =
		inject<MatDialogRef<CopyConfigurationDialogComponent>>(MatDialogRef);
	dialogData = signal(inject<PLEditConfigData>(MAT_DIALOG_DATA));
	private _dialogData$ = toObservable(this.dialogData);

	private branchService = inject(PlConfigBranchService);
	private uiService = inject(UiService);
	private _viewId$ = this.uiService.viewId;
	viewId = toSignal(this._viewId$);

	branchApplicability = combineLatest([
		this._dialogData$,
		this._viewId$,
	]).pipe(
		switchMap(([data, viewId]) =>
			this.branchService.getBranchApplicability(
				data.currentBranch,
				viewId || ''
			)
		)
	);
	private _groups = this.branchApplicability.pipe(
		map((applic) => applic.groups)
	);
	private _untouchedViews = this.branchApplicability.pipe(
		map((applic) => applic.views)
	);
	views = combineLatest([this._groups, this._untouchedViews]).pipe(
		switchMap(([groups, notModified]) =>
			from(notModified).pipe(
				map((view) => {
					const newView: viewWithChangesAndGroups | viewWithGroups = {
						...view,
						groups: [],
					};
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

	selectDestinationBranch(event: MatSelectChange) {
		const oldData = this.dialogData();
		oldData.currentConfig = event.value;
		this.dialogData.set(oldData);
	}
	selectBranch(event: MatSelectChange) {
		const oldData = this.dialogData();
		oldData.copyFrom = event.value;
		this.dialogData.set(oldData);
	}
	onNoClick(): void {
		this.dialogRef.close();
	}
}
