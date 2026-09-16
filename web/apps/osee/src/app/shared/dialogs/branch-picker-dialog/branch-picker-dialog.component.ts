/*********************************************************************
 * Copyright (c) 2026 Boeing
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
import { ChangeDetectionStrategy, Component, inject } from '@angular/core';
import { MatButton } from '@angular/material/button';
import {
	MatDialogActions,
	MatDialogContent,
	MatDialogRef,
	MatDialogTitle,
} from '@angular/material/dialog';
import { MatIcon } from '@angular/material/icon';
import { BranchPickerComponent } from '@osee/shared/components';

@Component({
	selector: 'osee-branch-picker-dialog',
	imports: [
		BranchPickerComponent,
		MatDialogTitle,
		MatDialogContent,
		MatDialogActions,
		MatButton,
		MatIcon,
	],
	changeDetection: ChangeDetectionStrategy.OnPush,
	template: `
		<h1 mat-dialog-title>
			<div class="tw-flex tw-flex-row tw-items-center tw-gap-2">
				<mat-icon>merge_type</mat-icon>
				Select Branch
			</div>
		</h1>
		<mat-dialog-content class="tw-pt-2">
			<osee-branch-picker />
		</mat-dialog-content>
		<div
			mat-dialog-actions
			align="end"
			class="tw-gap-2">
			<button
				mat-stroked-button
				class="tw-text-foreground-text"
				(click)="dialogRef.close()">
				Done
			</button>
		</div>
	`,
})
export class BranchPickerDialogComponent {
	protected dialogRef = inject(MatDialogRef<BranchPickerDialogComponent>);
}
