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
import { MatIconButton } from '@angular/material/button';
import { MatSnackBar } from '@angular/material/snack-bar';
import { MatTooltip } from '@angular/material/tooltip';
import { DomSanitizer, SafeHtml } from '@angular/platform-browser';
import { UiService } from '@osee/shared/services';
import {
	debounceTime,
	filter,
	fromEvent,
	map,
	startWith,
	switchMap,
	take,
	takeUntil,
} from 'rxjs';
import {
	markdownFormattingActions,
	HeadingLevel,
	listOptions,
	headingOptions,
} from './markdown-editor-actions';
import { ArtifactExplorerHttpService } from '../../../ple/artifact-explorer/lib/services/artifact-explorer-http.service';
import {
	UploadImageDialogComponent,
	UploadImageDialogData,
	UploadImageDialogResult,
	ImageSize,
	MarkdownTableDialogComponent,
	MarkdownTableDialogData,
	MarkdownTableDialogResult,
	ColumnAlignment,
	CaptionPosition,
} from '@osee/shared/dialogs';
import { SUPPORTED_IMAGE_FORMATS_LABEL } from '@osee/shared/types/constants';
import { isSupportedImageFile } from '@osee/shared/utils';
import { MarkdownImageService } from './markdown-image.service';
import { HelpTopicRegistryService } from '../help-drawer/help-topic-registry.service';
import { HelpAnchorDirective } from '../help-drawer/help-anchor.directive';
import { HelpButtonComponent } from '../help-drawer/help-button.component';
import { SplitButtonComponent } from '../split-button/split-button.component';
import {
	ToolbarSectionDropdownComponent,
	ToolbarDropdownAction,
} from './toolbar-section-dropdown.component';

@Component({
	selector: 'osee-markdown-editor',
	imports: [
		MatIcon,
		MatIconButton,
		FormsModule,
		MatTooltip,
		HelpAnchorDirective,
		HelpButtonComponent,
		SplitButtonComponent,
		ToolbarSectionDropdownComponent,
	],
	templateUrl: './markdown-editor.component.html',
	changeDetection: ChangeDetectionStrategy.OnPush,
	host: {
		class: 'tw-flex tw-flex-row',
	},
})
export class MarkdownEditorComponent {
	disabled = input(false);
	artifactId = input<string>('');
	mdContent = model.required<string>();

	/**
	 * VS Code-style undo/redo with intelligent grouping.
	 *
	 * Typing groups are coalesced into a single undo entry until a boundary:
	 * - Pause in typing (debounce timeout)
	 * - Cursor position jump (click elsewhere)
	 * - Whitespace after non-whitespace (word boundary)
	 * - Explicit programmatic action (toolbar button, paste)
	 *
	 * `undoStack` stores committed states (oldest first).
	 * `redoStack` stores states undone (newest redo-able first).
	 */
	private readonly undoStack: string[] = [];
	private readonly redoStack: string[] = [];
	private isUndoRedoAction = false;
	private readonly maxHistory = 200;
	private undoGroupTimer: ReturnType<typeof setTimeout> | null = null;
	private lastEditCursorPos = -1;
	private lastCharWasWhitespace = false;
	private pendingGroupStart: string | null = null;
	/** Pause duration (ms) before typing commits as a group. */
	private readonly undoGroupDelay = 1000;
	protected readonly canUndo = signal(false);
	protected readonly canRedo = signal(false);
	protected readonly formattingActions = markdownFormattingActions;
	protected readonly headingOptions = headingOptions;
	protected readonly listOptions = listOptions;

	/**
	 * Toolbar collapse state (0–3).
	 * 0 = all expanded, 1 = Media collapsed, 2 = Media+Insert, 3 = Media+Insert+Format.
	 *
	 * Computed from container width using pre-measured section widths.
	 * The toolbar uses flex-nowrap + overflow-hidden so collapsing sections
	 * doesn't change the container width — avoiding oscillation.
	 */
	protected readonly collapseState = signal(0);

	/** Whether the Format section is collapsed (priority 2 = collapses 2nd) */
	protected readonly isFormatCollapsed = computed(
		() => this.collapseState() >= 2
	);
	/** Whether the Insert section is collapsed (priority 1 = collapses 1st) */
	protected readonly isInsertCollapsed = computed(
		() => this.collapseState() >= 1
	);
	/** Whether the Media section is collapsed (priority 3 = collapses last) */
	protected readonly isMediaCollapsed = computed(
		() => this.collapseState() >= 3
	);

	/** Actions for collapsed Format section dropdown */
	protected readonly formatDropdownActions = computed<
		ToolbarDropdownAction[]
	>(() => {
		const disabled = this.isEditorDisabled();
		return [
			{ id: 'heading', name: 'Heading', icon: 'title', disabled },
			{ id: 'bold', name: 'Bold', icon: 'format_bold', disabled },
			{
				id: 'italic',
				name: 'Italic',
				icon: 'format_italic',
				disabled,
			},
			{
				id: 'strikethrough',
				name: 'Strikethrough',
				icon: 'strikethrough_s',
				disabled,
			},
		];
	});

	/** Actions for collapsed Insert section dropdown */
	protected readonly insertDropdownActions = computed<
		ToolbarDropdownAction[]
	>(() => {
		const disabled = this.isEditorDisabled();
		return [
			{
				id: 'list',
				name: 'Bulleted List',
				icon: 'format_list_bulleted',
				disabled,
			},
			{ id: 'inline-code', name: 'Inline Code', icon: 'code', disabled },
			{ id: 'link', name: 'Link', icon: 'link', disabled },
			{
				id: 'blockquote',
				name: 'Blockquote',
				icon: 'format_quote',
				disabled,
			},
			{
				id: 'code-block',
				name: 'Code Block',
				icon: 'data_object',
				disabled,
			},
			{
				id: 'horizontal-rule',
				name: 'Horizontal Rule',
				icon: 'horizontal_rule',
				disabled,
			},
		];
	});

	/** Actions for collapsed Media section dropdown */
	protected readonly mediaDropdownActions = computed<
		ToolbarDropdownAction[]
	>(() => {
		const uploadDisabled =
			!this.canUploadImage() || this.isUploading();
		const editDisabled = this.disabled() || this.showImages();
		const formatDisabled = this.isEditorDisabled();
		return [
			{
				id: 'upload-image',
				name: 'Upload Image',
				icon: 'image',
				disabled: uploadDisabled,
			},
			{
				id: 'insert-table',
				name: 'Insert or Edit Table',
				icon: 'table_chart',
				disabled: editDisabled,
			},
			{
				id: 'select-table',
				name: 'Select Table at Cursor',
				icon: 'select_all',
				disabled: editDisabled,
			},
			{
				id: 'figure-caption',
				name: 'Figure Caption',
				icon: 'video_label',
				disabled: formatDisabled,
			},
			{
				id: 'table-caption',
				name: 'Table Caption',
				icon: 'legend_toggle',
				disabled: formatDisabled,
			},
		];
	});

	/**
	 * Toolbar layout constants — element pixel widths derived from CSS.
	 * Only these base sizes need updating if CSS changes.
	 */
	private static readonly ICON_BTN_WIDTH = 32; // mat-icon-button with !tw-w-8
	private static readonly SPLIT_BTN_WIDTH = 47; // icon + divider + arrow + border
	private static readonly SEPARATOR_WIDTH = 12; // │ char ~4px + tw-mx-1 (4px each side)
	private static readonly COLLAPSED_BTN_WIDTH = 48; // collapsed dropdown button
	private static readonly TOOLBAR_PADDING = 16; // tw-px-2 = 8px each side
	private static readonly GAP = 2; // tw-gap-0.5

	/**
	 * Toolbar section definitions.
	 * Each section declares its item composition so widths are computed
	 * automatically. Adding/removing items here is all that's needed.
	 *
	 * `collapsePriority`: lower = collapses first. null = never collapses.
	 */
	private static readonly TOOLBAR_SECTIONS: {
		name: string;
		iconButtons: number;
		splitButtons: number;
		extraWidth: number;
		collapsePriority: number | null;
	}[] = [
		{ name: 'history', iconButtons: 2, splitButtons: 0, extraWidth: 0, collapsePriority: null },
		{ name: 'media', iconButtons: 5, splitButtons: 0, extraWidth: 0, collapsePriority: 3 },
		{ name: 'format', iconButtons: 3, splitButtons: 1, extraWidth: 0, collapsePriority: 2 },
		{ name: 'insert', iconButtons: 5, splitButtons: 1, extraWidth: 0, collapsePriority: 1 },
		{ name: 'view', iconButtons: 3, splitButtons: 0, extraWidth: 0, collapsePriority: null }, // preview, toggle-panel, fullscreen
		{ name: 'help', iconButtons: 0, splitButtons: 0, extraWidth: 36, collapsePriority: null }, // help button (ml-auto, no separator before it)
	];

	/** Computed expanded width for a section given its item counts. */
	private static sectionExpandedWidth(section: {
		iconButtons: number;
		splitButtons: number;
		extraWidth: number;
	}): number {
		return (
			section.iconButtons * MarkdownEditorComponent.ICON_BTN_WIDTH +
			section.splitButtons * MarkdownEditorComponent.SPLIT_BTN_WIDTH +
			section.extraWidth
		);
	}

	/**
	 * Sections sorted by collapse priority (lowest first = collapses first).
	 * Only includes collapsible sections.
	 */
	private static readonly COLLAPSE_ORDER = MarkdownEditorComponent.TOOLBAR_SECTIONS
		.filter((s) => s.collapsePriority !== null)
		.sort((a, b) => a.collapsePriority! - b.collapsePriority!);

	/** Total width with all sections expanded (computed from section definitions). */
	private static readonly TOTAL_EXPANDED = (() => {
		const sections = MarkdownEditorComponent.TOOLBAR_SECTIONS;
		const sectionWidths = sections.reduce(
			(sum, s) => sum + MarkdownEditorComponent.sectionExpandedWidth(s),
			0
		);
		// 4 separators between the first 5 sections (help has no separator)
		const separatorCount = sections.length - 2;
		const flexChildCount = sections.length + separatorCount;
		const gapTotal = (flexChildCount - 1) * MarkdownEditorComponent.GAP;
		return (
			MarkdownEditorComponent.TOOLBAR_PADDING +
			gapTotal +
			sectionWidths +
			separatorCount * MarkdownEditorComponent.SEPARATOR_WIDTH
		);
	})();

	/**
	 * Pre-computed collapse thresholds.
	 * thresholds[i] = minimum container width to stay at collapse state i.
	 * If containerWidth < thresholds[i], we move to state i+1.
	 */
	private static readonly COLLAPSE_THRESHOLDS = (() => {
		const thresholds: number[] = [MarkdownEditorComponent.TOTAL_EXPANDED];
		let current = MarkdownEditorComponent.TOTAL_EXPANDED;
		for (const section of MarkdownEditorComponent.COLLAPSE_ORDER) {
			const expanded = MarkdownEditorComponent.sectionExpandedWidth(section);
			current = current - expanded + MarkdownEditorComponent.COLLAPSED_BTN_WIDTH;
			thresholds.push(current);
		}
		return thresholds;
	})();

	private toolbarResizeObserver: ResizeObserver | null = null;
	private readonly toolbarEl =
		viewChild<ElementRef<HTMLDivElement>>('toolbarEl');

	private readonly dialog = inject(MatDialog);
	private readonly snackBar = inject(MatSnackBar);
	private readonly uiService = inject(UiService);
	private readonly imageService = inject(MarkdownImageService);
	private readonly artExpHttpService = inject(ArtifactExplorerHttpService);
	private readonly domSanitizer = inject(DomSanitizer);
	private readonly helpRegistry = inject(HelpTopicRegistryService);

	private readonly _registerHelp = this.helpRegistry.register({
		id: 'markdown-editor',
		label: 'Markdown Editor',
		markdownPath: 'assets/help/markdown-editor/overview.md',
		sections: [
			{ id: 'toolbar', label: 'Toolbar', anchorId: 'md-editor-toolbar' },
			{
				id: 'history',
				label: 'History',
				anchorId: 'md-editor-history',
			},
			{
				id: 'format',
				label: 'Format',
				anchorId: 'md-editor-format',
			},
			{
				id: 'insert',
				label: 'Insert',
				anchorId: 'md-editor-insert',
			},
			{
				id: 'media',
				label: 'Media',
				anchorId: 'md-editor-media',
			},
			{
				id: 'view',
				label: 'View',
				anchorId: 'md-editor-view',
			},
			{
				id: 'formatting',
				label: 'Formatting',
				anchorId: 'md-editor-textarea',
			},
			{ id: 'images', label: 'Images', anchorId: 'md-editor-image-btn' },
			{ id: 'tables', label: 'Tables', anchorId: 'md-editor-table-btn' },
			{
				id: 'captions',
				label: 'Captions',
				anchorId: 'md-editor-captions',
			},
			{ id: 'preview', label: 'Preview', anchorId: 'md-editor-preview' },
			{
				id: 'fullscreen',
				label: 'Fullscreen',
				anchorId: 'md-editor-fullscreen-btn',
			},
		],
	});

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

				// Intercept Ctrl+Z / Ctrl+Y for custom undo/redo.
				fromEvent<KeyboardEvent>(textarea.nativeElement, 'keydown')
					.pipe(takeUntilDestroyed(this.destroyRef))
					.subscribe((e) => {
						if (e.ctrlKey && e.key === 'z' && !e.shiftKey) {
							e.preventDefault();
							this.undo();
						} else if (
							(e.ctrlKey && e.key === 'y') ||
							(e.ctrlKey && e.shiftKey && e.key === 'Z')
						) {
							e.preventDefault();
							this.redo();
						}
					});

				// Listen to input events to detect typing boundaries for undo grouping.
				fromEvent<InputEvent>(textarea.nativeElement, 'input')
					.pipe(takeUntilDestroyed(this.destroyRef))
					.subscribe((e) => {
						if (this.isUndoRedoAction) {
							return;
						}
						this.handleInputForUndoGrouping(e, textarea.nativeElement);
					});

				// Record initial state so first undo has something to revert to.
				this.undoStack.push(this.mdContent());
				this.canUndo.set(false);
			}

			// Toolbar collapse: measure sections and observe resize
			this.initToolbarCollapse();
		});

		this.destroyRef.onDestroy(() => {
			if (this.undoGroupTimer) {
				clearTimeout(this.undoGroupTimer);
			}
			if (this.isDragging) {
				this.clearDragStyles();
			}
			this.revokeImageObjectUrls();
			if (this.toolbarResizeObserver) {
				this.toolbarResizeObserver.disconnect();
			}
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
		const imageLinkRegex =
			/<image-link(?:\s+size="(xs|s|m|l)")?>(\d+)<\/image-link>/g;
		const matches = [...content.matchAll(imageLinkRegex)];
		const artifactIds = [...new Set(matches.map((m) => m[2]))];

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
						// Replace all variants of the image-link tag (with or without size)
						const tagRegex = new RegExp(
							`<image-link(?:\\s+size="(xs|s|m|l)")?>` +
								img.artifactId.replace(
									/[.*+?^${}()|[\]\\]/g,
									'\\$&'
								) +
								`</image-link>`,
							'g'
						);
						contentWithImages = contentWithImages.replace(
							tagRegex,
							(_, size) => {
								const sizePercent = this.getSizePercent(size);
								const style = sizePercent
									? ` style="max-width:${sizePercent}%"`
									: '';
								return `<img src="${img.objectUrl}" alt="Image ${img.artifactId}"${style} />`;
							}
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

	private getSizePercent(size: string | undefined): number | null {
		switch (size) {
			case 'xs':
				return 25;
			case 's':
				return 50;
			case 'm':
				return 75;
			case 'l':
				return 100;
			default:
				return null;
		}
	}

	/**
	 * Applies a formatting action at the cursor position.
	 * If text is selected and the action has a prefix/suffix,
	 * wraps the selection. Otherwise inserts the template text.
	 */
	applyFormatting(action: (typeof markdownFormattingActions)[number]): void {
		// Commit any pending typing as its own undo group first.
		this.commitExplicitUndoState();

		const content = this.mdContent();
		const cursorPos =
			this.savedSelectionStart >= 0
				? this.savedSelectionStart
				: content.length;
		const selEnd =
			this.savedSelectionEnd >= 0
				? this.savedSelectionEnd
				: content.length;

		const selectedText = content.substring(cursorPos, selEnd);
		const before = content.substring(0, cursorPos);
		const after = content.substring(selEnd);

		let insertedText: string;
		let newCursorPos: number;

		if (selectedText && action.prefix !== undefined) {
			// Wrap selected text with prefix/suffix
			const suffix = action.suffix ?? action.prefix;
			insertedText = action.prefix + selectedText + suffix;
			newCursorPos = cursorPos + insertedText.length;
		} else if (!selectedText && action.prefix !== undefined) {
			// No selection — insert prefix + placeholder + suffix and select placeholder
			const suffix = action.suffix ?? action.prefix;
			const placeholder = action.placeholder ?? 'text';
			insertedText = action.prefix + placeholder + suffix;
			// Position cursor to select the placeholder
			newCursorPos = cursorPos + action.prefix.length;
			this.savedSelectionStart = newCursorPos;
			this.savedSelectionEnd = newCursorPos + placeholder.length;
			this.isUndoRedoAction = true;
			this.mdContent.set(before + insertedText + after);
			this.isUndoRedoAction = false;
			this.commitCurrentState(before + insertedText + after);
			this.restoreTextareaSelection();
			return;
		} else {
			// Block-level insertion (no prefix/suffix)
			insertedText = action.markdown;
			newCursorPos = cursorPos + insertedText.length;
		}

		this.isUndoRedoAction = true;
		this.mdContent.set(before + insertedText + after);
		this.isUndoRedoAction = false;
		this.commitCurrentState(before + insertedText + after);

		this.savedSelectionStart = newCursorPos;
		this.savedSelectionEnd = newCursorPos;
		this.restoreTextareaSelection();
	}

	applyFormattingAndClose(
		action: (typeof markdownFormattingActions)[number]
	): void {
		this.applyFormatting(action);
	}

	applyHeading(level: HeadingLevel | string): void {
		const numLevel =
			typeof level === 'string' ? (parseInt(level, 10) as HeadingLevel) : level;
		if (numLevel < 1 || numLevel > 6) {
			return;
		}
		const prefix = '#'.repeat(numLevel) + ' ';
		this.applyFormatting({
			name: `Heading ${numLevel}`,
			icon: 'title',
			markdown: `${prefix}Heading ${numLevel}`,
			prefix,
			suffix: '',
			placeholder: `Heading ${numLevel}`,
			group: 'block',
		});
	}

	applyList(value: string): void {
		const option = this.listOptions.find((o) => o.value === value);
		if (!option) {
			return;
		}
		this.applyFormatting({
			name: option.name,
			icon: option.icon,
			markdown: option.markdown,
			group: 'block',
		});
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
				disableClose: true,
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
						return this.imageService
							.uploadImageArtifact(this.artifactId(), result.file)
							.pipe(
								map((uploadResult) => ({
									uploadResult,
									caption: result.caption,
									captionPosition: result.captionPosition,
									size: result.size,
								}))
							);
					})
				)
				.subscribe({
					next: ({
						uploadResult,
						caption,
						captionPosition,
						size,
					}) => {
						this.insertImageLink(
							uploadResult.artifactId,
							caption,
							captionPosition,
							size
						);
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

	private insertImageLink(
		artifactId: string,
		caption = '',
		captionPosition: CaptionPosition = 'below',
		size: ImageSize | null = null
	): void {
		this.commitExplicitUndoState();

		const sizeAttr = size ? ` size="${size}"` : '';
		const imageTag = `<image-link${sizeAttr}>${artifactId}</image-link>`;
		const positionAttr =
			captionPosition === 'above' ? ' position="above"' : '';
		const escapedCaption = caption.replace(/</g, '&lt;');
		const captionTag = escapedCaption
			? `<figure-caption${positionAttr}>${escapedCaption}</figure-caption>`
			: '';
		const currentContent = this.mdContent();
		const cursorPos =
			this.savedSelectionStart >= 0
				? this.savedSelectionStart
				: currentContent.length;

		const before = currentContent.substring(0, cursorPos);
		const after = currentContent.substring(cursorPos);
		let newContent: string;
		if (captionTag && captionPosition === 'above') {
			newContent = before + captionTag + '\n\n' + imageTag + after;
		} else if (captionTag) {
			newContent = before + imageTag + '\n\n' + captionTag + after;
		} else {
			newContent = before + imageTag + after;
		}
		this.isUndoRedoAction = true;
		this.mdContent.set(newContent);
		this.isUndoRedoAction = false;
		this.commitCurrentState(newContent);
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
						caption: parsedTable.caption,
						captionPosition: parsedTable.captionPosition,
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

			if (parsedTable?.hasDuplicateCaption) {
				this.snackBar.open(
					'This table has captions both above and below. Only the above caption is editable here.',
					'Dismiss',
					{ duration: 3000 }
				);
			}

			const dialogRef = this.dialog.open(MarkdownTableDialogComponent, {
				data: dialogData,
				minWidth: '50%',
				maxWidth: '90vw',
				disableClose: true,
				autoFocus: false,
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
					this.commitExplicitUndoState();

					const positionAttr =
						result.captionPosition === 'above'
							? ' position="above"'
							: '';
					const escapedCaption = result.caption.replace(/</g, '&lt;');
					const captionTag = escapedCaption
						? `<table-caption${positionAttr}>${escapedCaption}</table-caption>`
						: '';
					let newContent: string;
					if (parsedTable) {
						// Replace existing table
						const before = content.substring(
							0,
							parsedTable.startIndex
						);
						const after = content.substring(parsedTable.endIndex);
						if (captionTag && result.captionPosition === 'above') {
							newContent =
								before +
								captionTag +
								'\n\n' +
								result.markdown +
								after;
						} else if (captionTag) {
							newContent =
								before +
								result.markdown +
								'\n\n' +
								captionTag +
								after;
						} else {
							newContent = before + result.markdown + after;
						}
					} else {
						// Insert new table at cursor or end
						const cursorPos =
							this.savedSelectionStart >= 0
								? this.savedSelectionStart
								: content.length;
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
						const tableWithCaption =
							captionTag && result.captionPosition === 'above'
								? captionTag + '\n\n' + result.markdown
								: captionTag
									? result.markdown + '\n\n' + captionTag
									: result.markdown;
						newContent =
							before +
							prefixNewlines +
							tableWithCaption +
							suffixNewlines +
							after;
					}
					this.isUndoRedoAction = true;
					this.mdContent.set(newContent);
					this.isUndoRedoAction = false;
					this.commitCurrentState(newContent);

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

		if (parsedTable.hasDuplicateCaption) {
			this.snackBar.open(
				'This table has captions both above and below. Only the above caption is included in the selection.',
				'Dismiss',
				{ duration: 3000 }
			);
		}
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
		caption: string;
		captionPosition: CaptionPosition;
		hasDuplicateCaption: boolean;
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

		// If not found, check if cursor is on a <table-caption> line
		if (separatorLineIndex === -1) {
			const captionMatch = lines[cursorLineIndex].match(
				/^<table-caption(?:\s+position="(above|below)")?>([^<]*)<\/table-caption>$/
			);
			if (captionMatch) {
				const captionPos = captionMatch[1] || 'below';

				if (captionPos === 'above') {
					// Caption is above its table — search BELOW for the table
					let searchBelow = cursorLineIndex + 1;
					while (
						searchBelow < lines.length &&
						lines[searchBelow].trim() === ''
					) {
						searchBelow++;
					}
					if (
						searchBelow < lines.length &&
						lines[searchBelow].trim().startsWith('|')
					) {
						if (
							searchBelow + 1 < lines.length &&
							isSeparatorLine(lines[searchBelow + 1])
						) {
							separatorLineIndex = searchBelow + 1;
						}
					}
				} else {
					// Caption is below its table — search ABOVE for the table
					let searchFrom = cursorLineIndex - 1;
					while (searchFrom >= 0 && lines[searchFrom].trim() === '') {
						searchFrom--;
					}
					for (let i = searchFrom; i >= 0; i--) {
						if (isSeparatorLine(lines[i])) {
							separatorLineIndex = i;
							break;
						}
						if (!lines[i].trim().startsWith('|')) {
							break;
						}
					}
				}
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

		// Check if a table-caption tag precedes the table (position="above")
		let caption = '';
		let captionPosition: CaptionPosition = 'below';
		let aboveCaptionLineIndex = headerLineIndex - 1;
		while (
			aboveCaptionLineIndex >= 0 &&
			lines[aboveCaptionLineIndex].trim() === ''
		) {
			aboveCaptionLineIndex--;
		}
		if (aboveCaptionLineIndex >= 0) {
			const aboveCaptionMatch = lines[aboveCaptionLineIndex].match(
				/^<table-caption(?:\s+position="(above|below)")?>([^<]+)<\/table-caption>$/
			);
			if (aboveCaptionMatch && aboveCaptionMatch[1] === 'above') {
				caption = aboveCaptionMatch[2].replace(/&lt;/g, '<');
				captionPosition = 'above';
				// Extend startIndex backward to include blank lines and caption line
				startIndex = 0;
				for (let i = 0; i < aboveCaptionLineIndex; i++) {
					startIndex += lines[i].length + 1;
				}
			}
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

		// Check if a table-caption tag follows the table (possibly with
		// blank lines in between) — only if no caption was found above
		// and only if the caption's position is not "above" (which would
		// belong to the next table)
		let hasDuplicateCaption = false;
		if (!caption) {
			let captionSearchIndex = dataEndLine;
			while (
				captionSearchIndex < lines.length &&
				lines[captionSearchIndex].trim() === ''
			) {
				captionSearchIndex++;
			}
			if (captionSearchIndex < lines.length) {
				const captionMatch = lines[captionSearchIndex].match(
					/^<table-caption(?:\s+position="(above|below)")?>([^<]+)<\/table-caption>$/
				);
				if (captionMatch && captionMatch[1] !== 'above') {
					captionPosition =
						(captionMatch[1] as CaptionPosition) || 'below';
					caption = captionMatch[2].replace(/&lt;/g, '<');
					// Extend endIndex to include blank lines and the caption line
					endIndex = 0;
					for (let i = 0; i <= captionSearchIndex; i++) {
						endIndex += lines[i].length + 1;
					}
					endIndex = Math.min(endIndex, content.length);
				}
			}
		} else {
			// Caption was found above — check if there's also one below (duplicate)
			let captionSearchIndex = dataEndLine;
			while (
				captionSearchIndex < lines.length &&
				lines[captionSearchIndex].trim() === ''
			) {
				captionSearchIndex++;
			}
			if (captionSearchIndex < lines.length) {
				const captionMatch = lines[captionSearchIndex].match(
					/^<table-caption(?:\s+position="(above|below)")?>([^<]+)<\/table-caption>$/
				);
				if (captionMatch && captionMatch[1] !== 'above') {
					hasDuplicateCaption = true;
				}
			}
		}

		return {
			headers,
			headerSpans,
			cells,
			alignments,
			startIndex,
			endIndex,
			caption,
			captionPosition,
			hasDuplicateCaption,
		};
	}

	private selectionOverlapsTable(
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
		// Split on unescaped pipes only (not \|)
		return trimmed
			.split(/(?<!\\)\|/)
			.map((cell) =>
				cell.trim().replace(/\\\|/g, '|').replace(/<br>/gi, '\n')
			);
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
		// Split on unescaped | but keep the raw content to distinguish
		// "| |" (empty cell with space) from "||" (span marker — no content at all)
		const rawCells = trimmed.split(/(?<!\\)\|/);

		const colCount = rawCells.length;
		const headers: string[] = Array(colCount).fill('');
		const headerSpans: number[] = Array(colCount).fill(1);

		let colIdx = 0;
		let i = 0;
		while (i < rawCells.length && colIdx < colCount) {
			const cellContent = rawCells[i]
				.trim()
				.replace(/\\\|/g, '|')
				.replace(/<br>/gi, '\n');
			headers[colIdx] = cellContent;

			// Count consecutive truly-empty cells (no characters at all between pipes)
			// This is the Flexmark colspan syntax: "||" has an empty string between pipes
			let span = 1;
			while (i + span < rawCells.length && rawCells[i + span] === '') {
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

	/**
	 * Initializes toolbar collapse behavior.
	 * Sets up a ResizeObserver to recompute collapse state on width changes.
	 * No DOM measurement needed — thresholds are static constants.
	 */
	private initToolbarCollapse(): void {
		const toolbar = this.toolbarEl()?.nativeElement;
		if (!toolbar) {
			return;
		}

		// Compute initial state
		this.computeCollapseState(toolbar.clientWidth);

		// Observe container width changes
		this.toolbarResizeObserver = new ResizeObserver((entries) => {
			for (const entry of entries) {
				const width =
					entry.contentBoxSize?.[0]?.inlineSize ??
					entry.contentRect.width;
				this.computeCollapseState(width);
			}
		});
		this.toolbarResizeObserver.observe(toolbar);
	}

	/**
	 * Computes which collapse state to use for the given container width.
	 *
	 * Walks the pre-computed thresholds array. The first threshold the
	 * container width meets or exceeds determines the state.
	 * Deterministic, no DOM measurement, no oscillation.
	 */
	private computeCollapseState(containerWidth: number): void {
		const thresholds = MarkdownEditorComponent.COLLAPSE_THRESHOLDS;
		let newState = thresholds.length - 1; // max collapsed state
		for (let i = 0; i < thresholds.length; i++) {
			if (containerWidth >= thresholds[i]) {
				newState = i;
				break;
			}
		}

		if (newState !== this.collapseState()) {
			this.collapseState.set(newState);
		}
	}

	/**
	 * Handles action selection from a collapsed Format section dropdown.
	 */
	protected handleFormatAction(actionId: string): void {
		switch (actionId) {
			case 'heading':
				this.applyHeading('2');
				break;
			case 'bold':
				this.applyFormattingById('Bold');
				break;
			case 'italic':
				this.applyFormattingById('Italic');
				break;
			case 'strikethrough':
				this.applyFormattingById('Strikethrough');
				break;
		}
	}

	/**
	 * Handles action selection from a collapsed Insert section dropdown.
	 */
	protected handleInsertAction(actionId: string): void {
		switch (actionId) {
			case 'list':
				this.applyList('bulleted');
				break;
			case 'inline-code':
				this.applyFormattingById('Inline Code');
				break;
			case 'link':
				this.applyFormattingById('Link');
				break;
			case 'blockquote':
				this.applyFormattingById('Blockquote');
				break;
			case 'code-block':
				this.applyFormattingById('Code Block');
				break;
			case 'horizontal-rule':
				this.applyFormattingById('Horizontal Rule');
				break;
		}
	}

	/**
	 * Handles action selection from a collapsed Media section dropdown.
	 */
	protected handleMediaAction(actionId: string): void {
		switch (actionId) {
			case 'upload-image':
				this.openUploadImageDialog();
				break;
			case 'insert-table':
				this.openTableDialog();
				break;
			case 'select-table':
				this.selectTableAtCursor();
				break;
			case 'figure-caption':
				this.applyFormattingById('Figure Caption');
				break;
			case 'table-caption':
				this.applyFormattingById('Table Caption');
				break;
		}
	}

	/**
	 * Applies a formatting action by its display name.
	 */
	private applyFormattingById(name: string): void {
		const action = this.formattingActions.find((a) => a.name === name);
		if (action) {
			this.applyFormatting(action);
		}
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

	private savedSelectionStart = -1;
	private savedSelectionEnd = -1;

	private saveTextareaSelection(): void {
		const textarea = this.editorTextarea()?.nativeElement;
		if (textarea) {
			this.savedSelectionStart = textarea.selectionStart;
			this.savedSelectionEnd = textarea.selectionEnd;
		}
	}

	private restoreTextareaSelection(): void {
		const start = this.savedSelectionStart;
		const end = this.savedSelectionEnd;
		setTimeout(() => {
			const textarea = this.editorTextarea()?.nativeElement;
			if (textarea && start >= 0) {
				textarea.focus();
				textarea.selectionStart = start;
				textarea.selectionEnd = end;
			}
		});
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

	/**
	 * Handles input events to implement VS Code-style undo grouping.
	 *
	 * Groups consecutive typing at the same cursor position into one undo entry.
	 * Starts a new group on:
	 * - Word boundaries (whitespace typed after non-whitespace)
	 * - Cursor jumps (selection-based edits like paste, or delete-selection)
	 * - Non-insertText input types (paste, cut, delete, etc.)
	 * - Idle timeout (user pauses typing)
	 */
	private handleInputForUndoGrouping(
		event: InputEvent,
		textarea: HTMLTextAreaElement
	): void {
		const currentValue = this.mdContent();
		const cursorPos = textarea.selectionStart;

		const isTyping = event.inputType === 'insertText';
		const isNewline =
			event.inputType === 'insertLineBreak' ||
			event.inputType === 'insertParagraph';
		const typedChar = event.data ?? '';
		const isWhitespace = /\s/.test(typedChar);

		// Determine if we need to break the group
		let shouldBreakGroup = false;

		if (!isTyping && !isNewline) {
			// Non-typing input (paste, cut, drop, delete, etc.) — always a new group
			shouldBreakGroup = true;
		} else if (isNewline) {
			// Newline always starts a new group
			shouldBreakGroup = true;
		} else if (
			this.lastEditCursorPos >= 0 &&
			cursorPos !== this.lastEditCursorPos + 1
		) {
			// Cursor jumped (user clicked elsewhere then typed)
			shouldBreakGroup = true;
		} else if (isWhitespace && !this.lastCharWasWhitespace) {
			// Word boundary: transition from non-whitespace to whitespace
			shouldBreakGroup = true;
		}

		if (shouldBreakGroup) {
			this.commitPendingUndoGroup();
		}

		// If no pending group, record the state before this edit
		if (this.pendingGroupStart === null) {
			this.pendingGroupStart =
				this.undoStack[this.undoStack.length - 1] ?? '';
		}

		this.lastEditCursorPos = cursorPos;
		this.lastCharWasWhitespace = isWhitespace || isNewline;

		// Reset the debounce timer
		if (this.undoGroupTimer) {
			clearTimeout(this.undoGroupTimer);
		}
		this.undoGroupTimer = setTimeout(() => {
			this.commitCurrentState(currentValue);
		}, this.undoGroupDelay);

		// Clear redo on new input
		if (this.redoStack.length > 0) {
			this.redoStack.length = 0;
			this.canRedo.set(false);
		}
	}

	/**
	 * Commits the pending typing group to the undo stack.
	 * Called when a group boundary is detected or after idle timeout.
	 */
	private commitPendingUndoGroup(): void {
		if (this.undoGroupTimer) {
			clearTimeout(this.undoGroupTimer);
			this.undoGroupTimer = null;
		}
		// Commit the current content as the latest undo state
		const current = this.mdContent();
		const top = this.undoStack[this.undoStack.length - 1];
		if (current !== top) {
			if (this.undoStack.length >= this.maxHistory) {
				this.undoStack.shift();
			}
			this.undoStack.push(current);
			this.canUndo.set(this.undoStack.length > 1);
		}
		this.pendingGroupStart = null;
	}

	/**
	 * Commits a specific value as the latest undo state (used by idle timeout).
	 */
	private commitCurrentState(value: string): void {
		this.undoGroupTimer = null;
		const top = this.undoStack[this.undoStack.length - 1];
		if (value !== top) {
			if (this.undoStack.length >= this.maxHistory) {
				this.undoStack.shift();
			}
			this.undoStack.push(value);
			this.canUndo.set(this.undoStack.length > 1);
		}
		this.pendingGroupStart = null;
	}

	/**
	 * Commits the current state immediately as its own undo group.
	 * Used for programmatic changes (toolbar actions, dialog results)
	 * that should each be a single undo step.
	 */
	private commitExplicitUndoState(): void {
		this.commitPendingUndoGroup();
	}

	undo(): void {
		// First commit any pending typing group
		this.commitPendingUndoGroup();

		if (this.undoStack.length <= 1) {
			return;
		}
		const current = this.undoStack.pop()!;
		this.redoStack.push(current);
		const previous = this.undoStack[this.undoStack.length - 1];

		this.isUndoRedoAction = true;
		this.mdContent.set(previous);
		this.isUndoRedoAction = false;

		this.lastEditCursorPos = -1;
		this.canUndo.set(this.undoStack.length > 1);
		this.canRedo.set(this.redoStack.length > 0);
	}

	redo(): void {
		if (this.redoStack.length === 0) {
			return;
		}
		const next = this.redoStack.pop()!;
		this.undoStack.push(next);

		this.isUndoRedoAction = true;
		this.mdContent.set(next);
		this.isUndoRedoAction = false;

		this.lastEditCursorPos = -1;
		this.canUndo.set(this.undoStack.length > 1);
		this.canRedo.set(this.redoStack.length > 0);
	}
}
