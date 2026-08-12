/*********************************************************************
 * Copyright (c) 2025 Boeing
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
import { Component, inject } from '@angular/core';
import { toSignal } from '@angular/core/rxjs-interop';
import { MatDialog } from '@angular/material/dialog';
import { MatIcon } from '@angular/material/icon';
import { MatIconButton } from '@angular/material/button';
import { MatTooltip } from '@angular/material/tooltip';
import { ChangeReportTableComponent } from '../artifact-explorer/lib/components/change-report-table/change-report-table.component';
import { UiService, BranchRoutedUIService } from '@osee/shared/services';
import { ActraPageTitleComponent } from '../../actra/actra-page-title/actra-page-title.component';
import { BranchPickerDialogComponent } from '../../shared/dialogs/branch-picker-dialog/branch-picker-dialog.component';

@Component({
	selector: 'osee-change-report',
	imports: [
		ChangeReportTableComponent,
		ActraPageTitleComponent,
		MatIcon,
		MatIconButton,
		MatTooltip,
	],
	template: `
		<div class="tw-flex tw-items-center tw-pr-6">
			<osee-actra-page-title
				title="Change Report"
				icon="differences" />
			<span class="tw-flex-1"></span>
			<button
				mat-icon-button
				(click)="openBranchPicker()"
				matTooltip="Select Branch">
				<mat-icon>merge_type</mat-icon>
			</button>
		</div>
		@if (branchId() && branchId() !== '0') {
			<osee-change-report-table [branchId]="branchId()!" />
		} @else {
			<div class="tw-p-6 tw-text-sm tw-opacity-50">
				Click the branch icon in the top-right to select a branch.
			</div>
		}
	`,
})
export class ChangeReportComponent {
	private uiService = inject(UiService);
	private dialog = inject(MatDialog);
	// Load-bearing: instantiating BranchRoutedUIService wires up the sync that
	// reads branchId/branchType from the URL query params into uiService. Do not
	// remove even though it is not referenced directly in this component.
	private _branchRouter = inject(BranchRoutedUIService);

	protected branchId = toSignal(this.uiService.id, { initialValue: '' });

	protected openBranchPicker() {
		this.dialog.open(BranchPickerDialogComponent, {
			minWidth: '400px',
		});
	}
}
export default ChangeReportComponent;
