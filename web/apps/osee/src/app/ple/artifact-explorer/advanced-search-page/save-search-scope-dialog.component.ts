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
 *
 * Author: Eihab Khudhair (ekhudhai)
 * Task 278 - Prompt user to choose Global or Private scope when saving a search
 *********************************************************************/
import { Component, inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import {
	MAT_DIALOG_DATA,
	MatDialogModule,
	MatDialogRef,
} from '@angular/material/dialog';
import { MatButtonModule } from '@angular/material/button';

export type SaveSearchScopeDialogData = {
	searchTitle: string;
};

export type SaveSearchScopeDialogResult =
	| { action: 'global' }
	| { action: 'private' }
	| { action: 'cancel' };

@Component({
	selector: 'osee-save-search-scope-dialog',
	standalone: true,
	imports: [CommonModule, MatDialogModule, MatButtonModule],
	template: `
		<div
			class="tw-px-6 tw-py-5 tw-rounded-2xl tw-bg-white tw-text-slate-900 tw-border tw-border-slate-200 dark:tw-bg-slate-900 dark:tw-text-slate-100 dark:tw-border-slate-700">
			<h2 class="tw-text-lg tw-font-semibold tw-mb-2">
				Save Search
			</h2>

			<p class="tw-text-sm tw-text-slate-600 dark:tw-text-slate-300 tw-mb-5">
				Choose where you want to save
				<strong class="tw-text-slate-900 dark:tw-text-slate-100">
					{{ data.searchTitle }}
				</strong>.
			</p>

            <div class="tw-flex tw-justify-center tw-gap-3 tw-mt-2 tw-flex-wrap">
				<button
					mat-stroked-button
					type="button"
					class="dark:tw-text-slate-100 dark:tw-border-slate-500"
					(click)="onCancel()">
					Cancel
				</button>

				<button
					mat-flat-button
					type="button"
					class="primary-button"
					(click)="onSavePrivate()">
					Private Search
				</button>

				<button
					mat-flat-button
					type="button"
					class="primary-button"
					(click)="onSaveGlobal()">
					Global Search
				</button>
			</div>
		</div>
	`,
})
export class SaveSearchScopeDialogComponent {
	private readonly dialogRef =
		inject<
			MatDialogRef<
				SaveSearchScopeDialogComponent,
				SaveSearchScopeDialogResult
			>
		>(MatDialogRef);

	readonly data = inject<SaveSearchScopeDialogData>(MAT_DIALOG_DATA);

	onSaveGlobal(): void {
		this.dialogRef.close({ action: 'global' });
	}

	onSavePrivate(): void {
		this.dialogRef.close({ action: 'private' });
	}

	onCancel(): void {
		this.dialogRef.close({ action: 'cancel' });
	}
}