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
	afterNextRender,
	ChangeDetectionStrategy,
	Component,
	computed,
	DestroyRef,
	ElementRef,
	inject,
	Injector,
	input,
	model,
	signal,
	viewChild,
} from '@angular/core';
import {
	takeUntilDestroyed,
	toObservable,
	toSignal,
} from '@angular/core/rxjs-interop';
import { FocusMonitor } from '@angular/cdk/a11y';
import { FormsModule } from '@angular/forms';
import { MatDialog } from '@angular/material/dialog';
import { MatIcon } from '@angular/material/icon';
import { MatMenu, MatMenuItem, MatMenuTrigger } from '@angular/material/menu';
import { MatSnackBar } from '@angular/material/snack-bar';
import { MatTooltip } from '@angular/material/tooltip';
import { DomSanitizer, SafeHtml } from '@angular/platform-browser';
import { UiService } from '@osee/shared/services';
import {
	debounceTime,
	filter,
	fromEvent,
	map,
	scan,
	startWith,
	switchMap,
	take,
	takeUntil,
} from 'rxjs';
import { mdExamples } from './markdown-editor-examples';
import { ArtifactExplorerHttpService } from '../../../ple/artifact-explorer/lib/services/artifact-explorer-http.service';
import {
	UploadImageDialogComponent,
	UploadImageDialogData,
	UploadImageDialogResult,
	MarkdownTableDialogComponent,
	MarkdownTableDialogData,
	MarkdownTableDialogResult,
	ColumnAlignment,
} from '@osee/shared/dialogs';
import { SUPPORTED_IMAGE_FORMATS_LABEL } from '@osee/shared/types/constants';
import { isSupportedImageFile } from '@osee/shared/utils';
import { MarkdownImageService } from './markdown-image.service';

@Component({
	selector: 'osee-markdown-editor',
	imports: [
		MatIcon,
		FormsModule,
		MatTooltip,
		MatMenu,
		MatMenuTrigger,
		MatMenuItem,
	],
	templateUrl: './markdown-editor.component.html',
	changeDetection: ChangeDetectionStrategy.OnPush,
	host: {
		class: 'tw-block',
	},
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
	private readonly snackBar = inject(MatSnackBar);
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

	protected readonly isFullscreen = signal(false);
	protected readonly isPreviewCollapsed = signal(false);
	protected readonly editorWidthPercent = signal(50);

	/**
	 * CSS height value for the editor in non-fullscreen mode.
	 * Accepts any valid CSS value (px, clamp, etc).
	 * Uses clamp() with dvh units to responsively adapt to viewport size.
	 */
	height = input('clamp(260px, 40dvh, 720px)');

	private readonly editorContainer =
		viewChild<ElementRef<HTMLDivElement>>('editorContainer');
	private readonly fullscreenWrapper =
		viewChild<ElementRef<HTMLDivElement>>('fullscreenWrapper');
	private readonly editorTextarea =
		viewChild<ElementRef<HTMLTextAreaElement>>('editorTextarea');
	private readonly focusMonitor = inject(FocusMonitor);
	private readonly injector = inject(Injector);
	private readonly destroyRef = inject(DestroyRef);

	protected readonly keyboardFocused = signal(false);

	private readonly _init: void = (() => {
		this.initFullscreenListener();
		afterNextRender(() => {
			const textarea = this.editorTextarea();
			if (textarea) {
				this.focusMonitor
					.monitor(textarea, false)
					.subscribe((origin) => {
						this.keyboardFocused.set(origin === 'keyboard');
					});
				this.destroyRef.onDestroy(() => {
					this.focusMonitor.stopMonitoring(textarea);
				});

				// Track the last known selection so toolbar buttons can
				// read it after the textarea loses focus on click.
				fromEvent(textarea.nativeElement, 'blur')
					.pipe(takeUntilDestroyed(this.destroyRef))
					.subscribe(() => {
						this.savedSelectionStart =
							textarea.nativeElement.selectionStart;
						this.savedSelectionEnd =
							textarea.nativeElement.selectionEnd;
					});
			}
		});
		this.destroyRef.onDestroy(() => {
			if (this.isDragging) {
				this.clearDragStyles();
			}
			this.revokeImageObjectUrls();
		});
	})();

	protected readonly isEditorDisabled = computed(
		() => this.disabled() || this.showImages()
	);

	protected readonly canUploadImage = computed(
		() => !this.disabled() && this.artifactId() !== '' && !this.showImages()
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

	protected readonly tableButtonTooltip = computed(() => {
		if (this.disabled()) {
			return 'Editing is disabled.';
		}
		if (this.showImages()) {
			return 'Exit image preview to edit tables.';
		}
		return 'Insert or Edit Table';
	});

	protected readonly selectTableTooltip = computed(() => {
		if (this.disabled()) {
			return 'Editing is disabled.';
		}
		if (this.showImages()) {
			return 'Exit image preview to select tables.';
		}
		return 'Select Table at Cursor';
	});

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

	openUploadImageDialog(): void {
		if (!this.canUploadImage() || this.isUploading()) {
			return;
		}

		const wasFullscreen = this.isFullscreen();
		const openDialog = () => {
			const dialogData: UploadImageDialogData = {
				artifactId: this.artifactId(),
			};

			const dialogRef = this.dialog.open(UploadImageDialogComponent, {
				data: dialogData,
				minWidth: '40%',
			});

			dialogRef
				.afterClosed()
				.pipe(
					take(1),
					filter((result): result is UploadImageDialogResult => {
						if (
							result === undefined ||
							result?.file === undefined
						) {
							if (wasFullscreen) {
								this.enterFullscreen();
							}
							return false;
						}
						return true;
					}),
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
						if (wasFullscreen) {
							this.enterFullscreen();
						}
					},
					error: (err) => {
						this.uiService.ErrorText = `Image upload failed: ${err?.message ?? 'Unknown error'}`;
						this.isUploading.set(false);
						if (wasFullscreen) {
							this.enterFullscreen();
						}
					},
				});
		};

		if (wasFullscreen) {
			document
				.exitFullscreen()
				.then(() => openDialog())
				.catch(() => openDialog());
		} else {
			openDialog();
		}
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
		const cursorPos = this.savedSelectionStart;

		const before = currentContent.substring(0, cursorPos);
		const after = currentContent.substring(cursorPos);
		const prefixNewlines =
			before.length > 0 && !before.endsWith('\n\n')
				? before.endsWith('\n')
					? '\n'
					: '\n\n'
				: '';
		const suffixNewlines =
			after.length > 0 && !after.startsWith('\n\n')
				? after.startsWith('\n')
					? '\n'
					: '\n\n'
				: '';
		this.mdContent.set(
			before + prefixNewlines + imageTag + suffixNewlines + after
		);
	}

	openTableDialog(): void {
		const content = this.mdContent();
		const parsedTable = this.parseTableAtSelection(
			content,
			this.savedSelectionStart,
			this.savedSelectionEnd
		);

		const wasFullscreen = this.isFullscreen();
		const openDialog = () => {
			if (
				parsedTable &&
				(parsedTable.headers.length > 50 ||
					parsedTable.cells.length > 100)
			) {
				this.snackBar.open(
					'Table exceeds maximum editable size (50 columns × 100 rows).',
					'Dismiss',
					{ duration: 4000 }
				);
				return;
			}

			const dialogData: MarkdownTableDialogData = parsedTable
				? {
						rows: parsedTable.cells.length,
						cols: parsedTable.headers.length,
						headers: parsedTable.headers,
						headerSpans: parsedTable.headerSpans,
						cells: parsedTable.cells,
						alignments: parsedTable.alignments,
						isEdit: true,
					}
				: {
						rows: 3,
						cols: 3,
						headers: ['', '', ''],
						headerSpans: [1, 1, 1],
						cells: [
							['', '', ''],
							['', '', ''],
							['', '', ''],
						],
						alignments: ['left', 'left', 'left'],
						isEdit: false,
					};

			const dialogRef = this.dialog.open(MarkdownTableDialogComponent, {
				data: dialogData,
				minWidth: '50%',
				maxWidth: '90vw',
			});

			dialogRef
				.afterClosed()
				.pipe(
					take(1),
					filter((result): result is MarkdownTableDialogResult => {
						if (result === undefined) {
							if (wasFullscreen) {
								this.enterFullscreen();
							}
							return false;
						}
						return true;
					})
				)
				.subscribe((result) => {
					if (parsedTable) {
						// Replace existing table
						const before = content.substring(
							0,
							parsedTable.startIndex
						);
						const after = content.substring(parsedTable.endIndex);
						this.mdContent.set(before + result.markdown + after);
					} else {
						// Insert new table at cursor or end
						const cursorPos =
							this.savedSelectionStart ?? content.length;
						const before = content.substring(0, cursorPos);
						const after = content.substring(cursorPos);
						const prefixNewlines =
							before.length > 0 && !before.endsWith('\n\n')
								? before.endsWith('\n')
									? '\n'
									: '\n\n'
								: '';
						const suffixNewlines =
							after.length > 0 && !after.startsWith('\n\n')
								? after.startsWith('\n')
									? '\n'
									: '\n\n'
								: '';
						this.mdContent.set(
							before +
								prefixNewlines +
								result.markdown +
								suffixNewlines +
								after
						);
					}
					if (wasFullscreen) {
						this.enterFullscreen();
					}
				});
		};

		if (wasFullscreen) {
			document
				.exitFullscreen()
				.then(() => openDialog())
				.catch(() => openDialog());
		} else {
			openDialog();
		}
	}

	selectTableAtCursor(): void {
		const textarea = this.editorTextarea()?.nativeElement;
		if (!textarea) {
			return;
		}
		const content = this.mdContent();
		const parsedTable = this.parseTableAtSelection(
			content,
			this.savedSelectionStart,
			this.savedSelectionEnd
		);

		if (!parsedTable) {
			this.snackBar.open(
				'No table found at the cursor position.',
				'Dismiss',
				{ duration: 3000 }
			);
			return;
		}

		textarea.focus();
		textarea.selectionStart = parsedTable.startIndex;
		textarea.selectionEnd = parsedTable.endIndex;
	}

	private parseTableAtSelection(
		content: string,
		selStart: number,
		selEnd: number
	): {
		headers: string[];
		headerSpans: number[];
		cells: string[][];
		alignments: ColumnAlignment[];
		startIndex: number;
		endIndex: number;
	} | null {
		if (!content) {
			return null;
		}

		const cursorPos = selStart;

		// Find the line the cursor/selection is on
		const lines = content.split('\n');
		let charIndex = 0;
		let cursorLineIndex = 0;

		for (let i = 0; i < lines.length; i++) {
			const lineEnd = charIndex + lines[i].length;
			if (cursorPos <= lineEnd) {
				cursorLineIndex = i;
				break;
			}
			charIndex += lines[i].length + 1; // +1 for the newline
		}

		// Look for a table around the cursor/selection
		// A markdown table is: header row, separator row (|:--|:-:|--:|), then data rows.
		// A separator line starts and ends with |, and each cell between pipes
		// contains only dashes, colons, and whitespace (with at least one dash).
		const isSeparatorLine = (line: string): boolean => {
			const trimmed = line.trim();
			if (!trimmed.startsWith('|') || !trimmed.endsWith('|')) {
				return false;
			}
			const inner = trimmed.slice(1, -1);
			const cells = inner.split('|');
			if (cells.length === 0) {
				return false;
			}
			return cells.every((cell) => {
				const c = cell.trim();
				return c.length > 0 && /^:?-+:?$/.test(c);
			});
		};

		// Find the separator line for a table containing the cursor.
		// Strategy: if the cursor is on a line starting with |, walk upward
		// to find the separator line (which is always line 2 of a table).
		// Also check if the cursor is on the separator or header line itself.
		let separatorLineIndex = -1;

		// First check if cursor line itself is a separator or starts with |
		if (isSeparatorLine(lines[cursorLineIndex])) {
			separatorLineIndex = cursorLineIndex;
		} else {
			// Walk upward from cursor to find a separator line
			for (let i = cursorLineIndex; i >= 0; i--) {
				if (isSeparatorLine(lines[i])) {
					// Verify the table extends down to include the cursor line
					let tableEndLine = i + 1;
					while (
						tableEndLine < lines.length &&
						lines[tableEndLine].trim().startsWith('|')
					) {
						tableEndLine++;
					}
					tableEndLine--; // Back to last valid row

					if (cursorLineIndex <= tableEndLine) {
						separatorLineIndex = i;
					}
					break;
				}
				// If we hit a line that doesn't start with |, stop searching
				if (!lines[i].trim().startsWith('|')) {
					break;
				}
			}
		}

		// If not found walking up, check if cursor is on the header line
		// (the line before a separator)
		if (separatorLineIndex === -1) {
			const nextLine = cursorLineIndex + 1;
			if (nextLine < lines.length && isSeparatorLine(lines[nextLine])) {
				// Verify the table extends to include data rows below
				let tableEndLine = nextLine + 1;
				while (
					tableEndLine < lines.length &&
					lines[tableEndLine].trim().startsWith('|')
				) {
					tableEndLine++;
				}
				separatorLineIndex = nextLine;
			}
		}

		// Handle selection that may overlap a table
		if (separatorLineIndex === -1 && selStart !== selEnd) {
			// Search all lines in the document for a separator whose table
			// overlaps the selection
			for (let i = 0; i < lines.length; i++) {
				if (isSeparatorLine(lines[i])) {
					const tableStartLine = i - 1;
					let tableEndLine = i + 1;
					while (
						tableEndLine < lines.length &&
						lines[tableEndLine].trim().startsWith('|')
					) {
						tableEndLine++;
					}
					tableEndLine--;

					if (
						this.selectionOverlapsTable(
							content,
							lines,
							selStart,
							selEnd,
							tableStartLine,
							tableEndLine
						)
					) {
						separatorLineIndex = i;
						break;
					}
				}
			}
		}

		if (separatorLineIndex === -1) {
			return null;
		}

		const headerLineIndex = separatorLineIndex - 1;
		if (headerLineIndex < 0) {
			return null;
		}

		// Parse header with column span detection
		const { headers, headerSpans } = this.parseHeaderRowWithSpans(
			lines[headerLineIndex]
		);
		if (headers.length === 0) {
			return null;
		}

		// Parse alignments from separator — use separator column count
		// (which represents the actual number of columns)
		const alignments = this.parseAlignments(
			lines[separatorLineIndex],
			headers.length
		);

		// Parse data rows
		let dataEndLine = separatorLineIndex + 1;
		while (
			dataEndLine < lines.length &&
			lines[dataEndLine].trim().startsWith('|')
		) {
			dataEndLine++;
		}

		const cells: string[][] = [];
		for (let i = separatorLineIndex + 1; i < dataEndLine; i++) {
			const row = this.parseTableRow(lines[i]);
			// Pad or trim to match header count
			while (row.length < headers.length) {
				row.push('');
			}
			cells.push(row.slice(0, headers.length));
		}

		// Ensure at least one data row
		if (cells.length === 0) {
			cells.push(Array(headers.length).fill(''));
		}

		// Calculate start and end character indices
		let startIndex = 0;
		for (let i = 0; i < headerLineIndex; i++) {
			startIndex += lines[i].length + 1;
		}

		// endIndex points to the last character of the last table line
		const lastTableLine = dataEndLine - 1;
		let endIndex = 0;
		for (let i = 0; i <= lastTableLine; i++) {
			endIndex += lines[i].length + 1;
		}
		// endIndex is now one past the newline of the last table line
		// Subtract 1 to point to the newline, or use content.length if at end
		endIndex = Math.min(endIndex, content.length);

		return { headers, headerSpans, cells, alignments, startIndex, endIndex };
	}

	private selectionOverlapsTable(
		_content: string,
		lines: string[],
		selStart: number,
		selEnd: number,
		tableStartLine: number,
		tableEndLine: number
	): boolean {
		let tableStartChar = 0;
		for (let i = 0; i < tableStartLine; i++) {
			tableStartChar += lines[i].length + 1;
		}
		let tableEndChar = tableStartChar;
		for (let i = tableStartLine; i <= tableEndLine; i++) {
			tableEndChar += lines[i].length + 1;
		}
		return selStart < tableEndChar && selEnd > tableStartChar;
	}

	private parseTableRow(line: string): string[] {
		let trimmed = line.trim();
		if (trimmed.startsWith('|')) {
			trimmed = trimmed.substring(1);
		}
		if (trimmed.endsWith('|')) {
			trimmed = trimmed.substring(0, trimmed.length - 1);
		}
		return trimmed
			.split('|')
			.map((cell) => cell.trim().replace(/<br>/gi, '\n'));
	}

	/**
	 * Parses a header row detecting Flexmark colspan syntax.
	 * Consecutive empty cells after a non-empty cell indicate a column span.
	 * E.g., "| Header 1 ||| Header 2 |" = Header 1 spans 3, Header 2 spans 1.
	 * Returns headers array (one per actual column) and spans array.
	 */
	private parseHeaderRowWithSpans(line: string): {
		headers: string[];
		headerSpans: number[];
	} {
		let trimmed = line.trim();
		if (trimmed.startsWith('|')) {
			trimmed = trimmed.substring(1);
		}
		if (trimmed.endsWith('|')) {
			trimmed = trimmed.substring(0, trimmed.length - 1);
		}
		const rawCells = trimmed.split('|').map((c) => c.trim());

		// Determine the total number of columns from the separator
		// (we use rawCells.length as the column count)
		const colCount = rawCells.length;
		const headers: string[] = Array(colCount).fill('');
		const headerSpans: number[] = Array(colCount).fill(1);

		let colIdx = 0;
		let i = 0;
		while (i < rawCells.length && colIdx < colCount) {
			const cellContent = rawCells[i].replace(/<br>/gi, '\n');
			headers[colIdx] = cellContent;

			// Count consecutive empty cells following this one (colspan)
			let span = 1;
			while (
				i + span < rawCells.length &&
				rawCells[i + span] === ''
			) {
				span++;
			}

			headerSpans[colIdx] = span;
			// Mark spanned columns as 0
			for (let s = 1; s < span; s++) {
				if (colIdx + s < colCount) {
					headerSpans[colIdx + s] = 0;
					headers[colIdx + s] = '';
				}
			}

			colIdx += span;
			i += span;
		}

		return { headers, headerSpans };
	}

	private parseAlignments(
		separatorLine: string,
		colCount: number
	): ColumnAlignment[] {
		const cells = this.parseTableRow(separatorLine);
		const alignments: ColumnAlignment[] = [];

		for (let i = 0; i < colCount; i++) {
			const cell = (cells[i] || '').trim();
			if (cell.startsWith(':') && cell.endsWith(':')) {
				alignments.push('center');
			} else if (cell.endsWith(':')) {
				alignments.push('right');
			} else {
				alignments.push('left');
			}
		}

		return alignments;
	}

	private revokeImageObjectUrls(): void {
		for (const url of this.imageObjectUrls) {
			URL.revokeObjectURL(url);
		}
		this.imageObjectUrls = [];
	}

	toggleFullscreen(): void {
		const wrapper = this.fullscreenWrapper()?.nativeElement;
		if (!wrapper) {
			return;
		}

		this.saveTextareaSelection();

		if (document.fullscreenElement) {
			document.exitFullscreen().catch(() => {
				this.uiService.ErrorText =
					'Unable to exit fullscreen. Try pressing Escape.';
			});
		} else {
			this.enterFullscreen();
		}
	}

	private enterFullscreen(): void {
		const wrapper = this.fullscreenWrapper()?.nativeElement;
		if (!wrapper) {
			return;
		}
		wrapper.requestFullscreen().catch(() => {
			this.uiService.ErrorText =
				'Fullscreen is not available in this context.';
		});
	}

	private initFullscreenListener(): void {
		fromEvent(document, 'fullscreenchange')
			.pipe(takeUntilDestroyed(this.destroyRef))
			.subscribe(() => {
				this.isFullscreen.set(!!document.fullscreenElement);
				this.restoreTextareaSelection();
			});
	}

	private savedSelectionStart = 0;
	private savedSelectionEnd = 0;

	private saveTextareaSelection(): void {
		const textarea = this.editorTextarea()?.nativeElement;
		if (textarea) {
			this.savedSelectionStart = textarea.selectionStart;
			this.savedSelectionEnd = textarea.selectionEnd;
		}
	}

	private restoreTextareaSelection(): void {
		afterNextRender(
			() => {
				const textarea = this.editorTextarea()?.nativeElement;
				if (textarea) {
					textarea.selectionStart = this.savedSelectionStart;
					textarea.selectionEnd = this.savedSelectionEnd;
					textarea.focus();
				}
			},
			{ injector: this.injector }
		);
	}

	togglePreviewCollapsed(): void {
		this.saveTextareaSelection();
		this.isPreviewCollapsed.update((v) => !v);
		this.restoreTextareaSelection();
	}

	private isDragging = false;

	onDividerMouseDown(event: MouseEvent): void {
		event.preventDefault();
		this.isDragging = true;
		document.body.style.cursor = 'col-resize';
		document.body.style.userSelect = 'none';

		const mouseup$ = fromEvent(document, 'mouseup').pipe(take(1));

		fromEvent<MouseEvent>(document, 'mousemove')
			.pipe(takeUntil(mouseup$), takeUntilDestroyed(this.destroyRef))
			.subscribe((e) => {
				const container = this.editorContainer()?.nativeElement;
				if (!container) {
					return;
				}
				const rect = container.getBoundingClientRect();
				const percent = ((e.clientX - rect.left) / rect.width) * 100;
				this.editorWidthPercent.set(
					Math.max(20, Math.min(80, percent))
				);
			});

		mouseup$.pipe(takeUntilDestroyed(this.destroyRef)).subscribe(() => {
			this.clearDragStyles();
		});
	}

	private clearDragStyles(): void {
		this.isDragging = false;
		document.body.style.cursor = '';
		document.body.style.userSelect = '';
	}

	onDividerKeyDown(event: KeyboardEvent): void {
		const step = 2;
		if (event.key === 'ArrowLeft') {
			event.preventDefault();
			this.editorWidthPercent.update((v) => Math.max(20, v - step));
		} else if (event.key === 'ArrowRight') {
			event.preventDefault();
			this.editorWidthPercent.update((v) => Math.min(80, v + step));
		}
	}

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
