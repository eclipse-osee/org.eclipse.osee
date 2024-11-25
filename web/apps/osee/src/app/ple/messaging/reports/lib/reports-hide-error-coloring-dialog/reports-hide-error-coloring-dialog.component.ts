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
import { MatButton } from '@angular/material/button';
import {
	MAT_DIALOG_DATA,
	MatDialogActions,
	MatDialogClose,
	MatDialogContent,
	MatDialogTitle,
} from '@angular/material/dialog';
import { ConnectionValidationResult } from '@osee/messaging/shared/types';

@Component({
	selector: 'osee-reports-hide-error-coloring-dialog',
	imports: [
		MatDialogContent,
		MatDialogActions,
		MatDialogTitle,
		MatDialogClose,
		MatButton,
	],
	template: `<h2 mat-dialog-title>Disable Error Coloring?</h2>
		<mat-dialog-content
			class="tw-flex tw-flex-col tw-items-center tw-gap-2 tw-p-4 tw-text-center">
			@if (data().branch === '-1') {
				<p>
					Validation has not been run on the selected connection.
					Disabling error coloring will prevent any potential errors
					from being highlighted in the report.
				</p>
			} @else {
				<p>
					There are validation errors on this connection. Disabling
					error coloring will prevent those errors from being
					highlighted in the report.
				</p>
			}
			<p>Would you like to disable error coloring?</p>
		</mat-dialog-content>
		<mat-dialog-actions align="end">
			<button
				mat-button
				[mat-dialog-close]="{ value: false }">
				No
			</button>
			<button
				mat-flat-button
				[mat-dialog-close]="{ value: true }"
				class="primary-button">
				Disable
			</button>
		</mat-dialog-actions>`,
})
export class ReportsHideErrorColoringDialogComponent {
	protected data = signal(
		inject<ConnectionValidationResult>(MAT_DIALOG_DATA)
	);
}
