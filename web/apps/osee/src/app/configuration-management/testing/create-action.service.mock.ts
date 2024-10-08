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
import { of } from 'rxjs';
import {
	MockUserResponse,
	testWorkType,
	testAgilePoints,
} from '@osee/shared/testing';
import { CreateAction } from '@osee/configuration-management/types';
import { CreateActionService } from '@osee/configuration-management/services';
import { testnewActionResponse } from './new-action.response.mock';

export const createActionServiceMock: Partial<CreateActionService> = {
	getPoints() {
		return of(testAgilePoints);
	},
	createAction(value: CreateAction, category: string) {
		return of(testnewActionResponse);
	},
	user: of(MockUserResponse),
	actionableItems: of([
		{
			id: '123' as `${number}`,
			name: 'First ARB',
			workType: 'ARB',
		},
		{
			id: '456' as `${number}`,
			name: 'Second ARB',
			workType: 'ARB',
		},
	]),
	workTypes: of([testWorkType]),
	createdTeamWorkflows: of([]),
};
