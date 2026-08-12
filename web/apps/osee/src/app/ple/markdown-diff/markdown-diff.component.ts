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
import { toObservable, toSignal } from '@angular/core/rxjs-interop';
import { FormsModule } from '@angular/forms';
import { MatIconButton } from '@angular/material/button';
import { MatIcon } from '@angular/material/icon';
import { MatProgressSpinner } from '@angular/material/progress-spinner';
import { MatPaginator, PageEvent } from '@angular/material/paginator';
import { MatTooltip } from '@angular/material/tooltip';
import {
	MatCell,
	MatCellDef,
	MatColumnDef,
	MatHeaderCell,
	MatHeaderCellDef,
	MatHeaderRow,
	MatHeaderRowDef,
	MatRow,
	MatRowDef,
	MatTable,
} from '@angular/material/table';
import { UiService, BranchRoutedUIService } from '@osee/shared/services';
import {
	debounceTime,
	distinctUntilChanged,
	firstValueFrom,
	of,
	shareReplay,
	switchMap,
} from 'rxjs';
import { ActraPageTitleComponent } from '../../actra/actra-page-title/actra-page-title.component';
import { MarkdownDiffEntryComponent } from './markdown-diff-entry/markdown-diff-entry.component';
import { MarkdownDiffService } from './services/markdown-diff.service';
import { ExportFormat, MarkdownDiffEntry } from './types/markdown-diff';
import { generateMarkdownExport, generateHtmlExport } from './utils/export';
import { MatMenu, MatMenuTrigger, MatMenuItem } from '@angular/material/menu';
import { BranchPickerDialogComponent } from '../../shared/dialogs/branch-picker-dialog/branch-picker-dialog.component';
import { MatDialog } from '@angular/material/dialog';

@Component({
	selector: 'osee-markdown-diff',
	imports: [
		ActraPageTitleComponent,
		MarkdownDiffEntryComponent,
		FormsModule,
		MatIconButton,
		MatIcon,
		MatProgressSpinner,
		MatPaginator,
		MatTooltip,
		MatTable,
		MatColumnDef,
		MatHeaderCell,
		MatHeaderCellDef,
		MatCell,
		MatCellDef,
		MatHeaderRow,
		MatHeaderRowDef,
		MatRow,
		MatRowDef,
		MatMenu,
		MatMenuTrigger,
		MatMenuItem,
	],
	changeDetection: ChangeDetectionStrategy.OnPush,
	styles: `
		mat-paginator {
			--mdc-outlined-text-field-outline-color: transparent;
			--mdc-outlined-text-field-hover-outline-color: transparent;
			--mdc-outlined-text-field-focus-outline-color: transparent;
			--mat-form-field-outlined-outline-color: transparent;
		}
	`,
	template: `
		<div class="tw-flex tw-items-center tw-pr-6">
			<osee-actra-page-title
				title="Markdown Change Report"
				icon="difference" />
			<span class="tw-flex-1"></span>
			<button
				mat-icon-button
				(click)="openBranchPicker()"
				matTooltip="Select Branch">
				<mat-icon>merge_type</mat-icon>
			</button>
			<button
				mat-icon-button
				[matMenuTriggerFor]="exportMenu"
				[disabled]="filteredCount() === 0 || exporting()"
				[matTooltip]="
					exporting()
						? 'Generating export…'
						: 'Export report as Markdown or HTML'
				">
				@if (exporting()) {
					<mat-spinner diameter="20" />
				} @else {
					<mat-icon>download</mat-icon>
				}
			</button>
			<mat-menu #exportMenu="matMenu">
				<button
					mat-menu-item
					(click)="exportReport('md')">
					<mat-icon>description</mat-icon>
					<span>Export as Markdown (.md)</span>
				</button>
				<button
					mat-menu-item
					(click)="exportReport('html')">
					<mat-icon>code</mat-icon>
					<span>Export as HTML (.html)</span>
				</button>
			</mat-menu>
		</div>

		@if (!branchId()) {
			<div class="tw-p-6 tw-text-sm tw-opacity-50">
				Click the branch icon in the top-right to select a branch.
			</div>
		} @else {
			@if (branchName()) {
				<div class="tw-px-6">
					<p class="tw-text-sm">
						Showing markdown attribute differences on
						<span class="tw-font-bold">{{ branchName() }}</span>
						compared to parent
						<span class="tw-font-bold">{{
							parentBranchName()
						}}</span>
					</p>
				</div>
			}

			<!-- Summary section -->
			<div class="tw-m-6 tw-flex tw-items-center tw-gap-3">
				<div class="tw-h-px tw-flex-1 tw-bg-foreground-divider"></div>
				<span
					class="tw-text-lg tw-font-bold tw-uppercase tw-tracking-wide">
					Summary
				</span>
				<div class="tw-h-px tw-flex-1 tw-bg-foreground-divider"></div>
			</div>

			<div class="tw-px-6">
				<!-- Table with integrated filter -->
				<div
					class="tw-overflow-hidden tw-rounded-lg tw-border tw-border-foreground-divider">
					<!-- Filter row -->
					<div
						class="tw-flex tw-h-12 tw-items-center tw-gap-3 tw-border-b tw-border-foreground-divider tw-bg-background-card tw-px-4">
						<mat-icon>search</mat-icon>
						<input
							class="tw-flex-1 tw-border-none tw-bg-transparent tw-text-sm tw-outline-none"
							[ngModel]="filterText()"
							(ngModelChange)="onFilterChange($event)"
							placeholder="Filter by name, ID, or change type..." />
						@if (filterText()) {
							<button
								mat-icon-button
								(click)="onFilterChange('')"
								matTooltip="Clear filter">
								<mat-icon class="tw-text-base">close</mat-icon>
							</button>
						}
						<span class="tw-text-xs">
							{{ filteredCount() }} result(s)
						</span>
					</div>

					<!-- Table -->
					<table
						mat-table
						[dataSource]="allEntries()"
						class="tw-w-full">
						<ng-container matColumnDef="artifactId">
							<th
								mat-header-cell
								*matHeaderCellDef
								class="tw-text-xs tw-font-bold tw-uppercase">
								Artifact ID
							</th>
							<td
								mat-cell
								*matCellDef="let entry"
								class="tw-text-sm">
								{{ entry.artifactId }}
							</td>
						</ng-container>
						<ng-container matColumnDef="artifactName">
							<th
								mat-header-cell
								*matHeaderCellDef
								class="tw-text-xs tw-font-bold tw-uppercase">
								Artifact Name
							</th>
							<td
								mat-cell
								*matCellDef="let entry"
								class="tw-text-sm">
								{{ entry.artifactName }}
							</td>
						</ng-container>
						<ng-container matColumnDef="changeType">
							<th
								mat-header-cell
								*matHeaderCellDef
								class="tw-text-xs tw-font-bold tw-uppercase">
								Change Type
							</th>
							<td
								mat-cell
								*matCellDef="let entry">
								<span
									class="tw-rounded tw-px-2 tw-py-0.5 tw-text-xs tw-font-bold"
									[class]="badgeClass(entry)">
									{{ entry.changeDescription }}
								</span>
							</td>
						</ng-container>
						<ng-container matColumnDef="actions">
							<th
								mat-header-cell
								*matHeaderCellDef
								class="tw-w-12"></th>
							<td
								mat-cell
								*matCellDef="let entry; let i = index"
								class="tw-w-12">
								<button
									mat-icon-button
									(click)="
										scrollToEntry(i);
										$event.stopPropagation()
									"
									matTooltip="Scroll to diff">
									<mat-icon class="tw-text-base"
										>visibility</mat-icon
									>
								</button>
							</td>
						</ng-container>
						<tr
							mat-header-row
							*matHeaderRowDef="displayedColumns"></tr>
						<tr
							mat-row
							*matRowDef="
								let row;
								columns: displayedColumns;
								let i = index
							"
							class="tw-cursor-pointer tw-transition-colors odd:tw-bg-background-selected-button even:tw-bg-background-background hover:tw-bg-background-hover"
							(click)="scrollToEntry(i)"></tr>
					</table>

					<!-- Paginator -->
					<mat-paginator
						[length]="filteredCount()"
						[pageSize]="pageSize()"
						[pageSizeOptions]="[5, 10, 25, 50]"
						[pageIndex]="pageIndex()"
						(page)="onPageChange($event)"
						showFirstLastButtons />
				</div>
			</div>

			<!-- Differences section -->
			<div class="tw-m-6 tw-flex tw-items-center tw-gap-3">
				<div class="tw-h-px tw-flex-1 tw-bg-foreground-divider"></div>
				<span
					class="tw-text-lg tw-font-bold tw-uppercase tw-tracking-wide">
					Differences
				</span>
				<div class="tw-h-px tw-flex-1 tw-bg-foreground-divider"></div>
			</div>

			<div class="tw-px-6 tw-pb-6">
				@for (entry of allEntries(); track $index) {
					<div [id]="'diff-' + $index">
						<osee-markdown-diff-entry [entry]="entry" />
					</div>
				}
			</div>
		}
	`,
})
export class MarkdownDiffComponent {
	private uiService = inject(UiService);
	private mdDiffService = inject(MarkdownDiffService);
	private dialog = inject(MatDialog);
	private _branchRouter = inject(BranchRoutedUIService);

	protected readonly filterText = signal('');
	protected readonly pageSize = signal(10);
	protected readonly pageIndex = signal(0);
	protected readonly exporting = signal(false);

	protected readonly displayedColumns = [
		'artifactId',
		'artifactName',
		'changeType',
		'actions',
	];

	private branchId$ = this.uiService.id;

	protected branchId = toSignal(this.branchId$, { initialValue: '' });

	private branchInfo = toSignal(
		this.branchId$.pipe(
			switchMap((id) => {
				if (!id || id === '0') {
					return of(null);
				}
				return this.mdDiffService.getBranchInfo(id);
			}),
			shareReplay(1)
		),
		{ initialValue: null }
	);

	protected branchName = computed(() => this.branchInfo()?.name ?? '');

	private parentBranchId = computed(
		() => this.branchInfo()?.parentBranch?.id ?? ''
	);

	private parentBranchInfo = toSignal(
		toObservable(this.parentBranchId).pipe(
			switchMap((id) => {
				if (!id || id === '0') {
					return of(null);
				}
				return this.mdDiffService.getBranchInfo(id);
			}),
			shareReplay(1)
		),
		{ initialValue: null }
	);

	protected parentBranchName = computed(
		() => this.parentBranchInfo()?.name ?? ''
	);

	/** Params for the paged entry request (includes page position). */
	private pageQuery = computed(() => ({
		branchId: this.branchInfo()?.id ?? '',
		parentBranchId: this.parentBranchId(),
		filter: this.filterText(),
		pageNum: this.pageIndex(),
		pageSize: this.pageSize(),
	}));

	/**
	 * The current page of markdown changes, fetched from the server. The server
	 * applies the attribute-type + free-text filtering and the paging, so the
	 * client only holds one page at a time. Debounced so rapid filter typing
	 * doesn't spam the server.
	 */
	protected allEntries = toSignal(
		toObservable(this.pageQuery).pipe(
			debounceTime(300),
			distinctUntilChanged(
				(a, b) =>
					a.branchId === b.branchId &&
					a.parentBranchId === b.parentBranchId &&
					a.filter === b.filter &&
					a.pageNum === b.pageNum &&
					a.pageSize === b.pageSize
			),
			switchMap(
				({ branchId, parentBranchId, filter, pageNum, pageSize }) => {
					if (
						!branchId ||
						!parentBranchId ||
						branchId === '0' ||
						parentBranchId === '0'
					) {
						return of([] as MarkdownDiffEntry[]);
					}
					return this.mdDiffService.getMarkdownChanges(
						branchId,
						parentBranchId,
						filter,
						pageNum,
						pageSize
					);
				}
			),
			shareReplay(1)
		),
		{ initialValue: [] as MarkdownDiffEntry[] }
	);

	/** Params for the total-count request (no page position). */
	private countQuery = computed(() => ({
		branchId: this.branchInfo()?.id ?? '',
		parentBranchId: this.parentBranchId(),
		filter: this.filterText(),
	}));

	/**
	 * Total matching entries across all pages, used for the paginator length.
	 * A separate server request from the page fetch so the length reflects the
	 * full filtered set rather than the current page.
	 */
	protected filteredCount = toSignal(
		toObservable(this.countQuery).pipe(
			debounceTime(300),
			distinctUntilChanged(
				(a, b) =>
					a.branchId === b.branchId &&
					a.parentBranchId === b.parentBranchId &&
					a.filter === b.filter
			),
			switchMap(({ branchId, parentBranchId, filter }) => {
				if (
					!branchId ||
					!parentBranchId ||
					branchId === '0' ||
					parentBranchId === '0'
				) {
					return of(0);
				}
				return this.mdDiffService.getMarkdownChangesCount(
					branchId,
					parentBranchId,
					filter
				);
			}),
			shareReplay(1)
		),
		{ initialValue: 0 }
	);

	protected openBranchPicker() {
		this.dialog.open(BranchPickerDialogComponent, {
			minWidth: '400px',
		});
	}

	protected onFilterChange(value: string) {
		this.filterText.set(value);
		this.pageIndex.set(0);
	}

	protected onPageChange(event: PageEvent) {
		this.pageIndex.set(event.pageIndex);
		this.pageSize.set(event.pageSize);
	}

	protected scrollToEntry(index: number) {
		const el = document.getElementById('diff-' + index);
		if (el) {
			el.scrollIntoView({ behavior: 'smooth', block: 'start' });
			el.classList.add('osee-help-highlight');
			setTimeout(() => {
				el.classList.remove('osee-help-highlight');
			}, 3200);
		}
	}

	protected badgeClass(entry: MarkdownDiffEntry): string {
		if (entry.changeType === 'New') {
			return 'tw-bg-osee-green-8 tw-bg-opacity-20 tw-text-osee-green-10';
		}
		if (entry.changeType.toLowerCase().includes('deleted')) {
			return 'tw-bg-osee-red-8 tw-bg-opacity-20 tw-text-osee-red-10';
		}
		return 'tw-bg-osee-light-blue-8 tw-bg-opacity-20 tw-text-osee-light-blue-10';
	}

	protected async exportReport(format: ExportFormat) {
		if (this.exporting()) {
			return;
		}
		const branchId = this.branchInfo()?.id ?? '';
		const parentBranchId = this.parentBranchId();
		if (!branchId || !parentBranchId) {
			return;
		}
		const branchName = this.branchName();
		const parentBranchName = this.parentBranchName();

		this.exporting.set(true);
		try {
			// Export the full matching set, not just the current page. Since the
			// component now only holds one page, fetch every matching entry from
			// the server (pageSize 0 = all) for the current filter.
			const entries = await firstValueFrom(
				this.mdDiffService.getMarkdownChanges(
					branchId,
					parentBranchId,
					this.filterText(),
					0,
					0
				)
			);
			let content: string;
			let mimeType: string;
			let extension: string;

			// Generation is async + batched so large reports don't lock the UI.
			if (format === 'md') {
				content = await generateMarkdownExport(
					entries,
					branchName,
					parentBranchName
				);
				mimeType = 'text/markdown';
				extension = 'md';
			} else {
				content = await generateHtmlExport(
					entries,
					branchName,
					parentBranchName
				);
				mimeType = 'text/html';
				extension = 'html';
			}

			const blob = new Blob([content], { type: mimeType });
			const url = URL.createObjectURL(blob);
			const a = document.createElement('a');
			a.href = url;
			a.download = `markdown-change-report-${branchName || 'branch'}.${extension}`;
			a.click();
			URL.revokeObjectURL(url);
		} catch (error) {
			this.uiService.ErrorText = `Failed to export report: ${
				error instanceof Error ? error.message : error
			}`;
		} finally {
			this.exporting.set(false);
		}
	}
}
export default MarkdownDiffComponent;
