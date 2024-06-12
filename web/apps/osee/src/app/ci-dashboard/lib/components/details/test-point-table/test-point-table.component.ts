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
import { ChangeDetectionStrategy, Component, inject } from '@angular/core';
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
import { HeaderService } from '@osee/shared/services';
import { CiDetailsService } from '../../../services/ci-details.service';
import { TestPointReference } from '../../../types';
import { testPointDetails } from '../../../table-headers/test-point-headers';

@Component({
	selector: 'osee-test-point-table',
	standalone: true,
	template: `<ng-container *ngIf="scriptResult | async as _scriptresult">
		<div
			class="mat-elevation-z8 tw-max-h-96 tw-overflow-scroll tw-overflow-x-auto">
			<mat-table [dataSource]="_scriptresult.testPoints">
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
						*matCellDef="let testPoint"
						class="tw-align-middle">
						<ng-container> {{ testPoint[header] }} </ng-container>
					</td>
				</ng-container>
				<tr
					mat-header-row
					*matHeaderRowDef="headers; sticky: true"></tr>
				<tr
					mat-row
					*matRowDef="let row; columns: headers; let i = index"
					class="odd:tw-bg-selected-button even:tw-bg-background-background"
					[attr.data-cy]="
						'script-test-point-table-row-' + row.name
					"></tr>
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
export class TestPointTableComponent {
	ciDetailsService = inject(CiDetailsService);
	headerService = inject(HeaderService);

	scriptResult = this.ciDetailsService.scriptResult;

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
