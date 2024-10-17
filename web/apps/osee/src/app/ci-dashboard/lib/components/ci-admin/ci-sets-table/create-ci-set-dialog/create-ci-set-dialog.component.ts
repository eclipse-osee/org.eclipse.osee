/*********************************************************************
 * Copyright (c) 2024 Boeing
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
import { Component, inject, signal } from '@angular/core';
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
import { MatSlideToggle } from '@angular/material/slide-toggle';
import { MatTooltip } from '@angular/material/tooltip';
import { writableSlice } from '@osee/shared/utils';
import { CISet } from '../../../../types';

@Component({
	selector: 'osee-create-ci-set-dialog',
	standalone: true,
	imports: [
		FormsModule,
		MatDialogTitle,
		MatDialogContent,
		MatDialogActions,
		MatDialogClose,
		MatFormField,
		MatTooltip,
		MatLabel,
		MatSlideToggle,
		MatInput,
		MatButton,
	],
	template: `<form #createCISetForm="ngForm">
		<h2 mat-dialog-title>Create New CI Set</h2>
		<mat-dialog-content>
			<mat-form-field class="tw-w-full">
				<mat-label>Name</mat-label>
				<input
					matInput
					type="text"
					[(ngModel)]="name"
					#input
					required
					name="name"
					data-cy="field-name" />
			</mat-form-field>
			<mat-slide-toggle
				[(ngModel)]="active"
				name="write_access"
				[labelPosition]="active() ? 'after' : 'before'"
				data-cy="field-write-access"
				class="tw-w-full tw-pb-4">
				@if (active()) {
					Active
				} @else {
					Not Active
				}
			</mat-slide-toggle>
		</mat-dialog-content>
		<mat-dialog-actions align="end">
			<button
				mat-button
				(click)="onNoClick()"
				data-cy="cancel-btn">
				Cancel
			</button>
			<div
				[matTooltip]="
					createCISetForm.invalid ||
					createCISetForm.pending ||
					createCISetForm.disabled
						? 'Complete the form to create CI Set'
						: 'Create CI Set'
				">
				<button
					mat-flat-button
					[mat-dialog-close]="data()"
					class="primary-button"
					[disabled]="
						createCISetForm.invalid ||
						createCISetForm.pending ||
						createCISetForm.disabled
					"
					data-cy="submit-btn">
					Ok
				</button>
			</div>
		</mat-dialog-actions>
	</form>`,
})
export class CreateCiSetDialogComponent {
	dialogRef = inject<MatDialogRef<CreateCiSetDialogComponent>>(MatDialogRef);

	protected data = signal(inject<CISet>(MAT_DIALOG_DATA));
	private nameAttr = writableSlice(this.data, 'name');
	protected name = writableSlice(this.nameAttr, 'value');
	private activeAttr = writableSlice(this.data, 'active');
	protected active = writableSlice(this.activeAttr, 'value');

	onNoClick() {
		this.dialogRef.close();
	}
}
