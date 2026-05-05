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
import {
	ChangeDetectionStrategy,
	Component,
	computed,
	inject,
	input,
	model,
	signal,
} from '@angular/core';
import { toObservable, toSignal } from '@angular/core/rxjs-interop';
import { FormsModule } from '@angular/forms';
import { MatDialog } from '@angular/material/dialog';
import { MatDivider } from '@angular/material/divider';
import { MatFormField } from '@angular/material/form-field';
import { MatIcon } from '@angular/material/icon';
import { MatInputModule } from '@angular/material/input';
import { MatMenu, MatMenuItem, MatMenuTrigger } from '@angular/material/menu';
import { MatTooltip } from '@angular/material/tooltip';
import { UiService } from '@osee/shared/services';
import { debounceTime, filter, map, scan, startWith, switchMap, take } from 'rxjs';
import { mdExamples } from './markdown-editor-examples';
import { ArtifactExplorerHttpService } from '../../../ple/artifact-explorer/lib/services/artifact-explorer-http.service';
import { DomSanitizer, SafeHtml } from '@angular/platform-browser';
import {
	UploadImageDialogComponent,
	UploadImageDialogData,
	UploadImageDialogResult,
} from '../upload-image-dialog/upload-image-dialog.component';
import { MarkdownImageService } from '../../services/ple_aware/http/markdown-image.service';

const SUPPORTED_IMAGE_MIME_TYPES = [
	'image/png',
	'image/jpeg',
	'image/gif',
	'image/bmp',
	'image/webp',
	'image/svg+xml',
];

@Component({
	selector: 'osee-markdown-editor',
	imports: [
		MatIcon,
		FormsModule,
		MatFormField,
		MatDivider,
		MatInputModule,
		MatTooltip,
		MatMenu,
		MatMenuTrigger,
		MatMenuItem,
	],
	templateUrl: './markdown-editor.component.html',
	changeDetection: ChangeDetectionStrategy.OnPush,
})
export class MarkdownEditorComponent {
	disabled = input(false);
	artifactId = input<string>('');
	mdContent = model.required<string>();

	_history = toObservable(this.mdContent).pipe(
		scan((acc, curr) => {
			if (acc.length === this.maxHistory()) {
				acc = acc.splice(1);
			}
			return [...acc, curr];
		}, [] as string[])
	);
	history = toSignal(this._history);
	redoHistory = signal([] as string[]);
	maxHistory = signal(100);
	mdExamples = mdExamples;

	private readonly dialog = inject(MatDialog);
	private readonly uiService = inject(UiService);
	private readonly imageService = inject(MarkdownImageService);
	artExpHttpService = inject(ArtifactExplorerHttpService);
	domSanitizer = inject(DomSanitizer);

	protected readonly isUploading = signal(false);
	protected readonly dragActive = signal(false);
	private dragCounter = 0;

	protected readonly canUploadImage = computed(
		() => !this.disabled() && this.artifactId() !== ''
	);

	protected readonly uploadImageTooltip = computed(() => {
		if (this.disabled()) {
			return 'Editing is disabled';
		}
		if (this.artifactId() === '') {
			return 'Save the artifact first to enable image uploads';
		}
		if (this.isUploading()) {
			return 'Image upload in progress';
		}
		return 'Upload image';
	});

	// Markdown Preview

	mdPreview = toSignal(
		toObservable(this.mdContent).pipe(
			debounceTime(500),
			switchMap((content: string) =>
				this.artExpHttpService
					.convertMarkdownToHtmlPreview(content)
					.pipe(
						map((html: string) =>
							this.domSanitizer.bypassSecurityTrustHtml(html)
						)
					)
			),
			startWith(this.domSanitizer.bypassSecurityTrustHtml('') as SafeHtml)
		),
		{
			initialValue: this.domSanitizer.bypassSecurityTrustHtml(
				''
			) as SafeHtml,
		}
	);

	addExampleToMdContent(markdownExample: string) {
		this.mdContent.set(this.mdContent() + '\n\n' + markdownExample);
	}

	// Image Upload

	openUploadImageDialog(): void {
		if (!this.canUploadImage() || this.isUploading()) {
			return;
		}

		const dialogData: UploadImageDialogData = {
			branchId: '',
			artifactId: this.artifactId(),
		};

		this.dialog
			.open(UploadImageDialogComponent, {
				data: dialogData,
				minWidth: '40%',
			})
			.afterClosed()
			.pipe(
				take(1),
				filter(
					(result): result is UploadImageDialogResult =>
						result !== undefined && result?.file !== undefined
				),
				switchMap((result) => {
					this.isUploading.set(true);
					return this.imageService.uploadImageArtifact(
						this.artifactId(),
						result.file
					);
				})
			)
			.subscribe({
				next: (uploadResult) => {
					this.insertImageLink(uploadResult.artifactId);
					this.isUploading.set(false);
				},
				error: (err) => {
					this.uiService.ErrorText = `Image upload failed: ${err?.message ?? 'Unknown error'}`;
					this.isUploading.set(false);
				},
			});
	}

	onEditorDragOver(event: DragEvent): void {
		if (!this.canUploadImage()) {
			return;
		}
		event.preventDefault();
	}

	onEditorDragEnter(event: DragEvent): void {
		if (!this.canUploadImage()) {
			return;
		}
		event.preventDefault();
		this.dragCounter++;
		this.dragActive.set(true);
	}

	onEditorDragLeave(event: DragEvent): void {
		event.preventDefault();
		this.dragCounter--;
		if (this.dragCounter <= 0) {
			this.dragCounter = 0;
			this.dragActive.set(false);
		}
	}

	onEditorDrop(event: DragEvent): void {
		event.preventDefault();
		this.dragCounter = 0;
		this.dragActive.set(false);

		if (!this.canUploadImage() || this.isUploading()) {
			return;
		}

		const files = event.dataTransfer?.files;
		if (!files?.length) {
			return;
		}

		const imageFile = Array.from(files).find((f) =>
			SUPPORTED_IMAGE_MIME_TYPES.includes(f.type)
		);

		if (!imageFile) {
			this.uiService.ErrorText =
				'Unsupported file type. Supported image formats: PNG, JPG, JPEG, GIF, BMP, WEBP, SVG';
			return;
		}

		this.isUploading.set(true);
		this.imageService
			.uploadImageArtifact(this.artifactId(), imageFile)
			.pipe(take(1))
			.subscribe({
				next: (uploadResult) => {
					this.insertImageLink(uploadResult.artifactId);
					this.isUploading.set(false);
				},
				error: (err) => {
					this.uiService.ErrorText = `Image upload failed: ${err?.message ?? 'Unknown error'}`;
					this.isUploading.set(false);
				},
			});
	}

	private insertImageLink(artifactId: string): void {
		const imageTag = `<image-link>${artifactId}</image-link>`;
		const currentContent = this.mdContent();
		const separator = currentContent.length > 0 ? '\n\n' : '';
		this.mdContent.set(currentContent + separator + imageTag);
	}

	// Undo/Redo

	undo() {
		const latestHistoryValue = this.history()?.pop();

		if (latestHistoryValue) {
			if (latestHistoryValue === this.mdContent()) {
				const nextValue = computed(() => this.history()?.pop())();

				if (nextValue) {
					this.updateRedoHistory(this.mdContent());
					this.mdContent.set(nextValue);
				}
			} else {
				this.updateRedoHistory(this.mdContent());
				this.mdContent.set(latestHistoryValue);
			}
		}
	}

	updateRedoHistory(latestHistoryValue: string) {
		if (
			this.redoHistory()[this.redoHistory().length - 1] !==
			latestHistoryValue
		) {
			this.redoHistory.update((curr) => [...curr, latestHistoryValue]);
		}
	}

	redo() {
		const latestRedoHistoryValue = this.redoHistory().pop();

		if (latestRedoHistoryValue) {
			if (latestRedoHistoryValue === this.mdContent()) {
				const nextValue = computed(() => this.history()?.pop())();

				if (nextValue) {
					this.mdContent.set(nextValue);
				}
			} else {
				this.mdContent.set(latestRedoHistoryValue);
			}
		}
	}
}
