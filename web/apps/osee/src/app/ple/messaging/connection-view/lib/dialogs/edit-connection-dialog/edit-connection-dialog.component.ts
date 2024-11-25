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
	ChangeDetectionStrategy,
	Component,
	inject,
	signal,
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
import { ApplicabilityDropdownComponent } from '@osee/applicability/applicability-dropdown';
import type { connection } from '@osee/messaging/shared/types';
import { TransportTypeDropdownComponent } from '@osee/messaging/transports/dropdown';
import { writableSlice } from '@osee/shared/utils';

@Component({
	selector: 'osee-edit-connection-dialog',
	template: `<h1 mat-dialog-title>Editing {{ title() }}</h1>
		<mat-dialog-content>
			<form #connectionForm="ngForm">
				<mat-form-field id="connection-name-field">
					<mat-label>Add a Name</mat-label>
					<input
						matInput
						type="text"
						name="name"
						[(ngModel)]="name"
						#input
						required />
				</mat-form-field>
				<br />
				<mat-form-field id="connection-description-field">
					<mat-label>Add a Description</mat-label>
					<input
						matInput
						type="text"
						name="description"
						[(ngModel)]="description"
						#input />
				</mat-form-field>
				<br />
				<osee-transport-type-dropdown
					#transportTypeDropdown
					[(transportType)]="
						transportType
					"></osee-transport-type-dropdown>
				<br />
				<osee-applicability-dropdown
					[(applicability)]="applicability"
					[required]="true">
				</osee-applicability-dropdown>
			</form>
		</mat-dialog-content>
		<div mat-dialog-actions>
			<button
				mat-button
				(click)="onNoClick()">
				Cancel
			</button>
			<button
				mat-flat-button
				[mat-dialog-close]="connection()"
				class="primary-button"
				[disabled]="connectionForm.invalid || connectionForm.pending">
				Ok
			</button>
		</div>`,
	imports: [
		MatDialogTitle,
		MatDialogContent,
		MatDialogActions,
		MatDialogClose,
		MatFormField,
		MatLabel,
		FormsModule,
		MatInput,
		MatButton,
		ApplicabilityDropdownComponent,
		TransportTypeDropdownComponent,
	],
	changeDetection: ChangeDetectionStrategy.OnPush,
})
export class EditConnectionDialogComponent {
	protected title = signal(inject<connection>(MAT_DIALOG_DATA).name.value);
	protected connection = signal<connection>(
		inject<connection>(MAT_DIALOG_DATA)
	);
	private nameAttr = writableSlice(this.connection, 'name');
	protected name = writableSlice(this.nameAttr, 'value');
	private descriptionAttr = writableSlice(this.connection, 'description');
	protected description = writableSlice(this.descriptionAttr, 'value');
	protected transportType = writableSlice(this.connection, 'transportType');
	protected applicability = writableSlice(this.connection, 'applicability');
	private _dialogRef = inject(MatDialogRef<EditConnectionDialogComponent>);
	onNoClick() {
		this._dialogRef.close();
	}
}
