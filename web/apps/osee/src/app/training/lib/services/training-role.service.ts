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
import { Observable, of } from 'rxjs';
import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { TrainingRoleRecord } from '../types/training-role';

@Injectable({
	providedIn: 'root',
})
export class TrainingRoleService {
	private trainingRoleRecords: Observable<TrainingRoleRecord[]>;
	private trainingRoles: Observable<string[]>;

	constructor(private http: HttpClient) {
		this.trainingRoleRecords = of(this.TEST_ROLE_RECORDS);
		this.trainingRoles = of(this.TEST_ROLES_TO_ADD);
	}

	public getTrainingRoleRecords(): Observable<TrainingRoleRecord[]> {
		return this.trainingRoleRecords;
	}

	public getTrainingRoles(): Observable<string[]> {
		return this.trainingRoles;
	}

	private TEST_ROLES_TO_ADD: string[] = [
		'Tester',
		'Engineer',
		'Requirements',
	];

	private TEST_ROLE_RECORDS: TrainingRoleRecord[] = [
		{
			userName: 'John Smith',
			roleName: 'Tester',
			startDate: '1/2/2023',
			endDate: '4/27/2023',
		},
		{
			userName: 'John Smith',
			roleName: 'Engineer',
			startDate: '1/2/2023',
			endDate: '4/27/2023',
		},
		{
			userName: 'John Smith',
			roleName: 'Requirements',
			startDate: '1/2/2023',
			endDate: '4/27/2023',
		},
	];
}
