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
import { MatIcon } from '@angular/material/icon';
import { MatTooltip } from '@angular/material/tooltip';
import { DragAndDropUploadComponent } from '@osee/shared/components';

export type UploadImageDialogData = {
	readonly branchId: string;
	readonly artifactId: string;
};

export type UploadImageDialogResult = {
	readonly file: File;
};

const SUPPORTED_IMAGE_EXTENSIONS = [
	'.png',
	'.jpg',
	'.jpeg',
	'.gif',
	'.bmp',
	'.webp',
	'.svg',
];

const ACCEPTED_IMAGE_TYPES = SUPPORTED_IMAGE_EXTENSIONS.join(',');

@Component({
	selector: 'osee-upload-image-dialog',
	imports: [
		MatDialogTitle,
		MatDialogContent,
		MatDialogActions,
		MatButton,
		MatIconButton,
		MatIcon,
		MatTooltip,
		DragAndDropUploadComponent,
	],
	changeDetection: ChangeDetectionStrategy.OnPush,
	templateUrl: './upload-image-dialog.component.html',
})
export class UploadImageDialogComponent {
	private readonly dialogRef =
		inject<
			MatDialogRef<
				UploadImageDialogComponent,
				UploadImageDialogResult | undefined
			>
		>(MatDialogRef);
	protected readonly data = inject<UploadImageDialogData>(MAT_DIALOG_DATA);

	protected readonly selectedFile = signal<File | null>(null);
	protected readonly previewUrl = signal<string | null>(null);
	protected readonly acceptedTypes = ACCEPTED_IMAGE_TYPES;

	protected onFilesSelected(files: File[]): void {
		const file = files[0];
		if (!file) {
			return;
		}
		this.selectedFile.set(file);
		this.generatePreview(file);
	}

	protected removeFile(): void {
		this.revokePreview();
		this.selectedFile.set(null);
		this.previewUrl.set(null);
	}

	protected submitUpload(): void {
		const file = this.selectedFile();
		if (!file) {
			return;
		}
		this.dialogRef.close({ file });
	}

	protected cancelDialog(): void {
		this.revokePreview();
		this.dialogRef.close(undefined);
	}

	private generatePreview(file: File): void {
		this.revokePreview();
		const url = URL.createObjectURL(file);
		this.previewUrl.set(url);
	}

	private revokePreview(): void {
		const current = this.previewUrl();
		if (current) {
			URL.revokeObjectURL(current);
		}
	}
}
