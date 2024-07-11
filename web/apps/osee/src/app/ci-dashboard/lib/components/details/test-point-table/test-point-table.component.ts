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
} from '@angular/core';
import { AsyncPipe } from '@angular/common';
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
import { HeaderService } from '@osee/shared/services';
import { ResultReference, TestPointReference } from '../../../types';
import { testPointDetails } from '../../../table-headers/test-point-headers';

@Component({
	selector: 'osee-test-point-table',
	standalone: true,
	template: ` <div class="mat-elevation-z8">
		<mat-table [dataSource]="datasource">
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
						*matCellDef="let testPoint"
						class="tw-align-middle">
						<ng-container> {{ testPoint[header] }} </ng-container>
					</td>
				</ng-container>
			}
			<tr
				mat-header-row
				*matHeaderRowDef="headers; sticky: true"></tr>
			<tr
				mat-row
				*matRowDef="let row; columns: headers; let i = index"
				class="odd:tw-bg-selected-button even:tw-bg-background-background"
				[attr.data-cy]="'script-test-point-table-row-' + row.name"></tr>
		</mat-table>
	</div>`,
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
	],
	changeDetection: ChangeDetectionStrategy.OnPush,
})
export class TestPointTableComponent {
	scriptResult = input.required<ResultReference>();
	filterText = input<string>('');

	headerService = inject(HeaderService);

	datasource = new MatTableDataSource<TestPointReference>();

	private _filterEffect = effect(
		() => (this.datasource.filter = this.filterText())
	);

	private _dataEffect = effect(
		() => (this.datasource.data = this.scriptResult().testPoints)
	);

	getTableHeaderByName(header: keyof TestPointReference) {
		return this.headerService.getHeaderByName(testPointDetails, header);
	}

	headers: (keyof TestPointReference)[] = [
		'name',
		'testNumber',
		'result',
		'overallResult',
		'resultType',
		'interactive',
		'groupName',
		'groupType',
		'groupOperator',
		'expected',
		'actual',
		'requirement',
		'elapsedTime',
		'transmissionCount',
		'notes',
	];
}
