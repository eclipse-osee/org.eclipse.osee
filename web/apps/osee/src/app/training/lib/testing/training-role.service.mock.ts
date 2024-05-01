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
import { TrainingRoleRecord } from '../types/training-role';
import { of } from 'rxjs';
import { TrainingRoleService } from './../services/training-role.service';

export const TrainingRoleServiceMock: Partial<TrainingRoleService> = {
	getTrainingRoleRecords() {
		return of(TrainingRoleRecordMock);
	},
	getTrainingRoles() {
		return of(TrainingRoleMock);
	},
};

export const TrainingRoleMock: string[] = [
	'Tester',
	'Engineer',
	'Requirements',
];

export const TrainingRoleRecordMock: TrainingRoleRecord[] = [
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
