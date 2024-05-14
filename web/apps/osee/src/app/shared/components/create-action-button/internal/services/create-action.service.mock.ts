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
import { CreateActionService } from './create-action.service';
import { CreateAction } from '@osee/shared/types/configuration-management';
import {
	MockUserResponse,
	testnewActionResponse,
	testWorkType,
	testAgilePoints,
} from '@osee/shared/testing';

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
			id: '123',
			name: 'First ARB',
		},
		{
			id: '456',
			name: 'Second ARB',
		},
	]),
	workTypes: of([testWorkType]),
};
