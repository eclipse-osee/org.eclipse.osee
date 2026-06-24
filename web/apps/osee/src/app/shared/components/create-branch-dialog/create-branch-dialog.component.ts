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
		<h1 mat-dialog-title>
			<div class="tw-flex tw-flex-row tw-items-center tw-gap-2">
				Create Branch
			</div>
		</h1>
		<mat-dialog-content>
			<mat-form-field class="tw-w-full">
				<mat-label>Branch Name</mat-label>
				<input
					matInput
					[(ngModel)]="branchName" />
			</mat-form-field>
		</mat-dialog-content>
		<div
			mat-dialog-actions
			align="end"
			class="tw-gap-2">
			<button
				mat-stroked-button
				class="tw-text-osee-red-8"
				(click)="onCancel()">
				Cancel
			</button>
			<button
				mat-stroked-button
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
