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
import { CommonModule } from '@angular/common';
import {
	ChangeDetectionStrategy,
	Component,
	inject,
	signal,
} from '@angular/core';
import {
	MAT_DIALOG_DATA,
	MatDialogActions,
	MatDialogContent,
	MatDialogRef,
	MatDialogTitle,
} from '@angular/material/dialog';
import { MatButton, MatIconButton } from '@angular/material/button';
import { BytesPipe } from '@osee/shared/utils';
import { MatList, MatListItem } from '@angular/material/list';
import { MatTooltip } from '@angular/material/tooltip';
import { DragAndDropUploadComponent } from '@osee/shared/components';
import { MatIcon } from '@angular/material/icon';
import { MatSnackBar } from '@angular/material/snack-bar';

export type AddAttachmentsDialogData = {
	maxFiles?: number;
	maxFileSizeBytes?: number;
	accept?: string;
};

@Component({
	selector: 'osee-add-attachments-dialog',
	imports: [
		CommonModule,
		MatDialogTitle,
		MatDialogContent,
		MatDialogActions,
		MatButton,
		BytesPipe,
		MatList,
		MatListItem,
		MatTooltip,
		DragAndDropUploadComponent,
		MatIconButton,
		MatIcon,
	],
	providers: [BytesPipe],
	changeDetection: ChangeDetectionStrategy.OnPush,
	templateUrl: './add-attachments-dialog.component.html',
})
export class AddAttachmentsDialogComponent {
	private dialogRef =
		inject<MatDialogRef<AddAttachmentsDialogComponent, File[] | undefined>>(
			MatDialogRef
		);
	protected data = inject<AddAttachmentsDialogData>(MAT_DIALOG_DATA);
	private bytesPipe = inject(BytesPipe);
	private snackBar = inject(MatSnackBar);

	protected files = signal<File[]>([]);

	// Receive files from shared component and apply constraints/dedup
	onFilesSelected(files: File[]) {
		const current = this.files();
		const next = [...current];

		// 1) Enforce max files.
		let countRejectedForNumber = 0;
		let candidates = files;

		if (this.data?.maxFiles != null) {
			const remaining = Math.max(0, this.data.maxFiles - current.length);
			if (files.length > remaining) {
				countRejectedForNumber = files.length - remaining;
			}
			candidates = files.slice(0, remaining);
		}

		// 2) Enforce max size.
		const sizeRejected: File[] = [];
		const sizeAccepted: File[] = [];
		const maxBytes = this.data?.maxFileSizeBytes ?? null;

		for (const f of candidates) {
			if (maxBytes != null && f.size > maxBytes) {
				sizeRejected.push(f);
			} else {
				sizeAccepted.push(f);
			}
		}

		// 3) Dedup accepted files and update state.
		const existingKeys = new Set(next.map((f) => `${f.name}:${f.size}`));
		for (const f of sizeAccepted) {
			const key = `${f.name}:${f.size}`;
			if (!existingKeys.has(key)) {
				next.push(f);
				existingKeys.add(key);
			}
		}
		this.files.set(next);

		// 4) Snackbar summary for rejections.
		const messages: string[] = [];

		if (countRejectedForNumber > 0 && this.data?.maxFiles != null) {
			messages.push(
				`${countRejectedForNumber} file${countRejectedForNumber > 1 ? 's' : ''} not added due to max count (${this.data.maxFiles}).`
			);
		}

		if (sizeRejected.length > 0 && maxBytes != null) {
			const maxLabel = this.bytesPipe.transform(maxBytes, 0);
			messages.push(
				`${sizeRejected.length} file${sizeRejected.length > 1 ? 's' : ''} rejected for exceeding ${maxLabel}.`
			);
		}

		if (messages.length) {
			this.snackBar.open(messages.join(' '), 'Dismiss', {
				duration: 4000,
			});
		}
	}

	removeFile(index: number) {
		const arr = [...this.files()];
		arr.splice(index, 1);
		this.files.set(arr);
	}

	clear() {
		this.files.set([]);
	}

	submit() {
		const selected = this.files();
		this.dialogRef.close(selected.length ? selected : undefined);
	}

	cancel() {
		this.dialogRef.close(undefined);
	}
}
