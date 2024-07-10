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
	input,
} from '@angular/core';
import { AsyncPipe, NgFor, NgIf } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { MatList } from '@angular/material/list';
import { MatDivider } from '@angular/material/divider';
import { MatDialogModule } from '@angular/material/dialog';
import { MatPaginator } from '@angular/material/paginator';
import { MatInput } from '@angular/material/input';
import { MatFormField } from '@angular/material/form-field';
import { MatIcon } from '@angular/material/icon';
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
import { CiDetailsService } from '../../../services/ci-details.service';
import { HeaderService } from '@osee/shared/services';
import { ResultReference } from '../../../types';
import { scriptResHeaderDetails } from '../../../table-headers/script-headers';

@Component({
	selector: 'osee-run-info',
	standalone: true,
	template: `
		@if (scriptResult()) {
			<div class="mat-elevation-z8 tw-max-h-96 tw-overflow-scroll">
				<mat-table
					[dataSource]="rowKeys"
					class="tw-overflow-scroll">
					<ng-container matColumnDef="key">
						<th
							mat-header-cell
							*matHeaderCellDef
							class="tw-text-center tw-align-middle tw-font-medium tw-text-primary-600">
							Key
						</th>
						<td
							mat-cell
							*matCellDef="let rowValue"
							class="tw-align-middle">
							{{
								(getTableRowHeaderByName(rowValue) | async)
									?.humanReadable || ''
							}}
						</td>
					</ng-container>
					<ng-container matColumnDef="value">
						<th
							mat-header-cell
							*matHeaderCellDef
							class="tw-text-center tw-align-middle tw-font-medium tw-text-primary-600">
							Value
						</th>
						<td
							mat-cell
							*matCellDef="let rowValue"
							class="tw-align-middle">
							@if (
								(getTableRowHeaderByName(rowValue) | async)
									?.header;
								as _rowHeaderIndex
							) {
								{{ scriptResult()[_rowHeaderIndex] }}
							}
						</td>
					</ng-container>
					<tr
						mat-row
						*matRowDef="let row; columns: headers; let i = index"
						class="odd:tw-bg-selected-button even:tw-bg-background-background"
						[attr.data-cy]="
							'script-result-table-row-' + row.name
						"></tr>
					<tr
						mat-header-row
						*matHeaderRowDef="headers; sticky: true"></tr>
				</mat-table>
			</div>
		}
	`,
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
export class RunInfoComponent {
	scriptResult = input.required<ResultReference>();

	ciDetailsService = inject(CiDetailsService);
	headerService = inject(HeaderService);

	headers: string[] = ['key', 'value'];

	getTableRowHeaderByName(rowHeader: keyof ResultReference) {
		return this.headerService.getHeaderByName(
			scriptResHeaderDetails,
			rowHeader
		);
	}

	rowKeys: (keyof ResultReference)[] = [
		'name',
		'processorId',
		'runtimeVersion',
		'executionDate',
		'executionEnvironment',
		'machineName',
		'javaVersion',
		'scriptAborted',
		'elapsedTime',
		'osArchitecture',
		'osName',
		'osVersion',
		'oseeServerJar',
		'oseeServer',
		'oseeVersion',
		'executedBy',
	];
}
