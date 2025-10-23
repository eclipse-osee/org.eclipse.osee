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
import { MatButton } from '@angular/material/button';
import { BytesPipe } from '../../../pipes/bytes.pipe';

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
	],
	changeDetection: ChangeDetectionStrategy.OnPush,
	templateUrl: './add-attachments-dialog.component.html',
})
export class AddAttachmentsDialogComponent {
	dialogRef =
		inject<MatDialogRef<AddAttachmentsDialogComponent, File[] | undefined>>(
			MatDialogRef
		);
	data = inject<AddAttachmentsDialogData>(MAT_DIALOG_DATA);

	files = signal<File[]>([]);
	dragActive = signal<boolean>(false);

	onFileInputChange(event: Event) {
		const input = event.target as HTMLInputElement;
		const list = input.files;
		if (!list?.length) return;
		this.addFiles(Array.from(list));
		input.value = '';
	}

	onDrop(event: DragEvent) {
		event.preventDefault();
		this.dragActive.set(false);
		const list = event.dataTransfer?.files;
		if (!list?.length) return;
		this.addFiles(Array.from(list));
	}

	onDragOver(event: DragEvent) {
		event.preventDefault();
		this.dragActive.set(true);
	}

	onDragLeave(event: DragEvent) {
		event.preventDefault();
		this.dragActive.set(false);
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
		if (!selected.length) {
			this.dialogRef.close();
			return;
		}
		this.dialogRef.close(selected);
	}

	cancel() {
		this.dialogRef.close();
	}

	private addFiles(incoming: File[]) {
		const next = [...this.files()];

		if (this.data?.maxFiles != null) {
			const remaining = Math.max(0, this.data.maxFiles - next.length);
			incoming = incoming.slice(0, remaining);
		}

		if (this.data?.maxFileSizeBytes != null) {
			incoming = incoming.filter(
				(f) => f.size <= this.data.maxFileSizeBytes!
			);
		}

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
