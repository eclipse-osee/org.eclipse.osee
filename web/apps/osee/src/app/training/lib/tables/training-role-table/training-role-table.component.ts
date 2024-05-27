/*********************************************************************
 * Copyright (c) 2023 Boeing
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
import { AsyncPipe, NgClass } from '@angular/common';
import { Component, inject } from '@angular/core';
import {
	MatCell,
	MatCellDef,
	MatColumnDef,
	MatHeaderCell,
	MatHeaderCellDef,
	MatHeaderRow,
	MatHeaderRowDef,
	MatNoDataRow,
	MatRow,
	MatRowDef,
	MatTable,
	MatTableDataSource,
} from '@angular/material/table';
import { HeaderService } from '@osee/shared/services';
import { Observable, map } from 'rxjs';
import { TrainingRoleService } from '../../services/training-role.service';
import { TrainingRoleRecord } from '../../types/training-role';
import { trainingRoleHeaderDetails } from './training-role-table-header';

@Component({
	standalone: true,
	selector: 'osee-training-role-table',
	templateUrl: './training-role-table.component.html',
	styles: [],
	imports: [
		MatTable,
		MatNoDataRow,
		MatColumnDef,
		MatHeaderCell,
		MatHeaderCellDef,
		MatCell,
		MatCellDef,
		MatHeaderRow,
		MatHeaderRowDef,
		MatRow,
		MatRowDef,
		NgClass,
		AsyncPipe,
	],
})
export class TrainingRoleTableComponent {
	private headerService = inject(HeaderService);
	private trainingRoleService = inject(TrainingRoleService);

	private dataSource = new MatTableDataSource<TrainingRoleRecord>();

	coursesAsMatTableDataSource$: Observable<
		MatTableDataSource<TrainingRoleRecord>
	> = this.trainingRoleService.getTrainingRoleRecords().pipe(
		map((roles) => {
			const dataSource = this.dataSource;
			dataSource.data = roles;
			return dataSource;
		})
	);

	getTableHeadersByName(header: keyof TrainingRoleRecord) {
		return this.headerService.getHeaderByName(
			trainingRoleHeaderDetails,
			header
		);
	}

	headers: (keyof TrainingRoleRecord)[] = [
		'userName',
		'roleName',
		'startDate',
		'endDate',
	];
}
