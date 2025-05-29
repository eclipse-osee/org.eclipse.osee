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
import { AsyncPipe, NgClass } from '@angular/common';
import { HeaderService } from '@osee/shared/services';
import { MatPaginator, PageEvent } from '@angular/material/paginator';
import { FormsModule } from '@angular/forms';
import { CiDetailsListService } from '../../../services/ci-details-list.service';
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
import { Router } from '@angular/router';

@Component({
	selector: 'osee-script-list',
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
		NgClass,
	],
	changeDetection: ChangeDetectionStrategy.OnPush,
	template: ` @if (scriptDefs | async) {
			<div
				class="mat-elevation-z8 tw-max-h-96 tw-w-full tw-overflow-auto">
				<mat-table
					[dataSource]="dataSource"
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
								class="tw-align-middle"
								[ngClass]="{
									'tw-bg-primary-100 dark:tw-bg-primary-700':
										def.id === selectedScript(),
								}">
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
						*matRowDef="let row; columns: headers"
						class="odd:tw-bg-selected-button tw-w-full even:tw-bg-background-background hover:tw-bg-background-app-bar"
						[id]="row.id"
						[attr.data-cy]="
							'script-def-table-row-' + row.name
						"></tr>
				</mat-table>
			</div>
		}
		<mat-paginator
			[pageSizeOptions]="[10, 15, 20, 25, 50, 75, 100, 200, 500]"
			[pageSize]="this.ciDetailsService.currentPageSize()"
			[pageIndex]="this.ciDetailsService.currentPage()"
			(page)="setPage($event)"
			[length]="size()"
			[disabled]="false"></mat-paginator>`,
})
export class ScriptListComponent {
	content = input.required<DefReference[]>();
	size = input.required<number>();

	ciDetailsService = inject(CiDetailsListService);
	headerService = inject(HeaderService);
	router = inject(Router);

	private paginator = viewChild.required(MatPaginator);

	dataSource = new MatTableDataSource<DefReference>();
	private _updateDataSource = effect(() => {
		this.dataSource.data = this.content();
	});

	selectedScript = this.ciDetailsService.ciDefId;

	private _filterEffect = effect(() => {
		this.dataSource.filter = this.ciDetailsService.currentDefFilter();
		const filterValue = this.ciDetailsService.currentDefFilter();
		const tree = this.router.parseUrl(this.router.url);
		tree.queryParams['filter'] = filterValue;
		this.router.navigateByUrl(tree);
	});

	scriptDefs = this.ciDetailsService.scriptDefs.pipe(
		takeUntilDestroyed(),
		tap((defs) => (this.dataSource.data = defs))
	);

	setResultList(defId: string) {
		const tree = this.router.parseUrl(this.router.url);
		if (!defId || defId === '' || defId === '-1') {
			delete tree.queryParams['script'];
		} else {
			tree.queryParams['script'] = defId;
		}
		this.router.navigateByUrl(tree);
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
