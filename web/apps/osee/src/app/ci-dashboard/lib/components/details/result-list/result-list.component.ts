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
	inject,
	output,
} from '@angular/core';
import { AsyncPipe, NgClass } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { HeaderService } from '@osee/shared/services';
import { CiDetailsService } from '../../../services/ci-details.service';
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
	template: `@if (scriptResults | async; as _results) {
		<div class="mat-elevation-z8 tw-max-h-96 tw-w-full tw-overflow-auto">
			<mat-table [dataSource]="_results">
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
							*matCellDef="let result"
							class="tw-align-middle">
							@if (header === 'executionDate') {
								<button
									mat-list-item
									(click)="setResultId(result.id)">
									{{ result[header] }}
								</button>
							} @else if (header === 'failedCount') {
								<div
									class="tw-m-auto tw-h-5 tw-w-5 tw-rounded-full"
									[ngClass]="{
										'tw-bg-accent': result.scriptAborted,
										'tw-bg-warning':
											!result.scriptAborted &&
											result.failedCount > 0,
										'tw-bg-success':
											!result.scriptAborted &&
											result.failedCount === 0,
									}"
									[matTooltip]="
										result.scriptAborted
											? 'Aborted'
											: result.failedCount > 0
												? 'Failed'
												: 'Passed'
									"></div>
							} @else {
								{{ result[header] }}
							}
						</td>
					</ng-container>
				}
				<tr
					mat-header-row
					*matHeaderRowDef="headers; sticky: true"></tr>
				<tr
					mat-row
					*matRowDef="let row; columns: headers; let i = index"
					class="odd:tw-bg-selected-button tw-w-full even:tw-bg-background-background"
					[attr.data-cy]="'script-result-table-row-' + row.name"></tr>
			</mat-table>
		</div>
	}`,
	imports: [
		AsyncPipe,
		NgClass,
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
	],
	changeDetection: ChangeDetectionStrategy.OnPush,
})
export class ResultListComponent {
	ciDetailsService = inject(CiDetailsService);
	headerService = inject(HeaderService);
	resultId = output<`${number}`>();

	scriptResults = this.ciDetailsService.scriptResults;

	setResultId(resId: `${number}`) {
		this.resultId.emit(resId);
	}

	getTableHeaderByName(header: keyof ResultReference) {
		return this.headerService.getHeaderByName(
			scriptResListHeaderDetails,
			header
		);
	}

	headers: (keyof ResultReference)[] = ['executionDate', 'failedCount'];
}
