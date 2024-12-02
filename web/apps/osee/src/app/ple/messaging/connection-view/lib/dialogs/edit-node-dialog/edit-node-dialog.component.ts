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
import type { nodeData } from '@osee/messaging/shared/types';
import { NewNodeFormComponent } from '../../forms/new-node-form/new-node-form.component';

@Component({
	selector: 'osee-edit-node-dialog',
	template: `<h1 mat-dialog-title>Editing {{ title() }}</h1>
		<mat-dialog-content>
			<form #nodeForm="ngForm">
				<osee-new-node-form
					[(node)]="node"
					fieldPrefix="Edit"></osee-new-node-form>
			</form>
		</mat-dialog-content>
		<div
			mat-dialog-actions
			align="end">
			<button
				mat-button
				(click)="onNoClick()">
				Cancel
			</button>
			<button
				mat-flat-button
				[mat-dialog-close]="node()"
				class="primary-button"
				[disabled]="nodeForm.invalid || nodeForm.pending">
				Ok
			</button>
		</div>`,
	imports: [
		MatDialogTitle,
		MatDialogClose,
		MatDialogContent,
		MatDialogActions,
		MatButton,
		FormsModule,
		NewNodeFormComponent,
	],
	changeDetection: ChangeDetectionStrategy.OnPush,
})
export class EditNodeDialogComponent {
	protected title = signal(inject<nodeData>(MAT_DIALOG_DATA).name.value);
	protected node = signal<nodeData>(inject<nodeData>(MAT_DIALOG_DATA));
	private _dialogRef = inject(MatDialogRef<EditNodeDialogComponent>);
	protected onNoClick() {
		this._dialogRef.close();
	}
}
