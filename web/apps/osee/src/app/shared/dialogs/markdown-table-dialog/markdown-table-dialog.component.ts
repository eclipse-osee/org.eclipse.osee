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
import { MatTooltip } from '@angular/material/tooltip';
import { MatFormField, MatLabel } from '@angular/material/form-field';
import { MatInput } from '@angular/material/input';

export type ColumnAlignment = 'left' | 'center' | 'right';

export type MarkdownTableDialogData = {
	readonly rows: number;
	readonly cols: number;
	readonly headers: string[];
	readonly cells: string[][];
	readonly alignments: ColumnAlignment[];
	readonly isEdit: boolean;
};

export type MarkdownTableDialogResult = {
	readonly markdown: string;
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

	protected readonly isEdit = this.data.isEdit;
	protected readonly headers = signal<string[]>([...this.data.headers]);
	protected readonly cells = signal<string[][]>(
		this.data.cells.map((row) => [...row])
	);
	protected readonly alignments = signal<ColumnAlignment[]>([
		...this.data.alignments,
	]);

	protected readonly colCount = computed(() => this.headers().length);
	protected readonly rowCount = computed(() => this.cells().length);

	protected readonly canRemoveColumn = computed(() => this.colCount() > 1);
	protected readonly canRemoveRow = computed(() => this.rowCount() > 1);

	protected onColCountChange(event: Event): void {
		const value = parseInt(
			(event.target as HTMLInputElement).value,
			10
		);
		if (isNaN(value) || value < 1) {
			return;
		}
		const newCols = Math.min(50, value);
		this.resizeTable(newCols, this.rowCount());
	}

	protected onRowCountChange(event: Event): void {
		const value = parseInt(
			(event.target as HTMLInputElement).value,
			10
		);
		if (isNaN(value) || value < 1) {
			return;
		}
		const newRows = Math.min(100, value);
		this.resizeTable(this.colCount(), newRows);
	}

	private resizeTable(newCols: number, newRows: number): void {
		const currentHeaders = this.headers();
		const currentCells = this.cells();
		const currentAlignments = this.alignments();

		// Resize headers
		const resizedHeaders = Array.from({ length: newCols }, (_, i) =>
			i < currentHeaders.length ? currentHeaders[i] : ''
		);
		this.headers.set(resizedHeaders);

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

	protected updateCell(rowIndex: number, colIndex: number, value: string): void {
		this.cells.update((rows) => {
			const copy = rows.map((r) => [...r]);
			copy[rowIndex][colIndex] = value;
			return copy;
		});
	}

	protected cycleAlignment(colIndex: number): void {
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
		this.headers.update((h) => [...h, '']);
		this.cells.update((rows) => rows.map((r) => [...r, '']));
		this.alignments.update((a) => [...a, 'left']);
	}

	protected insertColumnAt(colIndex: number): void {
		this.headers.update((h) => {
			const copy = [...h];
			copy.splice(colIndex, 0, '');
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
		this.headers.update((h) => h.filter((_, i) => i !== colIndex));
		this.cells.update((rows) =>
			rows.map((r) => r.filter((_, i) => i !== colIndex))
		);
		this.alignments.update((a) => a.filter((_, i) => i !== colIndex));
	}

	protected addRow(): void {
		const emptyCells = Array(this.colCount()).fill('');
		this.cells.update((rows) => [...rows, emptyCells]);
	}

	protected insertRowAt(rowIndex: number): void {
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
		this.cells.update((rows) => rows.filter((_, i) => i !== rowIndex));
	}

	protected submitTable(): void {
		const markdown = this.generateMarkdown();
		this.dialogRef.close({ markdown });
	}

	protected cancelDialog(): void {
		this.dialogRef.close(undefined);
	}

	private generateMarkdown(): string {
		const headers = this.headers();
		const alignments = this.alignments();
		const cells = this.cells();

		const headerRow = '| ' + headers.map((h) => h || ' ').join(' | ') + ' |';

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
			(row) => '| ' + row.map((c) => c || ' ').join(' | ') + ' |'
		);

		return [headerRow, separatorRow, ...dataRows].join('\n');
	}
}
