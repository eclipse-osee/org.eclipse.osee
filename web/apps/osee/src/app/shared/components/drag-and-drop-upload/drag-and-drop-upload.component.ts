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
	EventEmitter,
	Output,
	input,
	signal,
} from '@angular/core';
import { MatButton } from '@angular/material/button';

@Component({
	selector: 'osee-drag-and-drop-upload',
	standalone: true,
	imports: [CommonModule, MatButton],
	changeDetection: ChangeDetectionStrategy.OnPush,
	templateUrl: './drag-and-drop-upload.component.html',
})
export class DragAndDropUploadComponent {
	// Inputs
	accept = input<string | null>(null); // e.g., '.txt,.pdf'
	multiple = input<boolean>(false); // Allow multiple files.
	title = input<string>('Drag & drop files here');
	subtitle = input<string>('or');
	buttonLabel = input<string>('Choose file');

	// Outputs: always emits an array for consistency
	@Output() filesSelected = new EventEmitter<File[]>();

	// Internal state
	dragActive = signal<boolean>(false);

	// Handlers
	onDragOver(event: DragEvent) {
		event.preventDefault();
		this.dragActive.set(true);
	}
	onDragLeave(event: DragEvent) {
		event.preventDefault();
		this.dragActive.set(false);
	}
	onDrop(event: DragEvent) {
		event.preventDefault();
		this.dragActive.set(false);
		const list = event.dataTransfer?.files;
		if (!list?.length) return;
		this.emitFiles(Array.from(list));
	}
	onFileInputChange(event: Event) {
		const input = event.target as HTMLInputElement;
		const list = input.files;
		if (!list?.length) return;
		const files = Array.from(list);
		this.emitFiles(files);
		input.value = ''; // Allow re-selecting same file.
	}

	private emitFiles(files: File[]) {
		// If multiple() is false, emit only the first file.
		const payload = this.multiple()
			? files
			: files.length
				? [files[0]]
				: [];
		if (payload.length) {
			this.filesSelected.emit(payload);
		}
	}
}
