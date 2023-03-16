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
import { Component } from '@angular/core';
import { HeaderService } from '@osee/shared/services';
import { TrainingRoleRecord } from '../../types/training-role';
import { trainingRoleHeaderDetails } from './training-role-table-header';
import { MatTableDataSource } from '@angular/material/table';
import { map, Observable } from 'rxjs';
import { TrainingRoleService } from './../../services/training-role.service';
import { CommonModule } from '@angular/common';
import { MatTableModule } from '@angular/material/table';

@Component({
	standalone: true,
	selector: 'osee-training-role-table',
	templateUrl: './training-role-table.component.html',
	styleUrls: ['./training-role-table.component.sass'],
	imports: [MatTableModule, CommonModule],
})
export class TrainingRoleTableComponent {
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

	constructor(
		private headerService: HeaderService,
		private trainingRoleService: TrainingRoleService
	) {}

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
