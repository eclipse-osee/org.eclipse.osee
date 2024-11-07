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
import { Component, inject } from '@angular/core';
import { MatButton } from '@angular/material/button';
import {
	MAT_DIALOG_DATA,
	MatDialogActions,
	MatDialogClose,
	MatDialogContent,
	MatDialogRef,
	MatDialogTitle,
} from '@angular/material/dialog';

@Component({
	selector: 'osee-messaging-remove-element-dialog',
	styles: [],
	standalone: true,
	imports: [
		MatDialogTitle,
		MatDialogContent,
		MatDialogActions,
		MatButton,
		MatDialogClose,
	],
	template: `<h1 mat-dialog-title>
			{{
				data.deleteRemove === 'delete'
					? 'Deleting Elements'
					: 'Removing from ' + data.removeFromName
			}}
		</h1>
		<mat-dialog-content>
			@if (data.deleteRemove === 'delete') {
				<p>Are you sure you want to delete the following elements?</p>
			} @else {
				<p>
					Are you sure you want to remove the following elements from
					{{ data.removeFromName }}?
				</p>
			}
			<ul>
				@for (
					element of data.elementsToRemove;
					track element.name.value
				) {
					<li>{{ element.name.value }}</li>
				}
			</ul>
		</mat-dialog-content>
		<mat-dialog-actions>
			<button
				mat-button
				mat-dialog-close="cancel"
				data-cy="cancel-btn">
				No
			</button>
			<button
				mat-flat-button
				class="primary-button"
				mat-dialog-close="ok"
				data-cy="submit-btn">
				Yes
			</button>
		</mat-dialog-actions> `,
})
export class RemoveElementDialogComponent {
	dialogRef =
		inject<MatDialogRef<RemoveElementDialogComponent>>(MatDialogRef);
	data = inject(MAT_DIALOG_DATA);
}
