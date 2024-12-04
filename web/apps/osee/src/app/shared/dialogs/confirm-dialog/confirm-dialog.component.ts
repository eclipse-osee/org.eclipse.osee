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
import { Component, computed, inject, signal } from '@angular/core';
import { MatButton } from '@angular/material/button';
import {
	MAT_DIALOG_DATA,
	MatDialogActions,
	MatDialogClose,
	MatDialogContent,
	MatDialogTitle,
} from '@angular/material/dialog';
import { MatLabel } from '@angular/material/form-field';

@Component({
	selector: 'osee-confirm-dialog',
	imports: [
		MatLabel,
		MatButton,
		MatDialogContent,
		MatDialogActions,
		MatDialogClose,
		MatDialogTitle,
	],
	template: `
		<h1 mat-dialog-title>{{ title() }}</h1>
		<mat-dialog-content>
			<mat-label>
				{{ text() }}
			</mat-label>
		</mat-dialog-content>
		<div mat-dialog-actions>
			<button
				mat-button
				[mat-dialog-close]="false">
				Cancel
			</button>
			<button
				mat-flat-button
				[mat-dialog-close]="true"
				class="primary-button">
				Ok
			</button>
		</div>
	`,
})
export class ConfirmDialogComponent {
	private data = signal(
		inject<{ title?: string; text?: string }>(MAT_DIALOG_DATA)
	);

	title = computed(() => this.data().title || 'Confirm');
	text = computed(() => this.data().text || 'Are you sure?');
}
