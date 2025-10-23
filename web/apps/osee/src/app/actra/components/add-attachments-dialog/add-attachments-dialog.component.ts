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
	changeDetection: ChangeDetectionStrategy.OnPush,
	templateUrl: './add-attachments-dialog.component.html',
})
export class AddAttachmentsDialogComponent {
	private dialogRef =
		inject<MatDialogRef<AddAttachmentsDialogComponent, File[] | undefined>>(
			MatDialogRef
		);
	protected data = inject<AddAttachmentsDialogData>(MAT_DIALOG_DATA);

	protected files = signal<File[]>([]);

	// Receive files from shared component and apply constraints/dedup
	onFilesSelected(incoming: File[]) {
		this.addFiles(incoming);
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

	private addFiles(incoming: File[]) {
		const next = [...this.files()];

		// Max files
		if (this.data?.maxFiles != null) {
			const remaining = Math.max(0, this.data.maxFiles - next.length);
			incoming = incoming.slice(0, remaining);
		}

		// Max size
		if (this.data?.maxFileSizeBytes != null) {
			incoming = incoming.filter(
				(f) => f.size <= this.data.maxFileSizeBytes!
			);
		}

		// Dedup by name+size
		const existing = new Set(next.map((f) => `${f.name}:${f.size}`));
		for (const f of incoming) {
			const key = `${f.name}:${f.size}`;
			if (!existing.has(key)) {
				next.push(f);
				existing.add(key);
			}
		}

		this.files.set(next);
	}
}
