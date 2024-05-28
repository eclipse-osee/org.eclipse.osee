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
import { TestBed } from '@angular/core/testing';
import { CreateActionService } from './create-action.service';
import { TestScheduler } from 'rxjs/testing';
import {
	BranchInfoServiceMock,
	MockUserResponse,
	branchRoutedUiServiceMock,
	testnewActionResponse,
} from '@osee/shared/testing';
import {
	BranchInfoService,
	BranchRoutedUIService,
} from '@osee/shared/services';
import { UserDataAccountService } from '@osee/auth';
import { userDataAccountServiceMock } from '@osee/auth/testing';
import { CreateAction } from '@osee/configuration-management/types';
import { ActionService } from './action.service';
import { actionServiceMock } from '@osee/configuration-management/testing';

describe('CreateActionService', () => {
	let service: CreateActionService;
	let scheduler: TestScheduler;

	beforeEach(() => {
		TestBed.configureTestingModule({
			providers: [
				{ provide: ActionService, useValue: actionServiceMock },
				{ provide: BranchInfoService, useValue: BranchInfoServiceMock },
				{
					provide: BranchRoutedUIService,
					useValue: branchRoutedUiServiceMock,
				},
				{
					provide: UserDataAccountService,
					useValue: userDataAccountServiceMock,
				},
			],
		});
		service = TestBed.inject(CreateActionService);
	});

	beforeEach(
		() =>
			(scheduler = new TestScheduler((actual, expected) => {
				expect(actual).toEqual(expected);
			}))
	);

	it('should be created', () => {
		expect(service).toBeTruthy();
	});

	it('should add an action', () => {
		scheduler.run(({ expectObservable }) => {
			const createAction = new CreateAction(MockUserResponse);
			expectObservable(service.createAction(createAction, '3')).toBe(
				'(a|)',
				{ a: testnewActionResponse }
			);
		});
	});

	it('should add an action and create a branch', () => {
		scheduler.run(({ expectObservable }) => {
			const create = new CreateAction(MockUserResponse);
			create.createBranchDefault = true;
			expectObservable(service.createAction(create, '3')).toBe('(a|)', {
				a: testnewActionResponse,
			});
		});
	});
});
