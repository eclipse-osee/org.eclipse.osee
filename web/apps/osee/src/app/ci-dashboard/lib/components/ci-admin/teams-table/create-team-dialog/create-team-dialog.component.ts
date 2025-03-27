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
import { MatTooltip } from '@angular/material/tooltip';
import { writableSlice } from '@osee/shared/utils';
import { ScriptTeam } from '../../../../types';

@Component({
	selector: 'osee-create-team-dialog',
	imports: [
		FormsModule,
		MatDialogTitle,
		MatDialogContent,
		MatDialogActions,
		MatDialogClose,
		MatFormField,
		MatTooltip,
		MatLabel,
		MatInput,
		MatButton,
	],
	template: `<form #createTeamForm="ngForm">
		<h2 mat-dialog-title>Create New Team</h2>
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
					createTeamForm.invalid ||
					createTeamForm.pending ||
					createTeamForm.disabled
						? 'Complete the form to create Team'
						: 'Create Team'
				">
				<button
					mat-flat-button
					[mat-dialog-close]="data()"
					class="primary-button"
					[disabled]="
						createTeamForm.invalid ||
						createTeamForm.pending ||
						createTeamForm.disabled
					"
					data-cy="submit-btn">
					Ok
				</button>
			</div>
		</mat-dialog-actions>
	</form>`,
})
export class CreateTeamDialogComponent {
	dialogRef = inject<MatDialogRef<CreateTeamDialogComponent>>(MatDialogRef);

	protected data = signal(inject<ScriptTeam>(MAT_DIALOG_DATA));
	private nameAttr = writableSlice(this.data, 'name');
	protected name = writableSlice(this.nameAttr, 'value');

	onNoClick() {
		this.dialogRef.close();
	}
}
