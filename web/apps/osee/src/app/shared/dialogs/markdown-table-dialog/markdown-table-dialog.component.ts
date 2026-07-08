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
	computed,
	DestroyRef,
	HostListener,
	inject,
	signal,
} from '@angular/core';
import { FormsModule } from '@angular/forms';
import {
	MAT_DIALOG_DATA,
	MatDialogActions,
	MatDialogContent,
	MatDialogRef,
	MatDialogTitle,
} from '@angular/material/dialog';
import { MatButton, MatIconButton } from '@angular/material/button';
import { MatIcon } from '@angular/material/icon';
import { MatSnackBar } from '@angular/material/snack-bar';
import { MatTooltip } from '@angular/material/tooltip';
import { MatFormField, MatLabel } from '@angular/material/form-field';
import { MatInput } from '@angular/material/input';
import { HelpTopicRegistryService } from '../../components/help-drawer/help-topic-registry.service';
import { HelpAnchorDirective } from '../../components/help-drawer/help-anchor.directive';
import { HelpButtonComponent } from '../../components/help-drawer/help-button.component';

export type ColumnAlignment = 'left' | 'center' | 'right';

export type CaptionPosition = 'above' | 'below';

export type MarkdownTableDialogData = {
	readonly rows: number;
	readonly cols: number;
	readonly headers: string[];
	readonly headerSpans: number[];
	readonly cells: string[][];
	readonly alignments: ColumnAlignment[];
	readonly isEdit: boolean;
	readonly caption?: string;
	readonly captionPosition?: CaptionPosition;
};

export type MarkdownTableDialogResult = {
	readonly markdown: string;
	readonly caption: string;
	readonly captionPosition: CaptionPosition;
};

type TableSnapshot = {
	readonly headers: string[];
	readonly headerSpans: number[];
	readonly cells: string[][];
	readonly alignments: ColumnAlignment[];
	readonly caption: string;
	readonly captionPosition: CaptionPosition;
};

@Component({
	selector: 'osee-markdown-table-dialog',
	imports: [
		FormsModule,
		MatDialogTitle,
		MatDialogContent,
		MatDialogActions,
		MatButton,
		MatIconButton,
		MatIcon,
		MatTooltip,
		MatFormField,
		MatLabel,
		MatInput,
		HelpAnchorDirective,
		HelpButtonComponent,
	],
	changeDetection: ChangeDetectionStrategy.OnPush,
	templateUrl: './markdown-table-dialog.component.html',
})
export class MarkdownTableDialogComponent {
	private readonly dialogRef =
		inject<
			MatDialogRef<
				MarkdownTableDialogComponent,
				MarkdownTableDialogResult | undefined
			>
		>(MatDialogRef);
	private readonly data = inject<MarkdownTableDialogData>(MAT_DIALOG_DATA);
	private readonly snackBar = inject(MatSnackBar);
	private readonly destroyRef = inject(DestroyRef);
	private readonly helpRegistry = inject(HelpTopicRegistryService);

	private readonly _registerHelp = this.helpRegistry.register({
		id: 'markdown-table-dialog',
		label: 'Table Editor',
		markdownPath: 'assets/help/markdown-table-dialog/overview.md',
		sections: [
			{ id: 'table-size', label: 'Table Size', anchorId: 'table-dialog-dimensions' },
			{ id: 'headers-spans', label: 'Headers & Spans', anchorId: 'table-dialog-headers' },
			{ id: 'editing-cells', label: 'Editing Cells', anchorId: 'table-dialog-cells' },
			{ id: 'column-alignment', label: 'Column Alignment', anchorId: 'table-dialog-alignment' },
			{ id: 'captions', label: 'Captions', anchorId: 'table-dialog-caption' },
			{ id: 'undo-redo', label: 'Undo & Redo', anchorId: 'table-dialog-undo' },
		],
	});

	private readonly maxCols = 50;
	private readonly maxRows = 100;

	@HostListener('document:keydown', ['$event'])
	protected onKeyDown(event: KeyboardEvent): void {
		if (event.ctrlKey && event.key === 'z') {
			event.preventDefault();
			this.undo();
		} else if (event.ctrlKey && event.key === 'y') {
			event.preventDefault();
			this.redo();
		} else if (event.key === 'Escape') {
			this.cancelDialog();
		}
	}

	protected readonly isEdit = this.data.isEdit;
	protected readonly caption = signal(this.data.caption ?? '');
	protected readonly captionPosition = signal<CaptionPosition>(
		this.data.captionPosition ?? 'below'
	);
	protected readonly headers = signal<string[]>([...this.data.headers]);
	protected readonly headerSpans = signal<number[]>([
		...this.data.headerSpans,
	]);
	protected readonly cells = signal<string[][]>([]);
	protected readonly alignments = signal<ColumnAlignment[]>([
		...this.data.alignments,
	]);

	// Undo/redo state management
	private undoStack: TableSnapshot[] = [];
	private redoStack: TableSnapshot[] = [];
	private readonly maxUndoHistory = 50;

	protected readonly canUndo = signal(false);
	protected readonly canRedo = signal(false);

	/** Captures a snapshot of the current table state. */
	private captureSnapshot(): TableSnapshot {
		return {
			headers: [...this.headers()],
			headerSpans: [...this.headerSpans()],
			cells: this.cells().map((r) => [...r]),
			alignments: [...this.alignments()],
			caption: this.caption(),
			captionPosition: this.captionPosition(),
		};
	}

	/** Call before any state-modifying operation to save undo state. */
	protected saveUndoState(): void {
		this.undoStack.push(this.captureSnapshot());
		if (this.undoStack.length > this.maxUndoHistory) {
			this.undoStack.shift();
		}
		this.redoStack = [];
		this.canUndo.set(true);
		this.canRedo.set(false);
	}

	protected undo(): void {
		if (this.undoStack.length === 0) {
			return;
		}
		this.redoStack.push(this.captureSnapshot());
		const snapshot = this.undoStack.pop()!;
		this.applySnapshot(snapshot);
		this.canUndo.set(this.undoStack.length > 0);
		this.canRedo.set(true);
	}

	protected redo(): void {
		if (this.redoStack.length === 0) {
			return;
		}
		this.undoStack.push(this.captureSnapshot());
		const snapshot = this.redoStack.pop()!;
		this.applySnapshot(snapshot);
		this.canUndo.set(true);
		this.canRedo.set(this.redoStack.length > 0);
	}

	private applySnapshot(snapshot: TableSnapshot): void {
		this.headers.set(snapshot.headers);
		this.headerSpans.set(snapshot.headerSpans);
		this.cells.set(snapshot.cells);
		this.alignments.set(snapshot.alignments);
		this.caption.set(snapshot.caption);
		this.captionPosition.set(snapshot.captionPosition);
	}
	protected readonly isLoading = signal(true);

	constructor() {
		// Load cells — render immediately for small tables, batch for large ones
		const allCells = this.data.cells.map((row) => [...row]);
		const totalCells = allCells.length * (this.data.cols || 1);

		if (totalCells <= 500) {
			// Small table: load immediately
			this.cells.set(allCells);
			this.isLoading.set(false);
		} else {
			// Large table: load first batch small for quick render, rest in larger batches
			const firstBatch = 10;
			const subsequentBatch = 45;
			this.cells.set(allCells.slice(0, firstBatch));
			let loaded = firstBatch;

			const loadBatch = () => {
				const end = Math.min(loaded + subsequentBatch, allCells.length);
				this.cells.set(allCells.slice(0, end));
				loaded = end;

				if (loaded < allCells.length) {
					setTimeout(loadBatch, 0);
				} else {
					this.isLoading.set(false);
				}
			};

			setTimeout(loadBatch, 0);
		}
	}

	protected readonly colCount = computed(() => this.headers().length);
	protected readonly rowCount = computed(() => this.cells().length);

	protected readonly canRemoveColumn = computed(() => this.colCount() > 1);
	protected readonly canRemoveRow = computed(() => this.rowCount() > 1);

	/**
	 * Returns which header column indices are "visible" (i.e., the start of a span).
	 * A header at index i is visible if no previous header's span covers it.
	 */
	protected readonly visibleHeaderIndices = computed(() => {
		const spans = this.headerSpans();
		const visible: number[] = [];
		let i = 0;
		while (i < spans.length) {
			visible.push(i);
			const span = spans[i] === undefined ? 1 : spans[i];
			i += span || 1;
		}
		return visible;
	});

	protected readonly hasSpans = computed(() => {
		return this.headerSpans().some((s) => s !== undefined && s !== 1);
	});

	/** Merge this column into the header to its left. */
	protected mergeLeft(colIndex: number): void {
		if (colIndex <= 0) {
			return;
		}
		const spans = this.headerSpans();
		const thisSpan = spans[colIndex] === undefined ? 1 : spans[colIndex];
		if (thisSpan === 0) {
			return; // Already spanned
		}
		this.saveUndoState();

		// Find the owner to the left (the nearest visible header)
		let ownerIdx = colIndex - 1;
		while (ownerIdx > 0 && spans[ownerIdx] === 0) {
			ownerIdx--;
		}

		const ownerSpan = spans[ownerIdx] === undefined ? 1 : spans[ownerIdx];

		// Transfer this column's entire span into the owner
		this.headerSpans.update((s) => {
			const copy = [...s];
			copy[ownerIdx] = ownerSpan + thisSpan;
			copy[colIndex] = 0;
			// Any columns that were already spanned by this column stay as 0
			return copy;
		});
		// Clear this column's header text
		this.headers.update((h) => {
			const copy = [...h];
			copy[colIndex] = '';
			return copy;
		});
	}

	/** Unmerge this column — splits the span. This column becomes owner of the right portion. */
	protected unmerge(colIndex: number): void {
		const spans = this.headerSpans();
		// Find the owner
		let ownerIdx = -1;
		for (let i = colIndex - 1; i >= 0; i--) {
			const s = spans[i] === undefined ? 1 : spans[i];
			if (s > 0 && i + s > colIndex) {
				ownerIdx = i;
				break;
			}
		}
		if (ownerIdx === -1) {
			return;
		}
		this.saveUndoState();

		const ownerSpan = spans[ownerIdx] === undefined ? 1 : spans[ownerIdx];
		const rightPortionSize = ownerIdx + ownerSpan - colIndex;

		this.headerSpans.update((s) => {
			const copy = [...s];
			// Owner keeps only up to this column
			copy[ownerIdx] = colIndex - ownerIdx;
			// This column becomes the new owner of the right portion
			copy[colIndex] = rightPortionSize;
			return copy;
		});
	}

	/** Any spanned column can be unmerged. */
	protected canUnmerge(colIndex: number): boolean {
		const spans = this.headerSpans();
		return (spans[colIndex] === undefined ? 1 : spans[colIndex]) === 0;
	}

	/** Check if a column can be merged left. */
	protected canMergeLeft(colIndex: number): boolean {
		if (colIndex <= 0) {
			return false;
		}
		const spans = this.headerSpans();
		const span = spans[colIndex] === undefined ? 1 : spans[colIndex];
		// Can only merge if this column is currently independent (span >= 1)
		return span >= 1;
	}

	/** Check if a column is currently spanned (part of another header's span). */
	protected isSpanned(colIndex: number): boolean {
		const spans = this.headerSpans();
		return (spans[colIndex] === undefined ? 1 : spans[colIndex]) === 0;
	}

	protected getSpan(colIndex: number): number {
		const span = this.headerSpans()[colIndex];
		return span === undefined ? 1 : span;
	}

	protected getSpanOwnerLabel(colIndex: number): string {
		// Walk left to find the header that owns this column
		const spans = this.headerSpans();
		for (let i = colIndex - 1; i >= 0; i--) {
			const span = spans[i] || 1;
			if (span > 0 && i + span > colIndex) {
				const name = this.headers()[i] || `Header ${i + 1}`;
				return `← ${name}`;
			}
		}
		return '';
	}

	protected getHeaderLabel(colIndex: number): string {
		const span = this.getSpan(colIndex);
		const label = `Header ${colIndex + 1}`;
		return span > 1 ? `${label} (${span} cols)` : label;
	}

	protected onColCountChange(event: Event): void {
		const input = event.target as HTMLInputElement;
		const value = parseInt(input.value, 10);
		if (isNaN(value) || value < 1) {
			input.value = String(this.colCount());
			return;
		}
		if (value > this.maxCols) {
			this.snackBar.open(
				`Maximum ${this.maxCols} columns allowed.`,
				'Dismiss',
				{ duration: 3000 }
			);
		}
		const newCols = Math.min(this.maxCols, value);
		this.saveUndoState();
		this.resizeTable(newCols, this.rowCount());
		input.value = String(newCols);
	}

	protected onRowCountChange(event: Event): void {
		const input = event.target as HTMLInputElement;
		const value = parseInt(input.value, 10);
		if (isNaN(value) || value < 1) {
			input.value = String(this.rowCount());
			return;
		}
		if (value > this.maxRows) {
			this.snackBar.open(
				`Maximum ${this.maxRows} rows allowed.`,
				'Dismiss',
				{ duration: 3000 }
			);
		}
		const newRows = Math.min(this.maxRows, value);
		this.saveUndoState();
		this.resizeTable(this.colCount(), newRows);
		input.value = String(newRows);
	}

	private resizeTable(newCols: number, newRows: number): void {
		const currentHeaders = this.headers();
		const currentCells = this.cells();
		const currentAlignments = this.alignments();
		const currentSpans = this.headerSpans();

		// Resize headers
		const resizedHeaders = Array.from({ length: newCols }, (_, i) =>
			i < currentHeaders.length ? currentHeaders[i] : ''
		);
		this.headers.set(resizedHeaders);

		// Resize header spans
		const resizedSpans = Array.from({ length: newCols }, (_, i) =>
			i < currentSpans.length ? currentSpans[i] : 1
		);
		// Clamp spans that would extend beyond new column count
		for (let i = 0; i < resizedSpans.length; i++) {
			if (i + resizedSpans[i] > newCols) {
				resizedSpans[i] = newCols - i;
			}
		}
		this.headerSpans.set(resizedSpans);

		// Resize alignments
		const resizedAlignments: ColumnAlignment[] = Array.from(
			{ length: newCols },
			(_, i) =>
				i < currentAlignments.length ? currentAlignments[i] : 'left'
		);
		this.alignments.set(resizedAlignments);

		// Resize cells
		const resizedCells: string[][] = Array.from(
			{ length: newRows },
			(_, rowIdx) => {
				const existingRow =
					rowIdx < currentCells.length ? currentCells[rowIdx] : [];
				return Array.from({ length: newCols }, (_, colIdx) =>
					colIdx < existingRow.length ? existingRow[colIdx] : ''
				);
			}
		);
		this.cells.set(resizedCells);
	}

	protected updateHeader(colIndex: number, value: string): void {
		this.headers.update((h) => {
			const copy = [...h];
			copy[colIndex] = value;
			return copy;
		});
	}

	protected updateCell(
		rowIndex: number,
		colIndex: number,
		value: string
	): void {
		this.cells.update((rows) => {
			const copy = rows.map((r) => [...r]);
			copy[rowIndex][colIndex] = value;
			return copy;
		});
	}

	protected cycleAlignment(colIndex: number): void {
		this.saveUndoState();
		this.alignments.update((a) => {
			const copy = [...a];
			const order: ColumnAlignment[] = ['left', 'center', 'right'];
			const current = order.indexOf(copy[colIndex]);
			copy[colIndex] = order[(current + 1) % 3];
			return copy;
		});
	}

	protected getAlignmentIcon(alignment: ColumnAlignment): string {
		switch (alignment) {
			case 'left':
				return 'format_align_left';
			case 'center':
				return 'format_align_center';
			case 'right':
				return 'format_align_right';
		}
	}

	protected getAlignmentLabel(alignment: ColumnAlignment): string {
		switch (alignment) {
			case 'left':
				return 'Left Aligned';
			case 'center':
				return 'Center Aligned';
			case 'right':
				return 'Right Aligned';
		}
	}

	protected addColumn(): void {
		if (this.colCount() >= this.maxCols) {
			this.snackBar.open(
				`Maximum ${this.maxCols} columns reached.`,
				'Dismiss',
				{ duration: 3000 }
			);
			return;
		}
		this.saveUndoState();
		this.headers.update((h) => [...h, '']);
		this.headerSpans.update((s) => [...s, 1]);
		this.cells.update((rows) => rows.map((r) => [...r, '']));
		this.alignments.update((a) => [...a, 'left']);
	}

	protected insertColumnAt(colIndex: number): void {
		if (this.colCount() >= this.maxCols) {
			this.snackBar.open(
				`Maximum ${this.maxCols} columns reached.`,
				'Dismiss',
				{ duration: 3000 }
			);
			return;
		}
		this.saveUndoState();

		// Check if inserting within an existing span
		const spans = this.headerSpans();
		let ownerIdx = -1;
		for (let i = 0; i < colIndex; i++) {
			const s = spans[i] === undefined ? 1 : spans[i];
			if (s > 0 && i + s > colIndex) {
				ownerIdx = i;
				break;
			}
		}

		this.headers.update((h) => {
			const copy = [...h];
			copy.splice(colIndex, 0, '');
			return copy;
		});
		this.headerSpans.update((s) => {
			const copy = [...s];
			if (ownerIdx >= 0) {
				// Inserting within a span: add as spanned (0) and increase owner
				copy.splice(colIndex, 0, 0);
				copy[ownerIdx] =
					(copy[ownerIdx] === undefined ? 1 : copy[ownerIdx]) + 1;
			} else {
				// Inserting outside any span: independent column
				copy.splice(colIndex, 0, 1);
			}
			return copy;
		});
		this.cells.update((rows) =>
			rows.map((r) => {
				const copy = [...r];
				copy.splice(colIndex, 0, '');
				return copy;
			})
		);
		this.alignments.update((a) => {
			const copy = [...a];
			copy.splice(colIndex, 0, 'left');
			return copy;
		});
	}

	protected removeColumn(colIndex: number): void {
		if (!this.canRemoveColumn()) {
			return;
		}
		this.saveUndoState();
		const spans = this.headerSpans();
		const span = spans[colIndex] === undefined ? 1 : spans[colIndex];

		if (span > 1) {
			// Removing the owner of a span: transfer ownership to the next column
			const nextCol = colIndex + 1;
			this.headerSpans.update((s) => {
				const copy = [...s];
				copy[nextCol] = span - 1;
				copy.splice(colIndex, 1);
				return copy;
			});
			// Transfer the header text to the new owner
			this.headers.update((h) => {
				const copy = [...h];
				copy[nextCol] = copy[colIndex];
				copy.splice(colIndex, 1);
				return copy;
			});
		} else if (span === 0) {
			// Removing a spanned-over column: reduce the owner's span
			let ownerIdx = -1;
			for (let i = colIndex - 1; i >= 0; i--) {
				const s = spans[i] === undefined ? 1 : spans[i];
				if (s > 0 && i + s > colIndex) {
					ownerIdx = i;
					break;
				}
			}
			this.headerSpans.update((s) => {
				const copy = [...s];
				if (ownerIdx >= 0) {
					copy[ownerIdx] =
						(copy[ownerIdx] === undefined ? 1 : copy[ownerIdx]) - 1;
				}
				copy.splice(colIndex, 1);
				return copy;
			});
			this.headers.update((h) => {
				const copy = [...h];
				copy.splice(colIndex, 1);
				return copy;
			});
		} else {
			// Independent column (span = 1): just remove
			this.headerSpans.update((s) => {
				const copy = [...s];
				copy.splice(colIndex, 1);
				return copy;
			});
			this.headers.update((h) => {
				const copy = [...h];
				copy.splice(colIndex, 1);
				return copy;
			});
		}

		this.cells.update((rows) =>
			rows.map((r) => r.filter((_, i) => i !== colIndex))
		);
		this.alignments.update((a) => a.filter((_, i) => i !== colIndex));
	}

	protected addRow(): void {
		if (this.rowCount() >= this.maxRows) {
			this.snackBar.open(
				`Maximum ${this.maxRows} rows reached.`,
				'Dismiss',
				{ duration: 3000 }
			);
			return;
		}
		this.saveUndoState();
		const emptyCells = Array(this.colCount()).fill('');
		this.cells.update((rows) => [...rows, emptyCells]);
	}

	protected insertRowAt(rowIndex: number): void {
		if (this.rowCount() >= this.maxRows) {
			this.snackBar.open(
				`Maximum ${this.maxRows} rows reached.`,
				'Dismiss',
				{ duration: 3000 }
			);
			return;
		}
		this.saveUndoState();
		const emptyCells = Array(this.colCount()).fill('');
		this.cells.update((rows) => {
			const copy = [...rows];
			copy.splice(rowIndex, 0, emptyCells);
			return copy;
		});
	}

	protected removeRow(rowIndex: number): void {
		if (!this.canRemoveRow()) {
			return;
		}
		this.saveUndoState();
		this.cells.update((rows) => rows.filter((_, i) => i !== rowIndex));
	}

	protected submitTable(): void {
		const markdown = this.generateMarkdown();
		const caption = this.caption().trim();
		const captionPosition = this.captionPosition();
		// Clear data first to speed up DOM teardown
		this.cells.set([]);
		this.headers.set([]);
		this.dialogRef.close({ markdown, caption, captionPosition });
	}

	protected toggleCaptionPosition(): void {
		this.saveUndoState();
		this.captionPosition.update((pos) =>
			pos === 'above' ? 'below' : 'above'
		);
	}

	protected cancelDialog(): void {
		// Clear data first to speed up DOM teardown
		this.cells.set([]);
		this.headers.set([]);
		this.dialogRef.close(undefined);
	}

	/** Default row height in pixels (minimum enforced during resize). */
	private readonly defaultRowHeight = 62;

	/** Row height stored per row index. */
	protected readonly rowHeights = signal<Record<number, number>>({});
	protected getRowHeight(rowIdx: number): string {
		const h = this.rowHeights()[rowIdx];
		return `${h || this.defaultRowHeight}px`;
	}

	protected onRowDividerMouseDown(event: MouseEvent, rowIdx: number): void {
		event.preventDefault();
		const startY = event.clientY;
		const row = (event.target as HTMLElement).closest('tr')
			?.previousElementSibling as HTMLTableRowElement | null;
		const startHeight = row?.offsetHeight ?? this.defaultRowHeight;

		document.body.style.cursor = 'row-resize';
		document.body.style.userSelect = 'none';

		const onMouseMove = (e: MouseEvent) => {
			const delta = e.clientY - startY;
			const newHeight = Math.max(
				this.defaultRowHeight,
				startHeight + delta
			);
			// Direct DOM update during drag — avoids change detection per frame
			if (row) {
				row.style.height = `${newHeight}px`;
			}
		};

		const cleanup = () => {
			document.body.style.cursor = '';
			document.body.style.userSelect = '';
			document.removeEventListener('mousemove', onMouseMove);
			document.removeEventListener('mouseup', onMouseUp);
		};

		const onMouseUp = (e: MouseEvent) => {
			cleanup();
			// Commit final height to signal once
			const delta = e.clientY - startY;
			const finalHeight = Math.max(
				this.defaultRowHeight,
				startHeight + delta
			);
			this.rowHeights.update((h) => ({ ...h, [rowIdx]: finalHeight }));
		};

		document.addEventListener('mousemove', onMouseMove);
		document.addEventListener('mouseup', onMouseUp);
		this.destroyRef.onDestroy(cleanup);
	}

	private generateMarkdown(): string {
		const headers = this.headers();
		const spans = this.headerSpans();
		const alignments = this.alignments();
		const cells = this.cells();

		// Escape pipes and replace newlines with <br> since markdown table rows must be single lines
		const escapeCell = (value: string): string => {
			const trimmed = value.trim();
			return (trimmed || ' ')
				.replace(/\|/g, '\\|')
				.replace(/\n/g, '<br>');
		};

		// Build header row with colspan syntax (empty cells = ||)
		let headerRow = '|';
		for (let i = 0; i < headers.length; i++) {
			const span = spans[i] === undefined ? 1 : spans[i];
			if (span === 0) {
				// This column is absorbed by a previous span — skip
				continue;
			}
			headerRow += ' ' + escapeCell(headers[i]) + ' |';
			// Add empty pipes for spanned columns
			for (let s = 1; s < span; s++) {
				headerRow += '|';
			}
		}

		const separatorRow =
			'| ' +
			alignments
				.map((a) => {
					switch (a) {
						case 'left':
							return ':--';
						case 'center':
							return ':-:';
						case 'right':
							return '--:';
					}
				})
				.join(' | ') +
			' |';

		const dataRows = cells.map(
			(row) => '| ' + row.map((c) => escapeCell(c)).join(' | ') + ' |'
		);

		return [headerRow, separatorRow, ...dataRows].join('\n');
	}
}
