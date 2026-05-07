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
import {
	debounceTime,
	filter,
	map,
	scan,
	startWith,
	switchMap,
	take,
} from 'rxjs';
import { mdExamples } from './markdown-editor-examples';
import { ArtifactExplorerHttpService } from '../../../ple/artifact-explorer/lib/services/artifact-explorer-http.service';
import { DomSanitizer, SafeHtml } from '@angular/platform-browser';
import {
	UploadImageDialogComponent,
	UploadImageDialogData,
	UploadImageDialogResult,
} from '@osee/shared/components';
import { SUPPORTED_IMAGE_FORMATS_LABEL } from '@osee/shared/types/constants';
import { isSupportedImageFile } from '@osee/shared/utils';
import { MarkdownImageService } from '@osee/shared/services';

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

	private readonly _history = toObservable(this.mdContent).pipe(
		scan((acc, curr) => {
			if (acc.length === this.maxHistory()) {
				acc = acc.splice(1);
			}
			return [...acc, curr];
		}, [] as string[])
	);
	private readonly history = toSignal(this._history);
	protected readonly redoHistory = signal([] as string[]);
	private readonly maxHistory = signal(100);
	protected readonly mdExamples = mdExamples;

	private readonly dialog = inject(MatDialog);
	private readonly uiService = inject(UiService);
	private readonly imageService = inject(MarkdownImageService);
	private readonly artExpHttpService = inject(ArtifactExplorerHttpService);
	private readonly domSanitizer = inject(DomSanitizer);

	private readonly branchId = toSignal(this.uiService.id, {
		initialValue: '',
	});

	protected readonly isUploading = signal(false);
	protected readonly dragActive = signal(false);
	protected readonly showImages = signal(false);
	protected readonly isLoadingImages = signal(false);
	protected readonly imagePreviewHtml = signal<SafeHtml>(
		this.domSanitizer.bypassSecurityTrustHtml('')
	);
	private dragCounter = 0;
	private imageObjectUrls: string[] = [];

	protected readonly isEditorDisabled = computed(
		() => this.disabled() || this.showImages()
	);

	protected readonly canUploadImage = computed(
		() =>
			!this.disabled() &&
			this.artifactId() !== '' &&
			!this.showImages()
	);

	protected readonly uploadImageTooltip = computed(() => {
		if (this.disabled()) {
			return 'Editing is disabled.';
		}
		if (this.artifactId() === '') {
			return 'Save the artifact first to enable image uploads.';
		}
		if (this.showImages()) {
			return 'Exit image preview to upload images.';
		}
		if (this.isUploading()) {
			return 'Image upload in progress.';
		}
		return 'Upload Image';
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

	toggleShowImages(): void {
		if (this.showImages()) {
			this.revokeImageObjectUrls();
			this.showImages.set(false);
			return;
		}

		const content = this.mdContent();
		const imageLinkRegex = /<image-link>(\d+)<\/image-link>/g;
		const matches = [...content.matchAll(imageLinkRegex)];
		const artifactIds = [...new Set(matches.map((m) => m[1]))];

		if (artifactIds.length === 0) {
			this.showImages.set(true);
			this.imagePreviewHtml.set(this.mdPreview()!);
			return;
		}

		this.isLoadingImages.set(true);

		this.imageService
			.fetchImagesAsObjectUrls(this.branchId(), artifactIds)
			.pipe(
				take(1),
				switchMap((imageUrls) => {
					this.imageObjectUrls = imageUrls.map(
						(img) => img.objectUrl
					);
					let contentWithImages = content;
					for (const img of imageUrls) {
						contentWithImages = contentWithImages.replaceAll(
							`<image-link>${img.artifactId}</image-link>`,
							`![Image ${img.artifactId}](${img.objectUrl})`
						);
					}
					return this.artExpHttpService
						.convertMarkdownToHtmlPreview(contentWithImages)
						.pipe(take(1));
				})
			)
			.subscribe({
				next: (html) => {
					this.imagePreviewHtml.set(
						this.domSanitizer.bypassSecurityTrustHtml(html)
					);
					this.isLoadingImages.set(false);
					this.showImages.set(true);
				},
				error: (err) => {
					this.uiService.ErrorText = `Failed to load image preview: ${err?.message ?? 'Unknown error'}`;
					this.isLoadingImages.set(false);
				},
			});
	}

	addExampleToMdContent(markdownExample: string) {
		this.mdContent.set(this.mdContent() + '\n\n' + markdownExample);
	}

	// Image Upload

	openUploadImageDialog(): void {
		if (!this.canUploadImage() || this.isUploading()) {
			return;
		}

		const dialogData: UploadImageDialogData = {
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
			isSupportedImageFile(f)
		);

		if (!imageFile) {
			this.uiService.ErrorText = `Unsupported file type. Supported formats: ${SUPPORTED_IMAGE_FORMATS_LABEL}`;
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

	private revokeImageObjectUrls(): void {
		for (const url of this.imageObjectUrls) {
			URL.revokeObjectURL(url);
		}
		this.imageObjectUrls = [];
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

	private updateRedoHistory(latestHistoryValue: string) {
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
