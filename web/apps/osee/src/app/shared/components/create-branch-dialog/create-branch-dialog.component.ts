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
import { NgClass } from '@angular/common';
import {
	Component,
	ChangeDetectionStrategy,
	inject,
	signal,
} from '@angular/core';
import { FormsModule } from '@angular/forms';
import { MatButton } from '@angular/material/button';
import {
	MAT_DIALOG_DATA,
	MatDialogActions,
	MatDialogContent,
	MatDialogRef,
	MatDialogTitle,
} from '@angular/material/dialog';
import { MatFormField, MatLabel } from '@angular/material/form-field';
import { MatInput } from '@angular/material/input';

@Component({
	selector: 'osee-create-branch-dialog',
	imports: [
		NgClass,
		MatFormField,
		MatLabel,
		FormsModule,
		MatDialogTitle,
		MatDialogContent,
		MatInput,
		MatDialogActions,
		MatButton,
	],
	template: `
		<div class="tw-flex tw-items-center">
			<h1
				mat-dialog-title
				class="tw-my-0 tw-flex tw-w-full tw-items-center tw-gap-4 tw-pt-4">
				Enter Branch Name
			</h1>
		</div>
		<div mat-dialog-content>
			<mat-form-field class="tw-w-full">
				<mat-label>Branch Name</mat-label>
				<input
					matInput
					[(ngModel)]="branchName" />
			</mat-form-field>
		</div>
		<div
			mat-dialog-actions
			align="end">
			<button
				mat-button
				(click)="onCancel()">
				Cancel
			</button>
			<button
				mat-button
				[ngClass]="{
					'tw-bg-osee-blue-7 tw-text-background-background dark:tw-bg-osee-blue-10':
						branchNameNotEmpty(),
				}"
				[disabled]="!branchNameNotEmpty()"
				(click)="onSubmit()">
				Submit
			</button>
		</div>
	`,
	changeDetection: ChangeDetectionStrategy.OnPush,
})
export class CreateBranchDialogComponent {
	dialogRef = inject<MatDialogRef<CreateBranchDialogComponent>>(MatDialogRef);
	dialogData = signal(
		inject<{
			branchName: string;
		}>(MAT_DIALOG_DATA)
	);
	branchName = this.dialogData().branchName;

	branchNameNotEmpty() {
		return this.branchName.length > 0;
	}

	onCancel(): void {
		this.dialogRef.close('');
	}

	onSubmit(): void {
		this.dialogRef.close(this.branchName);
	}
}
