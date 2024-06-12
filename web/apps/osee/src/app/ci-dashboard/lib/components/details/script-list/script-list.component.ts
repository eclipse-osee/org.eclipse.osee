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
	Output,
	inject,
	output,
	signal,
} from '@angular/core';
import { NgFor, NgIf, AsyncPipe } from '@angular/common';
import { HeaderService } from '@osee/shared/services';
import { MatFormField } from '@angular/material/form-field';
import { MatPaginator, PageEvent } from '@angular/material/paginator';
import { MatDialogModule } from '@angular/material/dialog';
import { MatDivider } from '@angular/material/divider';
import { MatList } from '@angular/material/list';
import { FormsModule } from '@angular/forms';
import { MatInput } from '@angular/material/input';
import { MatIcon } from '@angular/material/icon';
import { CiDetailsService } from '../../../services/ci-details.service';
import { Subject } from 'rxjs';
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
} from '@angular/material/table';
import { MatTooltip } from '@angular/material/tooltip';

@Component({
	selector: 'osee-script-list',
	standalone: true,
	template: `<ng-container *ngIf="scriptDefs | async as _defs">
		<div class="mat-elevation-z8 tw-max-h-96 tw-overflow-scroll">
			<mat-table [dataSource]="_defs">
				<ng-container
					[matColumnDef]="header"
					*ngFor="let header of headers">
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
						class="tw-align-middle focus:tw-bg-blue-300">
						<button
							mat-button
							(click)="setResultList(def.id)">
							{{ def[header] }}
						</button>
					</td>
				</ng-container>
				<tr
					mat-header-row
					*matHeaderRowDef="headers; sticky: true"></tr>
				<tr
					mat-row
					*matRowDef="let row; columns: headers; let i = index"
					class="odd:tw-bg-selected-button even:tw-bg-background-background"
					[attr.data-cy]="'script-def-table-row-' + row.name"></tr>
			</mat-table>
		</div>
		<mat-paginator
			[pageSizeOptions]="[10, 15, 20, 25, 50, 75, 100, 200, 500]"
			[pageSize]="currentPageSize | async"
			[pageIndex]="currentPage | async"
			(page)="setPage($event)"
			[length]="scriptCount | async"
			[disabled]="false"></mat-paginator>
	</ng-container>`,
	imports: [
		AsyncPipe,
		NgFor,
		NgIf,
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
		MatList,
		MatDivider,
		MatDialogModule,
		MatPaginator,
		MatInput,
		MatFormField,
		MatIcon,
	],
	changeDetection: ChangeDetectionStrategy.OnPush,
})
export class ScriptListComponent {
	ciDetailsService = inject(CiDetailsService);
	headerService = inject(HeaderService);

	scriptDefs = this.ciDetailsService.scriptDefs;
	scriptCount = this.ciDetailsService.scriptDefCount;
	currentPage = this.ciDetailsService.currentPage;
	currentPageSize = this.ciDetailsService.currentPageSize;

	filter = output();
	_filterChange = new Subject<string>();
	@Output() filterChange = this._filterChange;
	selectDef = this.ciDetailsService.ciDefId;

	setResultList(defId: string) {
		this.ciDetailsService.CiDefId = defId;
	}

	updateFilter(f: string) {
		this._filterChange.next(f);
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
