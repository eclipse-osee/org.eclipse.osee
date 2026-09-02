/*********************************************************************
 * Copyright (c) 2026 Boeing
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
	computed,
	inject,
} from '@angular/core';
import {
	MAT_DIALOG_DATA,
	MatDialogActions,
	MatDialogClose,
	MatDialogContent,
	MatDialogRef,
	MatDialogTitle,
} from '@angular/material/dialog';
import { MatButton } from '@angular/material/button';
import { MatIcon } from '@angular/material/icon';

export type DispatchResultDialogData = {
	readonly content: unknown;
	readonly downloadFileName?: string;
};

@Component({
	selector: 'osee-dispatch-result-dialog',
	changeDetection: ChangeDetectionStrategy.OnPush,
	imports: [
		MatDialogTitle,
		MatDialogContent,
		MatDialogActions,
		MatDialogClose,
		MatButton,
		MatIcon,
	],
	template: `
		<h1 mat-dialog-title>
			<div class="tw-flex tw-flex-row tw-items-center tw-gap-2">
				<mat-icon>task</mat-icon>
				Publish Results
			</div>
		</h1>
		<mat-dialog-content>
			@if (isDownloadable()) {
				<p class="tw-text-foreground-secondary-text">
					The response is ready for download.
				</p>
			} @else {
				<pre
					class="tw-max-h-[70vh] tw-overflow-auto tw-whitespace-pre-wrap tw-text-sm"
					>{{ formatContent(data.content) }}</pre
				>
			}
		</mat-dialog-content>
		<div
			mat-dialog-actions
			align="end"
			class="tw-gap-2">
			<button
				mat-stroked-button
				class="tw-text-foreground-text"
				mat-dialog-close>
				Close
			</button>
			@if (isDownloadable()) {
				<button
					mat-stroked-button
					type="button"
					(click)="downloadFile()">
					Download
				</button>
			}
		</div>
	`,
})
export class DispatchResultDialogComponent {
	private readonly dialogRef = inject(
		MatDialogRef<DispatchResultDialogComponent>
	);
	readonly data = inject<DispatchResultDialogData>(MAT_DIALOG_DATA);

	readonly isDownloadable = computed(() => !!this.data.downloadFileName);

	formatContent(content: unknown): string {
		if (typeof content === 'string') {
			return content;
		}
		return JSON.stringify(content, null, 2);
	}

	downloadFile(): void {
		const content =
			typeof this.data.content === 'string'
				? this.data.content
				: JSON.stringify(this.data.content, null, 2);
		const blob = new Blob([content], {
			type: 'application/octet-stream',
		});
		const url = URL.createObjectURL(blob);
		const anchor = document.createElement('a');
		anchor.href = url;
		anchor.download = this.data.downloadFileName || 'result.txt';
		anchor.click();
		URL.revokeObjectURL(url);
	}
}
