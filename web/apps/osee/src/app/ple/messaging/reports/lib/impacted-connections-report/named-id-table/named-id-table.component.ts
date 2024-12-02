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
	signal,
} from '@angular/core';
import { FormsModule } from '@angular/forms';
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
	MatTableDataSource,
} from '@angular/material/table';
import { MatTooltip } from '@angular/material/tooltip';
import { NamedId } from '@osee/shared/types';

@Component({
	selector: 'osee-named-id-table',
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
		FormsModule,
	],
	template: ` <mat-table [dataSource]="dataSource">
		@for (column of displayedColumns(); track column; let idx = $index) {
			<ng-container [matColumnDef]="column">
				<mat-header-cell
					*matHeaderCellDef
					[matTooltip]="columnMetaData()[idx].tooltip">
					{{ columnMetaData()[idx].displayName }}
				</mat-header-cell>
				<mat-cell *matCellDef="let row">
					{{ row[column] }}
				</mat-cell>
			</ng-container>
		}
		<mat-header-row
			*matHeaderRowDef="displayedColumns(); sticky: true"
			class="tw-text-primary-500"></mat-header-row>
		<mat-row
			*matRowDef="let row; columns: displayedColumns()"
			class="even:tw-bg-background-background hover:tw-bg-background-hover hover:tw-font-extrabold"></mat-row>
	</mat-table>`,
	changeDetection: ChangeDetectionStrategy.OnPush,
})
export class NamedIdTableComponent {
	content = input.required<NamedId[]>();
	protected dataSource = new MatTableDataSource<NamedId>();
	private _updateDataSource = effect(() => {
		this.dataSource.data = this.content();
	});

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
	]);
	protected displayedColumns = computed(() =>
		this.columnMetaData().map((v) => v.key)
	);
}
