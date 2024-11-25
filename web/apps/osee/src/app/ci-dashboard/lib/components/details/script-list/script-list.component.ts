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
	effect,
	inject,
	input,
	viewChild,
} from '@angular/core';
import { AsyncPipe } from '@angular/common';
import { HeaderService } from '@osee/shared/services';
import { MatPaginator, PageEvent } from '@angular/material/paginator';
import { FormsModule } from '@angular/forms';
import { CiDetailsService } from '../../../services/ci-details.service';
import { DefReference } from '../../../types';
import { scriptDefListHeaderDetails } from '../../../table-headers/script-headers';
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
import { takeUntilDestroyed } from '@angular/core/rxjs-interop';
import { tap } from 'rxjs';

@Component({
	selector: 'osee-script-list',
	template: ` @if (scriptDefs | async) {
			<div
				class="mat-elevation-z8 tw-max-h-96 tw-w-full tw-overflow-auto">
				<mat-table
					[dataSource]="datasource"
					[fixedLayout]="true"
					class="tw-w-full">
					@for (header of headers; track $index) {
						<ng-container [matColumnDef]="header">
							<th
								mat-header-cell
								*matHeaderCellDef
								class="tw-text-center tw-align-middle tw-font-medium tw-text-primary-600"
								[matTooltip]="
									(getTableHeaderByName(header) | async)
										?.description || ''
								">
								{{
									(getTableHeaderByName(header) | async)
										?.humanReadable || ''
								}}
							</th>
							<td
								mat-cell
								*matCellDef="let def"
								class="tw-align-middle">
								<button
									mat-button
									(click)="setResultList(def.id)">
									{{ def[header] }}
								</button>
							</td>
						</ng-container>
					}
					<tr
						mat-header-row
						*matHeaderRowDef="headers; sticky: true"
						class="tw-w-full"></tr>
					<tr
						mat-row
						*matRowDef="let row; columns: headers; let i = index"
						class="odd:tw-bg-selected-button tw-w-full even:tw-bg-background-background"
						[attr.data-cy]="
							'script-def-table-row-' + row.name
						"></tr>
				</mat-table>
			</div>
		}
		<mat-paginator
			[pageSizeOptions]="[10, 25, 50, 100, 200, 500]"
			[pageSize]="100"
			[length]="datasource.data.length"
			[disabled]="false"></mat-paginator>`,
	imports: [
		AsyncPipe,
		FormsModule,
		MatTable,
		MatColumnDef,
		MatHeaderCell,
		MatHeaderCellDef,
		MatTooltip,
		MatCell,
		MatCellDef,
		MatHeaderRow,
		MatHeaderRowDef,
		MatRow,
		MatRowDef,
		MatPaginator,
	],
	changeDetection: ChangeDetectionStrategy.OnPush,
})
export class ScriptListComponent {
	filterText = input<string>('');

	ciDetailsService = inject(CiDetailsService);
	headerService = inject(HeaderService);

	private paginator = viewChild.required(MatPaginator);

	datasource = new MatTableDataSource<DefReference>();

	private _filterEffect = effect(
		() => (this.datasource.filter = this.filterText())
	);

	private _paginatorEffect = effect(
		() => (this.datasource.paginator = this.paginator())
	);

	scriptDefs = this.ciDetailsService.scriptDefs.pipe(
		takeUntilDestroyed(),
		tap((defs) => (this.datasource.data = defs))
	);

	setResultList(defId: string) {
		this.ciDetailsService.CiDefId = defId;
	}

	getTableHeaderByName(header: keyof DefReference) {
		return this.headerService.getHeaderByName(
			scriptDefListHeaderDetails,
			header
		);
	}

	headers: (keyof DefReference)[] = ['name'];

	setPage(event: PageEvent) {
		this.ciDetailsService.pageSize = event.pageSize;
		this.ciDetailsService.page = event.pageIndex;
	}
}
