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
import { ChangeDetectionStrategy, Component, inject, signal } from '@angular/core';
import {
	MAT_DIALOG_DATA,
	MatDialogActions,
	MatDialogContent,
	MatDialogRef,
	MatDialogTitle,
} from '@angular/material/dialog';
import { MatButton } from '@angular/material/button';
import { BytesPipe } from '../../../pipes/bytes.pipe';

export type UpdateAttachmentDialogData = {
	attachment: {
		id: string;
		fileName: string;
		sizeBytes?: number;
		contentType?: string;
	};
	maxFileSizeBytes?: number;
	accept?: string;
};

@Component({
	selector: 'osee-update-attachment-dialog',
	imports: [
		CommonModule,
		MatDialogTitle,
		MatDialogContent,
		MatDialogActions,
		MatButton,
		BytesPipe,
	],
	changeDetection: ChangeDetectionStrategy.OnPush,
	templateUrl: './update-attachment-dialog.component.html',
})
export class UpdateAttachmentDialogComponent {
	dialogRef =
		inject<MatDialogRef<UpdateAttachmentDialogComponent, File | null>>(
			MatDialogRef
		);
	data = inject<UpdateAttachmentDialogData>(MAT_DIALOG_DATA);

	// Local state
	file = signal<File | null>(null);
	dragActive = signal<boolean>(false);

	// Handlers
	onFileInputChange(event: Event) {
		const input = event.target as HTMLInputElement;
		const list = input.files;
		if (!list?.length) return;
		this.setFile(list[0]);
		input.value = '';
	}

	onDrop(event: DragEvent) {
		event.preventDefault();
		this.dragActive.set(false);
		const list = event.dataTransfer?.files;
		if (!list?.length) return;
		this.setFile(list[0]);
	}

	onDragOver(event: DragEvent) {
		event.preventDefault();
		this.dragActive.set(true);
	}

	onDragLeave(event: DragEvent) {
		event.preventDefault();
		this.dragActive.set(false);
	}

	removeFile() {
		this.file.set(null);
	}

	// Close the dialog returning the selected file.
	submit() {
		const selected = this.file();
		if (!selected) {
			this.dialogRef.close(null);
			return;
		}
		this.dialogRef.close(selected);
	}

	// Close the dialog without returning a file.
	onCancel() {
		this.dialogRef.close(null);
	}

	// Helper to apply optional constraints before setting
	private setFile(incoming: File) {
		if (this.data?.maxFileSizeBytes != null) {
			if (incoming.size > this.data.maxFileSizeBytes) {
				// Optionally, show a message via another signal and return
				return;
			}
		}
		this.file.set(incoming);
	}
}
