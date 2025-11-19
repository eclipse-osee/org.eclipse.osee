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
import { DragAndDropUploadComponent } from '@osee/shared/components';
import { MatIcon } from '@angular/material/icon';
import { MatTooltip } from '@angular/material/tooltip';
import { MatSnackBar } from '@angular/material/snack-bar';

export type UpdateAttachmentDialogData = {
	attachment: {
		id: string;
		fileName: string;
		sizeBytes: number;
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
		DragAndDropUploadComponent,
		MatIcon,
		MatIconButton,
		MatTooltip,
	],
	providers: [BytesPipe],
	changeDetection: ChangeDetectionStrategy.OnPush,
	templateUrl: './update-attachment-dialog.component.html',
})
export class UpdateAttachmentDialogComponent {
	private dialogRef =
		inject<MatDialogRef<UpdateAttachmentDialogComponent, File | undefined>>(
			MatDialogRef
		);
	protected data = inject<UpdateAttachmentDialogData>(MAT_DIALOG_DATA);
	private bytesPipe = inject(BytesPipe);
	private snackBar = inject(MatSnackBar);

	// Local state: selected file
	protected file = signal<File | null>(null);

	// Receive files from the shared component, pick the first (single-file update)
	onFileSelected(files: File[]) {
		const total = files?.length ?? 0;
		const maxBytes = this.data?.maxFileSizeBytes ?? null;

		// Candidate is at most the first file.
		const candidate = total > 0 ? files[0] : null;

		// Validate size for the candidate.
		if (candidate && maxBytes != null && candidate.size > maxBytes) {
			const maxLabel = this.bytesPipe.transform(maxBytes, 0);

			this.snackBar.open(
				`Selected file exceeds ${maxLabel}.`,
				'Dismiss',
				{
					duration: 4000,
				}
			);

			// If rejected, ensure no file is set to prevent accidental updates.
			this.file.set(null);
		} else if (candidate) {
			this.file.set(candidate);
		}
	}

	removeFile() {
		this.file.set(null);
	}

	// Close the dialog returning the selected file (or undefined on cancel)
	submit() {
		this.dialogRef.close(this.file() ?? undefined);
	}

	cancel() {
		this.dialogRef.close(undefined);
	}
}
