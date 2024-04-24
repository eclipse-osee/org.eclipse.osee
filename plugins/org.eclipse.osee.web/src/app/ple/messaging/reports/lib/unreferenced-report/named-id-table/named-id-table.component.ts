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
	effect,
	input,
	model,
	output,
	signal,
} from '@angular/core';
import { FormsModule } from '@angular/forms';
import { MatIconButton } from '@angular/material/button';
import { MatIcon } from '@angular/material/icon';
import { MatFormField, MatInput, MatLabel } from '@angular/material/input';
import { MatPaginator, PageEvent } from '@angular/material/paginator';
import {
	MatTable,
	MatHeaderRow,
	MatHeaderRowDef,
	MatRow,
	MatRowDef,
	MatColumnDef,
	MatHeaderCell,
	MatHeaderCellDef,
	MatCell,
	MatCellDef,
	MatTableDataSource,
	MatFooterRow,
	MatFooterRowDef,
	MatFooterCell,
	MatFooterCellDef,
} from '@angular/material/table';
import { MatTooltip } from '@angular/material/tooltip';
import { NamedId } from '@osee/shared/types';

@Component({
	selector: 'osee-named-id-table',
	standalone: true,
	imports: [
		MatTable,
		MatHeaderRow,
		MatHeaderRowDef,
		MatRow,
		MatRowDef,
		MatColumnDef,
		MatHeaderCell,
		MatHeaderCellDef,
		MatCell,
		MatCellDef,
		MatTooltip,
		MatFooterRow,
		MatFooterRowDef,
		MatFooterCell,
		MatFooterCellDef,
		MatIcon,
		MatIconButton,
		MatPaginator,
		FormsModule,
		MatFormField,
		MatInput,
		MatLabel,
	],
	template: `<mat-form-field class="tw-w-full">
			<mat-label>Type to search</mat-label>
			<input
				matInput
				type="text"
				#input
				[(ngModel)]="filter" />
		</mat-form-field>
		<mat-table [dataSource]="dataSource">
			@for (
				column of displayedColumns();
				track column;
				let idx = $index
			) {
				<ng-container [matColumnDef]="column">
					<mat-header-cell
						*matHeaderCellDef
						[matTooltip]="columnMetaData()[idx].tooltip">
						{{ columnMetaData()[idx].displayName }}
					</mat-header-cell>
					<mat-cell *matCellDef="let row">
						{{ row[column] }}
						@if (
							columnMetaData()[idx].hasInteraction && inEditMode()
						) {
							<button
								mat-icon-button
								(click)="deleteValue(row)"
								[matTooltip]="'Delete ' + row.name">
								<mat-icon color="warn">delete</mat-icon>
							</button>
						}
					</mat-cell>
					<mat-footer-cell *matFooterCellDef>
						@if (
							columnMetaData()[idx].hasFooterInteraction &&
							inEditMode()
						) {
							<button
								mat-icon-button
								(click)="deleteAll()"
								matTooltip="Delete all visible">
								<mat-icon color="warn">delete</mat-icon>
							</button>
						}
					</mat-footer-cell>
				</ng-container>
			}
			<mat-header-row
				*matHeaderRowDef="displayedColumns(); sticky: true"
				class="tw-text-primary-500"></mat-header-row>
			<mat-row
				*matRowDef="let row; columns: displayedColumns()"
				class="
			 even:tw-bg-background-background hover:tw-bg-background-hover hover:tw-font-extrabold
			"></mat-row>
			<mat-footer-row
				*matFooterRowDef="displayedColumns()"></mat-footer-row>
		</mat-table>
		<mat-paginator
			[pageSizeOptions]="[
				10, 15, 20, 25, 50, 75, 100, 200, 250, 500, 1000, 1500, 2000,
				2500, 5000
			]"
			[pageIndex]="currentPage()"
			(page)="setPage($event)"
			[length]="size()"
			[disabled]="false"></mat-paginator>`,
	changeDetection: ChangeDetectionStrategy.OnPush,
})
export class NamedIdTableComponent {
	content = input.required<NamedId[]>();
	currentPage = model.required<number>();
	size = input.required<number>();
	inEditMode = input.required<boolean>();

	filter = model.required<string>();

	currentPageSize = output<number>();
	protected dataSource = new MatTableDataSource<NamedId>();
	private _updateDataSource = effect(() => {
		this.dataSource.data = this.content();
	});

	itemsToDelete = output<NamedId | NamedId[]>();

	protected columnMetaData = signal<
		{
			key: keyof NamedId | 'delete';
			tooltip: string;
			displayName: string;
			hasInteraction: boolean;
			hasFooterInteraction: boolean;
		}[]
	>([
		{
			key: 'id',
			tooltip: 'Artifact Id of Artifact',
			displayName: 'Id',
			hasInteraction: false,
			hasFooterInteraction: false,
		},
		{
			key: 'name',
			tooltip: 'Name of Artifact',
			displayName: 'Name',
			hasInteraction: false,
			hasFooterInteraction: false,
		},
		{
			key: 'delete',
			tooltip: '',
			displayName: '',
			hasInteraction: true,
			hasFooterInteraction: true,
		},
	]);
	protected displayedColumns = computed(() =>
		this.columnMetaData().map((v) => v.key)
	);

	deleteAll() {
		this.itemsToDelete.emit(this.content());
	}
	deleteValue(value: NamedId) {
		this.itemsToDelete.emit(value);
	}

	setPage(event: PageEvent) {
		this.currentPage.set(event.pageIndex);
		this.currentPageSize.emit(event.pageSize);
	}
}
