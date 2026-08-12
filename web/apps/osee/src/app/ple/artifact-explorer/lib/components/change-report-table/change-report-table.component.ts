/*********************************************************************
 * Copyright (c) 2022 Boeing
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
	signal,
} from '@angular/core';
import { toObservable, toSignal } from '@angular/core/rxjs-interop';
import { FormsModule } from '@angular/forms';
import { MatIconButton } from '@angular/material/button';
import { MatIcon } from '@angular/material/icon';
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
import { of, shareReplay, switchMap } from 'rxjs';
import { ChangeReportService } from './services/change-report.service';

@Component({
	selector: 'osee-change-report-table',
	imports: [
		FormsModule,
		MatIconButton,
		MatIcon,
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
		@if (branchName()) {
			<div class="tw-px-6 tw-pb-2">
				<p class="tw-text-sm">
					Showing changes made to
					<span class="tw-font-bold">{{ branchName() }}</span>
					compared to parent
					<span class="tw-font-bold">{{ parentBranchName() }}</span>
				</p>
			</div>
		}

		<div class="tw-px-6">
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
						{{ totalCount() }} result(s)
					</span>
				</div>

				<!-- Table -->
				<table
					mat-table
					[dataSource]="entries()"
					class="tw-w-full">
					<ng-container matColumnDef="ids">
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
							{{ entry.ids }}
						</td>
					</ng-container>
					<ng-container matColumnDef="names">
						<th
							mat-header-cell
							*matHeaderCellDef
							class="tw-text-xs tw-font-bold tw-uppercase">
							Name
						</th>
						<td
							mat-cell
							*matCellDef="let entry"
							class="tw-text-sm">
							{{ entry.names }}
						</td>
					</ng-container>
					<ng-container matColumnDef="itemType">
						<th
							mat-header-cell
							*matHeaderCellDef
							class="tw-text-xs tw-font-bold tw-uppercase">
							Item Type
						</th>
						<td
							mat-cell
							*matCellDef="let entry"
							class="tw-text-sm">
							{{ entry.itemType }}
						</td>
					</ng-container>
					<ng-container matColumnDef="itemKind">
						<th
							mat-header-cell
							*matHeaderCellDef
							class="tw-text-xs tw-font-bold tw-uppercase">
							Item Kind
						</th>
						<td
							mat-cell
							*matCellDef="let entry"
							class="tw-text-sm">
							{{ entry.itemKind }}
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
							*matCellDef="let entry"
							class="tw-text-sm">
							{{ entry.changeType }}
						</td>
					</ng-container>
					<ng-container matColumnDef="isValue">
						<th
							mat-header-cell
							*matHeaderCellDef
							class="tw-text-xs tw-font-bold tw-uppercase">
							Is Value
						</th>
						<td
							mat-cell
							*matCellDef="let entry"
							class="tw-max-w-[200px] tw-truncate tw-text-sm"
							[matTooltip]="entry.isValue">
							{{ entry.isValue }}
						</td>
					</ng-container>
					<ng-container matColumnDef="wasValue">
						<th
							mat-header-cell
							*matHeaderCellDef
							class="tw-text-xs tw-font-bold tw-uppercase">
							Was Value
						</th>
						<td
							mat-cell
							*matCellDef="let entry"
							class="tw-max-w-[200px] tw-truncate tw-text-sm"
							[matTooltip]="entry.wasValue">
							{{ entry.wasValue }}
						</td>
					</ng-container>
					<tr
						mat-header-row
						*matHeaderRowDef="displayedColumns"></tr>
					<tr
						mat-row
						*matRowDef="let row; columns: displayedColumns"
						class="odd:tw-bg-background-selected-button even:tw-bg-background-background"></tr>
				</table>

				<!-- Paginator -->
				<mat-paginator
					[length]="totalCount()"
					[pageSize]="pageSize()"
					[pageSizeOptions]="[10, 25, 50, 100]"
					[pageIndex]="pageIndex()"
					(page)="onPageChange($event)"
					showFirstLastButtons />
			</div>
		</div>
	`,
})
export class ChangeReportTableComponent {
	private crService = inject(ChangeReportService);

	branchId = input.required<string>();

	protected readonly filterText = signal('');
	protected readonly pageSize = signal(10);
	protected readonly pageIndex = signal(0);

	protected readonly displayedColumns = [
		'ids',
		'names',
		'itemType',
		'itemKind',
		'changeType',
		'isValue',
		'wasValue',
	];

	private branchId$ = toObservable(this.branchId);

	private branchInfo = toSignal(
		this.branchId$.pipe(
			switchMap((id) => {
				if (!id || id === '0') {
					return of(null);
				}
				return this.crService.getBranchInfo(id);
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
		toObservable(computed(() => this.parentBranchId())).pipe(
			switchMap((id) => {
				if (!id || id === '0') {
					return of(null);
				}
				return this.crService.getBranchInfo(id);
			}),
			shareReplay(1)
		),
		{ initialValue: null }
	);

	protected parentBranchName = computed(
		() => this.parentBranchInfo()?.name ?? ''
	);

	private queryParams = computed(() => ({
		branchId: this.branchInfo()?.id ?? '',
		parentBranchId: this.parentBranchId(),
		filter: this.filterText(),
		pageNum: this.pageIndex(),
		pageSize: this.pageSize(),
	}));

	protected entries = toSignal(
		toObservable(this.queryParams).pipe(
			switchMap(
				({ branchId, parentBranchId, filter, pageNum, pageSize }) => {
					if (
						!branchId ||
						!parentBranchId ||
						branchId === '0' ||
						parentBranchId === '0'
					) {
						return of([]);
					}
					return this.crService.getFilteredPaginatedChanges(
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
		{ initialValue: [] }
	);

	private countQuery = computed(() => ({
		branchId: this.branchInfo()?.id ?? '',
		parentBranchId: this.parentBranchId(),
		filter: this.filterText(),
	}));

	protected totalCount = toSignal(
		toObservable(this.countQuery).pipe(
			switchMap(({ branchId, parentBranchId, filter }) => {
				if (
					!branchId ||
					!parentBranchId ||
					branchId === '0' ||
					parentBranchId === '0'
				) {
					return of(0);
				}
				return this.crService.getFilteredPaginatedChangesCount(
					branchId,
					parentBranchId,
					filter
				);
			}),
			shareReplay(1)
		),
		{ initialValue: 0 }
	);

	protected onFilterChange(value: string) {
		this.filterText.set(value);
		this.pageIndex.set(0);
	}

	protected onPageChange(event: PageEvent) {
		this.pageIndex.set(event.pageIndex);
		this.pageSize.set(event.pageSize);
	}
}
