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
	model,
	signal,
} from '@angular/core';
import { AsyncPipe, NgFor, NgIf } from '@angular/common';
import { MatIcon } from '@angular/material/icon';
import { MatFormField } from '@angular/material/form-field';
import { MatInput } from '@angular/material/input';
import { MatPaginator } from '@angular/material/paginator';
import { MatDialogModule } from '@angular/material/dialog';
import { MatDivider } from '@angular/material/divider';
import { MatList } from '@angular/material/list';
import { FormsModule } from '@angular/forms';
import { HeaderService } from '@osee/shared/services';
import { CiDetailsService } from '../../../services/ci-details.service';
import { Subject } from 'rxjs';
import { ResultReference } from '../../../types';
import { scriptResListHeaderDetails } from '../../../table-headers/script-headers';
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
	selector: 'osee-result-list',
	standalone: true,
	template: `<ng-container *ngIf="scriptResults | async as _results">
		<div class="mat-elevation-z8 tw-max-h-96 tw-overflow-scroll">
			<mat-table [dataSource]="_results">
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
						*matCellDef="let result"
						class="tw-align-middle">
						<ng-container *ngIf="header === 'executionDate'">
							<button
								mat-list-item
								(click)="setResultId(result.id)">
								{{ result[header] }}
							</button>
						</ng-container>
						<ng-container *ngIf="header !== 'executionDate'">
							{{ result[header] }}
						</ng-container>
					</td>
				</ng-container>
				<tr
					mat-header-row
					*matHeaderRowDef="headers; sticky: true"></tr>
				<tr
					mat-row
					*matRowDef="let row; columns: headers; let i = index"
					class="odd:tw-bg-selected-button even:tw-bg-background-background"
					[attr.data-cy]="'script-result-table-row-' + row.name"></tr>
			</mat-table>
		</div>
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
export class ResultListComponent {
	ciDetailsService = inject(CiDetailsService);
	headerService = inject(HeaderService);

	scriptResults = this.ciDetailsService.scriptResults;

	setResultId(resId: string) {
		this.ciDetailsService.CiResultId = resId;
	}

	getTableHeaderByName(header: keyof ResultReference) {
		return this.headerService.getHeaderByName(
			scriptResListHeaderDetails,
			header
		);
	}

	headers: (keyof ResultReference)[] = ['executionDate'];
}
